package cz.komix;

/**
 * @author vanek
 * 
 * RuntimeException wrapper pro checked Exception zpusobene spatnou konfiguraci
 */
public class ConfigurationException extends NonSolvableException {

	private static final long serialVersionUID = -6710421229805852114L;

	public ConfigurationException() {
		super();
	}

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
