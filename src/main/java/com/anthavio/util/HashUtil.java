package com.anthavio.util;

import java.security.MessageDigest;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.UnhandledException;

/**
 * HashUtil - simple class for comuting md5 hash
 * 
 * @author peremsky on Jun 21, 2010
 */
public class HashUtil {

	private HashUtil() {
		// disable new
	}

	private static final String HEXES = "0123456789abcdef";

	public static String getHexString(byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}

	public static String MD5(String input) {
		return MD5(input, null);
	}

	public static String MD5(String input, String salt) {
		return hash("MD5", input, salt);
	}

	public static String SHA1(String input) {
		return SHA1(input, null);
	}

	public static String SHA1(String input, String salt) {
		return hash("SHA1", input, salt);
	}

	public static String hash(String algorithm, String input, String salt) {
		if (algorithm == null) {
			throw new NullArgumentException("algorithm");
		}
		if (input == null) {
			throw new NullArgumentException("input");
		}
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			String saltedInput = (salt != null ? salt : "") + (input != null ? input : "");
			byte[] hash = md.digest(saltedInput.getBytes("utf-8"));
			return getHexString(hash);
		} catch (Exception e) {
			throw new UnhandledException("Error calculating " + algorithm + " hash", e);
		}
	}
}
