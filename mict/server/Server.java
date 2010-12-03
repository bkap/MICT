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
		new Server(ConfigParser.expand(args)).start();
	}

	public static String parseUsername(String username) {
		boolean restart;
		do {
			restart = false;
			for(String forbidden : new String[] { ".", ";", ",", "\n", "*", " ", "registered", "nobody" }) {
				int index = username.indexOf(forbidden);
				while(index >= 0) {
					restart = true;
					username = username.substring(0, index) + username.substring(index + forbidden.length());
					index = username.indexOf(forbidden);
				}
			}
		} while(restart);
		return username;
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
			else if(option.equals("kick-time")) try { default_kick_time = Integer.parseInt(options[++i]); } catch(IndexOutOfBoundsException e) { System.err.println("Expected boolean argument for --anon-access. Ignoring."); } catch(NumberFormatException e) { System.err.println("Expected integer argument for --kick-time. Ignoring."); }
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
	private HashSet<String> kickedUsers = new HashSet<String>();
	private int default_kick_time = 0;

	public boolean isKicked(String username) {
		return kickedUsers.contains(username);
	}

	public void refreshPermissions() {
		Hashtable<String, PermissionSet> permissions = new Hashtable<String, PermissionSet>();
		// TODO for each user, and default users, get the permission set and load it
		this.permissions = permissions;
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

	public ArrayList<Waiter> getUser(String username) {
		username = parseUsername(username);
		ArrayList<Waiter> result = new ArrayList<Waiter>();
		for(Waiter w : clients) {
			if(w.getUserName().equals(username))
				result.add(w);
		}
		return result;
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

	public void kickUser(String username) {
		int milliseconds = default_kick_time;
		kickUser(username, milliseconds, "Kicked(" + milliseconds + ")!");
	}

	public void kickUser(String username, int milliseconds) {
		kickUser(username, milliseconds, "Kicked(" + milliseconds + ")!");
	}

	public void kickUser(String username, int milliseconds, String reason) {
		username = parseUsername(username);
		for(Waiter w : getUser(username))
			w.sendClose(reason);
		if(milliseconds < 1) return;
		kickedUsers.add(username);
		new KickTimer(username, milliseconds).start();
	}

	public void banUser(String username) {
		username = parseUsername(username);
		database.banUser(username);
		kickUser(username, -1, "Banned!");
	}

	public void pardonUser(String username) {
		username = parseUsername(username);
		database.pardonUser(username);
		kickedUsers.remove(username);
	}

	public void changeUserPermissions(String username, String permissions) {
		username = parseUsername(username);
		PermissionSet ps = new PermissionSet();
		String prior = database.authenticate("registered", "");
		ps.read(prior, "", ",", true);
		ps.read(permissions, "", ",", false);
		// TODO send changes to user
		database.changeUserPermissions(username, ps.toString());
	}

	public boolean addUser(String username, String passwd, String permissions) {
		username = parseUsername(username);
		if(username.equals("")) return false;
		PermissionSet ps = new PermissionSet();
		String prior = database.authenticate("registered", "");
		int index = prior.indexOf('*');
		while(index >= 0) {
			prior = prior.substring(0, index) + username + prior.substring(index + 1);
			index = prior.indexOf('*');
		}
		ps.read(prior, "", ",", false);
		ps.read(permissions, "", ",", false);
		return database.addUser(username, passwd, ps.toString());
	}

	public void deleteUser(String username) {
		username = parseUsername(username);
		database.deleteUser(username);
		kickUser(username, -1, "Deleted!");
	}

	public boolean changeUserPassword(String username, String passwd) {
		username = parseUsername(username);
		return database.changeUserPassword(username, passwd);
	}

	public PermissionSet authenticate(String username, String password) {
		username = parseUsername(username);
		if(username.equals("anonymous")) {
			password = "";
			if(!anon_access_allowed) return new PermissionSet();
		}
		if(isKicked(username)) return new PermissionSet();
		String perms = database.authenticate(username, password);
		PermissionSet ps = new PermissionSet();
		ps.read(perms, "", ",", false);
		return ps;
	}

	public String getPermissions(String username) {
		username = parseUsername(username);
		return database.getPermissions(username);
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

	class KickTimer extends Thread {
		public KickTimer(String user, int milliseconds) {
			this.user = user;
			this.milliseconds = milliseconds;
		}

		private String user;
		private int milliseconds;

		public void run() {
			try { Thread.sleep(milliseconds); } catch(InterruptedException e) {}
			kickedUsers.remove(user);
		}
	}
}
