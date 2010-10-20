package mict.client;

import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.net.ssl.*;

import mict.networking.*;

public class ClientConnection extends Thread {
	private static int DEFAULT_PORT = 56324;

	public ClientConnection(String server, int port, String username, String passwd, Client parent) {
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
		if(in == null) { return; }
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
		if(out == null) { return; }
		System.out.println("Dispatching: " + action + ' ' + phrase);
		if(action.startsWith(".")) { // it's a tool
			parent.getClientState().tools.getToolByID(action.substring(1)).draw(phrase, parent.getCanvasGraphics()); 
		} else { // it's not a tool
			if(action.startsWith("imgrect")) {
				try {
					String coords = action.substring("imgrect".length());
					int index = coords.indexOf('.');
					long x = Long.parseLong(coords.substring(0,index));
					long y = Long.parseLong(coords.substring(index+1));
					System.out.println("Attempting to draw server-provided rectangle at @(" + x + ',' + y + ").");
					ByteArrayInputStream in = new ByteArrayInputStream(phrase.getBytes());
					BufferedImage img = ImageIO.read(new EscapingInputStream(in));
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
			System.out.println("asking for rectangle @(" + x + ',' + y + ") at " + width + " by " + height);
			out.println("imgrect " + x + '.' + y + '.' + width + '.' + height);
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
		if(out == null) { return; }
		System.out.println("Drawing! ." + tool + ' ' + data);
		out.println('.' + tool + ' ' + data);
	}
}
