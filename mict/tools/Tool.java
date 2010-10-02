package mict.tools;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Image;
public interface Tool {
	/** this method will be called when the mouse is clicked on the canvas. It sets the initial 
	 * 
	 * @param locationOnScreen : the location on the canvas currently to update.
	 * @param locationOnCanvas : the location on the canvas as a whole where the mouse was pressed
	 * @param g : the graphics context in which to use the tool
	 */
	void mousePressed(Point locationOnScreen, Point locationOnCanvas, Graphics g);
	
	/** this method will be called intermittently as the mouse is dragged across the screen during a mouse press.
	 *  
	 * @param locationOnScreen: the location of the mouse on the canvas currently
	 * @param locationOnCanvas: the point in the overall canvas where the user is
	 * @param g the graphics context in which the tool will operate
	 */
	void mouseMoved(Point locationOnScreen, Point locationOnCanvas, Graphics g);
	
	/**this method will be called once the mouse has been picked up. At this point, the tool should be prepared to contact the
	 * server to update the canvas
	 * @param locationOnScreen the location on the graphics context that the user is drawing on
	 * @param locationOnCanvas the location on the canvas that the user is currently viewing.
	 * @param g the graphics context to draw on
	 */
	void mouseReleased(Point locationOnScreen, Point locationOnCanvas, Graphics g);
	/**get a string representation of the tool's last action. 
	 * 
	 * @return a String to be sent across the network that will represent the tool's action
	 */
	String serialize();
	/**given the serialized string of the tool (the string that is sent to the server), update the graphics context accordingly
	 * 
	 * @param s the String containing the serialized form of this tool's action
	 * @param g the graphics context to draw the data on
	 */
	void drawFromString(String s, Graphics g);
	
	Image getImage();
	
	String getTooltip();
	
	String getToolName();
	
}
