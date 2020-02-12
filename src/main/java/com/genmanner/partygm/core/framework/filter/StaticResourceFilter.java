package com.genmanner.partygm.core.framework.filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.genmanner.partygm.core.common.util.Utility;
import com.genmanner.partygm.core.framework.utils.AppParamConfig;

/**
 * 静态资源过滤器
 *  
 */
public class StaticResourceFilter implements Filter {
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		String strServletPath = request.getServletPath();
		
		byte[] bBuf = null;
		String relPath = request.getServletContext().getRealPath(strServletPath);
		File file = new File(relPath);
		if(!file.exists())
			bBuf = getBuffer(request, strServletPath);
		
		if (bBuf != null && bBuf.length > 0) {
			int iPrefixStart = strServletPath.lastIndexOf(".");

			if (iPrefixStart != -1) {
				String prefix = strServletPath.substring(iPrefixStart + 1);

				if (!Utility.isEmpty(prefix)) {
					String resType = mapType2Prefix.get(prefix);

					if (!Utility.isEmpty(resType)) {
						res.setContentType(resType);
					}
				}
			}

			res.getOutputStream().write(bBuf);
			res.flushBuffer();
		} else {
			chain.doFilter(req, res);
		}
		
	}

	public void init(FilterConfig filterConfig) throws ServletException {

	}

	public void destroy() {

	}

	protected boolean canSolve(HttpServletRequest request, String strServletPath) {
		/*if (strArrIgnoreServletPath != null) {
			for (String strIgnoreServletPath : strArrIgnoreServletPath) {
				if (strIgnoreServletPath.equalsIgnoreCase(strServletPath)) {
					return true;
				}
			}
		}*/

		String strRealPath = request.getSession().getServletContext().getRealPath(strServletPath);
		
		return new File(strRealPath).exists();
	}

	protected byte[] getBuffer(HttpServletRequest request, String strServletPath)
			throws IOException {
		byte[] bArrRet = (byte[]) mapCache.get(strServletPath);
		if (bArrRet == null) {
			File file = getFileFromOverridePath(request, strServletPath);

			if (file == null) {
				file = getFileFromClassPath(request, strServletPath);
			}

			if (file != null) {
				bArrRet = new byte[(int) file.length()];
				FileInputStream fis = new FileInputStream(file);
				fis.read(bArrRet);
				fis.close();
			} else {
				InputStream fis = getFileFromJar(strServletPath);

				if (fis != null) {
					bArrRet = new byte[10240];
					int iPos = 0, iCount = 0;

					while ((iCount = fis.read(bArrRet, iPos, bArrRet.length
							- iPos)) != -1) {
						iPos += iCount;

						if (iPos >= bArrRet.length) {
							bArrRet = Arrays.copyOf(bArrRet,
									bArrRet.length + 10240);
						}
					}

					fis.close();
					bArrRet = Arrays.copyOf(bArrRet, iPos);
				} else {
					bArrRet = new byte[0];
				}
			}
			
			if(!AppParamConfig.getInstance().getDebugMode()) {//开发模式下关闭缓存
				mapCache.put(strServletPath, bArrRet);
			}
			
		}

		return bArrRet != null && bArrRet.length > 0 ? bArrRet : null;
	}

	protected File getFileFromOverridePath(HttpServletRequest request,
			String strServletPath) throws IOException {
		String strRelativePath = strServletPath.toLowerCase().startsWith(
				strBasePathLocation.toLowerCase()) ? strServletPath
				.substring(strBasePathLocation.length()) : strServletPath;
		String strOverridePath = strBasePathLocation
				+ strOverridePathPrefix
				+ (strRelativePath.charAt(0) == '/' ? strRelativePath
						.substring(1) : strRelativePath);
		
		String relPath = request.getServletContext().getRealPath(strOverridePath);
		if(null == relPath) return null;
		
		File file = new File(relPath);
		return file.exists() ? file : null;
	}

	protected File getFileFromClassPath(HttpServletRequest request,
			String strServletPath) throws IOException {
		String strClassPath = strBasePathLocation
				+ strClassPathPrefix
				+ (strServletPath.charAt(0) == '/' ? strServletPath
						.substring(1) : strServletPath);

		String relPath = request.getServletContext().getRealPath(strClassPath);
		if(null == relPath) return null;
		
		File file = new File(relPath);
		return file.exists() ? file : null;
	}

	protected InputStream getFileFromJar(String strServletPath)
			throws IOException {
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
				+ strServletPath;
		Resource[] resources = resourcePatternResolver.getResources(pattern);

		if (resources != null && resources.length > 0) {
			return resources[0].getInputStream();
		}

		return null;
	}

	private static final Map<String, Object> mapCache = new Hashtable<String, Object>();

	private static final Map<String, String> mapType2Prefix = new Hashtable<String, String>();
	static {
		mapType2Prefix.put("js", "text/javascript");
		mapType2Prefix.put("css", "text/css");
		mapType2Prefix.put("json", "text/json");
		mapType2Prefix.put("jpg", "image/jpeg");
		mapType2Prefix.put("png", "image/png");
		mapType2Prefix.put("gif", "image/gif");
		mapType2Prefix.put("swf", "application/x-shockwave-flash");
	}

//	private static final String[] strArrIgnoreServletPath = new String[] { "/struts/utils.js"};

	private static final String strOverridePathPrefix = "resources-override/";
	private static final String strBasePathLocation = "/WEB-INF/";
	private static final String strClassPathPrefix = "classes/";
}
