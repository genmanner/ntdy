package com.genmanner.partygm.core.framework.boottask.manager;

import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;

import com.genmanner.partygm.core.framework.boottask.domain.TaskKind;
import com.genmanner.partygm.core.framework.exception.ManagerException;
import com.genmanner.partygm.core.framework.utils.SqlId;

/**
 * 任务类型Manager
 *  
 */
@Service
public class TaskKindManager {
	/**
	 * 从数据库中获取所有任务类型
	 * @return 所有任务类型
	 */
	public List<TaskKind> getAllTaskKindFromDb() {
		try {
			return sqlSessionTemplate.<TaskKind>selectList(getStatement(SqlId.SQL_SELECT));
		} catch (Exception e) {
			throw new ManagerException(String.format("查询对象列表出错！语句：%s", getStatement(SqlId.SQL_SELECT)), e);
		}
	}
	
	private String getStatement(String key) {
		return new StringBuffer(TaskKind.class.getName()).append(".").append(key).toString();
	}

	@Resource
	private SqlSessionTemplate sqlSessionTemplate;	
}
