/**
 * 
 */
package com.anthavio.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vanek
 *
 * java.util.Properties is very unsuitable for editing
 */
public class PropertiesUtil {

	public static List<PropertyLine> load(InputStream stream) throws IOException {
		if (stream == null) {
			throw new IllegalArgumentException("Null InputStream");
		}
		return load(new InputStreamReader(stream, Charset.forName("utf-8")));
	}

	public static List<PropertyLine> load(File file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("Null File");
		}
		return load(new FileReader(file));
	}

	public static List<PropertyLine> load(Reader reader) throws IOException {
		List<PropertyLine> properties = new ArrayList<PropertyLine>();
		BufferedReader br = new BufferedReader(reader);
		try {
			String line;
			int lineNumber = 0;
			while ((line = br.readLine()) != null) {
				lineNumber++;
				PropertyLine property = new PropertyLine(lineNumber, line);
				properties.add(property);
			}
		} finally {
			br.close();
		}
		return properties;
	}

	public static void save(List<PropertyLine> lines, File file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("Null File");
		}
		save(lines, new FileWriter(file));
	}

	public static void save(List<PropertyLine> lines, Writer writer) throws IOException {
		BufferedWriter bw = new BufferedWriter(writer);
		try {
			for (PropertyLine line : lines) {
				bw.write(line.toFileLine());
				bw.write("\n");
			}
		} finally {
			bw.close();
		}

	}

	public static class PropertyLine {

		private int lineNumber;

		private String name;

		private String value;

		private boolean comment;

		public PropertyLine() {
		}

		public PropertyLine(int lineNumber, String line) {
			this.lineNumber = lineNumber;
			if (line == null) {
				throw new IllegalArgumentException("null line argument");
			}
			this.comment = line.startsWith("#");

			int eqIdx = line.indexOf("=");
			if (eqIdx != -1) {
				if (comment) {
					this.name = line.substring(1, eqIdx).trim();
				} else {
					this.name = line.substring(0, eqIdx).trim();
				}
				this.value = line.substring(eqIdx + 1).trim();

			} else {
				//line without '=' character
				if (comment) {
					this.name = line.substring(1);
				} else {
					this.name = line;
				}
			}
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public int getLineNumber() {
			return lineNumber;
		}

		public void setLineNumber(int lineNumber) {
			this.lineNumber = lineNumber;
		}

		public boolean isComment() {
			return comment;
		}

		public void setComment(boolean comment) {
			this.comment = comment;
		}

		public boolean isProperty() {
			return this.name != null && this.name.indexOf(' ') == -1 && this.value != null;
		}

		public String toFileLine() {
			StringBuilder sb = new StringBuilder();
			if (comment) {
				sb.append("#");
			}
			sb.append(name);
			if (value != null) {
				sb.append(" = ");
				sb.append(value);
			}
			return sb.toString();
		}

		@Override
		public String toString() {
			return this.toFileLine();
		}
	}

}
