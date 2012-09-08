package com.anthavio.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vanek
 * 
 * Implementace {@link SSLSocketFactory} nacitajici certifikaty z JKS keystoru
 * 
 */
public class JksSslSocketFactory extends SSLSocketFactory {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private SSLContext sslcontext = null;

	/**
	 * Default IOC subclass friendly
	 */
	public JksSslSocketFactory() {
	}

	/**
	 * Standard initializing constructor
	 */
	public JksSslSocketFactory(URL keyStoreUrl, String keyStorePassword, String keyPassword, URL trustStoreUrl,
			String trustStorePassword) {
		if (keyStoreUrl == null) {
			throw new IllegalArgumentException("keyStoreUrl is null");
		}
		if (trustStoreUrl == null) {
			throw new IllegalArgumentException("trustStoreUrl is null");
		}
		KeyStore keyStore = loadKeyStore(keyStoreUrl, keyStorePassword);
		KeyStore trustStore = loadKeyStore(trustStoreUrl, trustStorePassword);
		createSSLContext(keyStore, keyPassword, trustStore);
	}

	/**
	 * keystore is same as truststore
	 */
	public JksSslSocketFactory(URL storeUrl, String storePassword, String keyPassword) {
		if (storeUrl == null) {
			throw new IllegalArgumentException("storeUrl is null");
		}
		KeyStore keyStore = loadKeyStore(storeUrl, storePassword);
		KeyStore trustStore = keyStore;
		createSSLContext(keyStore, keyPassword, trustStore);
	}

	/**
	 * truststore only
	 */
	public JksSslSocketFactory(URL storeUrl, String storePassword) {
		if (storeUrl == null) {
			throw new IllegalArgumentException("storeUrl is null");
		}
		KeyStore trustStore = loadKeyStore(storeUrl, storePassword);
		createSSLContext(null, null, trustStore);
	}

	/**
	 * keyStore and trustStore (already loaded)
	 */
	public JksSslSocketFactory(KeyStore keyStore, String keyPassword, KeyStore trustStore) {
		if (keyStore == null) {
			throw new IllegalArgumentException("keyStore is null");
		}
		if (trustStore == null) {
			throw new IllegalArgumentException("trustStore is null");
		}
		createSSLContext(keyStore, keyPassword, trustStore);
	}

	/**
	 * trustStore only (already loaded)
	 */
	public JksSslSocketFactory(KeyStore trustStore) {
		if (trustStore == null) {
			throw new IllegalArgumentException("trustStore is null");
		}
		createSSLContext(null, null, trustStore);
	}

	/**
	 * keystore is same as truststore (already loaded)
	 */
	public JksSslSocketFactory(KeyStore keyStore, String keyPassword) {
		if (keyStore == null) {
			throw new IllegalArgumentException("keyStore is null");
		}
		createSSLContext(keyStore, keyPassword, keyStore);
	}

	protected final void createSSLContext(KeyStore keyStore, String keyPassword, KeyStore trustStore) {
		try {
			KeyManager[] keymanagers = null;
			if (keyStore != null) {
				if (log.isDebugEnabled()) {
					logKeyStoreContent(keyStore, true);
				}
				keymanagers = createKeyManagers(keyStore, keyPassword);
			}

			TrustManager[] trustmanagers = null;
			if (trustStore != null) {
				if (log.isDebugEnabled()) {
					logKeyStoreContent(trustStore, false);
				}
				trustmanagers = createTrustManagers(trustStore);
			}

			sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(keymanagers, trustmanagers, new SecureRandom());
		} catch (Exception x) {
			throw new IllegalArgumentException("Failed to create SSLContext", x);
		}
	}

	private KeyManager[] createKeyManagers(final KeyStore keystore, final String password) throws KeyStoreException,
	NoSuchAlgorithmException, UnrecoverableKeyException {
		if (keystore == null) {
			throw new IllegalArgumentException("Keystore may not be null");
		}
		log.debug("Initializing KeyManagerFactory with algorithm " + KeyManagerFactory.getDefaultAlgorithm());
		KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmfactory.init(keystore, password != null ? password.toCharArray() : null);
		return kmfactory.getKeyManagers();
	}

	private TrustManager[] createTrustManagers(final KeyStore keystore) throws KeyStoreException,
	NoSuchAlgorithmException {
		if (keystore == null) {
			throw new IllegalArgumentException("Keystore may not be null");
		}
		log.debug("Initializing TrustManagerFactory with algorithm " + TrustManagerFactory.getDefaultAlgorithm());
		TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmfactory.init(keystore);
		TrustManager[] trustmanagers = tmfactory.getTrustManagers();
		for (int i = 0; i < trustmanagers.length; i++) {
			if (trustmanagers[i] instanceof X509TrustManager) {
				log.debug("Adding X509TrustManager " + trustmanagers[i]);
				trustmanagers[i] = new X509TrustManagerWrapper((X509TrustManager) trustmanagers[i]);
			}
		}
		return trustmanagers;
	}

	public static KeyStore loadKeyStore(URL location, String password) {
		try {
			KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream storeStream = location.openStream();
			try {
				store.load(storeStream, password != null ? password.toCharArray() : null);
			} finally {
				storeStream.close();
			}
			return store;
		} catch (Exception x) {
			throw new IllegalArgumentException("Cannot initialize keyStore " + location, x);
		}
	}

	private void logCertInfo(X509Certificate cert) {
		log.trace("  Subject DN: " + cert.getSubjectDN());
		log.trace("  Signature Algorithm: " + cert.getSigAlgName());
		log.trace("  Valid from: " + cert.getNotBefore());
		log.trace("  Valid until: " + cert.getNotAfter());
		log.trace("  Issuer: " + cert.getIssuerDN());
	}

	private void logKeyStoreContent(KeyStore keyStore, boolean keys) throws KeyStoreException {
		if (keys) {
			Enumeration<String> aliases = keyStore.aliases();
			while (aliases.hasMoreElements()) {
				String alias = aliases.nextElement();
				Certificate[] certs = keyStore.getCertificateChain(alias);
				if (certs != null) {
					if (log.isTraceEnabled()) {
						log.debug("Certificate chain '" + alias + "':");
						for (int c = 0; c < certs.length; c++) {
							if (certs[c] instanceof X509Certificate) {
								X509Certificate cert = (X509Certificate) certs[c];
								log.trace(" Certificate " + (c + 1) + ":");
								logCertInfo(cert);
							}
						}
					} else if (log.isDebugEnabled()) {
						log.debug("Certificate chain '" + alias + "' Subject DN: " + ((X509Certificate) certs[0]).getSubjectDN());
					}
				}
			}
		} else {
			Enumeration<String> aliases = keyStore.aliases();
			while (aliases.hasMoreElements()) {
				String alias = aliases.nextElement();
				if (log.isTraceEnabled()) {
					log.debug("Trusted certificate '" + alias + "':");
					Certificate trustedcert = keyStore.getCertificate(alias);
					if (trustedcert instanceof X509Certificate) {
						X509Certificate cert = (X509Certificate) trustedcert;
						logCertInfo(cert);
					}
				} else if (log.isDebugEnabled()) {
					log.debug("Trusted certificate '" + alias + "' Subject DN: "
							+ ((X509Certificate) keyStore.getCertificate(alias)).getSubjectDN());
				}
			}
		}
	}

	/*
	 * Konec factory blbinek
	 */

	public SSLContext getSSLContext() {
		return sslcontext;
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) throws IOException,
	UnknownHostException {
		return getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		return getSSLContext().getSocketFactory().createSocket(host, port);
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
	UnknownHostException {
		return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return sslcontext.getSocketFactory().getDefaultCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return sslcontext.getSocketFactory().getSupportedCipherSuites();
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		return sslcontext.getSocketFactory().createSocket(host, port);
	}

	@Override
	public Socket createSocket(InetAddress host, int port, InetAddress inetaddress1, int j) throws IOException {
		return sslcontext.getSocketFactory().createSocket(host, port, inetaddress1, j);
	}

	class X509TrustManagerWrapper implements X509TrustManager {
		private X509TrustManager defaultTrustManager = null;

		public X509TrustManagerWrapper(final X509TrustManager defaultTrustManager) {
			if (defaultTrustManager == null) {
				throw new IllegalArgumentException("Trust manager may not be null");
			}
			this.defaultTrustManager = defaultTrustManager;
		}

		/**
		 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[],String authType)
		 */
		public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
			if (log.isDebugEnabled() && certificates != null) {
				for (int c = 0; c < certificates.length; c++) {
					X509Certificate cert = certificates[c];
					if (log.isTraceEnabled()) {
						log.debug("Check client cert" + (c + 1) + ":");
						logCertInfo(cert);
					} else if (log.isDebugEnabled()) {
						log.debug("Check client cert" + (c + 1) + " Subject DN: " + cert.getSubjectDN());
					}
				}
			}
			defaultTrustManager.checkClientTrusted(certificates, authType);
		}

		/**
		 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[],String authType)
		 */
		public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
			if (log.isInfoEnabled() && certificates != null) {
				for (int c = 0; c < certificates.length; c++) {
					X509Certificate cert = certificates[c];
					if (log.isTraceEnabled()) {
						log.debug("Check server cert" + (c + 1) + ":");
						logCertInfo(cert);
					} else if (log.isDebugEnabled()) {
						log.debug("Check server cert" + (c + 1) + " Subject DN: " + cert.getSubjectDN());
					}
				}
			}
			defaultTrustManager.checkServerTrusted(certificates, authType);
		}

		/**
		 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
		 */
		public X509Certificate[] getAcceptedIssuers() {
			return defaultTrustManager.getAcceptedIssuers();
		}
	}

}
