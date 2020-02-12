package com.genmanner.partygm.core.framework.exception;

/**
 * 控制层异常信息 本类中异常暂未处理，留后期扩展 
 *  
 */
public class ControllerException extends RuntimeException {

	private static final long serialVersionUID = 4081863331832266720L;

	public ControllerException() {
		super();
	}

	public ControllerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ControllerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ControllerException(String message) {
		super(message);
	}

	public ControllerException(Throwable cause) {
		super(cause);
	}

}
