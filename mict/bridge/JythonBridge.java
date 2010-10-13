package mict.bridge;

import javax.swing.*;
import javax.script.*;

import org.python.core.Py;
import org.python.core.PySystemState;

import java.awt.Graphics;
import java.util.List;

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
        return (List<Tool>)jython.eval("get_tools(state)");
        }catch(ScriptException e) 
        {
        	e.printStackTrace();
            return new java.util.ArrayList<Tool>();
        }
    }
    public static List<Tool> updateToolList() {
        try {
            jython.eval("import " + SCRIPT_NAME);
            jython.eval(SCRIPT_NAME + ".reload_tools()");
            return (List<Tool>)jython.get(SCRIPT_NAME + ".tools_list");
        } catch(ScriptException e) {
            return new java.util.ArrayList<Tool>();
        }
    }

}
