//package edu.case.rdebase.mict, I presume. But that can be added later. For now, a flat package structure suits me just fine.

import javax.net.ssl.*;
import java.io.*;

public class Server extends Thread {
	public static void main(String[] args) {
		new Server(null).start();
	}

	public Server(String[] options) { // todo pass a configuration set based on args
		// read config files first, if applicable. Probably not.
		// load tool files
		// set config options from parameters
		// read user information
		// load whatever parts of canvas need to be loaded

		// Basically, do the crap that needs to be done before beginning to accept users.
	}

	private int serverport = 56324;

	public void run() {
		// start serving the canvas!
		
		try {
			SSLServerSocketFactory servsockfactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
			SSLServerSocket servsock = (SSLServerSocket)servsockfactory.createServerSocket(serverport);

			System.out.println("Server's up!");

			// main loop
			while(true) {
				SSLSocket sock = (SSLSocket)servsock.accept();
				Waiter w = new Waiter(sock, this);
				w.start();
			}
		} catch(IOException e) {
			System.out.println("Error in starting server: " + e.getMessage());
		}
	}

	public static void stopServer() {
		System.out.print("Saving canvas and stopping server ...");
		System.put.flush();

		// save canvas

		System.out.println(" Done. Good-bye.");
	}
}
