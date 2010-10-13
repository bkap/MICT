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
	
	private Image serverCanvas;
	private BufferedImage intermediateImage;
	public Canvas() {
	}
	public void setServerCanvas(Image serverCanvas) {
		this.serverCanvas = serverCanvas;
	}
	public Graphics getIntermediateGraphics() {
		if(intermediateImage == null) {
			intermediateImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
			intermediateImage.getGraphics().setColor(new Color(0,0,0,255));
			intermediateImage.getGraphics().fillRect(0, 0, getWidth(), getHeight());
			System.out.println(getWidth() + " " + getHeight());
			
		}
		return this.intermediateImage.getGraphics();
	}
	public Graphics getServerGraphics() {
		return this.serverCanvas.getGraphics();
	}
	public void paint(Graphics g) {
		if(serverCanvas != null) {
			g.drawImage(serverCanvas, 0, 0, g.getColor(), this);
		}
		g.drawImage(intermediateImage,0,0,this);
		
	}
	
}
