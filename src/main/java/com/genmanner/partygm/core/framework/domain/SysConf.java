package com.genmanner.partygm.core.framework.domain;

import com.genmanner.partygm.core.framework.domain.ABean;

/**
 * 系统配置信息
 *  
 */
public class SysConf extends ABean {	
	/**
	 * 序列ID
	 */
	private static final long serialVersionUID = -585940333766998815L;
	
	/**
	 * 获取配置值
	 * @return 配置值
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * 设置配置值
	 * @param value 配置值
	 */
	public void setValue(String Value) {
		this.value = Value;
	}
	
	/**
	 * 获取配置描述
	 * @return 配置描述
	 */
	public String getDesc() {
		return desc;
	}
	
	/**
	 * 设置配置描述
	 * @param desc 配置描述
	 */
	public void setDesc(String Desc) {
		this.desc = Desc;
	}
	
	private String value;
	private String desc;
}
