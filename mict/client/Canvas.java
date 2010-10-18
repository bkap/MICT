package mict.client;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class Canvas extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L; // @Ben: do we really need this? I mean, Canvas isn't even Serializable.
	private static final int MOUSE_HOVERED = 1;
	private static final int MOUSE_DRAGGED = 2;
	private static final int MOUSE_PRESSED = 3;
	private static final int MOUSE_RELEASED = 4;

	public Canvas(Client parent) {
		this.parent = parent;
		setCanvas(new BufferedImage(width, height, BufferedImage.TYPE_ARGB));
		setArtifactCanvas(new BufferedImage(width, height, BufferedImage.TYPE_ARGB));
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	private Client parent;
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
		g.drawImage(canvas, 0, 0, this);
		g.drawImage(artifacts, 0, 0, this);
	}

	public void mouseClicked(MouseEvent e) {} // DO NOT USE

	public void mousePressed(MouseEvent e) {
		// tool.mouseX, related
	}

	public void mouseReleased(MouseEvent e) {
		// tool.mouseX, related
	}

	public void mouseEntered(MouseEvent e) {} // I don't think there's a use for this, but I could be wrong

	public void mouseExited(MouseEvent e) {} // I don't think there's a use for this, but I could be wrong

	public void mouseDragged(MouseEvent e) {}

	public void mouseMoved(MouseEvent e) {
		render(e, MOUSE_HOVERED);
	}

	public void render(MouseEvent e, int type) {
		ClientState state = parent.getClientState();
		String phrase;
		switch(type) {
		case MOUSE_PRESSED:
			phrase = state.activeTool.mousePressed(e.getPoint(), artifactsGraphics);
			break;
		case MOUSE_RELEASED:
			phrase = state.activeTool.mouseReleased(e.getPoint(), artifactsGraphics);
			break;
		case MOUSE_HOVERED:
			phrase = state.activeTool.mouseHovered(e.getPoint(), artifactsGraphics);
			break;
		case MOUSE_DRAGGED:
			phrase = state.activeTool.mouseDragged(e.getPoint(), artifactsGraphics);
			break;
		default:
			return;
		}
		state.socket.draw(state.activeTool.getToolID(), phrase);
		state.activeTool.draw(phrase, canvasGraphics);
	}
}
