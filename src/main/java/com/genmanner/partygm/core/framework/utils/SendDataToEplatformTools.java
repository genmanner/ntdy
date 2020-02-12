package com.genmanner.partygm.core.framework.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sun.misc.BASE64Encoder;

import com.genmanner.partygm.core.common.util.DateUtil;
import com.genmanner.partygm.core.common.util.JsonUtil;
import com.genmanner.partygm.core.common.util.Utility;
import com.genmanner.partygm.core.framework.domain.TabDataSync;
import com.genmanner.partygm.core.framework.exception.ManagerException;
import com.genmanner.partygm.core.framework.realtimecomm.RealTimeCommTools;

/**
 * 业务系统发送数据到平台
 *  
 */
@Service
@Transactional
public class SendDataToEplatformTools {
	@Autowired
	private RealTimeCommTools realTimeCommTools;
	/**
	 * 处理表数据同步
	 */
	private final static String dealTabDataSyncTaskKindId = "dealTabDataSyncTaskKindId";
	
	/**
	 * 发送表数据到平台
	 * @param tabDataSync	:待发送数据
	 */
	public void sendTabDataToEplatform(TabDataSync tabDataSync) {
		chkSendData(tabDataSync);
		if(!Utility.isEmpty(tabDataSync.getDataList())) {
			realTimeCommTools.asyncSendMessage(dealTabDataSyncTaskKindId, warpSyncParams(tabDataSync));
		}
		
	}
	
	/**
	 * 发送表数据到平台
	 * @param taskKindId	:任务类型id
	 * @param tabDataSync	:待发送数据
	 */
	public void sendTabDataToEplatform(String taskKindId, TabDataSync tabDataSync) {
		chkTaskKindId(taskKindId);
		chkSendData(tabDataSync);
		if(!Utility.isEmpty(tabDataSync.getDataList())) {
			realTimeCommTools.asyncSendMessage(taskKindId, warpSyncParams(tabDataSync));
		}
		
	}
	
	/**
	 * 发送数据到平台
	 * @param taskKindId	:任务类型id
	 * @param data			:信息
	 */
	public void sendDataToEplatform(String taskKindId, String data) {
		chkTaskKindId(taskKindId);
		realTimeCommTools.asyncSendMessage(taskKindId, data);
	}
	
	/**
	 * 包装同步参数
	 * @param tabDataSync	:待包装对象
	 * @return				:返回包装后参数
	 */
	protected String warpSyncParams(TabDataSync tabDataSync) {
		Object val = null;
		String uppperCaseKey = null;
		Map<String, Object> newData = null;
		Map.Entry<String,Object> mapEntry = null;
		Iterator<Map.Entry<String,Object>> mapEntryIter = null;
		List<Map<String, Object>> dataList = tabDataSync.getDataList();
		List<Map<String, Object>> newDataList = new ArrayList<Map<String,Object>>(dataList.size());
		
		for(Map<String, Object> tmpMap : dataList) {
			newData = new HashMap<String, Object>(tmpMap.size());
			mapEntryIter = tmpMap.entrySet().iterator();
			while(mapEntryIter.hasNext()) {
				mapEntry = mapEntryIter.next();
				val = mapEntry.getValue();
				uppperCaseKey = mapEntry.getKey().toUpperCase();
				
				if(!Utility.isEmpty(val)) {
					if(val instanceof java.lang.Integer) {//整型
						newData.put(intType + uppperCaseKey, val);
						continue;
					} else if(val instanceof java.lang.Float) {//浮点型
						newData.put(floatType + uppperCaseKey, val);
						continue;
					} else if(val instanceof java.util.Date) {//日期类型(util)
						newData.put(dateType + uppperCaseKey, DateUtil.getTimeString((java.util.Date) val));
						continue;
					} else if(val instanceof java.sql.Date) {//日期类型(sql)
						newData.put(dateType + uppperCaseKey, DateUtil.getTimeString((java.sql.Date) val));
						continue;
					} else if(val instanceof byte[]) {//字节流
						newData.put(bytesType + uppperCaseKey, (new BASE64Encoder().encode((byte[]) val)));
						continue;
					}
				}
				
				newData.put(uppperCaseKey, val);
			}
			
			newDataList.add(newData);
		}
		
		dataList = null;
		tabDataSync.setDataList(newDataList);
		
		return JsonUtil.parseObject(tabDataSync);
	}
	
	/**
	 * 检查发送数据
	 * @param tabDataSync	:待检查数据
	 */
	protected void chkSendData(TabDataSync tabDataSync) {
		if(Utility.isEmpty(tabDataSync)) throw new ManagerException("发送对象为空");
		if(Utility.isEmpty(tabDataSync.getTargetTabName())) throw new ManagerException("目标表为空");
		if(Utility.isEmpty(tabDataSync.getTargetPkField())) throw new ManagerException("目标表主键为空");
	}
	
	/**
	 * 检查任务类型id
	 * @param taskKindId	:任务类型id
	 */
	protected void chkTaskKindId(String taskKindId) {
		if(Utility.isEmpty(taskKindId)) throw new ManagerException("任务类型id为空");
	}
	
	/**
	 * 整型
	 */
	private final String intType = "$int$";
	
	/**
	 * 浮点型
	 */
	private final String floatType = "$float$";
	
	/**
	 * 日期类型
	 */
	private final String dateType = "$date$";
	
	/**
	 * 字节数组
	 */
	private final String bytesType = "$bytes$";
}
