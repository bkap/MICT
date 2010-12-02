package mict.networking;

import java.io.*;

public class EscapingInputStream extends FilterInputStream {
	public static String read(String input) {
		String result = "";
		byte[] bs = input.getBytes();
		for(int i = 0; i < bs.length;) {
			byte read = bs[i++];
			if(read == '\\') {
				read = bs[i++];
				if(read == 0x6e) result += '\n';
				if(read == 0x73) result += ' ';
				if(read == 0x5c) result += '\\';
				if(read == 0x00) result += (char)(read + 0x80);
			} else result += (char)read;
		}
		return result;
	}

	public EscapingInputStream(InputStream in) {
		super(in);
	}

	public int read() throws IOException {
		int read = super.read();
		if(read == '\\') {
			read = super.read();
			if(read == 0x6e) return 0x0a; // 'n'  for '\n'
			if(read == 0x73) return 0x20; // 's'  for ' '
			if(read == 0x5c) return 0x5c; // '\\' for '\\'
			if(read == 0x00) return super.read() + 0x80;
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
