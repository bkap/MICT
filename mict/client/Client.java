package mict.client;

import javax.swing.*;

import java.awt.Graphics;
import java.awt.Image;
import mict.bridge.JythonBridge;
import mict.tools.ToolManager;

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
		canvas = new Canvas();
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

	@Override
	public void start() {
		state.canvas_graphics = canvas.getGraphics();
		state.intermediate_graphics = canvas.getIntermediateGraphics();
	}
	private ClientState state = new ClientState();
	private Canvas canvas;
	private ToolBox toolbox;
	public ClientState getClientState() {
		return state;
	}
	public void jumpTo(final long x, final long y, final Image bitmap) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				state.x = x;
				state.y = y;
				state.canvas_graphics.drawImage(bitmap, 0, 0, Client.this);
			}
		});
	}
	public Graphics getServerGraphics() {
		return this.canvas.getServerGraphics();
	}
}
