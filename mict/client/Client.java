package mict.client;

import javax.swing.*;


import java.awt.Graphics2D;

import mict.tools.ToolManager;

/**
 * The Client is the main class for the client side. It consists of a Canvas and a Toolbox. It also does the work to bridge those. All initialization should happen here. It is an Applet, but can be run as an application by sticking its contentPane in a JFrame.
 * @author  Ben Kaplan
 */
public class Client extends JApplet {
	public static void main(String[] args) {
		Client c = new Client();
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
		canvas.setSize(300, 300);
		this.getContentPane().setLayout(new java.awt.BorderLayout());
		canvas.setPreferredSize(canvas.getSize());
		this.state.canvas = canvas;
		
		
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
	@Override
	/**
	 * any initialization on graphical stuff should go here because the
	 * Swing event thread isn't created when the  constructor is called 
	 */
	public void start() {

		String servername = JOptionPane.showInputDialog(this, "Please enter the URL of the server to connect to","MICT",JOptionPane.PLAIN_MESSAGE);

		//if we still haven't specified a server, don't connect to a server
		if(servername == null) servername = "";
		if(servername.equals("")) {
			System.out.println("not connected");
			tools = ToolManager.getServerToolManager(state);
		} else {
				tools = ToolManager.getClientToolManager(state);
				//tools = ToolManager.getServerToolManager(state);
		}
			
		state.tools = tools;
		toolbox = new ToolBox(state,tools);
		tools.setToolBox(toolbox);
		

		/* toolbox = new ToolBox(state);
		 * tools= ToolManager.getClientToolManager(state,toolbox);
		 * state.tools = tools;
			
		*/
		this.getContentPane().add(toolbox, java.awt.BorderLayout.WEST);
		this.getContentPane().add(canvas, java.awt.BorderLayout.CENTER);
		canvas.start(tools, servername);
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
