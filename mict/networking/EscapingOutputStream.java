package mict.networking;

public class EscapingOutputStream extends FilterOutputStream {
	public EscapingOutputStream(Outputstream out) {
		super(out);
	}

	public void write(int b) throws IOException {
		if(b == '\n') {
			super.write('\\');
			super.write('n');
		} else if(b == '\\') {
			super.write('\\');
			super.write('\\');
		} else super.write(b);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		int next = off;
		while(next < len) {
			if(b[next] == '\\' || b[next] == '\n') {
				write(b[next++]);
			}
			int i = next;
			for(; i < len; i++) {
				if(b[i] == '\\' || b[i] == '\n') break;
			}
			super.write(b, next, i - next);
			next = i;
		}
	}
}
