package mict.tools;

import java.util.HashMap;
import java.util.List;
import java.awt.Graphics2D;
import mict.bridge.JythonBridge;
import mict.client.ClientState;

/** the ToolManager is used to create the list of tools. It then will also
 * select the appropriate tool based on the tool name. It is primarily used on
 * the server
 * 
 */
public class ToolManager {
	
	/** Creates a new ToolManager, which will query Jython for the list of tools
	 * @param s : The ClientState object that the tools will use to get properties such as the currently selected color
	*/
	public ToolManager(ClientState s) {
		toolList = JythonBridge.getToolList(s);
		tools = new HashMap<String, Tool>();
		for(Tool t : toolList) {
			tools.put(t.getToolID(), t);
		}
	}

	public ToolManager() {
		this(null);
	}

	private HashMap<String, Tool> tools;
	private List<Tool> toolList;

	/** Get all of the Tools tracked by this ToolManager
	*
	*@return the list of tools
	*/
	public List<Tool> getAllTools() {
		return toolList;
	}
	/**This method passses the given phrase and graphics to the Tool with the given ID.
	 * 
	 * @param toolid the toolID of the tool to use for parsing the draw command
	 * @param phrase the phrase that describes the form to draw
	 * @param g the graphics to draw on
	 * @see mict.tools.Tool#draw(String, Graphics2D)
	 */
	public void draw(String toolid, String phrase, Graphics2D g) {
		getToolByID(toolid).draw(phrase,g);
	}
	/** get a tool based on its toolID
	*
	*
	* @param id the unique identifier of the tool, as returned by {mict.tools.Tool#getToolID Tool.getToolID()}
	* @see mict.tools.Tool#getToolID()
	*/
	public Tool getToolByID(String id) {
		return tools.get(id);
	}
}
