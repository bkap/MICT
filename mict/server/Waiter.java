package mict.server;

import javax.net.ssl.*;
import java.io.*;

public class Waiter extends Thread {
	public Waiter(SSLSocket patron, Server parent) throws IOException {
		this.patron = patron;
		this.parent = parent;
		out = new PrintWriter(new OutputStreamWriter(patron.getOutputStream()));
		in = new BufferedReader(new InputStreamReader(patron.getInputStream()));
		setDaemon(true);
	}

	private SSLSocket patron;
	private Server parent;
	private PrintWriter out;
	private BufferedReader in;

	public void run() {
		// DO WORK, SON
		// authenticate the user
		while(true) { // general recieve loop
			// determine type of message
			// parse it!
		}
		// cleanup
	}

	protected void close() {
		parent.clients.remove(this);
		try {
			patron.close();
			in.close();
			out.close();
		} catch(IOException e) {
			// Nothing to see here, move along.
		}
	}
}
