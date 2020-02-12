package com.genmanner.partygm.core.framework.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 表之间数据同步包装类<br>
 * 1.同步数据列表中不含目标表主键字段,默认为插入<br>
 * 2.包含目标表主键字段,则判断是插入还是更新<br>
 * 3.图片传输需转换成字节码(byte[])
 *  
 */
public class TabDataSync implements Serializable {
	private static final long serialVersionUID = 3378474040292857844L;

	/**
	 * 目标表名
	 */
	private String targetTabName;
	
	/**
	 * 目标表主键字段
	 */
	private String targetPkField = "id";
	
	/**
	 * 同步数据对象(map对象key值为数据库对应列)
	 */
	private List<Map<String, Object>> dataList;
	
	/**
	 * 默认构造函数
	 */
	public TabDataSync() {}
	
	/**
	 * 构造函数
	 * @param targetTabName		:目标表名
	 */
	public TabDataSync(String targetTabName) {
		this.targetTabName = targetTabName;
	}

	/**
	 * 构造函数
	 * @param targetTabName		:目标表名
	 * @param targetPkField		:目标表主键字段
	 */
	public TabDataSync(String targetTabName, String targetPkField) {
		this.targetTabName = targetTabName;
		this.targetPkField = targetPkField;
	}

	public String getTargetTabName() {
		return targetTabName;
	}

	public void setTargetTabName(String targetTabName) {
		this.targetTabName = targetTabName;
	}

	public String getTargetPkField() {
		return targetPkField;
	}

	public void setTargetPkField(String targetPkField) {
		this.targetPkField = targetPkField;
	}

	public List<Map<String, Object>> getDataList() {
		return dataList;
	}

	public void setDataList(List<Map<String, Object>> dataList) {
		this.dataList = dataList;
	}
	
}
