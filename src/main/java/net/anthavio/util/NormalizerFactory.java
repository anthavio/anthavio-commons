package net.anthavio.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NormalizerFactory {

	public static interface StringNormalizer {

		public String normalize(String text);
	}

	public static StringNormalizer getNormalizationStringFilter() {
		Exception x6;
		try {
			return new Java6Normalizer();
		} catch (Exception x) {
			x6 = x;
		}
		try {
			return new Java5Normalizer();
		} catch (Exception e) {
		}
		throw new IllegalStateException("Cannot instantiate neither normalizer", x6);
	}

	public static class Java6Normalizer implements StringNormalizer {
		private final Method normalizer;
		private final Object nfd;

		public Java6Normalizer() throws IllegalAccessException, ClassNotFoundException {
			normalizer = java6GetMethodNormalizer();
			nfd = java6GetNFD();
		}

		public String normalize(String text) {
			try {
				return java6Invoke(text, normalizer, nfd);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static String java6Invoke(String text, Method normalizer, Object nfd) throws IllegalAccessException,
	InvocationTargetException {
		//return java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD);// JDK 1.6 method
		return (String) normalizer.invoke(null, new Object[] { text, nfd });
	}

	private static Method java6GetMethodNormalizer() throws ClassNotFoundException {
		Class c = Class.forName("java.text.Normalizer");
		Method[] methods = c.getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals("normalize")) {
				return methods[i];
			}
		}
		return null;
	}

	private static Object java6GetNFD() throws ClassNotFoundException, IllegalAccessException {
		Class x = Class.forName("java.text.Normalizer$Form");
		Object nfd = null;
		for (Field f : x.getDeclaredFields()) {
			if (f.getName().equals("NFD")) {
				nfd = f.get(null);
			}
		}
		return nfd;
	}

	public static class Java5Normalizer implements StringNormalizer {
		private final Method normalizer;

		public Java5Normalizer() throws ClassNotFoundException {
			normalizer = java5GetMethodNormalizer();
		}

		public String normalize(String text) {
			try {
				return java5Invoke(text, normalizer);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static final Integer ZERO = 0;

	private static String java5Invoke(String text, Method normalizer) throws InvocationTargetException,
	IllegalAccessException {
		//return sun.text.Normalizer.decompose(text, false, 0);// Pre-JDK 1.6 method
		return (String) normalizer.invoke(null, new Object[] { text, Boolean.FALSE, ZERO });
	}

	private static Method java5GetMethodNormalizer() throws ClassNotFoundException {
		Class c = Class.forName("sun.text.Normalizer");
		Method[] methods = c.getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals("decompose") && methods[i].getGenericParameterTypes().length == 3) {
				return methods[i];
			}
		}
		return null;
	}

	// method that is not portable
	// but it is faster
	// java 1.5.0_12: 2659ms vs 4096ms for 10 million calls
	// java 1.6.0_02: 3094ms vs 4264ms for 10 million calls
	//	public static String normalize(String text)
	//	{
	//		return sun.text.Normalizer.decompose(text, false, 0);// Pre-JDK 1.6 method
	//		return java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD);// JDK 1.6 method
	//	}

}
