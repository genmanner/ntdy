package com.genmanner.partygm.core.framework.realtimecomm;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.genmanner.partygm.core.common.framework.UUIDUtils;

/**
 * 任务配置工具类
 *  
 */
@Service
public class TaskConfigTools {
	/**
	 * 获取任务流水号，更新配置，并锁定记录。必须在事务中处理
	 * @param taskKindId 任务类型编号
	 * @return 任务类型
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	public int fetchStreamIdAndLock(String taskKindId) {
		List<Map<String, Object>> lstConf = jdbcTemplate.queryForList("select t.currMaxStreamId as CURRMAXSTREAMID from framework_rt_taskconfig t"
					+ " where t.taskKindId = ? for update", new Object[]{taskKindId});
		
		if(lstConf!=null && lstConf.size()>0) {
			int streamId = Integer.parseInt(lstConf.get(0).get("CURRMAXSTREAMID").toString())+1;
			jdbcTemplate.update("update framework_rt_taskconfig t set t.currMaxStreamId = ?"
					+ " where t.taskKindId = ?", new Object[]{streamId, taskKindId});
			return streamId;
		}
		
		jdbcTemplate.update("insert into framework_rt_taskconfig(id, currMaxStreamId, taskKindId) values (?, ?, ?)"
				, new Object[]{UUIDUtils.create(), 1, taskKindId});
		return 1;
	}

	@Resource
	private JdbcTemplate jdbcTemplate;
}
