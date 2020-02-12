package com.genmanner.partygm.core.framework.realtimecomm.domain;

import com.genmanner.partygm.core.framework.domain.ABean;

/**
 * 任务流水信息
 *  
 */
public class TaskStream extends ABean {
	/**
	 * 序列ID
	 */
	private static final long serialVersionUID = 6355548712880017730L;
	
	/**
	 * 获取任务类型Id
	 * @return 任务类型Id
	 */
	public String getTaskKindId() {
		return taskKindId;
	}
	
	/**
	 * 设置任务类型Id
	 * @param taskKindId 任务类型Id
	 */
	public void setTaskKindId(String taskKindId) {
		this.taskKindId = taskKindId;
	}
	
	/**
	 * 获取任务流水号
	 * @return 任务流水号
	 */
	public int getTaskStreamId() {
		return taskStreamId;
	}

	/**
	 * 设置任务流水号
	 * @param taskStreamId 任务流水号
	 */
	public void setTaskStreamId(int taskStreamId) {
		this.taskStreamId = taskStreamId;
	}

	/**
	 * 获取任务参数
	 * @return 任务参数
	 */
	public String getParams() {
		return params;
	}

	/**
	 * 设置任务参数
	 * @param params 任务参数
	 */
	public void setParams(String params) {
		this.params = params;
	}
	
	/**
	 * 获取学校代码
	 * @return 学校代码
	 */
	public String getUniversityCode() {
		return universityCode;
	}
	
	/**
	 * 设置学校代码
	 * @param universityCode 学校代码
	 */
	public void setUniversityCode(String universityCode) {
		this.universityCode = universityCode;
	}

	/**
	 * 获取任务状态
	 * @return 任务状态
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 设置任务状态
	 * @param status 任务状态
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	private String taskKindId;
	private int taskStreamId;
	private String params;
	private String universityCode;
	private String status;
}
