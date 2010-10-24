package mict.client;

import mict.tools.Tool;
import mict.tools.ToolManager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import mict.client.ClientConnection;

/**
 * @author  bkaplan
 */
public class ClientState {
	/**
	 * @uml.property  name="activeTool"
	 * @uml.associationEnd  
	 */
	public Tool activeTool;
	public BufferedImage clipboard;
	public Graphics clipboard_graphics;

	public Color selectedColor = Color.BLACK;

	/**
	 * @uml.property  name="tools"
	 * @uml.associationEnd  
	 */
	public ToolManager tools;
}
