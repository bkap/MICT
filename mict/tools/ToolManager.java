package mict.tools;

import java.util.HashMap;
import java.util.List;
import mict.bridge.JythonBridge;
import mict.client.ClientState;
/** the ToolManager is used to create the list of tools. It then will also
 * select the appropriate tool based on the tool name. It is primarily used on
 * the server
 */
public class ToolManager {
    private HashMap<String, Tool> tools;
    private List<Tool> toolList;
    /** Creates a new ToolManager, which will query Jython for the list of tools
     */
    public ToolManager(ClientState s) {
    	System.out.println("tool manager");
        toolList = JythonBridge.getToolList(s);
        tools = new HashMap<String, Tool>();
        for(Tool t: toolList) {
            tools.put(t.getToolID(), t);
        }
        
    }
    public ToolManager() {
    	this(null);
    }
    /** Get all of the Tools tracked by this ToolManager
     *
     *@return the list of tools
     */
    public List<Tool> getAllTools() {
        return toolList;
    }

    /** get a tool based on its toolid
     *
     *
     * @param id the unique identifier of the tool, as returned by {mict.tools.Tool#getToolID Tool.getToolID()}
     * @see Tool.getToolID()
     */
    public Tool getToolByID(String id) {
        return tools.get(id);
    }
}
