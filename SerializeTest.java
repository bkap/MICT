import org.junit.*;
import mict.bridge.JythonBridge;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.IOException;
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
	@Test
	public void testMultipleMissingFiles() {
		String server_tools =  "";
		for(String file: hashes.keySet()) {
			server_tools += file + ";" + hashes.get(file) + ":";
		}
		//this is not a vaild file name, and not a valid hash
		//the client should request this file
		server_tools += "notreallyhere~~;abcedfghijk:thisdoesn'texisteither;foobarbazedfea";
		Assert.assertEquals(JythonBridge.getNeededClientTools(server_tools), "notreallyhere~~:thisdoesn'texisteither");
	}
	@Test
	public void testModifiedFile() {
		File f = new File("tools/notatool.py");
		f.deleteOnExit();
		try {
			PrintWriter pw = new PrintWriter(f);
			pw.println("import mict.tools");
			pw.println("class FakeTool(mict.tools.Tool) :");
			pw.println("\tdef __init__(self,client_state=None):");
			pw.println("\t\tpass");
			pw.flush();
			//we now have a file
			String tools = JythonBridge.getToolDescriptions();
			//now we modify the file
			pw.println("trollolololol");
			pw.flush();
			Assert.assertEquals(JythonBridge.getNeededClientTools(tools),"notatool.py");
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
