package mict.server;

import javax.net.ssl.*;
import java.io.*;
import java.util.*;

import mict.networking.*;
import mict.util.*;

/**
 * @author rde
 */
public class Server extends Thread {
	public static void main(String[] args) {
		new Server(expand(args)).start();
	}

	public Server(String[] options) {
		boolean database_enabled = true;
		String connstring = "jdbc:postgresql://rdebase.com/mict?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
		String dbusername = "mict";
		String dbpasswd = "";
		for(int i = 0; i < options.length; i++) {
			String option = options[i].trim();
			while(option.startsWith("-")) option = option.substring(1);
			if(option.equals("disable-database")) database_enabled = false;
			else if(option.equals("enable-database")) database_enabled = true;
			else if(option.equals("dbconnstring")) try { connstring = options[++i]; } catch(IndexOutOfBoundsException e) { System.err.println("Expected argument for --dbconnstring. Ignoring."); }
			else if(option.equals("dbusername")) try { dbusername = options[++i]; } catch(IndexOutOfBoundsException e) { System.err.println("Expected argument for --dbusername. Ignoring."); }
			else if(option.equals("dbpasswd")) try { dbpasswd = options[++i]; } catch(IndexOutOfBoundsException e) { System.err.println("Expected argument for --dbpasswd. Ignoring."); }
			else if(option.equals("anonymous-access")) try { anon_access_allowed = ConfigParser.is(options[++i]); } catch(IndexOutOfBoundsException e) { System.err.println("Expected boolean argument for --anon-access. Ignoring."); }
		}
		// load whatever parts of canvas need to be loaded
		database = new DatabaseLayer(connstring, dbusername, dbpasswd, database_enabled);
		canvas = new CanvasManager(database, this);

		// Basically, do the crap that needs to be done before beginning to accept users.
		int serverport = 56324;
		startport = serverport + 1;
		try {
			SSLServerSocketFactory servsockfactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
			servsock = (SSLServerSocket)servsockfactory.createServerSocket(serverport);
		} catch(IOException e) {
			System.out.println("Error starting server:");
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	protected Vector<Waiter> clients = new Vector<Waiter>();
	private DatabaseLayer database;
	private SSLServerSocket servsock;
	/**
	 * @uml.property name="canvas"
	 * @uml.associationEnd  
	 */
	private CanvasManager canvas;
	private int startport;
	private Vector<Integer> portsopen = new Vector<Integer>();
	private Hashtable<String, PermissionSet> permissions = new Hashtable<String, PermissionSet>();
	private boolean anon_access_allowed = true;

	public void refreshPermissions() {
		Hashtable<String, PermissionSet> permissions = new Hashtable<String, PermissionSet>();
		// TODO for each user, and default users, get the permission set and load it
		this.permissions = permissions;
	}

	protected static String[] expand(String[] args) {
		LinkedList<String> xargs = new LinkedList<String>();
		for(int i = 0; i < args.length; i++) {
			xargs.add(args[i]);
		}
		ListIterator<String> i = xargs.listIterator(0);
		while(i.hasNext()) {
			String s = i.next();
			while(s.startsWith("-")) s = s.substring(1);
			if(s.equals("")) {
				i.remove();
				continue;
			}
			if(s.startsWith("config=")) {
				i.remove();
				String file = s.substring("config=".length());
				expand(i, file);
			}
		}
		args = new String[xargs.size()];
		int j = 0;
		for(i = xargs.listIterator(); i.hasNext(); j++) {
			String s = i.next();
			args[j] = s;
		}
		return args;
	}

	protected static void expand(ListIterator<String> i, String file) {
		LinkedList<String> yargs = new LinkedList<String>();
		try {
			FileInputStream fin = new FileInputStream(file);
			String line = "";
			boolean reading = true;
			while(reading) {
				int read = fin.read();
				if(read < 0) {
					reading = false;
					read = '\n';
				} else {
					line += (char)read;
				}
				if(read == '\n' && !line.equals("")) {
					int index = line.indexOf('=');
					if(index < 0) yargs.add(line);
					else {
						yargs.add(line.substring(0,index).trim());
						yargs.add(line.substring(index+1).trim());
					}
					line = "";
				}
			}
			fin.close();
		} catch(IOException e) {
			System.err.println("Could not read configuration file " + file + ". Contents ignored.");
		}
		ListIterator<String> j = yargs.listIterator(0);
		while(j.hasNext()) {
			String s = j.next();
			String t = s;
			while(s.startsWith("-")) s = s.substring(1);
			if(s.equals("")) {
				continue;
			}
			if(s.equals("config")) {
				j.remove();
				if(j.hasNext()) {
					String file2 = j.next();
					j.remove();
					expand(i, file2);
				} else {
					System.err.println("In file " + file + ", expected filename after config= directive. Ignoring.");
				}
			} else {
				i.add(t);
			}
		}
	}

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

	public int getUserCount() {
		return clients.size();
	}

	/**
	 * @return
	 * @uml.property name="canvas"
	 */
	public CanvasManager getCanvas() {
		return canvas;
	}

	public PermissionSet authenticate(String username, String password) {
		if(username.equals("anonymous")) {
			password = "";
			if(!anon_access_allowed) return new PermissionSet();
		}
		String perms = database.authenticate(username, password);
		PermissionSet ps = new PermissionSet();
		ps.read(perms, "", ",", false);
		return ps;
	}

	public void stopServer() {
		for(Waiter w : clients) {
			w.sendClose("_Server is shutting down.");
		}

		System.out.print("Saving canvas and stopping server ...");
		System.out.flush();

		canvas.saveAll();

		System.out.println(" Done. Good-bye.");
	}

	public OneTimeServer reservePort() {
		System.out.println("One-time servers active at start of new server: " + portsopen.size());
		int p = startport;
		for(; portsopen.contains(new Integer(p)) && p < startport + 100; p++) {}
		if(p >= startport + 100) {
			System.out.println("SERVER OVERLOAD: TOO MANY PORTS OPEN TO CREATE A SINGLE-USE SERVER");
			System.exit(5);
		}
		try {
			OneTimeServer s = OneTimeServer.serve(p, portsopen);
			portsopen.add(new Integer(p));
			return s;
		} catch(IOException e) {
			System.err.println("Could not reserve a single-use server:");
			e.printStackTrace(System.err);
			System.exit(6);
		}
		return null;
	}

	public String toString() {
		return "mict.server.Server";
	}
}
