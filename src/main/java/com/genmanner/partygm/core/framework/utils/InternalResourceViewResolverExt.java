package com.genmanner.partygm.core.framework.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.genmanner.partygm.core.common.framework.SpringTools;


/**
 * 解决系统JSP加载地址的问题
 * <br>JSP的读取顺序
 * <br>1、程序中二次开发覆盖
 * <br>2、当前配置的页面
 * <br>3、从classes中找到的页面
 * <br>4、jar中找到的页面：此项会复制到当前工程文件中
 *  
 *
 */
public class InternalResourceViewResolverExt extends
		InternalResourceViewResolver {
	/**
	 * 查看当前文件是否存在，不存在从
	 */
	@Override
	public View resolveViewName(String viewName, Locale locale)
			throws Exception {
		ServletContext servletContext = SpringTools.getServletContext();
		String overridePath = strOverridePathPrefix+viewName+super.getSuffix();//二次开发覆盖
		String webinfPath = strJspPathPrefix+viewName+super.getSuffix();//当前配置
		String classesPath = strClassPathPrefix+viewName+super.getSuffix();//classes中
		String jarPath = strJarPathPrefix+viewName+super.getSuffix();//jar
		
		String overridePathString=servletContext.getRealPath(overridePath);
		String webinfPathString=servletContext.getRealPath(webinfPath);
		ClassPathResource classPathResource = new ClassPathResource(classesPath);
		URL fileURL=this.getClass().getResource(jarPath);
		if(AppParamConfig.getInstance().getDebugMode()){
			super.setCache(false);
		}
		if(overridePathString!=null && new File(overridePathString).exists()){
			super.setPrefix(strOverridePathPrefix);
		}else if(webinfPathString!=null && new File(webinfPathString).exists()){
			super.setPrefix(strJspPathPrefix);
			//当前配置不用修改
		}else if(classPathResource.exists()){
			super.setPrefix("/");
			String copyPath = servletContext.getRealPath("/")+viewName+super.getSuffix();
			copyFile(classPathResource.getInputStream(), copyPath);
		}else if(fileURL!=null){
			super.setPrefix(strJspPathPrefix);
			InputStream is = fileURL.openStream();
			String copyPath = servletContext.getRealPath("/")+super.getPrefix()+viewName+super.getSuffix();
			copyFile(is,copyPath);
		}
		return super.resolveViewName(viewName, locale);
	}
	
	private void copyFile(InputStream is,String copyPath){
		FileOutputStream fos = null;
		try {
			File file = new File(copyPath);
			if(file.exists() && !AppParamConfig.getInstance().getDebugMode()) return;
			file.delete();
	        if(!file.getParentFile().exists())
	            file.getParentFile().mkdirs();
	        file.createNewFile();
			fos=new FileOutputStream(file);
			if(is!=null){
				byte[] b = new byte[1024]; 
				int i=0;
				while((i=is.read(b,0,1024)) != -1){  
				  fos.write(b,0,i);  
				}  
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(is!=null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(fos!=null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
	}
	
	private static final String strOverridePathPrefix = "/WEB-INF/jsp-override";
	private static final String strJspPathPrefix = "/WEB-INF";
	private static final String strClassPathPrefix = "/WEB-INF";
	private static final String strJarPathPrefix = "/WEB-INF";

}
