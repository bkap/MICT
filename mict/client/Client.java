package mict.client;

import javax.swing.*;

import java.awt.Graphics;
import java.awt.Image;
import mict.bridge.JythonBridge;

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
		canvas = new JPanel();
		canvas.setSize(100, 100);
		canvas.setPreferredSize(canvas.getSize());
		Box b = javax.swing.Box.createHorizontalBox();
		toolbox = new ToolBox();
		b.add(toolbox);
		b.add(canvas);
		this.getContentPane().add(b);
	}
	public void paint(Graphics g) {
		g.fillRect(10, 10, 10, 10);
	}

	private ClientState state = new ClientState();
	private JPanel canvas;
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
}
