package mict.bridge;


import java.util.*;
import javax.script.*;

import org.python.core.Py;
import org.python.core.PySystemState;

import mict.client.ClientConnection;
import mict.client.ClientState;
import mict.tools.Tool;

public abstract class JythonBridge {
	private static final String SCRIPT_NAME = "javabridge";
	private static ScriptEngine jython = new ScriptEngineManager().getEngineByName("jython");

	/** gets the panel containing all of the tools and their handlers from
	* the server. In order to keep this from being too hacky, we're using
	* Jython for this.
	* 
	* @param serverURL : the server to connect to in order to get the tool
	* information
	* 
	* @param g: The graphics context used for the drawing. This will be used by the tools' event handlers to actually
	* modify the canvas.
	*
	* @return a JPanel containing all of the tools, with icons.
	*/
	public static void initialize() {
		PySystemState engineSys = new PySystemState();
		engineSys.path.append(Py.newString((JythonBridge.class.getResource("pylib")).getPath()));
		Py.setSystemState(engineSys);
		jython = new ScriptEngineManager().getEngineByName("python");
	}

	public static List<Tool> getToolList(ClientState s) {
		try {
			jython.eval("from " + SCRIPT_NAME + " import get_tools");
			System.out.println("imported tools");
			jython.put("state", s);
			List list = (List)jython.eval("get_tools(state)");
			Vector<Tool> result = new Vector<Tool>();
			for(Object o : list) {
				result.add((Tool)o);
			}
			return result;
		} catch(ScriptException e) {
			System.err.println("Script fails ate getting tool list:");
			e.printStackTrace(System.err);
			return new java.util.ArrayList<Tool>();
		}
	}

	public static List<Tool> updateToolList() {
		try {
			jython.eval("import " + SCRIPT_NAME);
			jython.eval(SCRIPT_NAME + ".reload_tools()");
			List list = (List)jython.get(SCRIPT_NAME + ".tools_list");
			Vector<Tool> result = new Vector<Tool>();
			for(Object o : list) {
				result.add((Tool)o);
			}
			return result;
		} catch(ScriptException e) {
			return new java.util.ArrayList<Tool>();
		}
	}
	public static List<String> getNeededClientTools(String required_tools) {
		try {
			jython.eval("import " + SCRIPT_NAME);
			List needed_tools = (List)jython.eval(SCRIPT_NAME + ".get_needed_tools(\"" + required_tools + "\")");
			List<String> tools = new ArrayList<String>();
			for(Object tool_o: needed_tools) {
				tools.add((String)tool_o);
				//need to request tools
			}
			return tools;
		} catch(ScriptException e) {
			return new Vector<String>();
		}
		
	}
	public static Tool addClientTool(String form, ClientState s) {
		try {
			//this should not be called unless the script has already been imported
			jython.put("state", s);
			jython.put("serialized_tool", form);
			Tool t = (Tool)jython.eval(SCRIPT_NAME + ".add_tool(serialized_tool,state)");
			return t;
		} catch(ScriptException e) {
			return null;
		}
	}
	public static List<Tool> getClientTools(ClientState s) {
		try {
			jython.eval("import " + SCRIPT_NAME);
			jython.put("state", s);
			List tools = (List)jython.eval(SCRIPT_NAME + ".get_client_tools(state)");
			List<Tool> result_list = new Vector<Tool>();
			for(Object tool_o : tools) {
				result_list.add((Tool)tool_o);
			}
			return result_list;
		} catch(ScriptException e) {
			return new Vector<Tool>();
		}
	}
}
