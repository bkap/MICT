package mict.client;

import javax.swing.JPanel;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
/**
 *  This is the ToolBox component of the Client. It contains 2 panels: the tool panel and the color chooser.
 *  The Tool Panel is filled with {mict.client.ToolButton Tool buttons} that allow you to trigger the active tool.
 *  The color choosing panel allows the user to select the active color for new shapes.
 * 
 * 
 * @author bkaplan
 *
 */
public class ToolBox extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color activeColor = Color.BLACK;
	private JButton colorButton;
	public ToolBox() {
		colorButton = new JButton(new ColorIcon());
		this.add(colorButton);
		colorButton.addActionListener(new ColorChooserListener());
	}
	private class ColorIcon implements Icon {
		
		public int getIconHeight() {
			return 16;
		}
		
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			// TODO Auto-generated method stub
			Color c2 = g.getColor();
			g.setColor(activeColor);
			g.fillRect(x, y, getIconWidth(), getIconHeight());
			g.setColor(c2);
		}
		@Override
		public int getIconWidth() {
			return 16;
		}
		
	}
	private class ColorChooserListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Color c = JColorChooser.showDialog(ToolBox.this, "Select Active Color", activeColor);
			//this isn't working on the Mac
			if(c != null) {
				activeColor = c;
				
			}
		}
	}
}
