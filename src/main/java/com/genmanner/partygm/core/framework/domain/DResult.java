package com.genmanner.partygm.core.framework.domain;

import java.io.Serializable;

/**
 * Ajax处理结果对象
 *  
 */
public class DResult implements Serializable {
	public Integer startIndex;
	/**
	 * 状态信息,正确返回OK,否则返回 ERROR
	 */
	private Status status;
	/**
	 * 消息对象,在当前状态下返回的信息内容
	 */
	private Object message;
	Object jData;
	
	public Object getjData() {
		return jData;
	}

	public void setjData(Object jData) {
		this.jData = jData;
	}

	public DResult() {
		super();
	}

	/**
	 * 对象初始化
	 * @param status  状态
	 * @param message  消息
	 */
	public DResult(Status status, Object message) {
		this.status = status;
		this.message = message;
	}
	public DResult(Status status, Object message,Integer startIndex) {
		this.status = status;
		this.message = message;
		this.startIndex = startIndex;
	}
	/**
	 * 结果类型信息
	 */
	public enum Status {
		OK, ERROR
	}

	/**
	 * 添加成功结果信息
	 * @param message 返回的信息可以是字符串
	 */
	public void addOK(Object message) {
		this.message = message;
		this.status = Status.OK;
	}

	/**
	 * 添加错误消息
	 * @param message
	 */
	public void addError(Object message) {
		this.message = message;
		this.status = Status.ERROR;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public Integer getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}
	
}
