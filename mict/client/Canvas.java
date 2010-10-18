package mict.client;

import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Canvas extends JPanel {
	private static final long serialVersionUID = 1L; // @Ben: do we really need this? I mean, Canvas isn't even Serializable.

	public Canvas() {
		setCanvas(new BufferedImage(width, height, BufferedImage.TYPE_ARGB));
		setArtifactCanvas(new BufferedImage(width, height, BufferedImage.TYPE_ARGB));
	}
	
	private long x = 0L;
	private long y = 0L;
	private BufferedImage canvas;
	private BufferedImage artifacts;
	private Graphics2D canvasGraphics;
	private Graphics2D artifactsGraphics;

	public long getX() {
		return x;
	}

	public long getY() {
		return y;
	}

	public void setCanvas(BufferedImage canvas) {
		this.canvas = canvas;
		canvasGraphics = (Graphics2D)canvas.getGraphics();
	}

	public void setArtifactCanvas(BufferedImage canvas) {
		this.artifacts = canvas;
		artifactsGraphics = (Graphics2D)artifacts.getGraphics();
	}

	public Graphics2D getCanvasGraphics() {
		return canvasGraphics();
	}

	public Graphics2D getArtifactCanvasGraphics() {
		return artifactsGraphics();
	}

	public void paint(Graphics g) {
		g.drawImage(serverCanvas, 0, 0, this);
	}
}
