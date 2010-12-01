package mict.client;

import java.awt.*;
import java.awt.image.*;

import mict.tools.*;
import mict.util.*;

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

	public Canvas canvas;

	public PermissionSet permissions = new PermissionSet();
}
