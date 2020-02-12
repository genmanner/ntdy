package com.genmanner.partygm.core.framework.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.genmanner.partygm.core.common.dispatch.ExceptionMsgBase;
import com.genmanner.partygm.core.common.dispatch.IDispatch;
import com.genmanner.partygm.core.common.dispatch.IExceptionSolver;
import com.genmanner.partygm.core.common.dispatch.ITaskSchedule;
import com.genmanner.partygm.core.common.util.BeanUtil;
import com.genmanner.partygm.core.framework.domain.SysConf;
import com.genmanner.partygm.core.framework.manager.SysConfManager;

/**
 * 系统配置工具类
 *  
 */
@Service
public class SysConfTools {
	/**
	 * 获取任务类型
	 * @param taskKindId 任务类型编号
	 * @return 任务类型
	 */
	public SysConf getSysConf(String sysConfId) {
		SysConf retSysConf = null;

		synchronized (this) {
			if(mapSysConf.size() == 0) {
				init();
			}
			
			retSysConf = mapSysConf.get(sysConfId);
		}
		
		return retSysConf;
	}
	
	/**
	 * 获取所有任务类型
	 * @return 所有任务类型
	 */
	public Map<String, SysConf> getAllSysConf() {
		Map<String, SysConf> ret = null;
		
		synchronized (this) {
			if(mapSysConf.size() == 0) {
				init();
			}
			
			ret = (Map<String, SysConf>)mapSysConf.clone();
		}
		
		return  ret;
	}
	
	/**
	 * 重新加载任务类型
	 */
	public void reLoad() {
		synchronized (this) {
			mapSysConf.clear();
		}
		
		init();
	}
	
	/**
	 * 获取默认Dispatch
	 * @return 默认Dispatch
	 */
	public IDispatch getDefaultDispatch() {
		return (IDispatch)BeanUtil.loadBeanByName(getSysConf(SYSCONF_KEY_DEFAULT_DISPATCH_BEANNAME).getValue());
	}
	
	/**
	 * 获取默认ExceptionSolver
	 * @return 默认ExceptionSolver
	 */
	public IExceptionSolver getDefaultExceptionSolver() {
		return (IExceptionSolver)BeanUtil.loadBeanByName(getSysConf(SYSCONF_KEY_DEFAULT_EXCEPTION_SOLVER_BEANNAME).getValue());
	}
	
	protected void init() {
		List<SysConf> lstSysConf = sysConfManager.getAllSysConfFromDb();
		
		synchronized (this) {
			for(SysConf sysConf : lstSysConf) {
				mapSysConf.put(sysConf.getId(), sysConf);
			}
		}
		
		int iDelay = Integer.parseInt(mapSysConf.get(SYSCONF_KEY_REFRESH_TICK).getValue());
		
		if(iDelay > 0) {
			IDispatch dispatch = getDefaultDispatch();
			IExceptionSolver exceptionSolver = getDefaultExceptionSolver();
			ITaskSchedule taskSchedule = new ITaskSchedule() {
				@Override
				public void logicOperate() throws ExceptionMsgBase {
					reLoad();
				}
			};
			Long iTaskId = dispatch.create(taskSchedule, exceptionSolver);
			dispatch.notice(iTaskId, iDelay);
		}
	}

	@Resource
	SysConfManager sysConfManager;
	
	HashMap<String, SysConf> mapSysConf = new HashMap<String, SysConf>();
	
	//平台端
	public static final String SYSCONF_KEY_REFRESH_TICK = "1";
	public static final String SYSCONF_KEY_PLATFORM_ADDRESS = "2";
	public static final String SYSCONF_KEY_DEFAULT_DISPATCH_BEANNAME = "3";
	public static final String SYSCONF_KEY_DEFAULT_EXCEPTION_SOLVER_BEANNAME = "4";
	
	//业务系统端
	public static final String SYSCONF_UNIVERSITY_CODE = "10001";
	public static final String SYSCONF_ENCRYPTED_RSA_PUBLIC_KEY = "10002";	
}
