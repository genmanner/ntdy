package com.genmanner.partygm.core.framework.domain;

import java.io.Serializable;

/**
 * 实体抽象类,所有类的一个初级实体
 *  
 */
public abstract class ABean implements Serializable {
	
	/**
	 * 获取主键
	 */
	public String getId(){
		return id;
	}

	/**
	 * 设置ID属性,主要用于人工指定键值
	 */
	public void setId(String id){
		this.id=id;
	}	
	
	/**
	 * 获取记录创建时间
	 * @return 记录创建时间
	 */
	public String getAddTime() {
		return addTime;
	}

	/**
	 * 设置记录创建时间
	 * @param addTime 记录创建时间
	 */
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	/**
	 * 获取最后一次修改时间
	 * @return 最后一次修改时间
	 */
	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	/**
	 * 设置最后一次修改时间
	 * @param lastUpdateTime 最后一次修改时间
	 */
	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	/**
	 * 按配置编码生成主键生成方式如下：
	 * 系统会在对应的配置表中记录相应的编码生成格式，可配置格式见开发文档
	 * 调用本函数后即可生成一个主键，并把生成的主键返回和初始化到ID属性中
	 * @param key 系统配置的主键KEY
	 */
	public String setIdByFrameWork(String key){
		//请实现主键调用程序拼接，和一并完成完成开发说明文档
		
		return null;
	}
	
	/**
	 * 每一个表的主键
	 */
	private String id;
	
	/**
	 * 记录创建时间
	 */
	private String addTime;
	
	/**
	 * 记录最后一次修改时间
	 */
	private String lastUpdateTime;

}
