package mict.client;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;
import java.awt.Point;
import javax.swing.JComponent;
public class CanvasObserver implements MouseInputListener{
	private ClientState state;
	public CanvasObserver(ClientState state) {
		this.state = state;
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point locationOnScreen = new Point(e.getX(), e.getY());
	
		JComponent source = ((JComponent)e.getSource());
		java.awt.Graphics g = source.getGraphics();
		state.ActiveTool.mousePressed(locationOnScreen, g);
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Point locationOnScreen = new Point(e.getX(), e.getY());
		
		JComponent source = ((JComponent)e.getSource());
		java.awt.Graphics g = source.getGraphics();
		state.ActiveTool.mouseReleased(locationOnScreen, g);
		state.socket.sendConnection(state.ActiveTool.getToolID(), state.ActiveTool.serialize());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point locationOnScreen = new Point(e.getX(), e.getY());
		JComponent source = ((JComponent)e.getSource());
		java.awt.Graphics g = source.getGraphics();
		state.ActiveTool.mouseMoved(locationOnScreen, g);
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
