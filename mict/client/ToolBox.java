package mict.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import mict.tools.Tool;
import mict.tools.ToolManager;

/**
 * This is the ToolBox component of the Client. It contains 2 panels: the tool panel and the color chooser. The Tool Panel is filled with {mict.client.ToolButton Tool buttons} that allow you to trigger the active tool. The color choosing panel allows the user to select the active color for new shapes.
 * @author  bkaplan
 */
public class ToolBox extends JPanel {
	private static final long serialVersionUID = 1L; // @Ben this is incorrect. pick something that's even remotely entropic, please.

	public ToolBox(ClientState state, ToolManager tools) {
		this.state = state;
		this.setPreferredSize(new java.awt.Dimension(100,300));
		this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		ButtonGroup bg = new ButtonGroup();
		 
		for(Tool t : tools.getAllTools()) {
			ToolButton b = new ToolButton(t, state);
			bg.add(b);
		}
		bg.setSelected(bg.getElements().nextElement().getModel(), true);
		state.activeTool = ((ToolButton)bg.getElements().nextElement()).getTool();
	
		JPanel toolPanel = new JPanel();
		toolPanel.setLayout(new GridLayout((bg.getButtonCount() + 1) / 2,2));
		Enumeration<AbstractButton> allButtons = bg.getElements();
		while(allButtons.hasMoreElements()) {
			toolPanel.add(allButtons.nextElement());
		}
		this.add(toolPanel);
		this.add(Box.createVerticalGlue());
		colorButton = new JButton(new ColorIcon());
		Box b = Box.createHorizontalBox();
		b.add(Box.createHorizontalGlue());
		b.add(colorButton);
		b.add(Box.createHorizontalGlue());
		this.add(b);
		this.add(Box.createVerticalGlue());
		colorButton.addActionListener(new ColorChooserListener());
	}

	private Color activeColor = Color.BLACK;
	private JButton colorButton;
	/**
	 * @uml.property  name="state"
	 * @uml.associationEnd  
	 */
	private ClientState state;
	/**
	 * @uml.property  name="tools"
	 * @uml.associationEnd  
	 */
	private ToolManager tools;

	private class ColorIcon implements Icon {
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(activeColor);
			g.fillRect(x, y, getIconWidth(), getIconHeight());
		}

		public int getIconHeight() {
			return 32;
		}

		public int getIconWidth() {
			return 32;
		}
		
	}

	private class ColorChooserListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Color c = JColorChooser.showDialog(ToolBox.this, "Select Active Color", activeColor);
			//this isn't working on the Mac
			if(c != null) {
				System.out.println("setting active color: " + c);
				activeColor = c;
				state.selectedColor = c;
			}
		}
	}
}
