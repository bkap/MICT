package mict.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
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
		this(state);
		setToolManager(tools);
	
		
	}
	public void setToolManager(ToolManager tools) {
		
		for(Tool t : tools.getAllTools()) {
			this.addTool(t);
		}
		if(bg.getButtonCount()> 0) {
			this.remove(loading);
			bg.setSelected(bg.getElements().nextElement().getModel(), true);
			state.activeTool = ((ToolButton)bg.getElements().nextElement()).getTool();
			this.validate();
		}
	}
	public ToolBox(ClientState state) {
		this.state = state;
		this.setPreferredSize(new java.awt.Dimension(100,300));
		this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		bg = new ButtonGroup();
		toolButtons = new Vector<ToolButton>();
		toolPanel = new JPanel();
		this.add(loading);
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
		state.selectedColor = activeColor;
	}
	private Color activeColor = Color.BLACK;
	private JButton colorButton;
	private Vector<ToolButton> toolButtons;
	private ButtonGroup bg;
	private JPanel toolPanel;
	private JLabel loading = new JLabel("Loading...");
	/**
	 * @uml.property  name="state"
	 * @uml.associationEnd  
	 */
	private ClientState state;
	/** Adds each tool in the list to the ToolBox. Also will remove the loading label if it's still there.
	 * @uml.property  name="tools"
	 * @uml.associationEnd  
	 */
	public void addTools(List<Tool> tools) {
		this.remove(loading);
		for(Tool t: tools) {
			addTool(t);
		}
		bg.setSelected(bg.getElements().nextElement().getModel(), true);
		state.activeTool = ((ToolButton)bg.getElements().nextElement()).getTool();
		System.out.println("tools added: " + bg.getButtonCount());
		
	}
	/** adds a single tool to the ToolBox. This will create a button for the tool with the Image specified in the tool or the tool's name
	 * if there is no icon. The tooltip will be set to the result of {@see mict.tools.Tool#getTooltip()}
	 * @see mict.client.ToolButton
	 * @param t
	 */
	public void addTool(Tool t) {
		this.remove(loading);
		ToolButton newButton = new ToolButton(t, state);
		bg.add(newButton);
		toolButtons.add(newButton);
		toolPanel.setLayout(new GridLayout((bg.getButtonCount() + 1) / 2,2));
		toolPanel.add(newButton);
		this.revalidate();
		
	}
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
