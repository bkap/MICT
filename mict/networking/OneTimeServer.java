package mict.networking;

import java.io.*;
import java.net.*;
import java.util.*;

public class OneTimeServer extends Thread {
	public static OneTimeServer serve(int port, Vector<Integer> openports) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		OneTimeServer server = new OneTimeServer(port, out, openports);
		server.setDaemon(true);
		server.start();
		return server;
	}

	protected OneTimeServer(int port, ByteArrayOutputStream out, Vector<Integer> openports) throws IOException {
		servsocket = new ServerSocket(port);
		this.out = out;
		this.port = port;
		this.openports = openports;
	}

	private int port;
	private ServerSocket servsocket;
	private ByteArrayOutputStream out;
	private Vector<Integer> openports;

	public void run() {
		try {
			Socket client = servsocket.accept();
			out.writeTo(client.getOutputStream());
			out.close();
			client.close();
			servsocket.close();
		} catch(IOException e) {
			System.err.println("One-time server died:");
			e.printStackTrace(System.err);
		}
		openports.remove(new Integer(port));
	}

	public OutputStream getOutputStream() {
		return out;
	}

	public int getPort() {
		return port;
	}
}
