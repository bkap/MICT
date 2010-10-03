package mict.client;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import mict.tools.Tool;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
/** These are the buttons we will use to make the tool panel. When pressed, these button will automatically set their
 * tool as the active tool
 * 
 * @author Ben Kaplan
 *
 */
public class ToolButton extends JButton {
	private Tool t;
	private ClientState state;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5088345052860020236L;
	public ToolButton(Tool t, ClientState state) {
		super(new ImageIcon(t.getImage()));
		this.t = t;
		this.state = state;
		this.setToolTipText(t.getTooltip());
		this.addActionListener(new ToolListener());
	
	}
	private class ToolListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			state.ActiveTool = t;
		}
	}
}
