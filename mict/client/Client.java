package mict.client;

import javax.swing.*;
import java.awt.*;

import mict.tools.*;
import mict.util.*;

/**
 * The Client is the main class for the client side. It consists of a Canvas and a Toolbox. It also does the work to bridge those. All initialization should happen here. It is an Applet, but can be run as an application by sticking its contentPane in a JFrame.
 * @author  Ben Kaplan
 * @since 093010
 */
public class Client extends JApplet {
	public static void main(String[] args) {
		args = ConfigParser.expand(args);
		String server = null;
		String username = null;
		String passwd = "";
		for(int i = 0; i < args.length; i++) {
			String option = args[i].trim();
			while(option.startsWith("-")) option = option.substring(1);
			if(option.equals("server")) try { server = args[++i]; } catch(IndexOutOfBoundsException e) { System.err.println("Expected argument for --server. Ignoring."); }
			else if(option.equals("username")) try { username = args[++i]; } catch(IndexOutOfBoundsException e) { System.err.println("Expected argument for --username. Ignoring."); }
			else if(option.equals("passwd")) try { passwd = args[++i]; } catch(IndexOutOfBoundsException e) { passwd = ""; }
		}
		Client c = new Client(server);
		c.username = username;
		c.passwd = passwd;
		JFrame frame = new JFrame("MICT v0.0");
		frame.setContentPane(c.getContentPane());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		c.start();
		frame.pack();
		frame.setVisible(true);
	}

	private static final long serialVersionUID = -6467296753041382320L;

	public Client(String server) { 
		canvas = new Canvas(state, server);
		canvas.setSize(500, 500);
		this.getContentPane().setLayout(new java.awt.BorderLayout());
		canvas.setPreferredSize(canvas.getSize());
		this.state.canvas = canvas;
		this.server = server;
	}

	public Client() {
		this(null);
	}
	/**
	 * @uml.property  name="state"
	 * @uml.associationEnd  
	 */
	private ClientState state = new ClientState();
	/**
	 * @uml.property  name="canvas"
	 * @uml.associationEnd  
	 */
	private Canvas canvas;
	/**
	 * @uml.property  name="toolbox"
	 * @uml.associationEnd  
	 */
	private ToolBox toolbox;
	private ToolManager tools;
	private AdminPanel panel;

	private String server;
	private String username = null;
	private String passwd = null;
	@Override
	/**
	 * any initialization on graphical stuff should go here because the
	 * Swing event thread isn't created when the  constructor is called 
	 */
	public void start() {
		if(server == null)
			server = JOptionPane.showInputDialog(this, "Please enter the URL of the server to connect to","MICT",JOptionPane.PLAIN_MESSAGE);

		//if we still haven't specified a server, don't connect to a server
		if(server == null) server = "";
		if(server.equals("")) {
			System.out.println("not connected");
			tools = ToolManager.getServerToolManager(state);
		} else {
			tools = ToolManager.getClientToolManager(state);
		}
			
		state.tools = tools;
		toolbox = new ToolBox(state,tools);
		panel = new AdminPanel(state);
		tools.setToolBox(toolbox);

		/* toolbox = new ToolBox(state);
		 * tools= ToolManager.getClientToolManager(state,toolbox);
		 * state.tools = tools;
		 */

		this.getContentPane().add(toolbox, java.awt.BorderLayout.WEST);
		this.getContentPane().add(canvas, java.awt.BorderLayout.CENTER);
		this.getContentPane().add(panel, java.awt.BorderLayout.EAST);
		canvas.start(tools, server, username, passwd);
	}

	public ClientState getClientState() {
		return state;
	}

	/*public void jumpTo(final long x, final long y, final Image bitmap) { // what the hell is this.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				state.x = x;
				state.y = y;
				state.canvas_graphics.drawImage(bitmap, 0, 0, Client.this);
			}
		});
	}*/

	/**
	 * @return
	 * @uml.property  name="canvas"
	 */
	public Canvas getCanvas() {
		return canvas;
	}

	public Graphics2D getCanvasGraphics() {
		return canvas.getCanvasGraphics();
	}
}
