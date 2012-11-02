package com.anthavio;

/**
 * 
 * RuntimeException wrapper for invalid/failed configuration checked Exception
 * 
 * @author vanek
 * 
 */
public class ConfigException extends NonSolvableException {

	private static final long serialVersionUID = -6710421229805852114L;

	public ConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigException(String message) {
		super(message);
	}

	public ConfigException(Throwable cause) {
		super(cause);
	}
}
