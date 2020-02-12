package com.genmanner.partygm.core.framework.boottask.domain;

import com.genmanner.partygm.core.framework.domain.ABean;

/**
 * 任务类型信息
 *  
 */
public class TaskKind extends ABean {	
	/**
	 * 序列ID
	 */
	private static final long serialVersionUID = -8954864369964551531L;
	
	/**
	 * 获取分发器Bean名字
	 * @return 分发器Bean名字
	 */
	public String getDispatchBeanName() {
		return dispatchBeanName;
	}
	
	/**
	 * 设置分发器Bean名字
	 * @param dispatchBeanName 分发器Bean名字
	 */
	public void setDispatchBeanName(String dispatchBeanName) {
		this.dispatchBeanName = dispatchBeanName;
	}
	
	/**
	 * 获取任务Bean名字
	 * @return 任务Bean名字
	 */
	public String getTaskBeanName() {
		return taskBeanName;
	}
	
	/**
	 * 设置任务Bean名字
	 * @param taskBeanName 任务Bean名字
	 */
	public void setTaskBeanName(String taskBeanName) {
		this.taskBeanName = taskBeanName;
	}
	
	/**
	 * 获取异常处理器Bean名字
	 * @return 异常处理器Bean名字
	 */
	public String getExceptionSolverBeanName() {
		return exceptionSolverBeanName;
	}
	
	/**
	 * 设置异常处理器Bean名字
	 * @param exceptionSolverBeanName 异常处理器Bean名字
	 */
	public void setExceptionSolverBeanName(String exceptionSolverBeanName) {
		this.exceptionSolverBeanName = exceptionSolverBeanName;
	}
	
	private String dispatchBeanName;
	private String taskBeanName;
	private String exceptionSolverBeanName;
}
