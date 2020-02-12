package com.genmanner.partygm.core.framework.boottask.domain;

import com.genmanner.partygm.core.framework.domain.ABean;

/**
 * 自启动定时任务
 *  
 */
public class BootTimerTask extends ABean {	
	/**
	 * 序列ID
	 */
	private static final long serialVersionUID = -7633127488485320485L;	
	
	/**
	 * 获取任务名称
	 * @return 任务名称
	 */
	public String getTaskName() {
		return taskName;
	}
	
	/**
	 * 设置任务名称
	 * @param taskName 任务名称
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
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
	
	/**
	 * 获取定时周期
	 * @return 定时周期
	 */
	public Integer getPeriod() {
		return period;
	}
	
	/**
	 * 设置定时周期
	 * @param period 定时周期
	 */
	public void setPeriod(Integer period) {
		this.period = period;
	}
	
	/**
	 * 获取是否启用
	 * @return 是否启用
	 */
	public Boolean getEnabled() {
		return enabled;
	}
	
	/**
	 * 设置是否启用
	 * @param enabled 是否启用
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	private String taskName;
	private String dispatchBeanName;
	private String taskBeanName;
	private String exceptionSolverBeanName;
	private Integer period;
	private Boolean enabled;
}
