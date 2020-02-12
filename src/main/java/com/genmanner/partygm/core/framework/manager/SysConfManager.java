package com.genmanner.partygm.core.framework.manager;

import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;

import com.genmanner.partygm.core.framework.domain.SysConf;
import com.genmanner.partygm.core.framework.exception.ManagerException;
import com.genmanner.partygm.core.framework.utils.SqlId;

/**
 * 系统配置Manager
 *  
 */
@Service
public class SysConfManager {
	/**
	 * 从数据库中获取所有任务类型
	 * @return 所有任务类型
	 */
	public List<SysConf> getAllSysConfFromDb() {
		try {
			return sqlSessionTemplate.<SysConf>selectList(getStatement(SqlId.SQL_SELECT));
		} catch (Exception e) {
			throw new ManagerException(String.format("查询对象列表出错！语句：%s", getStatement(SqlId.SQL_SELECT)), e);
		}
	}
	
	private String getStatement(String key) {
		return new StringBuffer(SysConf.class.getName()).append(".").append(key).toString();
	}

	@Resource
	private SqlSessionTemplate sqlSessionTemplate;	
}
