package mict.server;

import java.awt.image.*;
import java.io.*;
import javax.net.ssl.*;
import javax.imageio.*;

import mict.bridge.JythonBridge;
import mict.networking.*;

/**
 * @author  bkaplan
 */
public class Waiter extends Thread {
	public Waiter(SSLSocket patron, Server parent) throws IOException {
		this.patron = patron;
		this.parent = parent;
		out = new patron.getOutputStream();
		in = new BufferedReader(new InputStreamReader(patron.getInputStream()));
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
	private BufferedReader in;
	private long x = 0;
	private long y = 0;
	private long w = 1024;
	private long h = 1024;
	//private PermissionSet perms;
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

			// send tool set
			// set prior x,y
			String buffer = "";
			String action = "";
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
				/*history.add(*/parent.getCanvas().draw(x, y, tool, phrase, this); //);
			}
		} else { // it's not a tool
			if(action.startsWith("imgrect")) {
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
			} else if(action.startsWith("requesttool")) {
				String pickled = JythonBridge.serializeTool(phrase);
				out.write(("tool " + pickled + "\n").toBytes());
				out.flush();
			} else {
				System.out.println("Oops, that action doesn't exist.");
			}
		}
		System.out.println();
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
		try {
			BufferedImage img = parent.getCanvas().getCanvasRect(x, y, width, height);
			out.write(("imgrect" + x + '.' + y + ' ').toBytes());
			ImageIO.write(img, "png", out); // TODO create ostream as a bytearrayoutputstream, send bytes through properly
			out.flush();
		} catch(IOException e) {
			System.err.println("Bad operation while quilting a canvas patch:");
			e.printStackTrace(System.err);
		}
	}

	public void sendMoveOrder(long x, long y) {
		
	}

	public void sendToolSet() {
		
	}

	public void sendCanvasChange(long x, long y, String tool, String data) {
		System.out.println("Sending a change back to the user");
		send('.' + tool + '@' + x + ',' + y, data);
	}

	protected void send(String type, String data) {
		System.out.println('[' + type + " " + data + ']');
		out.write((type.toBytes() + ' ' + data.toBytes()).toBytes());
		out.flush();
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
			System.out.println("Nothing to see here, move along.");
		}
		if(username != null) {
			parent.getCanvas().saveAll();
		}
	}

	public String getUserName() {
		return username;
	}
}
