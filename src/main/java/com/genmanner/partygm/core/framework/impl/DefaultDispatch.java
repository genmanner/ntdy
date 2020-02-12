package com.genmanner.partygm.core.framework.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.genmanner.partygm.core.common.dispatch.IDispatch;
import com.genmanner.partygm.core.common.dispatch.IExceptionSolver;
import com.genmanner.partygm.core.common.dispatch.ITaskSchedule;
import com.genmanner.partygm.core.framework.utils.SysConfTools;

/**
 * 默认Dispatch
 *  
 */
@Service
public class DefaultDispatch implements IDispatch {
	/**
	 * 创建一个任务
	 * @param schedule 任务调度对象
	 * @param solver 异常处理对象
	 * @return 任务Id
	 */
	@Override
	public long create(ITaskSchedule schedule, IExceptionSolver solver) {
		return sysConfTools.getDefaultDispatch().create(schedule, solver);
	}

	/**
	 * 获取任务调度对象
	 * @param iTaskId 任务Id
	 * @return 任务调度对象
	 */
	@Override
	public ITaskSchedule getSchedule(long iTaskId) {
		return sysConfTools.getDefaultDispatch().getSchedule(iTaskId);
	}

	/**
	 * 启动定时调度
	 * @param iTaskId 任务Id
	 * @param iPeriod 定时周期
	 * @return 是否启动成功
	 */
	@Override
	public boolean start(long iTaskId, int iPeriod) {
		return sysConfTools.getDefaultDispatch().start(iTaskId, iPeriod);
	}

	/**
	 * 通知调度，延迟iDelay ms后，调度任务一次
	 * @param iTaskId 任务Id
	 * @param iDelay 延迟值
	 * @return 是否通知成功 
	 */
	@Override
	public boolean notice(long iTaskId, int iDelay) {
		return sysConfTools.getDefaultDispatch().notice(iTaskId, iDelay);
	}

	/**
	 * 在当前Context中执行任务
	 * @param iTaskId 任务Id
	 * @return 是否执行成功
	 */
	@Override
	public boolean execute(long iTaskId) {
		return sysConfTools.getDefaultDispatch().execute(iTaskId);
	}

	/**
	 * 取消任务调度
	 * @param iTaskId 任务Id
	 * @return 是否取消成功
	 */
	@Override
	public boolean cancel(long iTaskId) {
		return sysConfTools.getDefaultDispatch().cancel(iTaskId);
	}
	
	/**
	 * 删除任务
	 * @param iTaskId 任务Id
	 * @return 是否删除成功
	 */
	public boolean remove(long iTaskId) {
		return sysConfTools.getDefaultDispatch().remove(iTaskId);
	}
	
	@Resource
	SysConfTools sysConfTools;
}
