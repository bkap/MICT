package mict.client;

import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics;
public class Canvas extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Image serverCanvas;
	
	public Canvas() {
		
	}
	public void setServerCanvas(Image serverCanvas) {
		this.serverCanvas = serverCanvas;
	}
	public Graphics getServerGraphics() {
		return this.serverCanvas.getGraphics();
	}
	public void paint(Graphics g) {
		g.drawImage(serverCanvas, 0, 0, g.getColor(), this);
		super.paint(g);
	}
	
}
