package mict.client;

import javax.swing.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import mict.tools.ToolManager;

/**
 * The Client is the main class for the client side. It consists of a Canvas and a Toolbox. It also does the work to bridge those.
 * All initialization should happen here.
 * It is an Applet, but can be run as an application by sticking its contentPane in a JFrame.
 * @author bkaplan
 *
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

	public Client() { 
		//JythonBridge.initialize();
		state.canvas = canvas;
		canvas.setSize(300, 300);
		canvas.setPreferredSize(canvas.getSize());
		CanvasObserver obs = new CanvasObserver(state);
		canvas.addMouseListener(obs);
		canvas.addMouseMotionListener(obs);
		Box b = javax.swing.Box.createHorizontalBox();
		ToolManager t = new ToolManager(state);
		toolbox = new ToolBox(state, t);
		b.add(toolbox);
		b.add(canvas);
		this.getContentPane().add(b);
	}

	private ClientState state = new ClientState();
	private Canvas canvas = new Canvas();
	private ToolBox toolbox;

	@Override
	/**
	 * any initialization on graphical stuff should go here because the
	 * Swing event thread isn't created when the  constructor is called 
	 */
	public void start() {
		String servername = JOptionPane.showInputDialog(this, "Please enter the URL of the server to connect to","MICT",JOptionPane.PLAIN_MESSAGE);
		if(servername == null) servername = localhost;
		state.socket = new ClientConnection(servername, "username", "password", this);
		state.socket.requestCanvasRect(canvas.getX(), canvas.getY(), canvas.getWidth(), canvas.getHeight());
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

	public Graphics getServerGraphics() {
		return canvas.getCanvasGraphics();
	}
}
