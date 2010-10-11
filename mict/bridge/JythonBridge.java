package mict.bridge;

import javax.swing.*;
import javax.script.*;
import java.awt.Graphics;
import java.util.List;
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
	public static JComponent getTools(String serverURL, Graphics g) {
		try {
			jython.eval("from " + SCRIPT_NAME + " import bridge");
			System.out.println("imported javabridge");
			jython.put("graphics",g);
			jython.eval("result = bridge('getTools','" + serverURL + "', graphics)");
			return (JComponent)jython.get("result");
		} catch(ScriptException e) {
			System.err.println("failed to get tools: " + e.getMessage());
		}
		return null;
	}
    public static List<Tool> getToolList() {
        try {
        jython.eval("from " + SCRIPT_NAME + " import tools_list");
        System.out.println("imported tools");
        return (List<Tool>)jython.get("tools_list");
        }catch(ScriptException e) {
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
