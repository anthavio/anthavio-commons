package net.anthavio.util;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author martin.vanek
 *
 */
public class NamedThreadFactory implements ThreadFactory {

	static final AtomicInteger poolNumber = new AtomicInteger(1);

	final AtomicInteger threadNumber = new AtomicInteger(1);

	private final String prefix;

	private final boolean deamon;

	private UncaughtExceptionHandler uncaughtExceptionHandler;

	public NamedThreadFactory() {
		this("P:" + poolNumber.getAndIncrement() + "-T:", true);
	}

	public NamedThreadFactory(String prefix) {
		this(prefix, true);
	}

	public NamedThreadFactory(String prefix, boolean deamon) {
		if (prefix == null || prefix.length() == 0) {
			throw new IllegalArgumentException("Empty prefix");
		}
		this.prefix = prefix;
		this.deamon = deamon;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setName(prefix + threadNumber.getAndIncrement());
		thread.setDaemon(deamon);
		thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
		return thread;
	}

	public boolean isDeamon() {
		return deamon;
	}

	public String getPrefix() {
		return prefix;
	}

	public UncaughtExceptionHandler getUncaughtExceptionHandler() {
		return uncaughtExceptionHandler;
	}

	public void setUncaughtExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler) {
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;
	}

}
