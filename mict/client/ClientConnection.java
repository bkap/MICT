package mict.client;

import javax.net.ssl.*;
import java.io.*;

public class ClientConnection extends Thread {
	private static int DEFAULT_PORT = 56234;

	public ClientConnection(String server, int port, String username, String passwd, Client parent) {
		this.controller = controller;
		this.serverport = port;
		this.parent = parent;
		try {
			SSLSocketFactory sockfactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
			waiter = (SSLSocket)sockfactory.createSocket(server, port);
			out = new PrintWriter(new OutputStreamWriter(waiter.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(waiter.getInputStream()));
			out.println(username + ' ' + passwd);
		} catch(IOException e) {
			System.err.println("Could not open connection to server: ");
			e.printStackTrace(err);
		}
		setDaemon(true);
		
	}

	public ClientConnection(String server, String username, String passwd, Client parent) {
		this(server, DEFAULT_PORT, username, passwd, parent);
	}

	private Object controller;
	private SSLSocket waiter;
	private PrintWriter out;
	private BufferedReader in;
	private Client parent;
	private int serverport;
	
	public void run() {
		// DO WORK SON
		String buffer = "";
		String action = "";
		while(true) {
			int read = -1;
			try {
				read = in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(read == -1) break;
			if(read == ' ') {
				if(action == "") action = buffer;
				else dispatch(action, buffer);
				buffer = "";
			} else if(read == '\n') {
				dispatch(action, buffer);
				buffer = "";
				action = "";
			} else {
				buffer += (char)read;
			}
		}
		close();
	}

	private void dispatch(String action, String phrase) {
		if(action.startsWith(".")) { // it's a tool
			parent.getClientState().tools.getToolByID(action.substring(1)).draw(phrase, parent.getCanvasGraphics()); 
		} else { // it's not a tool
			if(action.startsWith("imgrect")) {
				String coords = action.substring("imgrect".length);
				int index = coords.indexOf('.');
				long x = Long.parseLong(coords.substring(0,index));
				long y = Long.parseLong(coords.substring(index+1));
				ByteArrayImputStream in = new ByteArrayInputStream(phrase.getBytes());
				BufferedImage img = ImangeIO.read(new EscapingInputStream(in));
				parent.
			}
			// TODO fill this out later
		}
	}

	public void requestCanvasRect(long x, long y, long width, long height) {
		out.println("imgrect " + x + '.' + y + '.' + width + '.' + height);
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

	public void draw(String tool, String data) {
		// TODO implement this method
	}
}
