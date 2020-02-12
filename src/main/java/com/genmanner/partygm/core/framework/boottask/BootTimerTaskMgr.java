package com.genmanner.partygm.core.framework.boottask;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.genmanner.partygm.core.common.dispatch.IDispatch;
import com.genmanner.partygm.core.common.dispatch.IExceptionSolver;
import com.genmanner.partygm.core.common.dispatch.ITaskSchedule;
import com.genmanner.partygm.core.common.util.BeanUtil;
import com.genmanner.partygm.core.framework.boottask.domain.BootTimerTask;
import com.genmanner.partygm.core.framework.boottask.manager.BootTimerTaskManager;

/**
 * 自启动定时任务管理器
 *  
 */
@Service
public class BootTimerTaskMgr {
	/**
	 * 启动所有自启动定时任务
	 */
	public void StartAll() {
		StopAll();
		List<BootTimerTask> lstBootTimerTask = bootTimerTaskManager.getAllBootTimerTaskFromDb();
		
		for(BootTimerTask bootTimerTask : lstBootTimerTask) {
			IDispatch dispatch = (IDispatch)BeanUtil.loadBeanByName(bootTimerTask.getDispatchBeanName());
			ITaskSchedule taskSchedule = (ITaskSchedule)BeanUtil.loadBeanByName(bootTimerTask.getTaskBeanName());
			IExceptionSolver exceptionSolver = (IExceptionSolver)BeanUtil.loadBeanByName(bootTimerTask.getExceptionSolverBeanName());
			int iPeriod = bootTimerTask.getPeriod();
			Long iTaskId = dispatch.create(taskSchedule, exceptionSolver);
			lstTaskId.add(iTaskId);
			lstDispatch.add(dispatch);
			
			if(iPeriod > 0) {
				dispatch.start(iTaskId, iPeriod);
			} else {
				dispatch.notice(iTaskId, 0);
			}
		}
	}
	
	/**
	 * 停止所有自启动定时任务
	 */
	public void StopAll() {
		for(int i=0; i<lstDispatch.size(); ++i) {
			IDispatch dispatch = lstDispatch.get(i);
			Long iTaskId = lstTaskId.get(i);
			dispatch.remove(iTaskId);
		}

		lstTaskId.clear();
		lstDispatch.clear();
	}

	@Resource
	BootTimerTaskManager bootTimerTaskManager;
	
	List<Long> lstTaskId = new LinkedList<Long>();
	List<IDispatch> lstDispatch = new LinkedList<IDispatch>();
}
