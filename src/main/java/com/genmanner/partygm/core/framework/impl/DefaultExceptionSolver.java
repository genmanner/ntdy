package com.genmanner.partygm.core.framework.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.genmanner.partygm.core.common.dispatch.ExceptionMsgBase;
import com.genmanner.partygm.core.common.dispatch.IDispatch;
import com.genmanner.partygm.core.common.dispatch.IExceptionSolver;
import com.genmanner.partygm.core.framework.utils.SysConfTools;

/**
 * 默认异常处理实现类
 *  
 */
@Service
public class DefaultExceptionSolver implements IExceptionSolver {
	/**
	 * 逻辑操作
	 * @param dispatcher 调度器
	 * @param taskId 任务Id
	 * @param exception 异常
	 * @throws ExceptionMsgBase 内联异常
	 */
	public void exceptionSolve(IDispatch dispatcher, long taskId, ExceptionMsgBase exception) throws ExceptionMsgBase {
		sysConfTools.getDefaultExceptionSolver().exceptionSolve(dispatcher, taskId, exception);
	}
	
	@Resource
	SysConfTools sysConfTools;
}
