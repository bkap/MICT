package mict.client;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import mict.tools.Tool;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * These are the buttons we will use to make the tool panel. When pressed, these button will automatically set their tool as the active tool
 * @author  Ben Kaplan
 */
public class ToolButton extends JToggleButton {
	private static final long serialVersionUID = -5088345052860020236L;

	public ToolButton(Tool t, ClientState state) {
		if(t.getIcon() != null) {
			setIcon(new ImageIcon(t.getIcon()));
		} else {
			setText(t.getToolName());
		}
		this.t = t;
		this.state = state;
		this.setToolTipText(t.getTooltip());
		this.addActionListener(new ToolListener());
	}

	/**
	 * @uml.property  name="t"
	 * @uml.associationEnd  
	 */
	private Tool t;
	/**
	 * @uml.property  name="state"
	 * @uml.associationEnd  
	 */
	private ClientState state;

	public Tool getTool() {
		return t;
	}

	private class ToolListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			state.activeTool = t;
		}
	}
}
