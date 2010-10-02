package mict.server;

import javax.net.ssl.*;
import java.io.*;

public class Waiter extends Thread {
	public Waiter(SSLSocket patron, Server server) throws IOException {
		this.patron = patron;
		this.server = server;
		out = new PrintWriter(new OutputStreamWriter(patron.getOutputStream()));
		in = new BufferedReader(new InputStreamReader(patron.getInputStream()));
		setDaemon(true);
	}

	private SSLSocket patron;
	private Server server;
	private PrintWriter out;
	private BufferedReader in;

	public void run() {
		// DO WORK, SON
	}

	public void close() {
		try {
			patron.close();
			in.close();
			out.close();
		} catch(IOexception e) {
			// Nothing to see here, move along.
		}
	}
}
