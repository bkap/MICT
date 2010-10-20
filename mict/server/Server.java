package mict.server;

import javax.net.ssl.*;
import java.io.*;
import java.util.*;

public class Server extends Thread {
	public static void main(String[] args) {
		new Server(args).start();
	}

	public Server(String[] options) { // todo pass a configuration set based on args
		// read config files first, if applicable. Probably not.
		// load tool files
		// set config options from parameters
		// read user information
		// load whatever parts of canvas need to be loaded
		String connstring = "jdbc:postgresql://rdebase.com/mict?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
		String dbusername = "mict";
		String dbpasswd = PrivateTemporaryConfigurationClass.dbpasswd; // Sorry, github.
		DatabaseLayer database = new DatabaseLayer(connstring, dbusername, dbpasswd);
		canvas = new CanvasManager(database, this);

		// Basically, do the crap that needs to be done before beginning to accept users.
		int serverport = 56324;
		try {
			SSLServerSocketFactory servsockfactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
			servsock = (SSLServerSocket)servsockfactory.createServerSocket(serverport);
		} catch(IOException e) {
			System.out.println("Error starting server:");
			e.printStackTrace(System.err);
		}
	}

	protected Vector<Waiter> clients = new Vector<Waiter>();
	private SSLServerSocket servsock;
	private CanvasManager canvas;

	public void run() {
		// start serving the canvas!
		
		try {
			System.out.println("Server's up!");

			// main loop
			while(true) {
				SSLSocket sock = (SSLSocket)servsock.accept();
				Waiter w = new Waiter(sock, this);
				clients.add(w);
				w.start();
				System.out.println("Got a client to connect!");
			}
		} catch(IOException e) {
			System.out.println("Error in main loop:");
			e.printStackTrace(System.err);
		}
	}

	public Waiter[] getUsers() {
		Waiter[] users = new Waiter[clients.size()];
		for(int i = 0; i < users.length; i++) {
			users[i] = clients.get(i);
		}
		return users;
	}

	public CanvasManager getCanvas() {
		return canvas;
	}

	public Object /*PermissionSet*/ authenticate(String username, String password) {
		// todo
		System.out.println("Authenticating user " + username + " with password " + password + ": success by default.");
		return null;
	}

	public static void stopServer() {
		// disconnect all users

		System.out.print("Saving canvas and stopping server ...");
		System.out.flush();

		// save canvas

		System.out.println(" Done. Good-bye.");
	}
}
