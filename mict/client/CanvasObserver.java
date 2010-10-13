package mict.client;

import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;
import java.awt.Point;
import javax.swing.JComponent;

public class CanvasObserver implements MouseInputListener {

	private ClientState state;

	public CanvasObserver(ClientState state) {
		this.state = state;
	}
	
	public void mouseClicked(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
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
		g.setColor(state.selectedColor);
		state.activeTool.mouseReleased(locationOnScreen, g);
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
		System.out.println("dragged");
		state.activeTool.mouseDragged(locationOnScreen, g);
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	}
}
