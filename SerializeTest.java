import org.junit.*;
import mict.bridge.JythonBridge;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
public class SerializeTest {
	HashMap<String,String> hashes;
	
	public SerializeTest() {
		String tools = JythonBridge.getToolDescriptions();
		String[] tool_descrs = tools.split(":");
		hashes = new HashMap<String, String>();
		for(String tool_descr: tool_descrs) {
			String[] tool_parts = tool_descr.split(";");
			hashes.put(tool_parts[0],tool_parts[1]);
		}
	}
	@Before
	public void setUp() {
		JythonBridge.resetJython();
	}
	@Test
	public void testAllGood() {
		String server_tools =  "";
		for(String file: hashes.keySet()) {
			server_tools += file + ";" + hashes.get(file) + ":";
		}
		server_tools = server_tools.replaceAll(":$","");
		Assert.assertEquals(JythonBridge.getNeededClientTools(server_tools),"");
	}
	@Test
	public void testMissingFile() {
	
		String server_tools =  "";
		for(String file: hashes.keySet()) {
			server_tools += file + ";" + hashes.get(file) + ":";
		}
		//this is not a vaild file name, and not a valid hash
		//the client should request this file
		server_tools += "notreallyhere~~;abcedfghijk";
		Assert.assertEquals(JythonBridge.getNeededClientTools(server_tools), "notreallyhere~~");
	}
}
