package mict.client;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.net.ssl.*;

import mict.networking.*;
import mict.test.*;
import mict.tools.*;

public class ClientConnection extends Thread {
	private static int DEFAULT_PORT = 56324;

	public ClientConnection(String server, int port, String username, String passwd, Client parent) {
		this.server = server;
		this.controller = controller;
		this.serverport = port;
		this.parent = parent;
		if(server != "") {
			try {
				SSLSocketFactory sockfactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
				waiter = (SSLSocket)sockfactory.createSocket(server, port);
				out = new PrintWriter(new OutputStreamWriter(waiter.getOutputStream()), true);
				in = new BufferedReader(new InputStreamReader(waiter.getInputStream()));
				out.println(username + ' ' + passwd);
			} catch(IOException e) {
				System.err.println("Could not open connection to server: ");
				e.printStackTrace(System.err);
			}
		}
		setDaemon(true);
	}

	public ClientConnection(String server, String username, String passwd, Client parent) {
		this(server, DEFAULT_PORT, username, passwd, parent);
	}

	private String server;
	private Object controller;
	private SSLSocket waiter;
	private PrintWriter out;
	private BufferedReader in;
	private Client parent;
	private int serverport;
	
	public void run() {
		// DO WORK SON
		Canvas canvas = parent.getCanvas();
		String buffer = "";
		String action = "";
		try {
			while(true) {
				int read = in.read();
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
		} catch(IOException e) {
			e.printStackTrace(System.err);
		}
		close();
	}

	private void dispatch(String action, String phrase) {
		if(action.startsWith(".")) { // it's a tool
			System.out.println("Recieving draw from the server: " + action + " " + phrase);
			ClientState state = parent.getClientState();
			String t = action.substring(1);
			int index = t.indexOf('@');
			Tool tool = state.tools.getToolByID(t.substring(0, index));
			t = t.substring(index+1);
			index = t.indexOf(',');
			int x = Integer.parseInt(t.substring(0,index));
			int y = Integer.parseInt(t.substring(index+1));
			Graphics2D g = (Graphics2D)parent.getCanvasGraphics().create();
			Canvas c = parent.getCanvas();
			g.translate(x - c.getUserX(), y - c.getUserY());
			tool.draw(phrase, g); 
			c.repaint();
		} else { // it's not a tool
			if(action.equals("imgrect")) {
				try {
					int index = phrase.indexOf('.');
					long x = Long.parseLong(phrase.substring(0,index));
					String rest = phrase.substring(index+1);
					index = rest.indexOf('@');
					long y = Long.parseLong(rest.substring(0,index));
					int port = Integer.parseInt(rest.substring(index+1));
					Socket s = new Socket(server, port);
					BufferedImage img = ImageIO.read(s.getInputStream());
					s.close();
					Canvas c = parent.getCanvas();
					c.getCanvasGraphics().drawImage(img, (int)(x - c.getUserX()), (int)(y - c.getUserY()), c);
					c.repaint();
				} catch(IOException e) {
					System.err.println("Wow, that really should never have happened:");
					e.printStackTrace(System.err);
				}
			}
			// TODO fill this out later
			else System.out.println("nothing happened. Improper action string, could not be handled.");
		}
	}

	public void requestCanvasRect(long x, long y, long width, long height) {
		if(out != null) {
			out.println("imgrect " + x + '.' + y + '.' + width + '.' + height);
		} else {
			System.err.println("Tried to request a rectangle before establishing a connection. Oops.");
		}
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

	public void sendDraw(String tool, String data) {
		if(out == null) {
			System.err.println("Tried to send an action before establishing a connection. Oops.");
			return;
		}
		System.out.println("Drawing! ." + tool + ' ' + data);
		out.println('.' + tool + ' ' + data);
	}
}
