/* ==================================================================
 * 文件：          com.genmanner.partygm.core.framework.utils.JSONUtils.java
 * 创建日期： 2009-11-6 下午04:37:31
 * 功能：           工具类
 * 所含类:   
 * 修改记录：

 * 日期                        作者                内容
 * ==================================================================
 * 2009-11-6 下午04:37:31    创建文件，实现基本功能
 *
 * ==================================================================
 */
package com.genmanner.partygm.core.framework.utils;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * <br>1)实现request,response,session获取
 * <br>2)绕过页面直接输出文本的简化函数
 *  
 */
public class JSONUtils {

	// header 常量定义//
	private static final String ENCODING_PREFIX = "encoding";
	private static final String NOCACHE_PREFIX = "no-cache";
	private static final String ENCODING_DEFAULT = "UTF-8";
	private static final boolean NOCACHE_DEFAULT = true;

	// content-type 定义 //
	private static final String TEXT = "text/plain";
	private static final String JSON = "application/json";
	private static final String XML = "text/xml";
	private static final String HTML = "text/html";

	private static Logger logger = LoggerFactory.getLogger(JSONUtils.class);

	/**
	 * 直接输出内容的简便函数.
	 * @param headers 可变的header数组，目前接受的值为"encoding:"或"no-cache:",默认值分别为UTF-8和true.
	 */
	public static void render(final String contentType, final String content,
			final String... headers) {
		try {
			// 分析headers参数
			String encoding = ENCODING_DEFAULT;
			boolean noCache = NOCACHE_DEFAULT;
			for (String header : headers) {
				String headerName = StringUtils.substringBefore(header, ":");
				String headerValue = StringUtils.substringAfter(header, ":");

				if (StringUtils.equalsIgnoreCase(headerName, ENCODING_PREFIX)) {
					encoding = headerValue;
				} else if (StringUtils.equalsIgnoreCase(headerName,
						NOCACHE_PREFIX)) {
					noCache = Boolean.parseBoolean(headerValue);
				} else
					throw new IllegalArgumentException(headerName
							+ "不是一个合法的header类型");
			}
			RequestAttributes ra = RequestContextHolder.getRequestAttributes();  
			HttpServletResponse response = ((ServletRequestAttributes)ra).getResponse();
			// 设置headers参数
			String fullContentType = contentType + ";charset=" + encoding;
			response.setContentType(fullContentType);
			if (noCache) {
				response.setHeader("Pragma", "No-cache");
				response.setHeader("Cache-Control", "no-cache");
				response.setDateHeader("Expires", 0);
			}
			response.getWriter().write(content);
			response.getWriter().flush();

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 直接输出文本.
	 * @see #render(String, String, String...)
	 */
	public static void renderText(final String text, final String... headers) {
		render(TEXT, text, headers);
	}

	/**
	 * 直接输出HTML.
	 * @see #render(String, String, String...)
	 */
	public static void renderHtml(final String html, final String... headers) {
		render(HTML, html, headers);
	}

	/**
	 * 直接输出XML.
	 * @see #render(String, String, String...)
	 */
	public static void renderXml(final String xml, final String... headers) {
		render(XML, xml, headers);
	}

	/**
	 * 直接输出JSON.
	 * @param jsonString json字符串
	 * @see #render(String, String, String...)
	 */
	public static void renderJson(final String jsonString,
			final String... headers) {
		render(JSON, jsonString, headers);
	}

	/**
	 * 直接输出JSON.
	 * @param map Map对象,将被转化为json字符串.
	 * @see #render(String, String, String...)
	 */
	@SuppressWarnings("unchecked")
	public static void renderJson(final Map map, final String... headers) {
		String jsonString = JSONObject.fromObject(map).toString();
		render(JSON, jsonString, headers);
	}

	/**
	 * 直接输出JSON.
	 * @param object Java对象,将被转化为json字符串.
	 * @see #render(String, String, String...)
	 */
	public static void renderJson(final Object object, final String... headers) {
		String jsonString = JSONObject.fromObject(object).toString();
		render(JSON, jsonString, headers);
	}
	
	/**
	 * 直接输出JSON.
	 * @param object Java对象,将被转化为json字符串.
	 * @see #render(String, String, String...)
	 */
	public static void renderJsonByArray(final Object object, final String... headers) {
		String jsonString = JSONArray.fromObject(object).toString();
		render(JSON, jsonString, headers);
	}
	
}
