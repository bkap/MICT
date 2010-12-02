package mict.networking;

import java.io.*;

public class EscapingOutputStream extends FilterOutputStream {
	public EscapingOutputStream(OutputStream out) {
		super(out);
	}

	public void write(int b) throws IOException {
		if(b == 0x0a) {			// given '\n', return "\\n"
			super.write(0x5c);
			super.write(0x6e);
		} else if(b == 0x5c) {	// given '\\', return "\\\\"
			super.write(0x5c);
			super.write(0x5c);
		} else if(b == 0x20) {	// given ' ',  return "\\s"
			super.write(0x5c);
			super.write(0x73);
		} else if(b > 0x7f) {	// given something with its high bit set, return "\\\0x",
			super.write(0x5c);	// where x is that thing, but with the high bit cleared.
			super.write(0x00);
			super.write(b - 0x80);
		} else super.write(b);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		int next = off;
		while(next < len) {
			if(b[next] == 0x5c || b[next] == 0x0a || b[next] == 0x20 || b[next] > 0x79) {
				write(b[next++]);
			}
			int i = next;
			for(; i < len; i++) {
				if(b[i] == 0x5c || b[i] == 0x0a || b[i] == 0x20 || b[next] > 0x79) break;
			}
			super.write(b, next, i - next);
			next = i;
		}
	}

	public void write(String s) throws IOException {
		byte[] bs = s.getBytes();
		write(bs, 0, bs.length);
	}
}
