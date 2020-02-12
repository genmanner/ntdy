package com.genmanner.partygm.core.framework.utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.genmanner.partygm.core.common.framework.SpringTools;
/**
 * 初始化获取一些SPRINGBEAN的配置信息
 *  
 *
 */
public class InitContextLoaderListener extends ContextLoaderListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		//初始化SrpingTools
		ServletContext sc=event.getServletContext();
		SpringTools.servletcontext=sc;
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(sc);
		SpringTools.context=webApplicationContext;
		//初始化缓存 
		((CommonCacheMgr)SpringTools.getBean(CommonCacheMgr.class)).createCache();
	}

}
