package com.anthavio.commons.test;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;

import org.testng.annotations.Test;

import com.anthavio.NonSolvableException;


public class ExceptionTest {

	@Test
	public void test() {
		try {
			try {
				try {
					new ServerSocket(Integer.MIN_VALUE);
					new File("Invalid filename!@#$%^&*()").createNewFile();
				} catch (Exception x) {
					throw new Exception("I'm second level reason", x);
				}
			} catch (Exception x) {
				throw new NonSolvableException(x);
			}
		} catch (RuntimeException rx) {
			StringWriter sw = new StringWriter();
			rx.printStackTrace(new PrintWriter(sw));
			String printStackTrace = sw.toString();
			//new Buffered
			//rx.printStackTrace();
		}
	}
}
