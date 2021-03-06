/**
 * 
 */
package net.anthavio.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;

/**
 * @author vanek
 * 
 */
public class FileUtils {

	/**
	 * Java does not support BOM in utf stream. This does.
	 */
	public static String readFile(File file) throws IOException {
		if (!file.exists()) {
			throw new IllegalArgumentException("File does not exist "
					+ file.getAbsolutePath());
		}
		if (!file.isFile()) {
			throw new IllegalArgumentException("Not a file "
					+ file.getAbsolutePath());
		}
		if (!file.canRead()) {
			throw new IllegalArgumentException("File is not readable "
					+ file.getAbsolutePath());
		}
		final int BOMS_SIZE = 4;

		PushbackInputStream pushBackStream = null;
		StringBuilder stringBuilder;
		try {
			pushBackStream = new PushbackInputStream(new FileInputStream(file),
					BOMS_SIZE);
			byte[] bom = new byte[BOMS_SIZE];
			pushBackStream.read(bom);

			String encoding = "UTF-8";

			if (bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB
					&& bom[2] == (byte) 0xBF) {
				encoding = "UTF-8";
				pushBackStream.unread(bom, 3, 1);
			} else if (bom[0] == (byte) 0xFF && bom[1] == (byte) 0xFE) {
				encoding = "UTF-16";
				pushBackStream.unread(bom);
			} else if (bom[0] == (byte) 0xFE && bom[1] == (byte) 0xFF) {
				encoding = "UTF-16";
				pushBackStream.unread(bom);
			} else if (bom[0] == (byte) 0x00 && bom[1] == (byte) 0x00
					&& bom[2] == (byte) 0xFE && bom[3] == (byte) 0xFF) {
				encoding = "UTF-32BE";
				pushBackStream.unread(bom);
			} else if (bom[0] == (byte) 0xFF && bom[1] == (byte) 0xFE
					&& bom[2] == (byte) 0x00 && bom[3] == (byte) 0x00) {
				encoding = "UTF-32LE";
				pushBackStream.unread(bom);
			} else {
				pushBackStream.unread(bom);
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					pushBackStream, encoding));
			String line = null;
			stringBuilder = new StringBuilder((int) file.length());
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append('\n');
			}
		} finally {
			if (pushBackStream != null)
				pushBackStream.close();
		}

		return stringBuilder.toString();
	}

}
