package mict.tools;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Image;
import java.io.Serializable;
public interface Tool extends Serializable {

	/** this method will be called when the mouse is clicked on the canvas.
	 * 
	 * @param locationOnScreen : the location on the canvas currently to update.
	 * @param locationOnCanvas : the location on the canvas as a whole where the mouse was pressed
	 * @param g : the graphics context in which to use the tool
	 */
	String mousePressed(Point locationOnScreen, Graphics g);
	
	/** this method will be called intermittently as the mouse is dragged across the screen during a mouse press.
	 *  
	 * @param locationOnScreen: the location of the mouse on the canvas currently
	 * @param locationOnCanvas: the point in the overall canvas where the user is
	 * @param g the graphics context in which the tool will operate
	 */
	String mouseDragged(Point locationOnScreen, Graphics g);
	
	/** this method will be called once the mouse has been picked up. At this point, the tool should be prepared to contact the server to update the canvas
	 * @param locationOnScreen the location on the graphics context that the user is drawing on
	 * @param locationOnCanvas the location on the canvas that the user is currently viewing.
	 * @param g the graphics context to draw on
	 */
	String mouseReleased(Point locationOnScreen, Graphics g);

	/** given the serialized string of the tool (the string that is sent to the server), update the graphics context accordingly
	 * 
	 * @param s the String containing the serialized form of this tool's action
	 * @param g the graphics context to draw the data on
	 */
	void draw(String s, Graphics g);
	
	/** given an encoded draw action, return a rectangle (x, y, w, h) that covers entirely the area of canvas changed by the decoded action
	 *
	 * @param phrase a string containing raw data provided by the user's version of the Tool
	 *
	 * @return an array describing a rectangular area afftected by the given phrase. Takes form { long xleft, long ytop, long width, long height }
	 */
	long[] getAfftectedArea(String phrase);

	Image getIcon();
	
	String getTooltip();
	
	/** provides the human-readable name of the tool (e.g. for tooltexts)
	 */
	String getToolName();

	/** provides the internal name of the tool
	 */
	String getToolID();

	/*get a single string representing a full command. Most likely just a
	 * collection of indivudal points from the mouse events.
	 *
	 * Usage (@RDE):
	 * This function should not exist. Instead:
	 * - For drawing, please send each phrase immediately. DO NOT KEEP INTERNAL STATE, EXCEPT FOR UI ELEMENTS
	 * - For history, assign an external agent to collate tool phrases into a full command. Or, preferrably, pass the undo() command to the server, and accept the corresponding rect_set() command.
	 */
	//String serlialize();
}
