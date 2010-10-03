package mict.client;

import javax.net.ssl.*;
import java.io.*;

public class ClientConnection extends Thread { // TODO thread?
	public ClientConnection(Object controller, String server, int port) {
		
		this.controller = controller;
		this.serverport = port;
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

	public ClientConnection(Object controller, String server) { // TODO @Ben figure out what part of the client-side architecture this should really be, and set the type accordingly
		this(controller, server, DEFAULT_PORT);
	}
	public void sendConnection(int ToolID, String command) {
		//TODO: implement this method
		
	}
	private Object controller;
	private SSLSocket waiter;
	private PrintWriter out;
	private BufferedReader in;
	private int serverport = 56324;
	private static int DEFAULT_PORT = 56234;
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
			// Nothing to see here, move along.
		}
	}
}
