package mict.server;

import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.net.ssl.*;
import javax.imageio.*;
import java.util.LinkedList;
import mict.bridge.JythonBridge;
import mict.networking.*;
import mict.util.*;

/**
 * @author rde
 */
public class Waiter extends Thread {
	public Waiter(SSLSocket patron, Server parent) throws IOException {
		this.patron = patron;
		this.parent = parent;
		out = patron.getOutputStream();
		in = patron.getInputStream();
		setDaemon(true);
	}

	private String username = null;
	private SSLSocket patron;
	/**
	 * @uml.property  name="parent"
	 * @uml.associationEnd  
	 */
	private Server parent;
	private OutputStream out;
	private InputStream in;
	private long x = 0;
	private long y = 0;
	private long w = 1024;
	private long h = 1024;
	//private Vector<HistoryLayer> history = new Vector<HistoryLayer>();

	public void run() {
		// DO WORK, SON
		try {
			String username = "";
			String password = "";
			while(true) {
				int read = in.read();
				if(read == -1) {
					close();
					return;
				} else if(read == '\n') {
					int index = username.indexOf(' ');
					if(index > 0) {
						password = username.substring(index + 1);
						username = username.substring(0, index);
					}
					break;
				} else {
					username += (char)read;
				}
			}
			/*perms = */parent.authenticate(username, password);
			/*if(!perms.acceptConnection()) {
				close();
				return;
			}*/
			this.username = username;

			sendToolSet();
			// set prior x,y
			String buffer = "";
			LinkedList<Byte> bitbuffer = new LinkedList<Byte>();
			String action = "";
			while(true) {
				int read = in.read();
				if(read == -1) break;
				if(read == ' ') {
					if(action.equals("")) {
						action = buffer;
					} else if(action.startsWith("#")) {
						dispatch(action.substring(1), Utility.toByteArray(bitbuffer));
					} else dispatch(action, buffer);
					// Utility.toByteArray consumes bitbuffer.
					buffer = "";
				} else if(read == '\n') {
					if(action.startsWith("#")) {
						dispatch(action.substring(1), Utility.toByteArray(bitbuffer));
					} else {
						dispatch(action, buffer);
					}
					// Utility.toByteArray consumes bitbuffer.
					buffer = "";
					action = "";
				} else if(action.startsWith("#")) {
					bitbuffer.addLast(new Byte((byte)read));
				} else {
					buffer += (char)read;
				}
			}
		} catch(IOException e) {
			System.err.println("Lost the connection with a user. Too bad, I guess:");
			e.printStackTrace(System.err);
		}
		close();
	}

	private void dispatch(String action, String phrase) {
		System.out.println("Dispatching phrase: " + action + " " + phrase);
		if(action.startsWith(".")) { // it's a tool
			String tool = action.substring(1);
			if(tool.equals("pan")) {
				int index = phrase.indexOf(',');
				int dx = Integer.parseInt(phrase.substring(0,index));
				int dy = Integer.parseInt(phrase.substring(index+1));
				move(x + dx, y + dy);
			} else {
				/*history.add(*/parent.getCanvas().draw(x, y, tool, phrase, this, null); //);
			}
		} else { // it's not a tool
			if(action.startsWith("imgrect")) { // receiving a request for an image
				int index = phrase.indexOf('.');
				long x = Long.parseLong(phrase.substring(0,index));
				phrase = phrase.substring(index+1);
				index = phrase.indexOf('.');
				long y = Long.parseLong(phrase.substring(0,index));
				phrase = phrase.substring(index+1);
				index = phrase.indexOf('.');
				long w = Long.parseLong(phrase.substring(0,index));
				long h = Long.parseLong(phrase.substring(index+1));
				System.out.println("Stitching and sharing a rectangular portion of the canvas @(" + x + ',' + y + ") at " + w + " by " + h);
				sendCanvasRectangle(x, y, w, h);
			} else if(action.equals("requesttool")) {
				String[] neededFiles = phrase.split(":");
				for(String file: neededFiles) {
					sendEscapedData("tool", JythonBridge.getSerializedToolFile(file));
				}
			} else {
				System.err.println("Oops, that action doesn't exist.");
			}
		}
		System.out.println();
	}

	private void dispatch(String action, byte[] data) {
		System.err.println("Nothing happened. Improper command '" + action + ", could not be handled.");
		if(action.startsWith("imgrect")) { // receiving an image
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
				parent.getCanvas().draw(this.x + x, this.y + y, "imgrect", "[[IMAGE]]", this, img);
			} catch(IOException e) {
				System.err.println("Wow, that really should never have happened:");
				e.printStackTrace(System.err);
			}
		}
	}

	/** checks to see if the given four-member long[] intersects with the user's viewing area
	 */
	public boolean intersects(long[] rect) {
		return
			!(rect[0] + rect[2] < x || rect[0] > x + w) &&
			!(rect[1] + rect[3] < y || rect[1] > y + h);
	}

	protected void move(long x, long y) {
		this.x = x;
		this.y = y;
	}

	public void sendCanvasRectangle(long x, long y, long width, long height) {
		BufferedImage img = parent.getCanvas().getCanvasRect(x, y, width, height);
		sendCanvasRectangle(x, y, img);
	}

	public void sendCanvasRectangle(long x, long y, BufferedImage img) {
		try {
			out.write(("#imgrect@" + x + '.' + y + ' ').getBytes());
			EscapingOutputStream eout = new EscapingOutputStream(out);
			ImageIO.write(img, "png", eout);
			eout.flush();
			out.write('\n');
			out.flush();
		} catch(IOException e) {
			System.err.println("Bad operation while quilting a canvas patch:");
			e.printStackTrace(System.err);
		}
	}

	public void sendMoveOrder(long x, long y) {
		
	}

	public void sendToolSet() {
		sendEscapedData("querytools", JythonBridge.getToolDescriptions());
	}

	public void sendCanvasChange(long x, long y, String tool, String data) {
		System.out.println("Sending a change back to the user");
		send('.' + tool + '@' + x + ',' + y, data);
	}

	protected void send(String type, String data) {
		try {
			out.write((type + ' ' + data + '\n').getBytes());
			out.flush();
		} catch(IOException e) {
			System.err.println("Error sending string: " + type + ' ' + data);
			e.printStackTrace(System.err);
		}
	}

	protected void sendEscapedData(String type, String data) {
		try {
			out.write((type + ' ').getBytes());
			EscapingOutputStream eout = new EscapingOutputStream(out);
			eout.write(data.getBytes());
			eout.flush();
			out.write('\n');
			out.flush();
		} catch(IOException e) {
			System.err.println("Error sending string: " + type + ' ' + data);
			e.printStackTrace(System.err);
		}
	}

	protected void sendClose(String reason) {
		String sep = reason.substring(0,1);
		send("close", reason.substring(1).replace(" ", sep));
		close();
	}

	protected void close() {
		System.out.println("Client is leaving!");
		parent.clients.remove(this);
		try {
			patron.close();
			in.close();
			out.close();
		} catch(IOException e) {
			// Nothing to see here, move along.
		}
		if(username != null) {
			parent.getCanvas().saveAll();
		}
	}

	public String getUserName() {
		return username;
	}
}
