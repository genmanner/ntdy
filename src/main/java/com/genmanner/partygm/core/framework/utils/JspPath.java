package com.genmanner.partygm.core.framework.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 设置controller对应JSP的主要路径
 *  
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JspPath {
	String value();
}
