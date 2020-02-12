package com.genmanner.partygm.core.framework.exception;
/**
 * manager 层异常信息 本类中异常暂未处理，留后期扩展 
 *  
 *
 */
public class ManagerException extends RuntimeException {

	private static final long serialVersionUID = -360277845666981697L;

	public ManagerException() {
		super();
	}

	public ManagerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ManagerException(String message) {
		super(message);
	}

	public ManagerException(Throwable cause) {
		super(cause);
	}

}
