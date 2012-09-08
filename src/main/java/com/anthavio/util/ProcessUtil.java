/**
 * 
 */
package com.anthavio.util;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * @author vanek
 *
 */
public class ProcessUtil {

	/** 
	 * Sun property pointing the main class and its arguments. 
	 * Might not be defined on non Hotspot VM implementations.
	 */
	public static final String SUN_JAVA_COMMAND = "sun.java.command";

	/**
	 * XXX A co treba {@link java.lang.ProcessBuilder} 
	 * Restart the current Java application
	 * @param runBeforeRestart some custom code to be run before restarting
	 */
	public static void restart(Runnable runBeforeRestart) {
		String javaPath = System.getProperty("java.home") + "/bin/java";
		List<String> vmArgsList = ManagementFactory.getRuntimeMXBean().getInputArguments();
		StringBuffer vmArgs = new StringBuffer();
		for (String vmArg : vmArgsList) {
			// if it's the agent argument : we ignore it otherwise the
			// address of the old application and the new one will be in conflict
			if (!vmArg.contains("-agentlib")) {
				vmArgs.append(vmArg);
				vmArgs.append(" ");
			}
		}
		// init the command to execute, add the vm args
		final StringBuffer cmd = new StringBuffer("\"" + javaPath + "\" " + vmArgs);

		// program main and program arguments
		String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(" ");
		if (mainCommand[0].endsWith(".jar")) {
			cmd.append("-jar " + new File(mainCommand[0]).getPath());
		} else {
			cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0]);
		}
		// finally add program arguments
		for (int i = 1; i < mainCommand.length; i++) {
			cmd.append(" ");
			cmd.append(mainCommand[i]);
		}
		// execute the command in a shutdown hook, to be sure that all the
		// resources have been disposed before restarting the application
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					Runtime.getRuntime().exec(cmd.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		if (runBeforeRestart != null) {
			runBeforeRestart.run();
		}
		System.exit(0);
	}
}
