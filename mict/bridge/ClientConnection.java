package mict.bridge;
import mict.tools.Tool;

/** a class that handles the client/server connection. Will actually be implemented in Jython
 *  
 * @author Ben Kaplan
 *
 */
public abstract class ClientConnection {
	/**get the tools that the server supports
	 * 
	 * @return
	 */
	public abstract Tool[] getTools();
	/**send a message to the server
	 * 
	 * @param s
	 * @return
	 */
	public abstract boolean sendToServer(String s);
	/**wait for a message from the server
	 * 
	 * @return
	 */
	public abstract String receiveFromServer();
}
