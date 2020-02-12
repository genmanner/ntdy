package com.genmanner.partygm.core.framework.boottask.manager;

import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;

import com.genmanner.partygm.core.framework.boottask.domain.BootTimerTask;
import com.genmanner.partygm.core.framework.exception.ManagerException;
import com.genmanner.partygm.core.framework.utils.SqlId;

/**
 * 自启动定时任务Manager
 *  
 */
@Service
public class BootTimerTaskManager {
	/**
	 * 从数据库中获取所有自启动定时任务
	 * @return 所有自启动定时任务
	 */
	public List<BootTimerTask> getAllBootTimerTaskFromDb() {
		try {
			return sqlSessionTemplate.<BootTimerTask>selectList(getStatement(SqlId.SQL_SELECT));
		} catch (Exception e) {
			throw new ManagerException(String.format("查询对象列表出错！语句：%s", getStatement(SqlId.SQL_SELECT)), e);
		}
	}
	
	private String getStatement(String key) {
		return new StringBuffer(BootTimerTask.class.getName()).append(".").append(key).toString();
	}

	@Resource
	private SqlSessionTemplate sqlSessionTemplate;	
}
