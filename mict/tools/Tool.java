package mict.tools;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.Serializable;
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

	Image getIcon();
	
	String getTooltip();
	
	/** provides the human-readable name of the tool (e.g. for tooltexts)
	 */
	String getToolName();

	/** provides the internal name of the tool
	 */
	String getToolID();
}
