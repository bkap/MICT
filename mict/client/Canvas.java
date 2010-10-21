package mict.client;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

public class Canvas extends JPanel implements MouseListener, MouseMotionListener, ComponentListener {
	private static final long serialVersionUID = 1L; // @Ben: do we really need this? I mean, Canvas isn't even Serializable.
	private static final int MOUSE_HOVERED = 1;
	private static final int MOUSE_DRAGGED = 2;
	private static final int MOUSE_PRESSED = 3;
	private static final int MOUSE_RELEASED = 4;

	public Canvas(Client parent) {
		this.parent = parent;
		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(this);
	}
	
	private Client parent;
	private long x = 0L;
	private long y = 0L;
	private int prevwidth = 0;
	private int prevheight = 0;
	private BufferedImage canvas;
	private BufferedImage artifacts;
	private Graphics2D canvasGraphics;
	private Graphics2D artifactsGraphics;
	private boolean inside = false;

	public long getUserX() {
		return x;
	}

	public long getUserY() {
		return y;
	}

	public void setCanvas(BufferedImage canvas) {
		this.canvas = canvas;
		canvasGraphics = (Graphics2D)canvas.getGraphics();
	}

	public void setArtifactCanvas(BufferedImage canvas) {
		this.artifacts = canvas;
		artifactsGraphics = (Graphics2D)artifacts.getGraphics();
		artifactsGraphics.setBackground(new Color(0,0,0,0));
	}

	public Graphics2D getCanvasGraphics() {
		return canvasGraphics;
	}

	public Graphics2D getArtifactCanvasGraphics() {
		return artifactsGraphics;
	}

	public void paint(Graphics g) {
		g.drawImage(canvas, 0, 0, this);
		if(!inside) return;
		g.drawImage(artifacts, 0, 0, this);
	}

	public void mouseEntered(MouseEvent e) {
		inside = true;
	}

	public void mouseExited(MouseEvent e) {
		inside = false;
	}

	public void mouseClicked(MouseEvent e) {} // DO NOT USE
	public void componentHidden(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}

	public void componentResized(ComponentEvent e) {
		BufferedImage nc = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = nc.getGraphics();
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(canvas, 0, 0, this);
		setCanvas(nc);
		BufferedImage na = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		setArtifactCanvas(na);
		ClientConnection conn = parent.getClientState().socket;
		if(getWidth() > prevwidth) {
			conn.requestCanvasRect(x + getWidth(), y, getWidth() - prevwidth, getHeight());
			if(getHeight() > prevheight) {
				conn.requestCanvasRect(x, y + getHeight(), prevwidth, getHeight() - prevheight);
			}
		} else if(getHeight() > prevheight) {
			conn.requestCanvasRect(x, y + getHeight(), getWidth(), getHeight() - prevheight);
		}
		prevwidth = getWidth();
		prevheight = getHeight();
	}

	public void mousePressed(MouseEvent e) {
		render(e, MOUSE_PRESSED);
    }

	public void mouseReleased(MouseEvent e) {
		render(e, MOUSE_RELEASED);
    }

	public void mouseDragged(MouseEvent e) {
		render(e, MOUSE_DRAGGED);
    }

	public void mouseMoved(MouseEvent e) {
		render(e, MOUSE_HOVERED);
	}

	public void render(MouseEvent e, int type) {
		if(!inside) return;
		ClientState state = parent.getClientState();
		String phrase;
		artifactsGraphics.clearRect(0, 0, getWidth(), getHeight());
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
		if(phrase == null || phrase.trim().equals("")) {
			repaint();
			return;
		}
		state.socket.sendDraw(state.activeTool.getToolID(), phrase);
		state.activeTool.draw(phrase, canvasGraphics);
		if(state.activeTool.getToolID().equals("pan")) {
			System.out.println("PANNING! DO SPECIAL STUFF!");
			int index = phrase.indexOf(',');
			int dx = Integer.parseInt(phrase.substring(0,index));
			int dy = Integer.parseInt(phrase.substring(index+1));
			x += dx;
			y += dy;
			if(dx > 0) { //moved the image on the canvas to the right, need left side
				state.socket.requestCanvasRect(x, y, dx, getHeight());
				if(dy > 0) {
					state.socket.requestCanvasRect(x + dx, y, getWidth() - dx, dy);
				} else if(dy < 0) {
					state.socket.requestCanvasRect(x + dx, y + getHeight() + dy, getWidth() - dx, -dy);
				}
			} else if(dx < 0) {
				state.socket.requestCanvasRect(x + getWidth() + dx, y, -dx, getHeight());
				if(dy > 0) {
					state.socket.requestCanvasRect(x, y, getWidth() + dx, dy);
				} else if(dy < 0) {
					state.socket.requestCanvasRect(x, y + getHeight() + dy, getHeight() + dx, -dy);
				}
			} else {
				if(dy > 0) {
					state.socket.requestCanvasRect(x, y, getWidth(), dy);
				} else if(dy < 0) {
					state.socket.requestCanvasRect(x, y + getHeight() + dy, getWidth(), -dy);
				}
			}
			BufferedImage nc = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics g = nc.getGraphics();
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.drawImage(canvas, dx, dy, this);
			setCanvas(nc);
		}
		repaint();
	}
}
