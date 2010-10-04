package mict.client;

import javax.net.ssl.*;
import java.io.*;

public class ClientConnection extends Thread {
	private static int DEFAULT_PORT = 56234;

	public ClientConnection(String server, int port, Client parent) {
		this.controller = controller;
		this.serverport = port;
		this.parent = parent;
		try {
			SSLSocketFactory sockfactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
			waiter = (SSLSocket)sockfactory.createSocket(server, port);
			out = new PrintWriter(new OutputStreamWriter(waiter.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(waiter.getInputStream()));
		} catch(IOException e) {
			System.err.println("Could not open connection to server: " + e.getMessage());
		}
		setDaemon(true);
		
	}

	public ClientConnection(String server) {
		this(server, DEFAULT_PORT);
	}

	public void draw(String tool, String data) {
		// TODO implement this method
	}

	private Object controller;
	private SSLSocket waiter;
	private PrintWriter out;
	private BufferedReader in;
	private Client parent;

	public void run() {
		// DO WORK SON
	}

	public void close() {
		if(waiter == null) return;
		try {
			waiter.close();
			in.close();
			out.close();
		} catch(IOException e) {
			// These aren't the droids you're looking for
		}
	}
}
