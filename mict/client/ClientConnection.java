package mict.client;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import javax.imageio.*;
import javax.net.ssl.*;
import javax.swing.*;

import mict.networking.*;
import mict.tools.*;
import mict.util.*;

/** This class is used to handle asynchronous communications between the client and the server.
 * @author rde
 */
public class ClientConnection extends Thread {
	private static int DEFAULT_PORT = 56324;

	public ClientConnection(String server, int port, String username, String passwd, Canvas canvas, ToolManager t, AdminPanel adpanel) {
		this.server = server;
		this.canvas = canvas;
		this.adpanel = adpanel;
		if(!server.equals("")) {
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

	public ClientConnection(String server, String username, String passwd, Canvas canvas, ToolManager t, AdminPanel adpanel) {
		this(server, DEFAULT_PORT, username, passwd, canvas, t, adpanel);
	}

	private String server;
	private SSLSocket waiter;
	private OutputStream out;
	private InputStream in;
	private ToolManager toolManager;
	private Canvas canvas;
	private AdminPanel adpanel;

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
			//canvas.getCanvasGraphics().setColor(Color.WHITE);
			//canvas.getCanvasGraphics().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
			canvas.draw(toolid, phrase, (int)(x - canvas.getUserX()), (int)(y - canvas.getUserY()));
			canvas.repaint();
		} else { // it's not a tool
			if(action.equals("querytools")) {
				System.out.println("tools queried");
				String needed = toolManager.updateClientTools(phrase);
				if(!needed.equals("")) {
					System.out.println("requesting " + needed);
					send("requesttool", needed);
				}
			} else if(action.equals("tool")) {
				ByteArrayInputStream bin = new ByteArrayInputStream(phrase.getBytes());
				EscapingInputStream ein = new EscapingInputStream(bin);
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				while(ein.available() > 0) {
					bout.write(ein.read());
				}
				System.out.println(phrase);
				System.out.println(bout.toString());
				toolManager.addTools(bout.toString());
			} else if(action.equals("permission")) {
				canvas.getClientState().permissions.setPermission(Permission.parse(phrase));
			} else if(action.equals("user")) {
				System.out.println("Got user! " + phrase);
				adpanel.addUser(phrase);
			} else if(action.equals("left")) {
				adpanel.removeUser(phrase);
			} else if(action.startsWith("perms.")) {
				final String user = action.substring("perms.".length());
				final String perms = phrase;
				Thread t = new Thread(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(
							canvas,
							"Permissions for " + user + ": " + perms,
							"User Information",
							JOptionPane.PLAIN_MESSAGE
						);
					}
				});
				t.setDaemon(true);
				t.start();
			} else if(action.equals("close")) {
				System.out.println("kicked!");
				String message = "You lost the connection to the server.";
				int index = message.indexOf('(');
				String time = null;
				if(index >= 0) {
					time = message.substring(index + 1);
					time = time.substring(0, time.length() - 2);
					message = "You were " + message.substring(0, index) + " from the server for " + time + " seconds!";
				} else {
					message = "Your accout was " + phrase;
				}
				JOptionPane.showMessageDialog(
					canvas,
					message,
					"Lost connection",
					JOptionPane.ERROR_MESSAGE
				);
				System.exit(0);
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
				if(img == null) System.err.println("Oh noes! got a null image from the server.");
				Graphics2D g = (Graphics2D)canvas.getCanvasGraphics().create();
				g.translate(-canvas.getUserX(), -canvas.getUserY());
				g.setColor(Color.WHITE);
				g.fillRect((int)x, (int)y, img.getWidth(canvas), img.getHeight(canvas));
				//System.out.println("Canvas rect @" + x + "," + y + " at (" + img.getWidth(canvas) + "," + img.getHeight(canvas) + ")");
				g.drawImage(img, (int)x, (int)y, canvas);
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

	public void requestUserList() {
		try {
			adpanel.clearUserList();
			send("userlist", "userlist");
		} catch(IOException e) {
			System.err.println("Connection failed us whilst trying to request list of users:");
			e.printStackTrace(System.err);
		}
	}

	public void kickUser(String username) {
		try {
			send("kick", username);
		} catch(IOException e) {
			System.err.println("Connection failed us whilst trying to defenestrate a user");
			e.printStackTrace(System.err);
		}
	}

	public void banUser(String username) {
		try {
			send("ban", username);
		} catch(IOException e) {
			System.err.println("Connection failed us whilst trying to more permanently defenestrate a user");
			e.printStackTrace(System.err);
		}
	}

	public void pardonUser(String username) {
		try {
			send("pardon", username);
		} catch(IOException e) {
			System.err.println("Connection failed us whilst trying to un-defenestrate a user");
			e.printStackTrace(System.err);
		}
	}

	public void modifyUserPermissions(String username, String permissions) {
		try {
			send("modperms", username + '.' + permissions);
		} catch(IOException e) {
			System.err.println("Connection failed us whilst trying to modify user permissions");
			e.printStackTrace(System.err);
		}
	}

	public void registerUser(String username, String passwd) {
		try {
			send("register", username + '.' + passwd);
		} catch(IOException e) {
			System.err.println("Connection failed us whilst trying to register as a new user");
			e.printStackTrace(System.err);
		}
	}

	public void deleteUser(String username) {
		try {
			send("deluser", username); // Del User! De-Luser! It's all sorts of magic!
		} catch(IOException e) {
			System.err.println("Connection failed us whilst trying to rid outselves of a pesky luser");
			e.printStackTrace(System.err);
		}
	}

	public void modifyUserPassword(String username, String password) {
		try {
			send("modpasswd", username + '.' + password);
		} catch(IOException e) {
			System.err.println("Connection failed us whilst trying to change a user's password");
			e.printStackTrace(System.err);
		}
	}

	public void requestUserPermissions(String username) {
		try {
			send("seeperms", username);
		} catch(IOException e) {
			System.err.println("Connection failed us whilst trying to determine another user's permissions");
			e.printStackTrace(System.err);
		}
	}

	/** Requests the section of the Canvas bounded by the given parameters. Data will be returned asynchronously and automatically drawn to the 
	 * canvas upon receipt. 
	 * 
	 * @param x the left bound of the rectangle
	 * @param y the top bound of the rectangle
	 * @param width the width of the rectangle in pixels
	 * @param height the height of the rectangle in pixels
	 */
	public void requestCanvasRect(long x, long y, long width, long height) {
		if(width < 1 || height < 1) return;
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
	/** transmit the image given by the ImageData to the server
	 * @see #sendImage(int, int, BufferedImage)
	 * @param img the ImageData holding the information to send
	 */
	public void sendImage(ImageData img) {
		sendImage(img.x, img.y, img.img);
	}
	/** Tell the server to draw the given image with the top left corner at x,y
	 * 
	 * @param x
	 * @param y
	 * @param img
	 */
	public void sendImage(int x, int y, BufferedImage img) {
		try {
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
	/** Send the data generated by the tool with the given toolID to the server.
	 * 
	 * @see mict.tools.Tool#draw(String, Graphics2D)
	 * @see mict.tools.Tool#getToolID()
	 * 
	 * @param tool the toolID of the tool used to generate the data
	 * @param data the serialized form of the data
	 * 
	 */
	public void sendDraw(String tool, String data) {
		try {
			if(out == null) {
				System.err.println("Tried to send an action before establishing a connection. Oops.");
				return;
			}
			//System.out.println("Drawing! ." + tool + ' ' + data);
			send('.' + tool, data);
		} catch(IOException e) {
			System.err.println("Could not send a draw command:");
			e.printStackTrace(System.err);
		}
	}
	/** Send the raw information given to the server.
	 * 
	 * If there is a more specific method for the type of data your are sending, use it instead.
	 * 
	 * @param action the String identifying how to handle the phrase
	 * @param phrase the data to be used by the method chosen based on the action
	 * @throws IOException
	 */
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
