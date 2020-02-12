package com.genmanner.partygm.core.framework.boottask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.genmanner.partygm.core.framework.boottask.domain.TaskKind;
import com.genmanner.partygm.core.framework.boottask.manager.TaskKindManager;

/**
 * 任务类型工具类
 *  
 */
@Service
public class TaskKindTools {
	/**
	 * 获取任务类型
	 * @param taskKindId 任务类型编号
	 * @return 任务类型
	 */
	public TaskKind getTaskKind(String taskKindId) {
		if(mapTaskKind.size() == 0) {
			init();
		}
		
		return mapTaskKind.get(taskKindId);
	}
	
	/**
	 * 获取所有任务类型
	 * @return 所有任务类型
	 */
	public Map<String, TaskKind> getAllTaskKind() {
		if(mapTaskKind.size() == 0) {
			init();
		}
		
		return mapTaskKind;
	}
	
	/**
	 * 重新加载任务类型
	 */
	public void reLoad() {
		mapTaskKind.clear();
		init();
	}
	
	protected void init() {
		List<TaskKind> lstTaskKind = taskKindManager.getAllTaskKindFromDb();
		
		for(TaskKind taskKind : lstTaskKind) {
			mapTaskKind.put(taskKind.getId(), taskKind);
		}
	}

	@Resource
	TaskKindManager taskKindManager;
	
	Map<String, TaskKind> mapTaskKind = new HashMap<String, TaskKind>();
}
