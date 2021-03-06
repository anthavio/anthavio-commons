package net.anthavio;

/**
 * This should never happen exception. Use in situation that really shouldn't happen...NEVER
 * 
 * @author martin.vanek
 * 
 */
public class NeverHappenException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NeverHappenException() {
		super();
	}

	public NeverHappenException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public NeverHappenException(String message) {
		super(message);
	}

	public NeverHappenException(Throwable throwable) {
		super(throwable);
	}

}
