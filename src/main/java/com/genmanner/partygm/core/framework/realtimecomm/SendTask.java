package com.genmanner.partygm.core.framework.realtimecomm;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import sun.misc.BASE64Encoder;

import com.genmanner.partygm.core.common.dispatch.ExceptionMsgBase;
import com.genmanner.partygm.core.common.dispatch.ITaskSchedule;
import com.genmanner.partygm.core.common.framework.SpringTools;
import com.genmanner.partygm.core.common.util.HttpUtil;
import com.genmanner.partygm.core.common.util.JsonUtil;
import com.genmanner.partygm.core.framework.realtimecomm.domain.TaskStream;
import com.genmanner.partygm.core.framework.realtimecomm.manager.TaskStreamManager;
import com.genmanner.partygm.core.framework.utils.MessageEncryptUtil;
import com.genmanner.partygm.core.framework.utils.SysConfTools;

/**
 * 数据发送任务【业务系统端】
 *  
 */
@Service
public class SendTask implements ITaskSchedule {
	/**
	 * 逻辑操作
	 * @throws ExceptionMsgBase 操作异常
	 * @return 操作结果
	 */
	@Override
	public void logicOperate() throws ExceptionMsgBase {
		if(bInSolve) {
			return;
		}
		
		bInSolve = true;
		
		try {
			List<TaskStream> lstTaskStream = taskStreamManager.selectAll(
					TaskStreamManager.TaskStreamStatus.TaskStreamStatus_New,
					TaskStreamManager.TaskStreamKind.TaskStreamKind_Send);
			
			if(lstTaskStream.size() > 0) {
				log.info("Prepare to Batch Change Send State[" + getTaskBatchMsg(lstTaskStream) + "].");
				taskStreamManager.BatchInSolving(lstTaskStream, TaskStreamManager.TaskStreamKind.TaskStreamKind_Send);
				log.info("Prepare to Batch Send[" + getTaskBatchMsg(lstTaskStream) + "].");
				List<TaskStream> lstSended = BatchSend(lstTaskStream);
				log.info("Batch Sended[" + getTaskBatchMsg(lstSended) + "].");
				taskStreamManager.BatchSolved(lstSended, TaskStreamManager.TaskStreamKind.TaskStreamKind_Send);
				log.info("Batch Changed Sended State[" + getTaskBatchMsg(lstSended) + "].");
				
				if(lstTaskStream.size() > 0) {
					taskStreamManager.BatchRollback(lstTaskStream, TaskStreamManager.TaskStreamKind.TaskStreamKind_Send);
					log.info("Batch Rollback[" + getTaskBatchMsg(lstTaskStream) + "].");
				}
			}
		} finally {
			bInSolve = false;
		}
	}
	
	/**
	 * 需要将任务通过MessageEncryptUtil加密，然后通过https发送平台端
	 * 可以适量批量发送，失败的任务只发送一次，交由异常处理完成重试
	 * @param lstTaskStream 任务流水链表
	 * @return 成功发送的任务流水链表
	 */
	protected List<TaskStream> BatchSend(List<TaskStream> lstTaskStream) {
		int iIgnore = 0;
		List<TaskStream> lstSended = new LinkedList<TaskStream>();	
		
		while(lstTaskStream.size() > iIgnore) {
			int iBatchSize = (lstTaskStream.size()-iIgnore>=BATCHSEND_SIZE ? BATCHSEND_SIZE : lstTaskStream.size()-iIgnore);
			List<TaskStream> lstToSend = lstTaskStream.subList(iIgnore, iIgnore+iBatchSize);
			
			if(sendData(lstToSend)) {
				lstSended.addAll(lstToSend);
				lstTaskStream.removeAll(lstToSend);
			} else {
				iIgnore += iBatchSize;
			}
		}
		
		for(int i=0; i<lstTaskStream.size();) {
			if(sendData(lstTaskStream.get(i))) {
				lstSended.add(lstTaskStream.get(i));
				lstTaskStream.remove(i);
			} else {
				++i;
			}
		}
		
		return lstSended;
	}
	
	private boolean sendData(List<TaskStream> lstToSend) {
		String data = "{count: " + BATCHSEND_SIZE + ", data:" + JsonUtil.parseObject(lstToSend) + "}";
		
		try {
			byte[] encryptData = MessageEncryptUtil.encryptOnBusiness(data.getBytes("UTF-8"));
			Map<String, String> mapParams = new HashMap<String, String>();
			mapParams.put("data", new BASE64Encoder().encode(encryptData));
			String url = SpringTools.getBean(SysConfTools.class)
					.getSysConf(SysConfTools.SYSCONF_KEY_PLATFORM_ADDRESS).getValue()+PLATFORM_DATA_RECEIVE_URL;
			String ret = HttpUtil.postData(url, mapParams);
			
			if("true".equals(ret)) {
				return true;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private boolean sendData(TaskStream taskStream) {
		List<TaskStream> lstToSend = new LinkedList<TaskStream>();
		lstToSend.add(taskStream);
		return sendData(lstToSend);
	}
	
	private String getTaskBatchMsg(List<TaskStream> lstToSend) {		
		if(lstToSend.size() > 0) {
			StringBuffer strRet = new StringBuffer(lstToSend.get(0).getTaskKindId())
					.append(", ").append(lstToSend.get(0).getTaskStreamId());
			
			for(int i=1; i<lstToSend.size(); ++i) {
				strRet.append("; ").append(lstToSend.get(i).getTaskKindId())
					.append(", ").append(lstToSend.get(i).getTaskStreamId());
			}
			
			return strRet.toString();
		}
		
		return "";
	}

	/**
	 * 是否正在处理
	 */
	boolean bInSolve = false;
	@Resource
	private TaskStreamManager taskStreamManager;
	
	private static final int BATCHSEND_SIZE = 50;		//发送任务的类型ID
	private static final String PLATFORM_DATA_RECEIVE_URL = "/login/datareceive.action";
	private static final Log log = LogFactory.getLog(SendTask.class);
}
