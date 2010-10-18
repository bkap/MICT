package mict.networking;

public class EscapingInputStream extends FilterInputStream {
	public EscapingInputStream(InputStream in) {
		super(in);
	}

	public int read() throws IOException {
		int read = super.read();
		if(read == '\\') {
			read = super.read();
			if(read == 'n') return '\n';
			else if(read == '\\') return '\\';
		}
		return read;
	}

	public int read(byte[] b, int off, int len) throws IOException {
		int next = off;
		while(next < len) {
			int read = read();
			if(read < 0) break;
			b[next++] = read;
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
