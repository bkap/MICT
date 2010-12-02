package mict.client;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

import mict.tools.ImageData;
import mict.tools.ToolManager;

/** This is the Canvas viewport. It contains the drawn image as well as
 * maintaining the connection to the server. All drawing operations should go
 * through here
 * @author bkaplan
 */
public class Canvas extends JPanel implements MouseListener, MouseMotionListener, ComponentListener {
	private static final long serialVersionUID = 1L; // @Ben: do we really need this? I mean, Canvas isn't even Serializable.
	private static final int MOUSE_HOVERED = 1;
	private static final int MOUSE_DRAGGED = 2;
	private static final int MOUSE_PRESSED = 3;
	private static final int MOUSE_RELEASED = 4;
	private static final int MOUSE_CLICKED = 5;

	public Canvas(ClientState state, String servername) {
		this.state = state;
		this.servername = servername;
		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(this);
	}

	public void start(ToolManager t, String servername, String username, String passwd) {
		//if we haven't already specified a server, ask for it
		if(username == null) username = "anonymous";
		if(passwd == null) passwd = "";
		this.servername = servername;
		socket = new ClientConnection(servername, username, passwd, this, t);
		BufferedImage c = new BufferedImage(getWidth() * 3, getHeight() * 3, BufferedImage.TYPE_INT_ARGB);
		setCanvas(c);
		setArtifactCanvas(new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB));
		Graphics g = getCanvasGraphics();
		socket.requestCanvasRect(imgx, imgy, c.getWidth(this), c.getHeight(this));
		socket.start();
	}

	/**
	 * @uml.property name="parent"
	 * @uml.associationEnd  
	 */
	private ClientState state;
	private long x = 0L;
	private long y = 0L;
	private long imgx = 0L;
	private long imgy = 0L;
	private int prevwidth = 0;
	private int prevheight = 0;
	private BufferedImage canvas;
	private BufferedImage artifacts;
	private String servername;
	/**
	 * @uml.property name="canvasGraphics"
	 */
	private Graphics2D canvasGraphics;
	private Graphics2D artifactsGraphics;
	private boolean inside = false;
	private boolean panned = false;

	/**
	 * @uml.property name="socket"
	 * @uml.associationEnd  
	 */
	public ClientConnection socket;

	public ClientState getClientState() {
		return state;
	}
	
	public long getUserX() {
		return x;
	}
	
	public long getUserY() {
		return y;
	}

	/**
	 * @param canvas
	 * @uml.property name="canvas"
	 */
	public void setCanvas(BufferedImage canvas) {
		this.canvas = canvas;
		canvasGraphics = (Graphics2D)canvas.getGraphics();
		imgx = (canvas.getWidth(this) - getWidth()) / -2;
		imgy = (canvas.getHeight(this) - getHeight()) / -2;
		canvasGraphics.translate(-imgx, -imgy);
	}

	public void setArtifactCanvas(BufferedImage canvas) {
		this.artifacts = canvas;
		artifactsGraphics = (Graphics2D)artifacts.getGraphics();
		artifactsGraphics.setBackground(new Color(0,0,0,0));
	}

	/**
	 * @return
	 * @uml.property name="canvasGraphics"
	 */
	public Graphics2D getCanvasGraphics() {
		return canvasGraphics;
	}

	public Graphics2D getArtifactCanvasGraphics() {
		return artifactsGraphics;
	}

	public BufferedImage getCanvasImage() {
		return canvas;
	}

	public Graphics getClipboardGraphics() {
		return state.clipboard_graphics;
	}

	public Color getSelectedColor() {
		return state.selectedColor;
	}

	public void paint(Graphics g) {
		g.drawImage(canvas,
			(int)(imgx),
			(int)(imgy),
			this
		);
		if(!inside) return;
		g.drawImage(artifacts, 0, 0, this);
	}

	public void mouseEntered(MouseEvent e) {
		inside = true;
	}

	public void mouseExited(MouseEvent e) {
		inside = false;
	}

	public void componentHidden(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}

	public void componentResized(ComponentEvent e) {
		if(socket == null) return;
		BufferedImage nc = new BufferedImage(3 * getWidth(), 3 * getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = nc.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, nc.getWidth(this), nc.getHeight(this));
		g.drawImage(canvas, getWidth() - canvas.getWidth(this), getHeight() - canvas.getHeight(), this);
		setCanvas(nc);
		BufferedImage na = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		setArtifactCanvas(na);
		socket.requestCanvasRect(x, y, getWidth(), getHeight());
	}

	public void mousePressed(MouseEvent e) {
		render(e, MOUSE_PRESSED);
    }

	public void mouseReleased(MouseEvent e) {
		render(e, MOUSE_RELEASED);
		if(!panned) return;
		panned = true;
		BufferedImage nc = new BufferedImage(getWidth() * 3, getHeight() * 3, BufferedImage.TYPE_INT_ARGB);
		Graphics g = nc.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, nc.getWidth(this), nc.getHeight(this));
		g.drawImage(canvas, (int)imgx + getWidth(), (int)imgy + getHeight(), this);
		setCanvas(nc);
		repaint();
    }

	public void mouseDragged(MouseEvent e) {
		render(e, MOUSE_DRAGGED);
    }

	public void mouseMoved(MouseEvent e) {
		render(e, MOUSE_HOVERED);
	}

	public void mouseClicked(MouseEvent e) {
		render(e,MOUSE_CLICKED);
	} 

	public void render(MouseEvent e, int type) {
		if(!inside || state.activeTool == null) return;
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
		case MOUSE_CLICKED:
			phrase = state.activeTool.mouseClicked(e.getPoint(), artifactsGraphics);
			break;
		default:
			return;
		}
		ImageData data = state.activeTool.getLastImage();
		if(data != null) {
			socket.sendImage(data.x, data.y, data.img);
			getCanvasGraphics().drawImage(data.img, data.x, data.y, this);
			return;
		}
		if(phrase == null || phrase.trim().equals("")) {
			repaint();
			return;
		}

		System.out.println("Hey, user@(" + x + "," + y + ")!");
		socket.sendDraw(state.activeTool.getToolID(), phrase);
		state.activeTool.draw(phrase, canvasGraphics);
		if(state.activeTool.getToolID().equals("pan")) {
			int index = phrase.indexOf(',');
			long dx = Integer.parseInt(phrase.substring(0,index));
			long dy = Integer.parseInt(phrase.substring(index+1));
			x += dx;
			y += dy;
			imgx -= dx;
			imgy -= dy;
			panned = true;
		}
		repaint();
	}
	
	public void draw(String toolid, String phrase, int offx, int offy) {
		Graphics2D g = (Graphics2D)getCanvasGraphics().create();
		g.translate(offx, offy);
		state.tools.draw(toolid, phrase, g);
	}
}
