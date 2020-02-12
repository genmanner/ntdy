package com.genmanner.partygm.core.framework.realtimecomm;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.genmanner.partygm.core.common.dispatch.ExceptionMsgBase;
import com.genmanner.partygm.core.common.dispatch.ITaskSchedule;
import com.genmanner.partygm.core.framework.realtimecomm.domain.TaskStream;
import com.genmanner.partygm.core.framework.realtimecomm.manager.TaskStreamManager;

/**
 * 检测发送/接收到的数据任务
 *  
 */
@Service
public class CheckTaskStreamTask implements ITaskSchedule {
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
					TaskStreamManager.TaskStreamStatus.TaskStreamStatus_Solving,
					TaskStreamManager.TaskStreamKind.TaskStreamKind_Send);
			
			if(lstTaskStream!=null && lstTaskStream.size() > 0) {
				taskStreamManager.BatchRollback(lstTaskStream, TaskStreamManager.TaskStreamKind.TaskStreamKind_Send);
				
				for(TaskStream taskStream : lstTaskStream) {
					log.info("Reset Send TaskStream[" + taskStream.getTaskKindId() + ", " + taskStream.getTaskStreamId() + "].");
				}
			}
			
			lstTaskStream = taskStreamManager.selectAll(
					TaskStreamManager.TaskStreamStatus.TaskStreamStatus_New,
					TaskStreamManager.TaskStreamKind.TaskStreamKind_Send);
			
			if(lstTaskStream!=null && lstTaskStream.size() > 0) {
				realTimeCommTools.wakeupTask(RealTimeCommTools.SENDTASK_KINDID);
			}
			
			lstTaskStream = taskStreamManager.selectAll(
					TaskStreamManager.TaskStreamStatus.TaskStreamStatus_Solving,
					TaskStreamManager.TaskStreamKind.TaskStreamKind_Receive);
			
			if(lstTaskStream!=null && lstTaskStream.size() > 0) {
				taskStreamManager.BatchRollback(lstTaskStream, TaskStreamManager.TaskStreamKind.TaskStreamKind_Receive);
			
				for(TaskStream taskStream : lstTaskStream) {
					log.info("Reset Receive TaskStream[" + taskStream.getTaskKindId() + ", " + taskStream.getTaskStreamId() + "].");
				}
			}
			
			realTimeCommTools.wakeupTask(taskStreamManager.selectAll(
					TaskStreamManager.TaskStreamStatus.TaskStreamStatus_New,
					TaskStreamManager.TaskStreamKind.TaskStreamKind_Receive));
		} finally {
			bInSolve = false;
		}
	}
	
	/**
	 * 是否正在处理
	 */
	boolean bInSolve = false;
	
	@Resource
	private TaskStreamManager taskStreamManager;
	@Resource
	private RealTimeCommTools realTimeCommTools;

	private static final Log log = LogFactory.getLog(CheckTaskStreamTask.class);
}
