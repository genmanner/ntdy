package com.genmanner.partygm.core.framework.realtimecomm;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.genmanner.partygm.core.common.dispatch.ExceptionMsgBase;
import com.genmanner.partygm.core.common.dispatch.ITaskSchedule;
import com.genmanner.partygm.core.framework.realtimecomm.domain.TaskStream;
import com.genmanner.partygm.core.framework.realtimecomm.manager.TaskStreamManager;

/**
 * 数据接收任务抽象类，一类任务一个实现类【平台端】
 *  
 */
public abstract class AReceiveTask implements ITaskSchedule {
	/**
	 * 批量处理任务
	 * 任务可以只处理一次，交由异常处理完成重试
	 * @param lstTaskStream 任务列表
	 * @return 成功发送的任务列表，实现需要保证返回时lstTaskStream中仅有失败的任务
	 */
	protected abstract List<TaskStream> BatchSolve(List<TaskStream> lstTaskStream);
	
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
			List<TaskStream> lstTaskStream = taskStreamManager.selectAllByTaskKindId(taskKindId,
					TaskStreamManager.TaskStreamStatus.TaskStreamStatus_New, 
					TaskStreamManager.TaskStreamKind.TaskStreamKind_Receive);
			
			if(lstTaskStream.size() > 0) {
				log.info("Prepare to Batch Change Receive State[" + getTaskBatchMsg(lstTaskStream) + "].");
				taskStreamManager.BatchInSolving(lstTaskStream, TaskStreamManager.TaskStreamKind.TaskStreamKind_Receive);
				log.info("Prepare to Batch Receive[" + getTaskBatchMsg(lstTaskStream) + "].");
				List<TaskStream> lstSended = BatchSolve(lstTaskStream);
				log.info("Batch Received[" + getTaskBatchMsg(lstSended) + "].");
				taskStreamManager.BatchSolved(lstSended, TaskStreamManager.TaskStreamKind.TaskStreamKind_Receive);
				log.info("Batch Changed Received State[" + getTaskBatchMsg(lstSended) + "].");
				
				if(lstTaskStream.size() > 0) {
					taskStreamManager.BatchRollback(lstTaskStream, TaskStreamManager.TaskStreamKind.TaskStreamKind_Receive);
					log.info("Batch Rollback[" + getTaskBatchMsg(lstTaskStream) + "].");
				}
			}
		} finally {
			bInSolve = false;
		}
	}
	
	/**
	 * 获取任务类型ID
	 * @return 任务类型ID
	 */
	public String getTaskKindId() {
		return taskKindId;
	}
	
	/**
	 * 设置任务类型ID
	 * @param taskKindId 任务类型ID
	 */
	public void setTaskKindId(String taskKindId) {
		this.taskKindId = taskKindId;
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
	
	@Resource
	private TaskStreamManager taskStreamManager;
	
	private boolean bInSolve = false;
	private String taskKindId;
	private static final Log log = LogFactory.getLog(AReceiveTask.class);
}
