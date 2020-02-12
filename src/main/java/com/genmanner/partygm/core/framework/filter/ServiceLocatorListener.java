package com.genmanner.partygm.core.framework.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import com.genmanner.partygm.core.common.util.FileUtil;
import com.genmanner.partygm.core.common.util.Utility;
import com.genmanner.partygm.core.framework.utils.AppParamConfig;
/**
 * 从JAR中把应用的JSP复制出来，功能来源学工老框架
 *  
 *
 */
public class ServiceLocatorListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent arg0) {
	}

	public void contextInitialized(ServletContextEvent sce) {
		
	     if(AppParamConfig.getInstance().getCopyJarJspFile()) {
	    	 for(String pathRegex : JSPPATHREGEX) {
	    		 copyFileFromJar(sce.getServletContext(), pathRegex, "", false);
	    	 }
	     }
	     
	}
	
	protected static void fetchPageEarly(String strUrl, String []strSession) {
		try {
			log.info("Fetch Page: " + strUrl + " ...");
        	postUrl(strUrl, "", strSession);
        } catch (IOException e) {
        	log.error("Fetch Page " + strUrl + " Earlily Error!");
        }
	}

	
	
	protected static String postUrl(String strUrl, String strParams, String []strSession) throws IOException {
		return postUrl(strUrl, strParams, strSession, "GET");
	}
	
	protected static String postUrl(String strUrl, String strParams, String []strSession, String strMethod) throws IOException {
		URL url = new URL(strUrl);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		
		if(strSession!=null && strSession.length>0 && !Utility.isEmpty(strSession[0])) {
			connection.setRequestProperty( "Cookie", strSession[0]);
		}
		
		connection.setRequestMethod(strMethod); 
		connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");		            
      	
      	if(!Utility.isEmpty(strParams)) {
      		connection.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "GB2312");
			out.write(strParams);
			out.flush();
			out.close();
      	}
		
		String sCurrentLine = "";
		String sTotalString = "";
		InputStream l_urlStream = connection.getInputStream();
		BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream));
		
		while ((sCurrentLine = l_reader.readLine()) != null) {
			sTotalString += sCurrentLine + "\r\n";
		}

	    if(strSession!=null && strSession.length>0) {
	    	String session_value = connection.getHeaderField("Set-Cookie" );
	    	
	    	if(!Utility.isEmpty(session_value) && Utility.isEmpty(strSession[0])) {
			    String[] sessionId = session_value.split(";");
			    strSession[0] = sessionId[0];
	    	}
	    }
		
		return sTotalString;
	}
	
	protected void copyFileFromJar(ServletContext servletContext, String fileRegex, String subDir, boolean bGeneralJarPath) {
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + fileRegex;
		
		try {
			Resource[] resources = resourcePatternResolver.getResources(pattern);

			if(resources!=null && resources.length>0) {
				String strFileSeparator = "/";
				
				for(Resource res : resources) {
					if(ResourceUtils.URL_PROTOCOL_JAR.equals(res.getURL().getProtocol())) {
						String strUrlPath = res.getURL().getPath();
						String strPathPrefix = subDir;
						String strJarPath = strUrlPath.substring(strUrlPath.indexOf(
								ResourceUtils.JAR_URL_SEPARATOR)+ResourceUtils.JAR_URL_SEPARATOR.length());
						
						if(bGeneralJarPath) {
							String strJarFullName = strUrlPath.substring(0, strUrlPath.indexOf(ResourceUtils.JAR_URL_SEPARATOR));
							strJarFullName = strJarFullName.substring(strJarFullName.lastIndexOf(strFileSeparator)+1);
							String strRootDir = "";
							
							if(strJarPath.indexOf(strFileSeparator) != -1) {
								strRootDir = strJarPath.substring(0, strJarPath.indexOf(strFileSeparator)+1);
								strJarPath = strJarPath.substring(strJarPath.indexOf(strFileSeparator)+1);
							}
							
							strPathPrefix += strRootDir + (strJarFullName.endsWith(".jar") 
									? strJarFullName.substring(0, strJarFullName.length()-4) : strJarFullName) + strFileSeparator;
						}
						
						String strFileDest = servletContext.getRealPath(strPathPrefix+strJarPath);
						
						if(!new File(strFileDest).exists()) {
							if(prepareSubPath(strFileDest)) {
								try {
									InputStream fis = res.getInputStream();
									OutputStream fos = new FileOutputStream(strFileDest);
									byte[] bArrBuffer = new byte[BUFFER_SIZE];
									int iCount = 0;
									
									while ((iCount = fis.read(bArrBuffer, 0, bArrBuffer.length)) != -1) {
										fos.write(bArrBuffer, 0, iCount);
									}
									
									fis.close();
									fos.close();								
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								System.out.println("文件路径准备失败:" + strFileDest + "!");
							}
						}else{
							if(prepareSubPath(strFileDest)) {
								try {
									InputStream fis = res.getInputStream();
									OutputStream fos = new FileOutputStream(strFileDest);
									byte[] bArrBuffer = new byte[BUFFER_SIZE];
									int iCount = 0;
									
									while ((iCount = fis.read(bArrBuffer, 0, bArrBuffer.length)) != -1) {
										fos.write(bArrBuffer, 0, iCount);
									}
									
									fis.close();
									fos.close();								
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								System.out.println("文件路径准备失败:" + strFileDest + "!");
							}
						}
					}
				}
			}
		} catch (IOException e) {
		}
	}
	
	protected boolean prepareSubPath(String strDest) {			
		try {
			String strSubPath = strDest.substring(0, strDest.lastIndexOf(File.separator));
			FileUtil.creatDirs("", strSubPath);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private static final String[] JSPPATHREGEX = new String[] {
		"WEB-INF/jsp/**/*.jsp"
	};
	

	private static Logger log = LoggerFactory.getLogger(ServiceLocatorListener.class);
	private static final int BUFFER_SIZE = 102400;
}
