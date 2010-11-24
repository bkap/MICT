package mict.client;

import java.io.*;
import java.net.*;
import java.awt.Graphics2D;
import java.awt.image.*;
import java.util.*;
import javax.imageio.*;
import javax.net.ssl.*;

import mict.networking.*;
import mict.tools.*;

/**
 * @author rde
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
				out = waiter.getOutputStream();
				in = waiter.getInputStream();
				out.write((username + ' ' + passwd + '\n').getBytes());
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
	private OutputStream out;
	private InputStream in;
	private ToolManager toolManager;
	private Canvas canvas;
	
	public void run() {
		// DO WORK SON
		String buffer = "";
		ByteArrayOutputStream bitbuffer = new ByteArrayOutputStream();
		String action = "";
		try {
			while(!waiter.isClosed()) {
				int read = in.read();
				if(read == -1) break;
				if(read == ' ') {
					if(action.equals("")) {
						action = buffer;
					} else if(action.startsWith("#")) {
						dispatch(action.substring(1), bitbuffer.toByteArray());
					} else dispatch(action, buffer);
					bitbuffer.reset();
					buffer = "";
				} else if(read == '\n') {
					if(action.startsWith("#")) {
						dispatch(action.substring(1), bitbuffer.toByteArray());
					} else {
						dispatch(action, buffer);
					}
					bitbuffer.reset();
					buffer = "";
					action = "";
				} else if(action.startsWith("#")) {
					bitbuffer.write(read);
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
			// oh boo hoo. If the connection fails to terminate, it's no good anyway
		}
	}

	public void finalize() {
		terminateConnection();
	}

	private void dispatch(String action, String phrase) throws IOException {
		if(action.startsWith(".")) { // it's a tool
			String t = action.substring(1);
			int index = t.indexOf('@');
			String toolid = t.substring(0,index);
			t = t.substring(index+1);
			index = t.indexOf(',');
			int x = Integer.parseInt(t.substring(0,index));
			int y = Integer.parseInt(t.substring(index+1));
			canvas.draw(toolid, phrase, (int)(canvas.getUserX() + x), (int)(canvas.getUserY() + y));
			canvas.repaint();
		} else { // it's not a tool
			if(action.equals("querytools")) {
				String needed = toolManager.updateClientTools(phrase);
				send("requesttool", needed);
			} else if(action.equals("tool")) {
				toolManager.addTools(phrase);
			} else {
				System.err.println("Nothing happened. Improper command '" + action + /*' ' + phrase +*/ "', could not be handled.");
			}
		}
	}

	public void dispatch(String action, byte[] data) {
		if(action.startsWith("imgrect")) {
			try {
				int index = action.indexOf('@');
				String rest = action.substring(index+1);
				index = rest.indexOf('.');
				long x = Long.parseLong(rest.substring(0,index));
				long y = Long.parseLong(rest.substring(index+1));
				ByteArrayInputStream bin = new ByteArrayInputStream(data);
				EscapingInputStream ebin = new EscapingInputStream(bin);
				BufferedImage img = ImageIO.read(ebin);
				ebin.close();
				bin.close();
				canvas.getCanvasGraphics().drawImage(img, (int)(x - canvas.getUserX()), (int)(y - canvas.getUserY()), canvas);
				canvas.repaint();
			} catch(IOException e) {
				System.err.println("Wow, that really should never have happened:");
				e.printStackTrace(System.err);
			}
		} else {
			System.err.println("Nothing happened. Improper command '" + action + "', could not be handled.");
		}
	}

	public boolean isConnected() {
		return out != null;
	}

	public void requestCanvasRect(long x, long y, long width, long height) {
		try {
			if(out != null) {
				send("imgrect", "" + x + '.' + y + '.' + width + '.' + height);
			} else {
				System.err.println("Tried to request a rectangle before establishing a connection. Oops.");
			}
		} catch(IOException e) {
			System.err.println("Could not request rectangular section of canvas:");
			e.printStackTrace(System.err);
		}
	}

	public void sendImage(String type, int x, int y, BufferedImage img) {
		try {
			BufferedImage img = parent.getCanvas().getCanvasRect(x, y, width, height);
			out.write(("#imgrect@" + x + '.' + y + ' ').getBytes());
			EscapingOutputStream eout = new EscapingOutputStream(out);
			ImageIO.write(img, "png", eout);
			eout.flush();
			out.write('\n');
			out.flush();
		} catch(IOException e) {
			System.err.println("Bad operation while sending an image to the server:");
			e.printStackTrace(System.err);
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
		try {
			if(out == null) {
				System.err.println("Tried to send an action before establishing a connection. Oops.");
				return;
			}
			System.out.println("Drawing! ." + tool + ' ' + data);
			send('.' + tool, data);
		} catch(IOException e) {
			System.err.println("Could not send a draw command:");
			e.printStackTrace(System.err);
		}
	}

	public void send(String action, String phrase) throws IOException {
		if(out == null) {
			System.err.println("Tried to send an action before establishing a connection. Oops.");
			return;
		}
		out.write((action + ' ' + phrase + '\n').getBytes());
		out.flush();
	}

	public String toString() {
		return "mict.client.ClientConnection";
	}
}
