import javax.swing.*;
import java.io.*;
import java.net.*;
import javax.script.*;
import java.awt.Graphics;
public abstract class JythonBridge {

    private static final String SCRIPT_NAME = "javabridge";
    private static ScriptEngine jython = new ScriptEngineManager().getEngineByName("jython");
    
    /** gets the panel containing all of the tools and their handlers from
     * the server. In order to keep this from being too hacky, we're using
     * Jython for this.
     * @arg serverURL : the server to connect to in order to get the tool
     * information
     *
     */
    public static JComponent getTools(String serverURL, Graphics g) {
        try {
            jython.eval("from " + SCRIPT_NAME + " import bridge");
            System.out.println("imported");
            jython.put("graphics",g);
            jython.eval("result = bridge('getTools','" + serverURL + "', graphics)");
            return (JComponent)jython.get("result");
        } catch(ScriptException ex) {
            System.err.println("failed to get tools");
        }
        return null;
    }

    
}
