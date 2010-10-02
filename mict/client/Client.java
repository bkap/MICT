package mict.client;

import javax.swing.*;

import mict.bridge.JythonBridge;
import mict.tools.Tool;
public class Client extends JApplet {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6467296753041382320L;
	
	public Client() { 
        super();
        this.add(JythonBridge.getTools("localhost",this.getGraphics()));

    }
    
    public static void main(String[] args) {
        Client c = new Client();
        JFrame frame = new JFrame("MICT v0.0");
        frame.setContentPane(c.getContentPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


    }
}
