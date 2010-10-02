package mict.bridge;
import javax.swing.*;
import javax.script.*;
import java.awt.Graphics;
import mict.client.ClientState;
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
            System.out.println("imported");
            jython.put("graphics",g);
            jython.eval("result = bridge('getTools','" + serverURL + "', graphics)");
            return (JComponent)jython.get("result");
        } catch(ScriptException ex) {
            System.err.println("failed to get tools");
        }
        return null;
    }
    public static ClientConnection getConnection(String serverURL, Graphics g, ClientState state) {
        try {
            jython.eval("import " + SCRIPT_NAME + " as pystuff");
            System.out.println("imported");
            jython.put("graphics",g);
            jython.put("state", state);
            String command = "result = pystuff.ClientConn('" + serverURL + "', graphics,state)";
            System.out.println(command);
            jython.eval(command);
            return (ClientConnection)jython.get("result");
        } catch(ScriptException ex) {
            System.err.println("failed to get tools");
            ex.printStackTrace();
        }
        return null;
    }

    
}
