package com.genmanner.partygm.core.framework.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.genmanner.partygm.core.common.cache.CacheTools;
import com.genmanner.partygm.core.common.cache.ICacheLoader;


/**
 * 本工具类为了实现把些系统需要的数据长驻内存，以达到快速提取数据的功能
 * <br>1、数据字典长驻内存
 * <br>2、一些基本的业务数据长驻内存
 * <br>请注意对于大表（数据量在一万条以上），以及表中有大存储数据字段不适合此操作
 *  
 *
 */
@Service
public class CommonCacheMgr {
	/**
	 * 获取缓存的Bean
	 * @param key Bean键值
	 * @return Bean的集合信息
	 */
	public List<Object> getBean(String key){
		Object value = cacheTools.getCacheVisitor().getData(beanCachePrefix+key);
		
		if(value instanceof List) {
			return (List<Object>) value;
		}
		
		return null;
	}
	
	/**
	 * 获取缓存的Sql对象
	 * @param key sql对象的键值
	 * @return Sql对象
	 */
	public List<Map<String,Object>> getSqlObject(String key){
		Object value = cacheTools.getCacheVisitor().getData(sqlCachePrefix+key);
		
		if(value instanceof List) {
			List<Object> lstValue = (List<Object>)value;
			
			if(lstValue==null || lstValue.size()==0 || lstValue.get(0) instanceof Map) {
				Map<Object, Object> mapData = (Map<Object, Object>)lstValue.get(0);
				
				if(mapData.keySet()==null || mapData.keySet().size()==0 
						|| mapData.keySet().toArray()[0] instanceof String) {
					return (List<Map<String, Object>>) value;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 系统运时后外界更新缓存数据
	 * @param key 编码 
	 */
	public void updateBeanCache(String key) {
		cacheTools.refreshData(beanCachePrefix+key);
	}
	
	/**
	 * 系统运时后外界更新缓存Sql对象
	 * @param key 编码 
	 */
	public void updateSqlCache(String key) {
		cacheTools.refreshData(sqlCachePrefix+key);
	}
	
	/**
	 * 启动时缓存生成
	 */
	public void createCache(){
		factoryBeanCache();
		factorySqlCache();
	}
	
	/**
	 * 刷新全部缓存数据
	 */
	public void flashCache(){		
		createCache();
	}

	protected void factoryBeanCache(){
		Properties beanProperties = getProperties(beanCachePath);
		Set<Object> set = beanProperties.keySet();
		
		if(set != null){
			for(Object obj : set){
				String key = (String)obj;
				LoadBeanCache(key+"."+SqlId.SQL_SELECT);
			}
		}
	}

	protected void factorySqlCache(){
		Properties sqlProperties = getProperties(sqlCachePath);
		Set<Object> set = sqlProperties.keySet();
		
		if(set != null){
			for(Object obj : set){
				String key = (String)obj;
				String value = (String)sqlProperties.get(key);
				cacheTools.getCacheVisitor().putData(sqlStatementCachePrefix+key, value);
				LoadSqlCache(key);
			}
		}
	}
	
	protected boolean LoadBeanCache(String beanKey) {
		ICacheLoader beanCachLoader = new ICacheLoader() {
			@Override
			public Object loadData(String key) {
				return sqlSessionTemplate.selectList(key.substring(sqlCachePrefix.length()));
			}
		};
		
		return cacheTools.loadData(sqlCachePrefix+beanKey, beanCachLoader) != null;
	}
	
	protected boolean LoadSqlCache(String sqlKey) {
		ICacheLoader sqlCachLoader = new ICacheLoader() {
			@Override
			public Object loadData(String key) {
				String strOriginalKey = key.substring(sqlCachePrefix.length());
				String strSql = (String)cacheTools.getCacheVisitor().getData(sqlStatementCachePrefix+strOriginalKey);
				return jdbcTemplate.queryForList(strSql);
			}
		};
		
		return cacheTools.loadData(sqlCachePrefix+sqlKey, sqlCachLoader) != null;	
	}
	
	protected static Properties getProperties(String path){
		Properties p = new Properties();
		Resource[] resources = getResources(path);
		
		if(null != resources) {
			InputStream is = null;
			for(Resource tmpRes : resources) {
				try {
					is = tmpRes.getInputStream();
					p.load(is);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if(null != is) {
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				}
				
			}
			
		}
		
	    return p;
	}
	
	/**
	 * 根据路径获取资源<br>
	 * 说明：<br>
	 * 1.本地路径出现重名资源配置会取第一个<br>
	 * 2.本地路径配置资源配置会覆盖jar中资源配置，<br>
	 *   意味中本地中资源配置路径跟jar包配置的路径一致，
	 *   则以本地路径为准(可以用来实现覆盖jar包中配置)
	 * @param patternPath	:路径匹配模式
	 * @return
	 */
	protected static Resource[] getResources(String patternPath) {
		Resource[] retRes = null;
		ResourcePatternResolver resResolver = new PathMatchingResourcePatternResolver();
		try {
			retRes = resResolver.getResources(patternPath);
			if(null != retRes && retRes.length > 0) {//去除已覆盖的资源
				List<Resource> tmpList = new ArrayList<Resource>(retRes.length);
				boolean isNotHadSaved;
				for(Resource tmpRetRes : retRes) {
					isNotHadSaved = true;
					for(int i = 0; i < tmpList.size(); i++) {//判断是否存在相同的文件名并且已保存信息是jar包中资源
						if(tmpRetRes.getFilename().equals(tmpList.get(i).getFilename())) {
							isNotHadSaved = false;
							
							if(tmpList.get(i).getURL().getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_JAR)
									&& tmpRetRes.getURL().getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_FILE)) {
								
								tmpList.add(i, tmpRetRes);
								break;
							}
							
						}
					}
					
					if(isNotHadSaved) {//保存
						tmpList.add(tmpRetRes);
					}
					
				}
				
				retRes = tmpList.toArray(new Resource[tmpList.size()]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return retRes;
	}
	
	@Autowired
	private CacheTools cacheTools;
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	//指定的路径
	private static final String beanCachePath="classpath*:cache/*-bean.properties";
	private static final String sqlCachePath="classpath*:cache/*-sql.properties";
	
	//缓存key前缀
	private static final String beanCachePrefix="$beanCache-";
	private static final String sqlCachePrefix="$sqlCache-";
	private static final String sqlStatementCachePrefix="$sqlStatement-";
}
