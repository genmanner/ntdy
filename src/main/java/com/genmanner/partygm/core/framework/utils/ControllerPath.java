package com.genmanner.partygm.core.framework.utils;

import com.genmanner.partygm.core.common.util.StringEx;


/**
 * Controller路径构建工具类
 * <br>用于在程序编写controller类实现时生成JSP的相关路径
 *  
 */
public class ControllerPath {

	/**
	 * 简单的实体类
	 */
	private Class<?> entityClass = null;
	/**
	 * 路径分隔符
	 */
	private static final String URL_SEPARATOR = "/";
	/**
	 * 路径分隔符
	 */
	private static final String PACKAGE_SEPARATOR = ".";
	/**
	 * 实体名称
	 */
	private String entityName = null;
	/**
	 * 跳转JSP路径
	 */
	private String jspPathStr = "";

	/**
	 * 按传入的Class类的生成相应的路径信息
	 * @param genericClass Controller类中Class相应的信息
	 */
	public ControllerPath(Class<?> genericClass) {
		if (genericClass == null)
			throw new IllegalArgumentException("[genericClass] - must not be null!");

		entityClass = BeanUtils.getGenericClass(genericClass);

		if (entityClass == null)
			throw new IllegalArgumentException(genericClass.getName()+ "不是泛型类型！");
		JspPath jspPath=genericClass.getAnnotation(JspPath.class)==null?null:(JspPath)genericClass.getAnnotation(JspPath.class);
		String packagePath=entityClass.getPackage().getName().replace(PACKAGE_SEPARATOR, URL_SEPARATOR);
		jspPathStr = jspPath==null?packagePath:jspPath.value();
		if(!jspPathStr.endsWith(URL_SEPARATOR)) jspPathStr+=URL_SEPARATOR;
		entityName = entityClass.getSimpleName();
	}

	/**
	 * 生成你自已添加方法的路径：在上面配置的基础路径后补上你添加的文件名称即可
	 * @param path 基本路径后的路径与文件名（不包含扩展名）
	 * @return 返回jsp路径信息
	 */
	public String createPath(String path){
		StringBuilder sb = new StringBuilder();
		sb.append(getBasePath());
		sb.append(path);
		return sb.toString();
	}
	
	/**
	 * 获取显示页面路径
	 * @return 返回单条记录查看Jsp路径信息 
	 */
	public String getOneViewPath() {
		StringBuilder sb = new StringBuilder();
		sb.append(getBasePath());
		sb.append(PagePrefix.VIEW);
		sb.append(StringEx.toUpperCaseFirstOne(entityName));
		return sb.toString();
	}

	/**
	 * 显示列表路径
	 * @return 返回列表查看Jsp路径信息 
	 */
	public String getListViewPath() {
		StringBuilder sb = new StringBuilder();
		sb.append(getBasePath());
		sb.append(PagePrefix.LIST);
		sb.append(StringEx.toUpperCaseFirstOne(entityName));
		return sb.toString();
	}

	/**
	 * 添加页面路径信息
	 * @return 返回添加页面Jsp路径信息 
	 */
	public String getAddViewPath() {
		StringBuilder sb = new StringBuilder();
		sb.append(getBasePath());
		sb.append(PagePrefix.ADD);
		sb.append(StringEx.toUpperCaseFirstOne(entityName));
		return sb.toString();
	}

	/**
	 * 编辑页面路径信息
	 * @return 返回修改页面Jsp路径信息
	 */
	public String getEditViewPath() {
		StringBuilder sb = new StringBuilder();
		sb.append(getBasePath());
		sb.append(PagePrefix.EDIT);
		sb.append(StringEx.toUpperCaseFirstOne(entityName));
		return sb.toString();
	}
	
	/**
	 * 查看Controller类的实体Class
	 * @return 查看Controller类的实体Class
	 */
	public String getEntityName(){
		return entityClass.getName();
	}
	
	/**
	 * 获取实体类名称,并且首字母小写
	 * @return			:返回首字母小写实体类名称
	 */
	public String getEntityNameWithFirstLettersLowercase() {
		return StringEx.toLowerCaseFirstOne(entityName);
	}

	/**
	 * 默认重定向到列表页面
	 * @return 默认重定向到列表页面
	 */
	public String getRedirectListPath() {
		return "redirect:/" + getBasePath();
	}
	
	/**
	 * 默认转到另一个地址
	 * @return 默认转到另一个地址
	 */
	public String getForwardListPath(){
		return "forward:/" + getBasePath();
	}

	/**
	 * 获取JSP路径信息
	 * @return 类名路径信息或者Controller中{@link JspPath}配置信息
	 */
	private String getBasePath() {
		StringBuffer sb = new StringBuffer();
		sb.append(jspPathStr);
		return sb.toString();
	}

}
