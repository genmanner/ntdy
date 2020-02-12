package com.genmanner.partygm.core.framework.realtimecomm.manager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.genmanner.partygm.core.common.framework.UUIDUtils;
import com.genmanner.partygm.core.framework.exception.ManagerException;
import com.genmanner.partygm.core.framework.realtimecomm.domain.TaskStream;
import com.genmanner.partygm.core.framework.utils.SqlId;

/**
 * 任务流水Manager
 *  
 */
@Service
public class TaskStreamManager {
	/**
	 * 任务流水类型
	 *  
	 *
	 */
	public static enum TaskStreamKind {
		/**
		 * 发送任务流水
		 */
		TaskStreamKind_Send,
		
		/**
		 * 接收任务流水
		 */
		TaskStreamKind_Receive
	}
	
	/**
	 * 任务流水状态
	 *  
	 *
	 */
	public static enum TaskStreamStatus {
		/**
		 * 新建（未处理）
		 */
		TaskStreamStatus_New {
			@Override
		    public String toString() {
				return "0";
			}
		},		
		/**
		 * 处理中
		 */
		TaskStreamStatus_Solving {
			@Override
		    public String toString() {
				return "1";
			}
		},
		
		/**
		 * 已处理
		 */
		TaskStreamStatus_Solved {
			@Override
		    public String toString() {
				return "2";
			}
		}
	}
	
	/**
	 * 批量标记任务处理中
	 * @param lstTaskStream 任务流水列表
	 * @param taskStreamKind 任务流水类型
	 */
	public void BatchInSolving(List<TaskStream> lstTaskStream, TaskStreamKind taskStreamKind) {
		BatchChangeStatus(lstTaskStream, TaskStreamStatus.TaskStreamStatus_Solving, taskStreamKind);	
	}
	
	/**
	 * 批量标记任务完成
	 * @param lstTaskStream 任务流水列表
	 * @param taskStreamKind 任务流水类型
	 */
	public void BatchSolved(List<TaskStream> lstTaskStream, TaskStreamKind taskStreamKind) {
		BatchChangeStatus(lstTaskStream, TaskStreamStatus.TaskStreamStatus_Solved, taskStreamKind);
	}
	
	/**
	 * 批量回滚任务
	 * @param lstTaskStream 任务流水列表
	 * @param taskStreamKind 任务流水类型
	 */
	public void BatchRollback(List<TaskStream> lstTaskStream, TaskStreamKind taskStreamKind) {
		BatchChangeStatus(lstTaskStream, TaskStreamStatus.TaskStreamStatus_New, taskStreamKind);
	}
	
	/**
	 * 插入一条任务流水，并立即存盘
	 * @param entity 任务流水
	 * @param taskStreamKind 任务流水类型
	 * @return 成功插入条数
	 */
	public int insert(TaskStream entity, TaskStreamKind taskStreamKind) {
		try {
			if (StringUtils.isBlank(entity.getId())) {
				entity.setId(UUIDUtils.create());
			}
			
			if (StringUtils.isBlank(entity.getAddTime())) {
				entity.setAddTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			}
			
			return sqlSessionTemplate.insert(getStatement(SqlId.SQL_INSERT, taskStreamKind), entity);
		} catch (Exception e) {
			throw new ManagerException(String.format("添加对象出错！语句：%s", getStatement(SqlId.SQL_INSERT, taskStreamKind)), e);
		}
	}
	
	/**
	 * 获取所有
	 * @param taskStreamKind 任务流水类型
	 * @return
	 */
	public List<TaskStream> selectAll(TaskStreamStatus status, TaskStreamKind taskStreamKind) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("status", status.toString());
		
		try {
			return sqlSessionTemplate.selectList(getStatement(SqlId.SQL_SELECT, taskStreamKind), params);
		} catch (Exception e) {
			throw new ManagerException(String.format("查询对象列表出错！语句：%s", getStatement(SqlId.SQL_SELECT, taskStreamKind)), e);
		}
	}
	
	/**
	 * 获取所有
	 * @param taskKindId 任务类型Id
	 * @param taskStreamKind 任务流水类型
	 * @return
	 */
	public List<TaskStream> selectAllByTaskKindId(String taskKindId, TaskStreamStatus status, TaskStreamKind taskStreamKind) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("taskKindId", taskKindId);
		params.put("status", status.toString());
		
		try {
			return sqlSessionTemplate.selectList(getStatement(SqlId.SQL_SELECT, taskStreamKind), params);
		} catch (Exception e) {
			throw new ManagerException(String.format("查询对象列表出错！语句：%s", getStatement(SqlId.SQL_SELECT, taskStreamKind)), e);
		}
	}
	
	protected void BatchChangeStatus(List<TaskStream> lstTaskStream, TaskStreamStatus status, TaskStreamKind taskStreamKind) {
		List<Object[]> lstParams = new LinkedList<Object[]>();
		
		for(TaskStream taskStream : lstTaskStream) {
			lstParams.add(new Object[]{status.toString(), taskStream.getId()});
		}
			
		jdbcTemplate.batchUpdate("update framework_rt_taskstream_"
				+ (taskStreamKind==TaskStreamKind.TaskStreamKind_Send ? "Send" : "Rec")
				+ " t set t.status = ? where t.id = ?", lstParams);
		jdbcTemplate.update("commit");
	}
	
	private String getStatement(String key, TaskStreamKind taskStreamKind) {
		StringBuffer strRet = new StringBuffer(TaskStream.class.getName());
		
		if(taskStreamKind == TaskStreamKind.TaskStreamKind_Send) {
			strRet.append("Send");
		} else {
			strRet.append("Receive");
		}
		
		return strRet.append(".").append(key).toString();
	}
	
	@Resource
	private JdbcTemplate jdbcTemplate;
	@Resource
	private SqlSessionTemplate sqlSessionTemplate;	
}
