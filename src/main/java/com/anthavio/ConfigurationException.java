package com.anthavio;

/**
 * 
 * RuntimeException wrapper for invalid/failed configuration checked Exception
 * 
 * @author vanek
 * 
 */
public class ConfigurationException extends NonSolvableException {

	private static final long serialVersionUID = -6710421229805852114L;

	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigurationException(String message) {
		super(message);
	}

	public ConfigurationException(Throwable cause) {
		super(cause);
	}
}
