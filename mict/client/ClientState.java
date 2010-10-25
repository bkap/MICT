package mict.client;

import mict.tools.Tool;
import mict.tools.ToolManager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** This class stores the shared-state for the Client. It's basically just a collection of global attributes
 * @author  bkaplan
 */
public class ClientState {
	/**
	 * @uml.property  name="activeTool"
	 * @uml.associationEnd  
	 */
	public Tool activeTool;
	/**this is the image that will hold the clipboard.
	 * 
	 */
	public BufferedImage clipboard;
	/** the graphics object that is drawn on to copy something to the clipboard
	 * 
	 */
	public Graphics clipboard_graphics;
	/** the color that this user will draw with
	 * 
	 */
	public Color selectedColor = Color.BLACK;

	/**
	 * @uml.property  name="tools"
	 * @uml.associationEnd  
	 */
	public ToolManager tools;
}
