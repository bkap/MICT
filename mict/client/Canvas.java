package mict.client;

import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
public class Canvas extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BufferedImage serverCanvas;
	private Graphics2D bufferedGraphics;
	public Canvas() {
	}
	public void setServerCanvas(BufferedImage serverCanvas) {
		this.serverCanvas = serverCanvas;
	}
	public Graphics2D getServerGraphics() {
		if(bufferedGraphics != null) {
			return bufferedGraphics;
		}
		bufferedGraphics = this.serverCanvas.createGraphics();
		return bufferedGraphics;
	}
	public void paint(Graphics g) {
		//super.paint(g);
		
		if(serverCanvas != null) {

			g.drawImage(serverCanvas, 0, 0, this);
			
		}
		
	}
	
}
