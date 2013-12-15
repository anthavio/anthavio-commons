package net.anthavio;

/**
 * Throw when unusable value is passed to be used. Typical for unexcpected value in switch/case
 * 
 * @author vanek
 * 
 */
public class NotSupportedException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public NotSupportedException(Object choice) {
		super("Value is not supported: '" + String.valueOf(choice) + "'");
	}
}
