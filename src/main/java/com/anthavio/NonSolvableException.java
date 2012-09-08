package com.anthavio;

/**
 * 
 * @author vanek
 *
 * RuntimeException wrapper pro neresitelne checked Exception
 */
public class NonSolvableException extends RuntimeException {

	private static final long serialVersionUID = -730258665663799312L;

	public NonSolvableException() {
		super();
	}

	public NonSolvableException(String message, Throwable cause) {
		super(message, cause);
		//setStackTrace(cause.getStackTrace());
	}

	public NonSolvableException(String message) {
		super(message);
	}

	public NonSolvableException(Throwable cause) {
		super(cause);
		//setStackTrace(cause.getStackTrace());
	}

}
