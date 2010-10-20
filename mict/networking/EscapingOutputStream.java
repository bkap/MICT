package mict.networking;

import java.io.*;

public class EscapingOutputStream extends FilterOutputStream {
	public EscapingOutputStream(OutputStream out) {
		super(out);
	}

	public void write(int b) throws IOException {
		if(b == '\n') {
			super.write('\\');
			super.write('n');
		} else if(b == '\\') {
			super.write('\\');
			super.write('\\');
		} else if(b == ' ') {
			super.write('\\');
			super.write('s');
		} else super.write(b);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		int next = off;
		while(next < len) {
			if(b[next] == '\\' || b[next] == '\n' || b[next] == ' ') {
				write(b[next++]);
			}
			int i = next;
			for(; i < len; i++) {
				if(b[i] == '\\' || b[i] == '\n' || b[i] == ' ') break;
			}
			super.write(b, next, i - next);
			next = i;
		}
	}
}
