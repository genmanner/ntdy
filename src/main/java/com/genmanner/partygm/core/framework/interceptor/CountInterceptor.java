package com.genmanner.partygm.core.framework.interceptor;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.genmanner.partygm.core.framework.utils.ParaMap;
/**
 * 实现拦截器 暂未使用
 *  
 *
 */
public class CountInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		HandlerMethod handlerMethod=(HandlerMethod)handler;
//		MethodParameter[] methodParameter = handlerMethod.getMethodParameters();
//		for(MethodParameter mp:methodParameter){
//			ParaMap paraMap=mp.getParameterAnnotation(ParaMap.class);
//			if(paraMap!=null){
//				String str=paraMap.value();
//				Class classs=mp.getParameterType();
//				Object obj=classs.newInstance();
//				Enumeration<String> enume=request.getParameterNames();
//				while(enume.hasMoreElements()){
//					String temp=enume.nextElement();
//					if(temp.contains(str+".") && temp.startsWith(str+".")){
//						classs.getMethod("put", Object.class,Object.class).invoke(obj, temp,request.getParameter(temp));
//					}
//				}
//			}
//		}
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		System.out.println("postHandle execute!");
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		//添加缓存校对逻辑，当对应的controller有动作时更新缓存，主要是添加修改删除时
		
		
		
		System.out.println("afterCompletion execute!");
	}

}
