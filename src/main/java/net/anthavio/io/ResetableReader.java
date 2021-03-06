/**
 * 
 */
package net.anthavio.io;

import java.io.IOException;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vanek
 * 
 * Class of NOT thread safe
 */
public class ResetableReader extends Reader {

	private static final Logger log = LoggerFactory.getLogger(ResetableReader.class);

	private Reader parent;

	private char[] buff;

	/**
	 * index to last buffer element
	 */
	private int bpos = 0;

	/**
	 * reset marker & buffer reading index
	 */
	private int rpos = -1;

	/**
	 * when overflow happens, reset is not possible anymore
	 */
	private boolean overflow = false;

	/**
	 * reset (second) read goes immediately to parent reader when buffer is fully read
	 * This improves performance, but makes second reset and thist read impossible  
	 */
	private boolean eagerRead = false; //eagerMode

	public ResetableReader(Reader parent, int size) {
		this.parent = parent;
		this.buff = new char[size];
	}

	@Override
	public void reset() throws IOException {
		if (overflow) {
			throw new IllegalStateException("Cannot reset. Maximal character count " + buff.length + " reached");
		}
		log.debug("reset " + new String(buff, 0, bpos));
		rpos = 0;
	}

	@Override
	public int read() throws IOException {
		if (rpos == -1) { //from parent
			int c = parent.read();
			if (c > 0) {
				if (bpos < buff.length) {
					buff[bpos++] = (char) c;
				} else {
					if (!overflow) {
						overflow = true;
						log.debug("Reset buffer maxed to " + bpos + ". Cannot add character " + (char) c);
					}
				}
			}
			return c;
		} else { //from buffer
			if (rpos == bpos) {
				rpos = -1; //end buffer read
				return buff[bpos];
			} else {
				return buff[rpos++];
			}
		}
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		//System.out.println("read " + len);
		int read;
		if (rpos == -1) { //read parent & fill buffer
			if (!overflow && (bpos + len + 1) > buff.length) {
				// We want to fill buffer fully before overflow
				// When required len is too big, decrease it to match remaining space in buffer
				int blen = buff.length - bpos - 1;
				if (blen != 0) { //this happens after end of non eager buffer read
					len = blen;
				}
			}
			read = parent.read(cbuf, off, len);
			if (read > 0) {
				if (bpos + read < buff.length) {
					System.arraycopy(cbuf, off, buff, bpos, read);
					bpos += read;
				} else {
					if (!overflow) {
						overflow = true;
						log.debug("Reset buffer size " + bpos + ". Cannot add " + read + " characters");
					}
				}
			}
		} else { //read buffer
			int blen = bpos - rpos;
			if (len < blen) {
				System.arraycopy(buff, rpos, cbuf, off, len);
				rpos += len;
				read = len;
			} else { //not enough adat in buffer
				if (eagerRead) {
					//continue reading from parent immediately
					System.arraycopy(buff, rpos, cbuf, off, blen);
					int pread = parent.read(cbuf, off + blen, len - blen);
					overflow = true;
					rpos = -1;
					read = blen + pread;
				} else {
					//return only rest of the buffer
					System.arraycopy(buff, rpos, cbuf, off, blen);
					rpos = -1; //end buffer read
					read = blen;
				}
			}
		}

		return read;
	}

	@Override
	public void close() throws IOException {
		parent.close();
	}

	@Override
	public int read(char[] c) throws IOException {
		return read(c, 0, c.length);
	}

}