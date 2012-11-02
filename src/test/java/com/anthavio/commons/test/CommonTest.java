package com.anthavio.commons.test;

import static org.fest.assertions.api.Assertions.assertThat;

import java.net.URL;
import java.security.KeyStore;

import org.jasypt.util.digest.Digester;
import org.testng.annotations.Test;

import com.anthavio.ssl.JksSslSocketFactory;
import com.anthavio.util.HashUtil;
import com.anthavio.util.StringUtil;

/**
 * @author vanek
 * 
 */
public class CommonTest {

	@Test
	public void testHashUtil() {
		// hodnoty hashu jsou unixovych md5sum a sha1sum
		String md5 = HashUtil.MD5("xxx", null);
		assertThat(md5).isEqualTo("f561aaf6ef0bf14d4208bb46a4ccb3ad");
		String sha1 = HashUtil.SHA1("xxx", null);
		assertThat(sha1).isEqualTo("b60d121b438a380c343d5ec3c2037564b82ffef3");
		// overime si jeste pres Jasypt
		byte[] digest = new Digester("SHA-1").digest("xxx".getBytes());
		assertThat(sha1).isEqualTo(HashUtil.getHexString(digest));
	}

	@Test
	public void testSslSocketFactory() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL jksUrl = loader.getResource("test.jks");
		JksSslSocketFactory different = new JksSslSocketFactory(jksUrl, "kokosak",
				"kokosak", jksUrl, "kokosak");
		different.getSSLContext();

		KeyStore jks = JksSslSocketFactory.loadKeyStore(jksUrl, "kokosak");

		different = new JksSslSocketFactory(jks, "kokosak", jks);
		different.getSSLContext();

		JksSslSocketFactory trustOnly = new JksSslSocketFactory(jks);
		trustOnly.getSSLContext();

		JksSslSocketFactory shared = new JksSslSocketFactory(jks, "kokosak");
		shared.getSSLContext();
	}

	@Test
	public void testNormalizer() {
		String czLow = "ěžščřďťň_ýáíéúůó_öüïëä_åçċęõŝ";
		String czUpc = czLow.toUpperCase();

		String asciiLow = "ezscrdtn_yaieuuo_ouiea_acceos";
		String asciiUpc = asciiLow.toUpperCase();

		assertThat(StringUtil.normalize(czLow)).isEqualTo(asciiLow);
		assertThat(StringUtil.normalize(czUpc)).isEqualTo(asciiUpc);

		assertThat(StringUtil.normalizeUC(czLow)).isEqualTo(asciiUpc);
		assertThat(StringUtil.normalizeUC(czUpc)).isEqualTo(asciiUpc);

		assertThat(StringUtil.normalizeLC(czLow)).isEqualTo(asciiLow);
		assertThat(StringUtil.normalizeLC(czUpc)).isEqualTo(asciiLow);
	}
}
