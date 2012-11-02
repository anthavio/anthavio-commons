package com.anthavio;

/**
 * RuntimeException wrapping unsolvable checked Exceptions
 * 
 * @author vanek
 * 
 */
public class NonSolvableException extends RuntimeException {

	private static final long serialVersionUID = -730258665663799312L;

	public NonSolvableException(String message, Throwable cause) {
		super(message, cause);
		// setStackTrace(cause.getStackTrace());
	}

	public NonSolvableException(String message) {
		super(message);
	}

	public NonSolvableException(Throwable cause) {
		super(cause);
		// setStackTrace(cause.getStackTrace());
	}

}
