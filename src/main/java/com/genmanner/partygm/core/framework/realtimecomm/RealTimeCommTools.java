package com.genmanner.partygm.core.framework.realtimecomm;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import sun.misc.BASE64Encoder;

import com.genmanner.partygm.core.common.dispatch.IDispatch;
import com.genmanner.partygm.core.common.dispatch.IExceptionSolver;
import com.genmanner.partygm.core.common.dispatch.ITaskSchedule;
import com.genmanner.partygm.core.common.util.BeanUtil;
import com.genmanner.partygm.core.common.util.HttpUtil;
import com.genmanner.partygm.core.framework.boottask.TaskKindTools;
import com.genmanner.partygm.core.framework.boottask.domain.TaskKind;
import com.genmanner.partygm.core.framework.realtimecomm.domain.TaskStream;
import com.genmanner.partygm.core.framework.realtimecomm.manager.TaskStreamManager;
import com.genmanner.partygm.core.framework.utils.MessageEncryptUtil;

/**
 * 通信工具类
 *  
 */
@Service
public class RealTimeCommTools {
	/**
	 * 同步发送数据到业务系统端【平台端】
	 * @param universityCode 学校标识码
	 * @param url 业务系统端url
	 * @param data 数据 
	 */
	public String syncSendData(String universityCode, String url, String data) {
		try {
			byte[] encryptData = MessageEncryptUtil.encryptOnBusiness(data.getBytes("UTF-8"));
			Map<String, String> mapParams = new HashMap<String, String>();
			mapParams.put("data", new BASE64Encoder().encode(encryptData));
			return HttpUtil.postData(loadUniversityUrlPrefix(universityCode)+url, mapParams);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	/**
	 * 异步发送报文【业务系统端】
	 * @param taskKindId 任务类型编号
	 * @param params 任务参数 
	 */
	public void asyncSendMessage(String taskKindId, String params) {
		TaskStream taskStream = new TaskStream();
		int taskStreamId = taskConfigTools.fetchStreamIdAndLock(taskKindId);
		taskStream.setParams(params);
		taskStream.setStatus("0");
		taskStream.setTaskKindId(taskKindId);
		taskStream.setTaskStreamId(taskStreamId);
		taskStreamManager.insert(taskStream, TaskStreamManager.TaskStreamKind.TaskStreamKind_Send);
		jdbcTemplate.execute("commit");			//确保任务流水存盘
		log.info("Save Task to Db[" + taskKindId + ", " + taskStreamId + "].");
		wakeupTask(SENDTASK_KINDID);
	}
	
	/**
	 * 异步接收报文.【平台端】
	 * @param taskStream 任务流水
	 */
	public void asyncReceiveMessage(TaskStream taskStream) {		
		taskStreamManager.insert(taskStream, TaskStreamManager.TaskStreamKind.TaskStreamKind_Receive);
		log.info("Save Task to Db[" + taskStream.getTaskKindId() + ", " + taskStream.getTaskStreamId() + "].");
		jdbcTemplate.execute("commit");			//确保任务流水存盘
		wakeupTask(taskStream.getTaskKindId());
	}
	
	/**
	 * 批量异步接收报文.【平台端】insert部分可用批量操作优化！
	 * @param taskStream 任务流水
	 */
	public void asyncReceiveMessage(TaskStream[] arrTaskStream) {
		for(TaskStream taskStream : arrTaskStream) {
			taskStreamManager.insert(taskStream, TaskStreamManager.TaskStreamKind.TaskStreamKind_Receive);
			log.info("Save Task to Db[" + taskStream.getTaskKindId() + ", " + taskStream.getTaskStreamId() + "].");
		}
		
		jdbcTemplate.execute("commit");			//确保任务流水存盘		
		wakeupTask(arrTaskStream);
	}
	
	/**
	 * 唤醒任务
	 * @param taskStream 任务流水
	 */
	public void wakeupTask(List<TaskStream> lstTaskStream) {
		if(lstTaskStream!=null && lstTaskStream.size()>0) {
			TaskStream[] arrTaskStream = new TaskStream[lstTaskStream.size()];
			lstTaskStream.toArray(arrTaskStream);
			wakeupTask(arrTaskStream);
		}
	}
	
	/**
	 * 唤醒任务
	 * @param taskStream 任务流水
	 */
	public void wakeupTask(TaskStream[] arrTaskStream) {
		Set<String> setTaskKindId = new HashSet<String>();
		
		for(TaskStream taskStream : arrTaskStream) {
			setTaskKindId.add(taskStream.getTaskKindId());
		}
		
		for(String taskKindId : setTaskKindId) {
			wakeupTask(taskKindId);
		}
	}
	
	/**
	 * 唤醒任务
	 * @param taskKindId 任务类型编号
	 */
	public void wakeupTask(String taskKindId) {
		TaskKind taskKind = taskKindTools.getTaskKind(taskKindId);
		Long taskId = mapTaskId.get(taskKindId);
		IDispatch dispatch = (IDispatch)BeanUtil.loadBeanByName(taskKind.getDispatchBeanName());
		
		if(taskId == null) {
			ITaskSchedule taskSchedule = (ITaskSchedule)BeanUtil.loadBeanByName(taskKind.getTaskBeanName());
			IExceptionSolver exceptionSolver = (IExceptionSolver)BeanUtil.loadBeanByName(taskKind.getExceptionSolverBeanName());
			taskId = dispatch.create(taskSchedule, exceptionSolver);
			mapTaskId.put(taskKindId, taskId);
			
			if(taskSchedule instanceof AReceiveTask) {
				((AReceiveTask)taskSchedule).setTaskKindId(taskKindId);
			}
		}
		
		dispatch.notice(taskId, 0);
	}
	
	/**
	 * 平台端装载业务系统URL前缀
	 * FIXME! 最终将从学校基本信息表中装载，需要缓存以提高效率，并保证缓存有效性
	 * @return 加密后的RSA私钥
	 */
	protected static String loadUniversityUrlPrefix(String universityCode) {
		return "http://localhost:8080/eplatform-product-main";
	}
	
	protected Map<String, Long> mapTaskId = new HashMap<String, Long>();

	@Resource
	private TaskConfigTools taskConfigTools;
	@Resource
	private TaskKindTools taskKindTools;
	@Resource
	private TaskStreamManager taskStreamManager;
	@Resource
	private JdbcTemplate jdbcTemplate;	
	
	public static final String SENDTASK_KINDID = "0";		//发送任务的类型ID
	private static final Log log = LogFactory.getLog(RealTimeCommTools.class);
}
