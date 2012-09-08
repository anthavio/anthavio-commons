package com.anthavio.util;

import com.anthavio.util.NormalizerFactory.StringNormalizer;

/**
 * StringNormalizer - normalizace textovych retezcu - tzn odstraneni hacku a carek a netisknutelnych znaku
 * @author peremsky on Aug 11, 2010
 */
public class StringUtil {

	private StringUtil() {
		//disable new
	}

	private static StringNormalizer normalizer= NormalizerFactory.getNormalizationStringFilter();

	/**
	 * @param s
	 * @return Normalizovany retezec
	 */
	public final static String normalize(String s) {
		return s == null ? null : normalizer.normalize(s).replaceAll(
				"[^\\p{ASCII}]", "");
	}

	/**
	 * @param s
	 * @return Normalizovany retezec.toLowerCase()
	 */
	public final static String normalizeLC(String s) {
		return s == null ? null : normalizer.normalize(s)
				.replaceAll("[^\\p{ASCII}]", "").toLowerCase();
	}

	/**
	 * @param s
	 * @return Normalizovany retezec.toUpperCase()
	 */
	public final static String normalizeUC(String s) {
		return s == null ? null : normalizer.normalize(s)
				.replaceAll("[^\\p{ASCII}]", "").toUpperCase();
	}
}
