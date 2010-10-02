package mict.bridge;
import mict.tools.Tool;

/** a class that handles the client/server connection. Will actually be implemented in Jython
 *  
 * @author Ben Kaplan
 *
 */
public abstract class ToolManager {
	/**get the tools that the server supports
	 * 
	 * @return
	 */
	public abstract Tool[] getTools();
	protected abstract void setTools(Tool[] tools);
}
