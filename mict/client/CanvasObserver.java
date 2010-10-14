package mict.client;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.JComponent;

public class CanvasObserver implements MouseListener, MouseMotionListener {

	private ClientState state;
	private boolean mousepress = false;
	public CanvasObserver(ClientState state) {
		this.state = state;
	}
	
	public void mouseClicked(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		mousepress = true;
		Point locationOnScreen = new Point(e.getX(), e.getY());
	
		JComponent source = ((JComponent)e.getSource());
		java.awt.Graphics g = source.getGraphics();
		g.setColor(state.selectedColor);
		state.activeTool.mousePressed(locationOnScreen, g);
		
	}

	public void mouseReleased(MouseEvent e) {
		Point locationOnScreen = new Point(e.getX(), e.getY());

		JComponent source = ((JComponent)e.getSource());
		java.awt.Graphics g = source.getGraphics();
		if(state != null && state.canvas.equals(source)) {
			
			state.canvas.getServerGraphics().setColor(state.selectedColor);
			System.out.println(state.canvas.getServerGraphics().getColor());
			state.activeTool.mouseReleased(locationOnScreen, state.canvas.getServerGraphics());
			
			
		}
		g.setColor(state.selectedColor);
		//state.activeTool.mouseReleased(locationOnScreen, g);
		mousepress = false;
		
		if(state.socket != null) {
			state.socket.sendConnection(state.activeTool.getToolID(), state.activeTool.serlialize());
		}
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mouseDragged(MouseEvent e) {
		Point locationOnScreen = new Point(e.getX(), e.getY());
		JComponent source = ((JComponent)e.getSource());
		java.awt.Graphics g = source.getGraphics();
		g.setColor(state.selectedColor);
		if(state != null && state.canvas.equals(source)) {
			g.clearRect(0, 0, source.getWidth(), source.getHeight());
			state.canvas.paint(g);
		}
		state.activeTool.mouseDragged(locationOnScreen, g);
	}

	public void mouseMoved(MouseEvent e) {
		if(mousepress) {
			mouseDragged(e);
		}
		// TODO Auto-generated method stub
	}
}
