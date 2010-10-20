package mict.networking;

import java.io.*;

public class EscapingInputStream extends FilterInputStream {
	public EscapingInputStream(InputStream in) {
		super(in);
	}

	public int read() throws IOException {
		int read = super.read();
		if(read == '\\') {
			read = super.read();
			if(read == 'n') return '\n';
			if(read == 's') return ' ';
			if(read == '\\') return '\\';
		}
		return read;
	}

	public int read(byte[] b, int off, int len) throws IOException {
		int next = off;
		while(next < len) {
			int read = read();
			if(read < 0) break;
			b[next++] = (byte)read;
		}
		if(next == off) return -1;
		return next - off;
	}

	public long skip(long n) throws IOException {
		long skipped = 0L;
		while(skipped < n) {
			int read = read();
			if(read == -1) break;
			skipped++;
		}
		return skipped;
	}

	public boolean markSupported() {
		return false;
	}
}
