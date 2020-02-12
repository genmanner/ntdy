package com.genmanner.partygm.core.framework.utils;

import java.text.SimpleDateFormat;

import com.genmanner.partygm.core.common.framework.SpringTools;
import com.genmanner.partygm.core.common.util.Utility;

/**
 * <br>
 * 系统参数配置类 <br>
 * 此类解决系统一些初始化参数的配置，相应的配置信息应写到main.xml文件中，请在对应的配置文件中配置参数信息
 * 
 *  
 */
public class AppParamConfig {
	protected static AppParamConfig instance = null;
	
	/**
	 * 资源路径
	 */
	private String resourcePath = "resources";
	/**
	 * 系统名称
	 */
	private String systemName = "工系统5.0";
	/**
	 * 文件上传目录
	 */
	private String uploadFileDir = "C:\\upload\\";
	/**
	 * 是否开启调试模式
	 */
	private boolean debugMode = false;
	/**
	 * 是否复制jar中JSP到工程中
	 */
	private boolean copyJarJspFile = false;

	/**
	 * 网站栏目JSON文件路劲
	 */
	private String generateNavigationPath = "C:\\siteNavigation\\";

	/**
	 * 打印模版上传路径
	 */
	private String printedTemplateDir = "c:/printedTemplateDir/";
	
	/**
	 * 头像存放临时路径
	 */
	private String photosPath = "c:/photosPath/"; 

	/**
	 * 后台网站底部显示信息
	 */
	private String sitFooterInfo = "南昌君谋科技有限公司";
	
	/**
	 * 最大创建tab页
	 */
	private Integer maxTabCount = 8;
	
	/**
	 * 学生账号类型
	 */
	private String stuAccountType = "student";
	
	/**
	 * 老师账号类型
	 */
	private String teacherAccountType = "teacher";
	
	/**
	 * 用户表外键关联学生表字段(主要为学生其他登录账号服务)
	 */
	private String userTabFKStuTabField = "admission_notice_number";
	
	/**
	 * 用户表外键关联学生表字段中文名称
	 */
	private String userTabFKStuTabFieldName = "录取通知书号";
	/**
	 * 用户表外键关联教师表字段
	 */
	private String userTabFKTeaTabField = "work_num";
	
	/**
	 * 登录设置中忽略密码输入次数配置
	 */
	private Integer ignoreConfigWhenPwdErrCount = -1;
	
	/**
	 * 自定义pageSize
	 */
	private Integer pageSize = 20;
	
	/**
	 * 默认辅导员用户组值
	 */
	private String schoolCounsellorUsergroupVal = "school_counsellor";
	
	/**
	 * 院系负责人用户组Val
	 */
	private String colleageLeaderUsergroupVal = "colleage_leader";
	
	/**
	 * 专业负责人用户组Val
	 */
	private String majorLeaderUsergroupVal = "major_leader";
	
	/**
	 * 模版替换数据后输出路径
	 */
	private String printedFilesDir = "c:/printedFilesDir/";
	
	/**
	 * 登录才能忽略的url
	 * 注：如果在后台中配置了,则对此url配置失效<br>
	 * 列如:/indexAction.action配置了忽略,但是在后台把这个地址赋给了
	 * 功能组,则此url配置失效
	 */
	private String[] ignoreUrlAfterLogin = null;
	
	/**
	 * 不需要登录可忽略的url<br>
	 * 注：如果在后台中配置了,则对此url配置失效<br>
	 * 列如:/indexAction.action配置了忽略,但是在后台把这个地址赋给了
	 * 功能组,则此url配置失效
	 */
	private String[] ignoreUrlBeforeLogin = null;
	
	/**
	 * 适配规则
	 */
	private String[] adapterRule = new String[] {"student_score"};
	
	/**
	 * 数据库类型
	 */
	private String dataBaseKind = "oracle";

	/**
	 * 网站的类型
	 */
	private String studentType = "21";
	
	/**
	 * 默认组织机构代码(资源未下发)
	 */
	private String defaultOrgId = "";
	
	/**
	 * 现场分配默认显示床位数
	 */
	private Integer defaultShowBed = 6;
	
	/**
	 * 现场分配监控刷新阀值(单位:秒)
	 */
	private Integer refreshTreshold = 10;
	
	/**
	 * 分数等级
	 */
	private Integer defaultScoreLevel = 4;
	
	/***
	 *学校所在省份（用于手机通讯录所在省份时显示城市）
	 */
	private String originProvinceStr = "001014";
	
	/***
	 * 系统基本URL路径
	 */
	private String systemBaseUrl;
	
	public String getSystemBaseUrl() {
		return systemBaseUrl;
	}

	public void setSystemBaseUrl(String systemBaseUrl) {
		this.systemBaseUrl = systemBaseUrl;
	}

	public String getOriginProvinceStr() {
		return originProvinceStr;
	}

	public void setOriginProvinceStr(String originProvinceStr) {
		this.originProvinceStr = originProvinceStr;
	}

	/*
	 * 当前时间
	 */
	public String getCurrentDateTime() {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new java.util.Date());
	}

	public String getStudentType() {
		return studentType;
	}

	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}

	private AppParamConfig() {}
	
	public static AppParamConfig getInstance() {
		if (instance == null) {
			instance = (AppParamConfig) SpringTools.getBean("appParamConfig");
		}
		return instance;
	}

	/**
	 * 资源路径
	 */
	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	/**
	 * 系统名称
	 */
	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	/**
	 * 文件上传目录
	 */
	public String getUploadFileDir() {
		return uploadFileDir;
	}

	public void setUploadFileDir(String uploadFileDir) {
		this.uploadFileDir = uploadFileDir;
	}

	/**
	 * 是否开启调试模式
	 */
	public Boolean getDebugMode() {
		return debugMode;
	}

	public void setDebugMode(Boolean debugMode) {
		this.debugMode = debugMode;
	}

	/**
	 * 是否复制jar中JSP到工程中
	 */
	public boolean getCopyJarJspFile() {
		return copyJarJspFile;
	}

	public void setCopyJarJspFile(boolean copyJarJspFile) {
		this.copyJarJspFile = copyJarJspFile;
	}

	public String getGenerateNavigationPath() {
		return generateNavigationPath;
	}

	public void setGenerateNavigationPath(String generateNavigationPath) {
		this.generateNavigationPath = generateNavigationPath;
	}

	public String getPrintedTemplateDir() {
		return printedTemplateDir;
	}

	public void setPrintedTemplateDir(String printedTemplateDir) {
		this.printedTemplateDir = printedTemplateDir;
	}

	public String getPrintedFilesDir() {
		return printedFilesDir;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public String getDataBaseKind() {
		return dataBaseKind;
	}

	public void setDataBaseKind(String dataBaseKind) {
		this.dataBaseKind = dataBaseKind;
	}

	public void setPrintedFilesDir(String printedFilesDir) {
		this.printedFilesDir = printedFilesDir;
	}

	public String getSitFooterInfo() {
		return sitFooterInfo;
	}

	public void setSitFooterInfo(String sitFooterInfo) {
		if(null != sitFooterInfo && "" != sitFooterInfo) {
			this.sitFooterInfo = sitFooterInfo;
		}
	}

	public Integer getMaxTabCount() {
		return maxTabCount;
	}

	public void setMaxTabCount(Integer maxTabCount) {
		if(null != maxTabCount) {
			this.maxTabCount = maxTabCount;
		}
	}

	public String getStuAccountType() {
		return stuAccountType;
	}

	public void setStuAccountType(String stuAccountType) {
		if(!Utility.isEmpty(stuAccountType)) {
			this.stuAccountType = stuAccountType;
		}
	}
	
	public String getTeacherAccountType() {
		return teacherAccountType;
	}

	public void setTeacherAccountType(String teacherAccountType) {
		if(!Utility.isEmpty(teacherAccountType)) {
			this.teacherAccountType = teacherAccountType;
		}
	}

	public Integer getIgnoreConfigWhenPwdErrCount() {
		return ignoreConfigWhenPwdErrCount;
	}

	public void setIgnoreConfigWhenPwdErrCount(Integer ignoreConfigWhenPwdErrCount) {
		if(null != ignoreConfigWhenPwdErrCount) {
			this.ignoreConfigWhenPwdErrCount = ignoreConfigWhenPwdErrCount;
		}
	}

    public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		if(null != pageSize) {
			this.pageSize = pageSize;
		}
	}

	public String getUserTabFKStuTabField() {
		return userTabFKStuTabField;
	}

	public void setUserTabFKStuTabField(String userTabFKStuTabField) {
		if(null != ignoreConfigWhenPwdErrCount) {
			this.userTabFKStuTabField = userTabFKStuTabField;
		}
	}

	public String getUserTabFKTeaTabField() {
		return userTabFKTeaTabField;
	}

	public String getUserTabFKStuTabFieldName() {
		return userTabFKStuTabFieldName;
	}

	public void setUserTabFKStuTabFieldName(String userTabFKStuTabFieldName) {
		this.userTabFKStuTabFieldName = userTabFKStuTabFieldName;
	}

	public void setUserTabFKTeaTabField(String userTabFKTeaTabField) {
		if(!Utility.isEmpty(userTabFKTeaTabField)) {
			this.userTabFKTeaTabField = userTabFKTeaTabField;
		}
	}

	public String getSchoolCounsellorUsergroupVal() {
		return schoolCounsellorUsergroupVal;
	}

	public void setSchoolCounsellorUsergroupVal(String schoolCounsellorUsergroupVal) {
		this.schoolCounsellorUsergroupVal = schoolCounsellorUsergroupVal;
	}

	public String getColleageLeaderUsergroupVal() {
		return colleageLeaderUsergroupVal;
	}

	public void setColleageLeaderUsergroupVal(String colleageLeaderUsergroupVal) {
		this.colleageLeaderUsergroupVal = colleageLeaderUsergroupVal;
	}

	public String[] getIgnoreUrlAfterLogin() {
		return ignoreUrlAfterLogin;
	}

	public void setIgnoreUrlAfterLogin(String[] ignoreUrlAfterLogin) {
		this.ignoreUrlAfterLogin = ignoreUrlAfterLogin;
	}

	public String[] getIgnoreUrlBeforeLogin() {
		return ignoreUrlBeforeLogin;
	}

	public void setIgnoreUrlBeforeLogin(String[] ignoreUrlBeforeLogin) {
		this.ignoreUrlBeforeLogin = ignoreUrlBeforeLogin;
	}

	public String[] getAdapterRule() {
		return adapterRule;
	}

	public void setAdapterRule(String[] adapterRule) {
		this.adapterRule = adapterRule;
	}

	public String getDefaultOrgId() {
		return defaultOrgId;
	}

	public void setDefaultOrgId(String defaultOrgId) {
		this.defaultOrgId = defaultOrgId;
	}

	public Integer getDefaultShowBed() {
		return defaultShowBed;
	}

	public void setDefaultShowBed(Integer defaultShowBed) {
		if(null != defaultShowBed) this.defaultShowBed = defaultShowBed;
	}

	public Integer getRefreshTreshold() {
		return refreshTreshold;
	}

	public void setRefreshTreshold(Integer refreshTreshold) {
		this.refreshTreshold = refreshTreshold;
	}

	public String getMajorLeaderUsergroupVal() {
		return majorLeaderUsergroupVal;
	}

	public void setMajorLeaderUsergroupVal(String majorLeaderUsergroupVal) {
		this.majorLeaderUsergroupVal = majorLeaderUsergroupVal;
	}

	public String getPhotosPath() {
		return photosPath;
	}

	public void setPhotosPath(String photosPath) {
		this.photosPath = photosPath;
	}
	public Integer getDefaultScoreLevel() {
		return defaultScoreLevel;
	}

	public void setDefaultScoreLevel(Integer defaultScoreLevel) {
		this.defaultScoreLevel = defaultScoreLevel;
	}
	
}
