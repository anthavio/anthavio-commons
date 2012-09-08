/**
 * 
 */
package cz.komix.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author vanek
 *
 */
public class ResourceUtil {

	public static String which(Class<?> clazz) {
		return which(clazz.getName(), clazz.getClassLoader());
	}

	public static String which(String classname, ClassLoader loader) {

		String classnameAsResource = classname.replace('.', '/') + ".class";

		if (loader == null) {
			loader = ClassLoader.getSystemClassLoader();
		}

		URL it = loader.getResource(classnameAsResource);
		if (it != null) {
			return it.toString();
		} else {
			return null;
		}
	}

	public interface NameFilter {

		boolean matches(String path, String name);
	}

	public static class RegexNameFilter implements NameFilter {

		private Matcher matcher;

		public RegexNameFilter(String regex) {
			Pattern pattern = Pattern.compile(regex);
			matcher = pattern.matcher("");
		}

		public boolean matches(String path, String name) {
			matcher.reset(name);
			return matcher.matches();
		}
	}

	public static interface ResourceProcessor<T> {
		public T process(URL url);
	}

	public static class ClassLoadProcessor implements ResourceProcessor<Class<?>> {

		private int idxPrefix;

		private ClassLoader classLoader;

		public ClassLoadProcessor(String packageName) {
			this(packageName, Thread.currentThread().getContextClassLoader());
		}

		public ClassLoadProcessor(String packageName, ClassLoader classLoader) {
			if (classLoader == null) {
				throw new IllegalArgumentException("ClassLoader is null");
			}
			URL baseUrl = classLoader.getResource(packageName.replace('.', '/'));
			if (baseUrl == null) {
				throw new IllegalArgumentException("Package not found " + packageName);
			}
			if (isJarURL(baseUrl)) {
				idxPrefix = baseUrl.getFile().indexOf("!/") + 2;
			} else {
				idxPrefix = baseUrl.getFile().length() - packageName.length();
			}
			this.classLoader = classLoader;
		}

		public Class<?> process(URL url) {
			String className = url.getFile().substring(idxPrefix, url.getFile().length() - 6).replace('/', '.');
			try {
				return classLoader.loadClass(className);
			} catch (ClassNotFoundException cnfx) {
				throw new IllegalArgumentException(cnfx);
			}
		}
	}

	public static List<Class<?>> listClasses(Package packg) {
		return listClasses(packg.getName());
	}

	public static List<Class<?>> listClasses(String packageName) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL baseUrl = classLoader.getResource(packageName.replace('.', '/'));
		if (baseUrl == null) {
			throw new IllegalArgumentException("Package not found " + packageName);
		}

		List<Class<?>> retval = list(baseUrl, new ClassLoadProcessor(packageName, classLoader));
		return retval;
	}

	public static List<Class<?>> listClasses(String packageName, ClassLoadProcessor processor) {
		if (processor == null) {
			throw new IllegalArgumentException("ClassLoadResourceProcessor is null");
		}
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL baseUrl = classLoader.getResource(packageName.replace('.', '/'));
		if (baseUrl == null) {
			throw new IllegalArgumentException("Package not found " + packageName);
		}

		List<Class<?>> retval = list(baseUrl, processor);
		return retval;
	}

	public static List<URL> list(URL url) {
		return list(url, new RegexNameFilter(".*"));
	}

	public static List<URL> list(URL url, String regex) {
		return list(url, new RegexNameFilter(regex));
	}

	public static <T> List<T> list(URL baseUrl, ResourceProcessor<T> processor) {
		if (baseUrl == null) {
			throw new IllegalArgumentException("URL is null");
		}
		if (processor == null) {
			throw new IllegalArgumentException("ResourceProcessor is null");
		}
		List<T> ret = new ArrayList<T>();
		try {
			if (isJarURL(baseUrl)) {
				//URL - jar:file:/C:/tmp/spring-core-3.0.5.RELEASE.jar!/org/springframework/util/comparator
				String strBaseUrl = baseUrl.toString();
				int idxJarEnd = strBaseUrl.indexOf("!/");
				String jarContentBase = strBaseUrl.substring(idxJarEnd + 2);
				String strJarUrl = strBaseUrl.substring(0, idxJarEnd + 2);
				JarInputStream jis = null;
				try {
					URL jarFileUrl = extractJarFileURL(baseUrl);
					jis = new JarInputStream(jarFileUrl.openStream());
					JarEntry jarEntry;
					while ((jarEntry = jis.getNextJarEntry()) != null) {
						String jarEntryName = jarEntry.getName();
						//We are traversing whole jar file content, so have to skip everything that it not inside jarContentBase
						if (jarEntryName.startsWith(jarContentBase) && jarEntry.isDirectory() == false) {
							//int idxLastSlash = jarEntryName.lastIndexOf('/');
							//String jarEntryPath = jarEntryName.substring(0, idxLastSlash);
							//String simpleName = jarEntryName.substring(idxLastSlash + 1); //without slash
							T result = processor.process(new URL(strJarUrl + jarEntryName));
							if (result != null) {
								ret.add(result);
							}
						}
					}

				} finally {
					if (jis != null) {
						try {
							jis.close();
						} catch (IOException iox) {
							//ignore this one to not forget potential existing exception
						}
					}
				}
			} else {
				File fileDir = getFile(baseUrl);
				recurse(fileDir, ret, processor);
			}
		} catch (IOException iox) {
			throw new IllegalArgumentException("Failed to list " + baseUrl, iox);
		}
		return ret;
	}

	private static <T> void recurse(File file, List<T> ret, ResourceProcessor<T> processor) {
		if (file.isDirectory()) {
			File[] list = file.listFiles();
			for (File filex : list) {
				recurse(filex, ret, processor);
			}
		} else {
			try {
				T result = processor.process(file.toURI().toURL());
				if (result != null) {
					ret.add(result);
				}
			} catch (MalformedURLException mux) {
				throw new IllegalArgumentException("File can't be converted to URL");
			}
		}
	}

	public static List<URL> list(URL baseUrl, NameFilter filter) {
		if (baseUrl == null) {
			throw new IllegalArgumentException("URL is null");
		}
		if (filter == null) {
			throw new IllegalArgumentException("NameFilter is null");
		}
		List<URL> ret = new ArrayList<URL>();
		try {
			if (isJarURL(baseUrl)) {
				//URL - jar:file:/C:/tmp/spring-core-3.0.5.RELEASE.jar!/org/springframework/util/comparator
				String jarContentPrefix = baseUrl.getFile().substring(baseUrl.getFile().indexOf("!/") + 2);
				String strBaseUrl = baseUrl.toString();
				if (strBaseUrl.endsWith("/") == false) {
					strBaseUrl += "/";
				}
				JarInputStream jis = null;
				try {
					URL jarFileUrl = extractJarFileURL(baseUrl);
					jis = new JarInputStream(jarFileUrl.openStream());
					JarEntry jarEntry;
					while ((jarEntry = jis.getNextJarEntry()) != null) {
						String jarEntryName = jarEntry.getName();
						//We are traversing whole jar file content, so have to skip everything that it not inside jarContentPrefix
						if (jarEntryName.startsWith(jarContentPrefix) && jarEntry.isDirectory() == false) {
							int idxLastSlash = jarEntryName.lastIndexOf('/');
							String jarEntryPath = jarEntryName.substring(0, idxLastSlash);
							String simpleName = jarEntryName.substring(idxLastSlash + 1); //without slash
							if (filter.matches(jarEntryPath, simpleName)) {
								ret.add(new URL(strBaseUrl + simpleName));
							}
						}
					}

				} finally {
					if (jis != null) {
						try {
							jis.close();
						} catch (IOException iox) {
							//ignore this one to not forget potential existing exception
						}
					}
				}
			} else {
				File fileDir = getFile(baseUrl);
				recurse(fileDir, ret, filter);
			}
		} catch (IOException iox) {
			throw new IllegalArgumentException("Failed to list " + baseUrl, iox);
		}
		return ret;
	}

	private static void recurse(File file, List<URL> ret, NameFilter filter) throws MalformedURLException {
		if (file.isDirectory()) {
			File[] list = file.listFiles();
			for (File filex : list) {
				recurse(filex, ret, filter);
			}
		} else {
			if (filter.matches(file.getParent(), file.getName())) {
				ret.add(file.toURI().toURL());
			}
		}
	}

	//next section is stolen from Spring ResourceUtils & StringUtils

	/** URL prefix for loading from the file system: "file:" */
	public static final String FILE_URL_PREFIX = "file:";

	/** URL protocol for a file in the file system: "file" */
	public static final String URL_PROTOCOL_FILE = "file";

	/** URL protocol for an entry from a jar file: "jar" */
	public static final String URL_PROTOCOL_JAR = "jar";

	/** URL protocol for an entry from a zip file: "zip" */
	public static final String URL_PROTOCOL_ZIP = "zip";

	/** URL protocol for an entry from a JBoss jar file: "vfszip" */
	public static final String URL_PROTOCOL_VFSZIP = "vfszip";

	/** URL protocol for a JBoss VFS resource: "vfs" */
	public static final String URL_PROTOCOL_VFS = "vfs";

	/** URL protocol for an entry from a WebSphere jar file: "wsjar" */
	public static final String URL_PROTOCOL_WSJAR = "wsjar";

	/** URL protocol for an entry from an OC4J jar file: "code-source" */
	public static final String URL_PROTOCOL_CODE_SOURCE = "code-source";

	/** Separator between JAR URL and file path within the JAR */
	public static final String JAR_URL_SEPARATOR = "!/";

	/**
	 * Determine whether the given URL points to a resource in a jar file,
	 * that is, has protocol "jar", "zip", "wsjar" or "code-source".
	 * <p>"zip" and "wsjar" are used by BEA WebLogic Server and IBM WebSphere, respectively,
	 * but can be treated like jar files. The same applies to "code-source" URLs on Oracle
	 * OC4J, provided that the path contains a jar separator.
	 * @param url the URL to check
	 * @return whether the URL has been identified as a JAR URL
	 */
	public static boolean isJarURL(URL url) {
		String protocol = url.getProtocol();
		return (URL_PROTOCOL_JAR.equals(protocol) || URL_PROTOCOL_ZIP.equals(protocol)
				|| URL_PROTOCOL_WSJAR.equals(protocol) || (URL_PROTOCOL_CODE_SOURCE.equals(protocol) && url.getPath().contains(
						JAR_URL_SEPARATOR)));
	}

	/**
	 * Extract the URL for the actual jar file from the given URL
	 * (which may point to a resource in a jar file or to a jar file itself).
	 * @param jarUrl the original URL
	 * @return the URL for the actual jar file
	 * @throws MalformedURLException if no valid jar file URL could be extracted
	 */
	public static URL extractJarFileURL(URL jarUrl) throws MalformedURLException {
		String urlFile = jarUrl.getFile();
		int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
		if (separatorIndex != -1) {
			String jarFile = urlFile.substring(0, separatorIndex);
			try {
				return new URL(jarFile);
			} catch (MalformedURLException ex) {
				// Probably no protocol in original jar URL, like "jar:C:/mypath/myjar.jar".
				// This usually indicates that the jar file resides in the file system.
				if (!jarFile.startsWith("/")) {
					jarFile = "/" + jarFile;
				}
				return new URL(FILE_URL_PREFIX + jarFile);
			}
		} else {
			return jarUrl;
		}
	}

	/**
	 * Resolve the given resource URL to a <code>java.io.File</code>,
	 * i.e. to a file in the file system.
	 * @param resourceUrl the resource URL to resolve
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to
	 * a file in the file system
	 */
	public static File getFile(URL resourceUrl) throws FileNotFoundException {
		return getFile(resourceUrl, "URL");
	}

	/**
	 * Resolve the given resource URL to a <code>java.io.File</code>,
	 * i.e. to a file in the file system.
	 * @param resourceUrl the resource URL to resolve
	 * @param description a description of the original resource that
	 * the URL was created for (for example, a class path location)
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to
	 * a file in the file system
	 */
	public static File getFile(URL resourceUrl, String description) throws FileNotFoundException {
		if (resourceUrl == null) {
			throw new IllegalArgumentException("Resource URL must not be null");
		}
		if (!URL_PROTOCOL_FILE.equals(resourceUrl.getProtocol())) {
			throw new FileNotFoundException(description + " cannot be resolved to absolute file path "
					+ "because it does not reside in the file system: " + resourceUrl);
		}
		try {
			return new File(toURI(resourceUrl).getSchemeSpecificPart());
		} catch (URISyntaxException ex) {
			// Fallback for URLs that are not valid URIs (should hardly ever happen).
			return new File(resourceUrl.getFile());
		}
	}

	/**
	 * Create a URI instance for the given URL,
	 * replacing spaces with "%20" quotes first.
	 * <p>Furthermore, this method works on JDK 1.4 as well,
	 * in contrast to the <code>URL.toURI()</code> method.
	 * @param url the URL to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException if the URL wasn't a valid URI
	 * @see java.net.URL#toURI()
	 */
	public static URI toURI(URL url) throws URISyntaxException {
		return toURI(url.toString());
	}

	/**
	 * Create a URI instance for the given location String,
	 * replacing spaces with "%20" quotes first.
	 * @param location the location String to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException if the location wasn't a valid URI
	 */
	public static URI toURI(String location) throws URISyntaxException {
		return new URI(replace(location, " ", "%20"));
	}

	/**
	 * Replace all occurences of a substring within a string with
	 * another string.
	 * @param inString String to examine
	 * @param oldPattern String to replace
	 * @param newPattern String to insert
	 * @return a String with the replacements
	 */
	public static String replace(String inString, String oldPattern, String newPattern) {
		if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
			return inString;
		}
		StringBuilder sb = new StringBuilder();
		int pos = 0; // our position in the old string
		int index = inString.indexOf(oldPattern);
		// the index of an occurrence we've found, or -1
		int patLen = oldPattern.length();
		while (index >= 0) {
			sb.append(inString.substring(pos, index));
			sb.append(newPattern);
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}
		sb.append(inString.substring(pos));
		// remember to append any characters to the right of a match
		return sb.toString();
	}

	/**
	 * Check that the given CharSequence is neither <code>null</code> nor of length 0.
	 * Note: Will return <code>true</code> for a CharSequence that purely consists of whitespace.
	 * <p><pre>
	 * StringUtils.hasLength(null) = false
	 * StringUtils.hasLength("") = false
	 * StringUtils.hasLength(" ") = true
	 * StringUtils.hasLength("Hello") = true
	 * </pre>
	 * @param str the CharSequence to check (may be <code>null</code>)
	 * @return <code>true</code> if the CharSequence is not null and has length
	 * @see #hasText(String)
	 */
	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}

	/**
	 * Check that the given String is neither <code>null</code> nor of length 0.
	 * Note: Will return <code>true</code> for a String that purely consists of whitespace.
	 * @param str the String to check (may be <code>null</code>)
	 * @return <code>true</code> if the String is not null and has length
	 * @see #hasLength(CharSequence)
	 */
	public static boolean hasLength(String str) {
		return hasLength((CharSequence) str);
	}
}
