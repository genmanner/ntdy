package com.genmanner.partygm.core.framework.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取请求的ip地址
 *  
 */
public class IpUtils {
	/**
	 * 获取当前网络ip
	 * @param request	:请求
	 * @return			:返回ip
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ipAddress = request.getHeader("x-forwarded-for");
		if(null == ipAddress || 0 == ipAddress.length() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		
		if(null == ipAddress || 0 == ipAddress.length() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		
		if(null == ipAddress || 0 == ipAddress.length() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {//根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ipAddress = inet.getHostAddress();
			}
		}
		
		if(null != ipAddress && ipAddress.length() > 15) { //对于通过多个代理的情况，第一个ip为客户端真实ip,多个ip按照','分割
			if(ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
			}
		}
		
		return ipAddress; 
	}
}
