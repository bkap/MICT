package mict.tools;

import java.util.HashMap;
import java.util.List;
import java.awt.Graphics2D;
import mict.bridge.JythonBridge;
import mict.client.ClientConnection;
import mict.client.ClientState;
import mict.client.ToolBox;

/** the ToolManager is used to create the list of tools. It then will also
 * select the appropriate tool based on the tool name. 
 * 
 */
public class ToolManager {
	
	/** Creates a new ToolManager, which will query Jython for the list of tools
	 * @param s : The ClientState object that the tools will use to get properties such as the currently selected color
	*/
	private ToolManager(List<Tool> toolList) {
		this.toolList = toolList; 
		tools = new HashMap<String, Tool>();
		for(Tool t : toolList) {
			tools.put(t.getToolID(), t);
		}
	}
	private ToolManager(List<Tool> toolList, ToolBox b, ClientState s) {
		this(toolList);
		this.b = b;
		this.state = s;
	}
	private ClientState state;

	private ToolManager() {
		this(null);
	}

	private HashMap<String, Tool> tools;
	private List<Tool> toolList;
	private ToolBox b;

	/** Get all of the Tools tracked by this ToolManager
	*
	*@return the list of tools
	*/
	public List<Tool> getAllTools() {
		return toolList;
	}
	public void addTools(String serial_form) {
		List<Tool> newtools = JythonBridge.addClientTool(serial_form, state);
		for(Tool t: newtools) {
		toolList.add(t);
		tools.put(t.getToolID(), t);
		if(this.b != null) {
			b.addTool(t);
		}
		}
	}
	public void setToolBox(ToolBox b) {
		this.b = b;
	}
	/**This method passses the given phrase and graphics to the Tool with the given ID.
	 * 
	 * @param toolid the toolID of the tool to use for parsing the draw command
	 * @param phrase the phrase that describes the form to draw
	 * @param g the graphics to draw on
	 * @see mict.tools.Tool#draw(String, Graphics2D)
	 */
	public void draw(String toolid, String phrase, Graphics2D g) {
		Tool usedTool = getToolByID(toolid);
		if(usedTool != null) {
			usedTool.draw(phrase,g);
		}
	}
	
	/**same as {@link mict.tools.ToolManager#getNeededClientTools(String)} except that it simultaneously updates this ToolManager
	 * 
	 * @param tools
	 * @return
	 */
	public String updateClientTools(String toolstr) {
		String neededTools = ToolManager.getNeededClientTools(toolstr);
		List<Tool> newTools = JythonBridge.getClientTools(state);
		System.out.println("adding tools");
		for(Tool t : newTools) {
			if(!tools.containsKey(t.getToolID())) {
				System.out.println("adding new tool");
				
				this.toolList.add(t);
				this.tools.put(t.getToolID(), t);
				this.b.addTool(t);
			}
		}
		
		return neededTools;
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
	/** @see #getServerToolManager(ClientState)
	 * 
	 * @return
	 */
	public static ToolManager getServerToolManager() {
		return new ToolManager(JythonBridge.getToolList(null));
	}
	/** Creates a ToolManager for the server, using all available tools
	 * 
	 * This is also used on the client side when you aren't connected to a server
	 * 
	 * @param s
	 * @return
	 */
	public static ToolManager getServerToolManager(ClientState s) {
		return new ToolManager(JythonBridge.getToolList(s));
	}
	/** Given a list of tool descriptions from the server, returns the list of files that the client needs transmitted to it
	 * 
	 * @see mict.bridge.JythonBridge#getNeededClientTools(String)
	 * @param tools
	 * @return
	 */
	public static String getNeededClientTools(String tools) {
		return JythonBridge.getNeededClientTools(tools);
	}
	/** Gets the client tools and adds them to the given toolbox. Not all that effective until after {@see #getNeededClientTools(String)} has been called
	 * 
	 * @param s
	 * @param b
	 * @return
	 */
	public static ToolManager getClientToolManager(ClientState s, ToolBox b) {
		return new ToolManager(JythonBridge.getClientTools(s), b,s);
	}
	/** @see #getClientToolManager(ClientState, ToolBox)
	 * 
	 * @param s
	 * @return
	 */
	public static ToolManager getClientToolManager(ClientState s) {
		return new ToolManager(JythonBridge.getClientTools(s),null,s);
	}
}
