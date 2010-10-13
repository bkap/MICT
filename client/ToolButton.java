package mict.client;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import mict.tools.Tool;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/** These are the buttons we will use to make the tool panel. When pressed, these button will automatically set their
 * tool as the active tool
 * 
 * @author Ben Kaplan
 *
 */
public class ToolButton extends JToggleButton {
	private Tool t;
	private ClientState state;

	private static final long serialVersionUID = -5088345052860020236L;

	public ToolButton(Tool t, ClientState state) {
		super();
		if(t.getIcon() != null) {
			this.setIcon(new ImageIcon(t.getIcon()));
		} else {
			this.setText(t.getToolName());
		}
		this.t = t;
		this.state = state;
		this.setToolTipText(t.getTooltip());
		this.addActionListener(new ToolListener());
	}
	public Tool getTool() {
		return t;
	}

	private class ToolListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			state.activeTool = t;
		}
	}
}
