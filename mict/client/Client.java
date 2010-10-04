package mict.client;

import javax.swing.*;
import mict.bridge.JythonBridge;

public class Client extends JApplet {
	public static void main(String[] args) {
		Client c = new Client();
		JFrame frame = new JFrame("MICT v0.0");
		frame.setContentPane(c.getContentPane());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	private static final long serialVersionUID = -6467296753041382320L;

	public Client() { 
		add(JythonBridge.getTools("localhost", getContentPane().getGraphics()));
	}

	private ClientState state = new ClientState();

	public jumpTo(long x, long y, Image bitmap) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				state.x = x;
				state.y = y;
				state.canvas_graphics.drawImage(bitmap, 0, 0, Client.this);
			}
		});
	}
}
