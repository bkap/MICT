package mict.tools;

import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;
/** The Tool interface that defines every tool in MICT. 
 *
 * Tools should be written in
 * Jython and stored in the tools folder on the server. Clients will update
 * tools automatically upon connecting to the server. Although it's not
 * specified by the Interface, every tool should have a constructor that takes
 * an optional {@link mict.client.ClientState} object. See the included tools
 * for examples of how to implement a tool. 
 *
 * Tools work via dispatch. Upon recieving a mouse event, the {@link mict.client.Canvas} on a client will
 * transmit the location of the mouse event to the tool through the
 * corresponding mouse* method. 
 *
 * Each mouse* method is passed two parameters: the location of the mouse event
 * and a graphics context. The graphics context passed is temporary and will be
 * cleared before each mouse event so the tool should draw the whole temporary
 * image each time a method is called. Each of these methods returns a String.
 * That String will be passed to the {@link #draw(String,Graphics2D)} method of
 * the Tool on the local client, on the server, and all other clients that will
 * see changes from this event. The Server will use the {@link
 * #getAffectedArea(String)} method to determine which clients are sent the
 * update.
 *
 * If the tool wishes to draw an image on the screen, it should have
 * {@link #getLastImage()} return a non-null value. Each image should only be returned
 * once (subsequent calls to the method should return a new image to be drawn,
 * or null if there is no new image.
 * @author Ben Kaplan
 * @author Rob Weisler
 */
public interface Tool extends Serializable {

	/** this method will be called when the mouse is clicked on the canvas.
	 * 
	 * @param locationOnScreen : the location on the canvas currently to update.
	 * @param g : the graphics context corresponding to the artifact layer of the canvas UI
	 */
	String mousePressed(Point locationOnScreen, Graphics2D g);
	
	/** this method will be called intermittently as the mouse is dragged across the screen during a mouse press.
	 *  
	 * @param locationOnScreen: the location of the mouse on the canvas currently
	 * @param g : the graphics context corresponding to the artifact layer of the canvas UI
	 */
	String mouseDragged(Point locationOnScreen, Graphics2D g);
	
	/** this method will be called intermittently as the mouse is moved across the screen while no buttons are being pressed
	 *  
	 * @param locationOnScreen: the location of the mouse on the canvas currently
	 * @param g : the graphics context corresponding to the artifact layer of the canvas UI
	 */
	String mouseHovered(Point locationOnScreen, Graphics2D g);
	
	/** this method will be called once the mouse has been picked up. At this point, the tool should be prepared to contact the server to update the canvas
	 * @param locationOnScreen the location on the graphics context that the user is drawing on
	 * @param g : the graphics context corresponding to the artifact layer of the canvas UI
	 */
	String mouseReleased(Point locationOnScreen, Graphics2D g);

	/**this method is called when the mouse is pressed and released in the same
	 * location. Only implement it if you want to use it.
	 * @param locationOnScreen the location on the graphics context that the
	 * user is drawing on
	 * @param g: the artifact layer of the canvasUI
	 */
	String mouseClicked(Point locationOnScreen, Graphics2D g);
	/** given the serialized string of the tool (the string that is sent to the server), update the graphics context accordingly
	 * 
	 * @param s the String containing the serialized form of this tool's action
	 * @param g the graphics context to draw the data on
	 */
	void draw(String s, Graphics2D g);
	
	/** given an encoded draw action, return a rectangle (x, y, w, h) that covers entirely the area of canvas changed by the decoded action
	 *
	 * @param phrase a string containing raw data provided by the user's version of the Tool
	 *
	 * @return an array describing a rectangular area afftected by the given phrase. Takes form { long xleft, long ytop, long width, long height }
	 */
	long[] getAffectedArea(String phrase);
	/** Gets the image that will represent the tool in the ToolBox
	 */
	Image getIcon();
	/**Returns the help text the user will see when they hover over the tool
	 */
	String getTooltip();
	
	/** provides the human-readable name of the tool (e.g. for tooltexts)
	 */
	String getToolName();

	/** provides the internal name of the tool.
	 *
	 * Every tool must have a unique toolID.
	 */
	String getToolID();
	/** Gets an image that should be drawn on the canvas at a specific point.
	 * Should return null if there is no image to be drawn.
	 *
	 */
	ImageData getLastImage();

}
