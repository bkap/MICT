package mict.client;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;

/**
 * @author  bkaplan
 */
public class Canvas extends JPanel implements MouseListener, MouseMotionListener, ComponentListener {
	private static final long serialVersionUID = 1L; // @Ben: do we really need this? I mean, Canvas isn't even Serializable.
	private static final int MOUSE_HOVERED = 1;
	private static final int MOUSE_DRAGGED = 2;
	private static final int MOUSE_PRESSED = 3;
	private static final int MOUSE_RELEASED = 4;

	public Canvas(ClientState state) {
		this.state = state;
		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(this);
	}
	public void start() {
		String servername = JOptionPane.showInputDialog(this, "Please enter the URL of the server to connect to","MICT",JOptionPane.PLAIN_MESSAGE);
		if(servername == null) servername = "";
		socket = new ClientConnection(servername, "username", "password", this);
		socket.requestCanvasRect(this.getUserX(), this.getUserY(), this.getWidth(), this.getHeight());
		this.setCanvas(new BufferedImage(this.getWidth(), this.getHeight(),BufferedImage.TYPE_INT_ARGB));
		this.setArtifactCanvas(new BufferedImage(this.getWidth(), this.getHeight(),BufferedImage.TYPE_INT_ARGB));
		Color c = Color.WHITE;
		Graphics g = this.getCanvasGraphics();
		g.setColor(c);
		g.fillRect(0, 0,this.getWidth(), this.getHeight());
		socket.start();
	}
	/**
	 * @uml.property  name="parent"
	 * @uml.associationEnd  
	 */
	private ClientState state;
	private long x = 0L;
	private long y = 0L;
	private int prevwidth = 0;
	private int prevheight = 0;
	private BufferedImage canvas;
	private BufferedImage artifacts;
	/**
	 * @uml.property  name="canvasGraphics"
	 */
	private Graphics2D canvasGraphics;
	private Graphics2D artifactsGraphics;
	private boolean inside = false;
	
	/**
	 * @uml.property  name="socket"
	 * @uml.associationEnd  
	 */
	public ClientConnection socket;
	
	
	public long getUserX() {
		return x;
	}
	
	public long getUserY() {
		return y;
	}

	/**
	 * @param canvas
	 * @uml.property  name="canvas"
	 */
	public void setCanvas(BufferedImage canvas) {
		this.canvas = canvas;
		canvasGraphics = (Graphics2D)canvas.getGraphics();
	}

	public void setArtifactCanvas(BufferedImage canvas) {
		this.artifacts = canvas;
		artifactsGraphics = (Graphics2D)artifacts.getGraphics();
		artifactsGraphics.setBackground(new Color(0,0,0,0));
	}

	/**
	 * @return
	 * @uml.property  name="canvasGraphics"
	 */
	public Graphics2D getCanvasGraphics() {
		return canvasGraphics;
	}

	public Graphics2D getArtifactCanvasGraphics() {
		return artifactsGraphics;
	}

	
	public Graphics getClipboardGraphics() {
		return state.clipboard_graphics;
	}
	public Color getSelectedColor() {
		return state.selectedColor;
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
		ClientConnection conn = socket;
		if(conn == null) return;
		BufferedImage nc = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = nc.getGraphics();
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(canvas, 0, 0, this);
		setCanvas(nc);
		BufferedImage na = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		setArtifactCanvas(na);
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
		socket.sendDraw(state.activeTool.getToolID(), phrase);
		state.activeTool.draw(phrase, canvasGraphics);
		if(state.activeTool.getToolID().equals("pan")) {
			System.out.println("PANNING! DO SPECIAL STUFF!");
			int index = phrase.indexOf(',');
			int dx = Integer.parseInt(phrase.substring(0,index));
			int dy = Integer.parseInt(phrase.substring(index+1));
			x += dx;
			y += dy;
			if(dx > 0) { //moved the image on the canvas to the right, need left side
				socket.requestCanvasRect(x, y, dx, getHeight());
				if(dy > 0) {
					socket.requestCanvasRect(x + dx, y, getWidth() - dx, dy);
				} else if(dy < 0) {
					socket.requestCanvasRect(x + dx, y + getHeight() + dy, getWidth() - dx, -dy);
				}
			} else if(dx < 0) {
				socket.requestCanvasRect(x + getWidth() + dx, y, -dx, getHeight());
				if(dy > 0) {
					socket.requestCanvasRect(x, y, getWidth() + dx, dy);
				} else if(dy < 0) {
					socket.requestCanvasRect(x, y + getHeight() + dy, getHeight() + dx, -dy);
				}
			} else {
				if(dy > 0) {
					socket.requestCanvasRect(x, y, getWidth(), dy);
				} else if(dy < 0) {
					socket.requestCanvasRect(x, y + getHeight() + dy, getWidth(), -dy);
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
	
	public void draw(String toolid, String phrase) {
		this.state.tools.draw(toolid, phrase, this.getCanvasGraphics());
	}
	
}
