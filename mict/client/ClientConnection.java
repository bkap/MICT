package mict.client;

import java.io.*;
import java.net.*;
import java.util.List;
import java.awt.Graphics2D;
import java.awt.image.*;
import javax.imageio.*;
import javax.net.ssl.*;

import mict.tools.ToolManager;



/**
 * @author  bkaplan
 */
public class ClientConnection extends Thread {
	private static int DEFAULT_PORT = 56324;

	public ClientConnection(String server, int port, String username, String passwd, Canvas canvas, ToolManager t) {
		this.server = server;
		//this.controller = controller;
		//this.serverport = port;
		this.canvas = canvas;
		if(server != "") {
			try {
				this.toolManager = t;
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

	public ClientConnection(String server, String username, String passwd, Canvas canvas, ToolManager t) {
		this(server, DEFAULT_PORT, username, passwd, canvas, t);
	}

	private String server;
	//private Object controller;
	private SSLSocket waiter;
	private PrintWriter out;
	private BufferedReader in;
	private ToolManager toolManager;
	/**
	 * @uml.property  name="parent"
	 * @uml.associationEnd  
	 */
	private Canvas canvas;
	//private int serverport;
	
	public void run() {
		// DO WORK SON
		String buffer = "";
		String action = "";
		try {
			while(!waiter.isClosed()) {
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
	
	public void terminateConnection() {
		try {
			waiter.close();
		} catch (IOException e) {
			// oh boo hoo. If the connection fails to terminate, it's no  good anyway
		}
	}
	public void finalize() {
		terminateConnection();
	}
	private void dispatch(String action, String phrase) {
		if(action.startsWith(".")) { // it's a tool
			System.out.println("Recieving draw from the server: " + action + " " + phrase);
			String t = action.substring(1);
			int index = t.indexOf('@');
			String toolid = t.substring(0,index);
			t = t.substring(index+1);
			index = t.indexOf(',');
			int x = Integer.parseInt(t.substring(0,index));
			int y = Integer.parseInt(t.substring(index+1));
			Graphics2D g = (Graphics2D)canvas.getCanvasGraphics().create();
			g.translate(canvas.getUserX() - x, canvas.getUserY() - y);
			canvas.draw(toolid, phrase);
			canvas.repaint();
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
		
					canvas.getCanvasGraphics().drawImage(img, (int)(x - canvas.getUserX()), (int)(y - canvas.getUserY()), canvas);
					canvas.repaint();
				} catch(IOException e) {
					System.err.println("Wow, that really should never have happened:");
					e.printStackTrace(System.err);
				}
			} else if(action.equals("querytools")) {
				List<String> needed = toolManager.updateClientTools(phrase);
				String tools = " ";
				for(String toolID: needed) {
					tools = tools.concat(toolID + " ");
				}
				out.println("requesttool" + tools);
			} else if(action.equals("tool")) {
				toolManager.addTool(phrase);
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
