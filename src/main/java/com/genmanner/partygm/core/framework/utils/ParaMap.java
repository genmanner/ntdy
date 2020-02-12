package com.genmanner.partygm.core.framework.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 设置页面Map集合
 *  
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ParaMap {
	String value();
}
