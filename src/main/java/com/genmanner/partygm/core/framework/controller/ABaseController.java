package com.genmanner.partygm.core.framework.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.genmanner.partygm.core.common.framework.Page;
import com.genmanner.partygm.core.common.framework.Pagetop;
import com.genmanner.partygm.core.common.tools.ExportExcel;
import com.genmanner.partygm.core.common.util.Utility;
import com.genmanner.partygm.core.framework.domain.ABean;
import com.genmanner.partygm.core.framework.domain.DResult;
import com.genmanner.partygm.core.framework.domain.DResult.Status;
import com.genmanner.partygm.core.framework.manager.ABaseManager;
import com.genmanner.partygm.core.framework.utils.ControllerPath;
import com.genmanner.partygm.core.framework.utils.JSONUtils;


/**
 * 基础控制器接口实现类
 *  
 */
@SuppressWarnings("all")
public abstract class ABaseController<T extends ABean>{
	
	/**
	 * 本地导出字段配置
	 */
	public Object[][] localExpFieldConfigs = null;
	
	/**
	 * 页面路径信息
	 */
	protected ControllerPath path = new ControllerPath(this.getClass());
	
	private Logger log = LoggerFactory.getLogger(ABaseController.class);

	/**
	 * 获取manager信息
	 * @return BaseManager 基础封装类
	 */
	protected abstract ABaseManager<T> getBaseManager();

	/**
	 * 不分页查询
	 * @param query 查询条件
	 * @return
	 */
	@RequestMapping(value="/list.action")
	public ModelAndView list(Page page) {
		List<T> list = getBaseManager().selectList(page);
		ModelAndView mav = new ModelAndView(path.getListViewPath(), "list", list);
		mav.addObject("page", page);
		return mav;
	}
	
	/**
	 * 无条件不分页查询
	 * @param query 查询条件
	 * @return
	 */
	@RequestMapping(value="/listall.action")
	public ModelAndView listAll() {
		List<T> list = getBaseManager().selectAll();
		ModelAndView mav = new ModelAndView(path.getListViewPath(), "list", list);
		return mav;
	}
	
	/**
	 * 分页查询
	 * @param query 查询条件
	 * @param page 分页用于界面查询分页排序参数
	 * @return
	 */
	@RequestMapping(value="/page.action")
	public ModelAndView page(Page page) {
		page = getBaseManager().selectPageList(page);
		ModelAndView mav = new ModelAndView(path.getListViewPath(), "page", page);
		mav.addObject("page", page);
		return mav;
	}
	
	/**
	 * 分页查询
	 * @param page 分页用于界面查询分页排序参数
	 * @return
	 */
	@RequestMapping(value="/pageall.action")
	public ModelAndView pageAll(Page page) {
		page = getBaseManager().selectPageAll(page);
		ModelAndView mav = new ModelAndView(path.getListViewPath(), "page", page);
		return mav;
	}
	
	/**
	 * 批量删除
	 * @param ids 主键集合
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/dellist.action", produces = MediaType.APPLICATION_JSON_VALUE)
	public DResult delList(String[] ids) {
		if (ArrayUtils.isEmpty(ids)) {
			log.error("未设置批量删除对象的ID号！对象：{}", path.getEntityName());
			return new DResult(Status.ERROR, "没有传入要删除的ID号数组！");
		}
		try {
			getBaseManager().deleteByIdInBatch(Arrays.asList(ids));
		} catch (Exception e) {
			log.error("批量删除对象失败！对象:" + path.getEntityName(), e);
			return new DResult(Status.ERROR, "批量删除失败！");
		}
		return new DResult(Status.OK, ids.length);
	}

	/**
	 * 删除当前记录
	 * @param id 主键
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/del.action", produces = MediaType.APPLICATION_JSON_VALUE)
	public DResult del(String id) {
		if (StringUtils.isBlank(id)) {
			log.error("要删除的ID号为null或空字符串！对象：{}", path.getEntityName());
			return new DResult(Status.ERROR, "没有传入要删除的ID号！");
		}
		int count = getBaseManager().deleteById(id);
		if (count == 0)
			return new DResult(Status.ERROR, "要删除的记录不存在！");
		log.debug("成功删除{}个对象，id:{},对象:{}", count, id, path.getEntityName());
		return new DResult(Status.OK, "删除成功！");
	}
	
	/**
	 * 打开新增页面
	 * @return
	 */
	@RequestMapping(value = "/add.action")
	public ModelAndView addView(T entity) {
		ModelAndView mav = new ModelAndView(path.getAddViewPath());
		mav.addObject(path.getEntityNameWithFirstLettersLowercase(), entity);
		return mav;
	}
	
	/**
	 * 修改记录
	 * @param id 主键
	 * @return
	 */
	@RequestMapping(value = "/updateview.action")
	public ModelAndView updateView(String id) {
		T entity = getBaseManager().selectById(id);
		ModelAndView mav = new ModelAndView(path.getEditViewPath());
		mav.addObject(path.getEntityNameWithFirstLettersLowercase(), entity);
		return mav;
	}
	
	/**
	 * 查看一条记录
	 * @param id 主键
	 * @return
	 */
	@RequestMapping(value = "/view.action")
	public ModelAndView view(String id) {
		T entity = getBaseManager().selectById(id);
		ModelAndView mav = new ModelAndView(path.getOneViewPath());
		mav.addObject(path.getEntityNameWithFirstLettersLowercase(), entity);
		return mav;
	}
	
	/**
	 * ajax异步保存
	 * @param entity		:保存实体类
	 * @return				:返回DResult结果集
	 */
	@ResponseBody
	@RequestMapping(value = "/saveajax.action", produces = MediaType.APPLICATION_JSON_VALUE)
	public DResult saveAjax(T entity) {
		DResult resultMsg = new DResult(Status.ERROR, "操作失败!");
		if (!Utility.isEmpty(entity)) {
			getBaseManager().insert(entity);
			resultMsg.setMessage("新增成功!");
			resultMsg.setStatus(Status.OK);
		}
		return resultMsg;
	}

	/**
	 * AJAX请求更新一条记录
	 * @param entity 当前对象泛型实体
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/updateajax.action", produces = MediaType.APPLICATION_JSON_VALUE)
	public DResult updateAjax(T entity) {
		try{
			getBaseManager().updateById(entity);
			return new DResult(Status.OK, "修改成功！");
		}catch(Exception err){
			return new DResult(Status.ERROR, "修改失败！");
		}
	}
	
	/**
	 * 保存当前记录
	 * @param entity 当前对象泛型实体
	 * @return
	 */
	@RequestMapping(value="/save.action")
	public ModelAndView save(T entity) {
		getBaseManager().insert(entity);
		return new ModelAndView(path.getRedirectListPath());
	}


	/**
	 * 修改记录
	 * @param id 主键
	 * @return
	 */
	@RequestMapping(value = "/update.action")
	public ModelAndView update(T entity) {
		getBaseManager().updateById(entity);
		return new ModelAndView(path.getRedirectListPath());
	}
	
	/**
	 * 获取本地配置导出字段(checked取值默认以最后一个)
	 * localExpFieldConfigs配置说明：
	 * 第一行是key值配置
	 * 第二行是excel中head配置
	 * 第三行是字符是否默认选中导出(可选配置)
	 */
	@ResponseBody
	@RequestMapping(value = "/getLocalExportFieldConfig.action")
	public String getLocalExportFieldConfig() {
		List<Map<String, Object>> configList = null;
		
		if(!Utility.isEmpty(localExpFieldConfigs) && localExpFieldConfigs.length >= 2) {
			configList = new ArrayList<Map<String, Object>>(localExpFieldConfigs[0].length);
			Map<String, Object> tmpMap = null;
			Object chkVal = true;
			
			int minIndex = Math.min(localExpFieldConfigs[0].length, localExpFieldConfigs[1].length);
			for(int i = 0; i < minIndex; i++) {
				tmpMap = new HashMap<String, Object>(3);
				tmpMap.put("id", localExpFieldConfigs[0][i]);
				tmpMap.put("text", localExpFieldConfigs[1][i]);
				
				if(3 == localExpFieldConfigs.length 
						&& i < localExpFieldConfigs[2].length) {//如果已配置则赋值
					chkVal = localExpFieldConfigs[2][i];
					tmpMap.put("checked", localExpFieldConfigs[2][i]);
				} else {//未配置按默认值true赋值或配置部分剩余未配置已最后一个配置为准
					tmpMap.put("checked", chkVal);
				}
				
				configList.add(tmpMap);
			}
			
		}
		
		if(Utility.isEmpty(configList)) configList = new ArrayList<Map<String, Object>>(0);
		
		JSONArray ja = JSONArray.fromObject(configList);
		JSONUtils.renderJson(ja.toString());
		
		return null;
	}
	
	/**
	 * 本地自定义导出
	 * @param expFileName		:导出文件名
	 * @param sheetTitle		:excel表单名
	 * @param dataset			:导出数据源
	 * @param expKeys			:导出字段key值
	 * @throws IOException 
	 */
	public void localCustomExportExcel(String expFileName, String sheetTitle, Collection dataset, String[] expKeys) throws IOException {
		if(Utility.isEmpty(expKeys)) expKeys = new String[0];
		String[] expHeaders = new String[expKeys.length];
		for(int i = 0; i < expKeys.length; i++) {//获取导出excel标题
			for(int j = 0; j < localExpFieldConfigs[0].length; j++) {
				if(localExpFieldConfigs[0][j].equals(expKeys[i])) {
					expHeaders[i] = (String) localExpFieldConfigs[1][j];
				}
			}
		}
		
		HttpServletResponse response = getResponse();
		ExportExcel<T> export = new ExportExcel<T>();
		ExportExcel.prepareResponseHeader(getRequest(), response, expFileName);
		export.exportExcel(sheetTitle, expHeaders, dataset, response.getOutputStream(), expKeys);
	}
	/**
	 * 本地自定义导出,为事项统计定制
	 * @param localCustomExportExcel_AffairStatistics	:导出文件名
	 * @param sheetTitle		:excel表单名
	 * @param dataset			:导出数据源
	 * @param expKeys			:导出字段key值
	 * @throws IOException 
	 */
	public void localCustomExportExcel_AffairStatistics(String expFileName, String sheetTitle,String[] expHeaders, Collection dataset, String[] expKeys) throws IOException {
		HttpServletResponse response = getResponse();
		ExportExcel<T> export = new ExportExcel<T>();
		ExportExcel.prepareResponseHeader(getRequest(), response, expFileName);
		export.exportExcel_AffairStatistics(sheetTitle, expHeaders, dataset, response.getOutputStream(), expKeys);
	}
	/**
	 * 本地自定义导出,为事项一览定制
	 * @param localCustomExportExcel_AffairBrowse	:导出文件名
	 * @param sheetTitle		:excel表单名
	 * @param dataset			:导出数据源
	 * @param expKeys			:导出字段key值
	 * @throws IOException 
	 */
	public void localCustomExportExcel_AffairBrowse(String expFileName, String sheetTitle,List headers1,List headers2, Collection dataset, String[] expKeys) throws IOException {
		HttpServletResponse response = getResponse();
		ExportExcel<T> export = new ExportExcel<T>();
		ExportExcel.prepareResponseHeader(getRequest(), response, expFileName);
		export.exportExcel_AffairBrowse(sheetTitle, headers1,headers2, dataset, response.getOutputStream(), expKeys);
	}
	
	/**
	 * 数据库自定义列导出
	 * @param expFileName		:导出文件名
	 * @param sheetTitle		:excel表单名
	 * @param page				:导出数据源
	 * @throws IOException 
	 */
	public void dBCustomExportExcel(String expFileName, String sheetTitle, Page page) throws IOException {
		List exportList = null;
		String[] expKeys = null;
		String[] expHeaders = null;
		if(!Utility.isEmpty(page) && !Utility.isEmpty(page.getPagetop())) {
			List<Pagetop>  pageTopList = page.getPagetop();
			exportList =  (List) page.getRes();
			expKeys = new String[pageTopList.size()];
			expHeaders =new String[pageTopList.size()];
			
			for(int i = 0; i < pageTopList.size(); i++) {
				expKeys[i] = pageTopList.get(i).getTopzdm();
				expHeaders[i] = pageTopList.get(i).getTopname();
			}
		}
		
		if(Utility.isEmpty(exportList)) exportList = new ArrayList<>(0);
		if(Utility.isEmpty(expKeys)) expKeys = new String[0];
		if(Utility.isEmpty(expHeaders)) expHeaders = new String[0];
		
		HttpServletResponse response = getResponse();
		ExportExcel<T> export = new ExportExcel<T>();
		ExportExcel.prepareResponseHeader(getRequest(), response, expFileName);
		export.exportExcel(sheetTitle, expHeaders, exportList, response.getOutputStream(), expKeys);
	}
	
	/**
	 * 提取request
	 * @return
	 */
	protected HttpServletRequest getRequest(){
		RequestAttributes ra = RequestContextHolder.getRequestAttributes();  
		HttpServletRequest request = ((ServletRequestAttributes)ra).getRequest();  
		return request;

	}
	
	/**
	 * 提取response
	 * @return
	 */
	protected HttpServletResponse getResponse(){
		RequestAttributes ra = RequestContextHolder.getRequestAttributes();  
		HttpServletResponse response = ((ServletRequestAttributes)ra).getResponse();
		return response;
	}
	
	/**
	 * 提取session
	 * @return
	 */
	protected HttpSession getSession(){
		RequestAttributes ra = RequestContextHolder.getRequestAttributes();  
		HttpSession session= ((ServletRequestAttributes)ra).getRequest().getSession();
		return session;
		
	}

}
