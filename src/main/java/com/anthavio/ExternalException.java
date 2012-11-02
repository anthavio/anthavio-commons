package com.anthavio;

/**
 * Wrapper for external system/module checked exceptions
 * 
 * @author martin.vanek
 * 
 */
public class ExternalException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ExternalException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExternalException(Throwable cause) {
		super(cause);
	}

}
