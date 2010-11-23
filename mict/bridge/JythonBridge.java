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


	public static List<Tool> getToolList(ClientState s) {
		try {
			jython.eval("from " + SCRIPT_NAME + " import get_server_tools");
			System.out.println("imported tools");
			jython.put("state", s);
			List list = (List)jython.eval("get_server_tools(state)");
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

	public static String getNeededClientTools(String server_tools) {
		try {
			jython.eval("import " + SCRIPT_NAME);
			String needed_tools = (String)jython.eval(SCRIPT_NAME + ".get_needed_tools(\"" + server_tools + "\")");
			return needed_tools;
		} catch(ScriptException e) {
			e.printStackTrace(System.err);
			return "";
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
	public static String getSerializedToolFile(String file) {
		try {
			jython.eval("import " + SCRIPT_NAME);
			return (String)jython.eval(SCRIPT_NAME + ".tools.get_tool_file(\"" + file + "\")");
			
		} catch(ScriptException ex) {
			return "";
		}
	}
	public static String serializeTool(String toolID) {
		
		try {
			jython.eval("import " + SCRIPT_NAME);
			String pickledTool = (String)jython.eval(SCRIPT_NAME + ".serialize_tool(\"" + toolID + "\")");
			return pickledTool;
		} catch (ScriptException e) {
			e.printStackTrace();	
			return "";
		}
		
	}
	public static List<Tool> addClientTool(String phrase, ClientState state) {
		try {
			jython.eval("import " + SCRIPT_NAME);
			jython.put("phrase",phrase);
			jython.put("state",state);
			List tools = (List)jython.eval(SCRIPT_NAME + ".deserialize_tool(phrase,state)");
			List<Tool> tools_t = new Vector<Tool>();
			for(Object o: tools) {
				tools_t.add((Tool)o);
			}
			return tools_t;
		} catch(ScriptException e) {
			e.printStackTrace();
			return null;
		}

	}
	public static String getToolDescriptions() {
		try {
			jython.eval("import " + SCRIPT_NAME);
			return (String)jython.eval(SCRIPT_NAME + ".tools.get_tool_files_and_hashes()");
		} catch(ScriptException ex) {
			return "";
		}
	}
	public static void main(String[] args) {

	}
}
