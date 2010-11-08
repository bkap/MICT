import org.junit.*;
import mict.tools.*;
import mict.client.*;
import mict.server.*;
import mict.bridge.JythonBridge;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.DebugGraphics;

public class ToolTest {
	private Point[] locations;
	private Client testClient;
	private Server testServer;

	public ToolTest() {
		locations = new Point[7];
		locations[0] = new Point(5,5);
		locations[1] = new Point(7,10);
		locations[2] = new Point(6,9);
		locations[3] = new Point(2,4);
		locations[4] = new Point(15,3);
		locations[5] = new Point(7,2);
		locations[6] = new Point(5,3);
	}

	@Before
	public void setup() {

	}

	@After
	public void tearDown() {

	}

	@org.junit.Test
	public void testfillRoundRectSerialization() {
		ClientState s = new ClientState();
		ToolManager t = ToolManager.getServerToolManager(s);
		String serializedTool = JythonBridge.serializeTool("fillRoundRect");
		Tool newTool = JythonBridge.deserializeTool("fillRoundRect;" + serializedTool);
		BufferedImage testGraphics = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);

		BufferedImage testGraphics2 = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
		BufferedImage clientImage = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
		BufferedImage resultImage = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
		Graphics2D tg = (Graphics2D)testGraphics.getGraphics();
		Graphics2D tg2 = (Graphics2D)testGraphics2.getGraphics();
		Graphics2D cig = (Graphics2D)clientImage.getGraphics();
		Graphics2D rig = (Graphics2D)resultImage.getGraphics();
		Tool oldTool = t.getToolByID("fillRoundRect");
		System.out.println(newTool);
		String builder = newTool.mousePressed(locations[0],tg);
		String builder2 = oldTool.mousePressed(locations[1],tg2);
		newTool.draw(builder,cig);
		oldTool.draw(builder2,rig);
		for(int i = 1; i < locations.length - 1; i++) {
			builder = newTool.mouseDragged(locations[i],tg);
			builder2 = newTool.mouseDragged(locations[i],tg2);
			newTool.draw(builder,cig);
			oldTool.draw(builder2,rig);
		}
		for(int x = 0; x < clientImage.getWidth(); x++) {
			for(int y = 0; y < clientImage.getHeight(); y++) {
				Assert.assertEquals(clientImage.getRGB(x,y), resultImage.getRGB(x,y));
				Assert.assertEquals(testGraphics.getRGB(x,y), testGraphics.getRGB(x,y));
			}
		}
	}

	@org.junit.Test
	public void testrectSerialization() {
		ClientState s = new ClientState();
		ToolManager t = ToolManager.getServerToolManager(s);
		String serializedTool = JythonBridge.serializeTool("rect");
		Tool newTool = JythonBridge.deserializeTool("rect;" + serializedTool);
		BufferedImage testGraphics = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);

		BufferedImage testGraphics2 = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
		BufferedImage clientImage = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
		BufferedImage resultImage = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
		Graphics2D tg = (Graphics2D)testGraphics.getGraphics();
		Graphics2D tg2 = (Graphics2D)testGraphics2.getGraphics();
		Graphics2D cig = (Graphics2D)clientImage.getGraphics();
		Graphics2D rig = (Graphics2D)resultImage.getGraphics();
		Tool oldTool = t.getToolByID("rect");
		System.out.println(newTool);
		String builder = newTool.mousePressed(locations[0],tg);
		String builder2 = oldTool.mousePressed(locations[1],tg2);
		newTool.draw(builder,cig);
		oldTool.draw(builder2,rig);
		for(int i = 1; i < locations.length - 1; i++) {
			builder = newTool.mouseDragged(locations[i],tg);
			builder2 = newTool.mouseDragged(locations[i],tg2);
			newTool.draw(builder,cig);
			oldTool.draw(builder2,rig);
		}
		for(int x = 0; x < clientImage.getWidth(); x++) {
			for(int y = 0; y < clientImage.getHeight(); y++) {
				Assert.assertEquals(clientImage.getRGB(x,y), resultImage.getRGB(x,y));
				Assert.assertEquals(testGraphics.getRGB(x,y), testGraphics.getRGB(x,y));
			}
		}
	}

	@org.junit.Test
	public void testroundrectSerialization() {
		ClientState s = new ClientState();
		ToolManager t = ToolManager.getServerToolManager(s);
		String serializedTool = JythonBridge.serializeTool("roundrect");
		Tool newTool = JythonBridge.deserializeTool("roundrect;" + serializedTool);
		BufferedImage testGraphics = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);

		BufferedImage testGraphics2 = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
		BufferedImage clientImage = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
		BufferedImage resultImage = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
		Graphics2D tg = (Graphics2D)testGraphics.getGraphics();
		Graphics2D tg2 = (Graphics2D)testGraphics2.getGraphics();
		Graphics2D cig = (Graphics2D)clientImage.getGraphics();
		Graphics2D rig = (Graphics2D)resultImage.getGraphics();
		Tool oldTool = t.getToolByID("roundrect");
		System.out.println(newTool);
		String builder = newTool.mousePressed(locations[0],tg);
		String builder2 = oldTool.mousePressed(locations[1],tg2);
		newTool.draw(builder,cig);
		oldTool.draw(builder2,rig);
		for(int i = 1; i < locations.length - 1; i++) {
			builder = newTool.mouseDragged(locations[i],tg);
			builder2 = newTool.mouseDragged(locations[i],tg2);
			newTool.draw(builder,cig);
			oldTool.draw(builder2,rig);
		}
		for(int x = 0; x < clientImage.getWidth(); x++) {
			for(int y = 0; y < clientImage.getHeight(); y++) {
				Assert.assertEquals(clientImage.getRGB(x,y), resultImage.getRGB(x,y));
				Assert.assertEquals(testGraphics.getRGB(x,y), testGraphics.getRGB(x,y));
			}
		}
	}

	@org.junit.Test
	public void testlineSerialization() {
		ClientState s = new ClientState();
		ToolManager t = ToolManager.getServerToolManager(s);
		String serializedTool = JythonBridge.serializeTool("line");
		Tool newTool = JythonBridge.deserializeTool("line;" + serializedTool);
		BufferedImage testGraphics = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);

		BufferedImage testGraphics2 = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
		BufferedImage clientImage = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
		BufferedImage resultImage = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
		Graphics2D tg = (Graphics2D)testGraphics.getGraphics();
		Graphics2D tg2 = (Graphics2D)testGraphics2.getGraphics();
		Graphics2D cig = (Graphics2D)clientImage.getGraphics();
		Graphics2D rig = (Graphics2D)resultImage.getGraphics();
		Tool oldTool = t.getToolByID("line");
		System.out.println(newTool);
		String builder = newTool.mousePressed(locations[0],tg);
		String builder2 = oldTool.mousePressed(locations[1],tg2);
		newTool.draw(builder,cig);
		oldTool.draw(builder2,rig);
		for(int i = 1; i < locations.length - 1; i++) {
			builder = newTool.mouseDragged(locations[i],tg);
			builder2 = newTool.mouseDragged(locations[i],tg2);
			newTool.draw(builder,cig);
			oldTool.draw(builder2,rig);
		}
		for(int x = 0; x < clientImage.getWidth(); x++) {
			for(int y = 0; y < clientImage.getHeight(); y++) {
				Assert.assertEquals(clientImage.getRGB(x,y), resultImage.getRGB(x,y));
				Assert.assertEquals(testGraphics.getRGB(x,y), testGraphics.getRGB(x,y));
			}
		}
	}

	@org.junit.Test
	public void testfilledovalSerialization() {
	ClientState s = new ClientState();
	ToolManager t = ToolManager.getServerToolManager(s);
	String serializedTool = JythonBridge.serializeTool("filledoval");
	Tool newTool = JythonBridge.deserializeTool("filledoval;" + serializedTool);
	BufferedImage testGraphics = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);

	BufferedImage testGraphics2 = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
	BufferedImage clientImage = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
	BufferedImage resultImage = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
	Graphics2D tg = (Graphics2D)testGraphics.getGraphics();
	Graphics2D tg2 = (Graphics2D)testGraphics2.getGraphics();
	Graphics2D cig = (Graphics2D)clientImage.getGraphics();
	Graphics2D rig = (Graphics2D)resultImage.getGraphics();
	Tool oldTool = t.getToolByID("filledoval");
	System.out.println(newTool);
	String builder = newTool.mousePressed(locations[0],tg);
	String builder2 = oldTool.mousePressed(locations[1],tg2);
	newTool.draw(builder,cig);
	oldTool.draw(builder2,rig);
	for(int i = 1; i < locations.length - 1; i++) {
	builder = newTool.mouseDragged(locations[i],tg);
	builder2 = newTool.mouseDragged(locations[i],tg2);
	newTool.draw(builder,cig);
	oldTool.draw(builder2,rig);
	}
	for(int x = 0; x < clientImage.getWidth(); x++) {
	for(int y = 0; y < clientImage.getHeight(); y++) {
	Assert.assertEquals(clientImage.getRGB(x,y), resultImage.getRGB(x,y));
	Assert.assertEquals(testGraphics.getRGB(x,y), testGraphics.getRGB(x,y));
	}
	}
	}
	@org.junit.Test
	public void testovalSerialization() {
	ClientState s = new ClientState();
	ToolManager t = ToolManager.getServerToolManager(s);
	String serializedTool = JythonBridge.serializeTool("oval");
	Tool newTool = JythonBridge.deserializeTool("oval;" + serializedTool);
	BufferedImage testGraphics = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);

	BufferedImage testGraphics2 = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
	BufferedImage clientImage = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
	BufferedImage resultImage = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
	Graphics2D tg = (Graphics2D)testGraphics.getGraphics();
	Graphics2D tg2 = (Graphics2D)testGraphics2.getGraphics();
	Graphics2D cig = (Graphics2D)clientImage.getGraphics();
	Graphics2D rig = (Graphics2D)resultImage.getGraphics();
	Tool oldTool = t.getToolByID("oval");
	System.out.println(newTool);
	String builder = newTool.mousePressed(locations[0],tg);
	String builder2 = oldTool.mousePressed(locations[1],tg2);
	newTool.draw(builder,cig);
	oldTool.draw(builder2,rig);
	for(int i = 1; i < locations.length - 1; i++) {
	builder = newTool.mouseDragged(locations[i],tg);
	builder2 = newTool.mouseDragged(locations[i],tg2);
	newTool.draw(builder,cig);
	oldTool.draw(builder2,rig);
	}
	for(int x = 0; x < clientImage.getWidth(); x++) {
	for(int y = 0; y < clientImage.getHeight(); y++) {
	Assert.assertEquals(clientImage.getRGB(x,y), resultImage.getRGB(x,y));
	Assert.assertEquals(testGraphics.getRGB(x,y), testGraphics.getRGB(x,y));
	}
	}
	}
	@org.junit.Test
	public void testpanSerialization() {
	ClientState s = new ClientState();
	ToolManager t = ToolManager.getServerToolManager(s);
	String serializedTool = JythonBridge.serializeTool("pan");
	Tool newTool = JythonBridge.deserializeTool("pan;" + serializedTool);
	BufferedImage testGraphics = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);

	BufferedImage testGraphics2 = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
	BufferedImage clientImage = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
	BufferedImage resultImage = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
	Graphics2D tg = (Graphics2D)testGraphics.getGraphics();
	Graphics2D tg2 = (Graphics2D)testGraphics2.getGraphics();
	Graphics2D cig = (Graphics2D)clientImage.getGraphics();
	Graphics2D rig = (Graphics2D)resultImage.getGraphics();
	Tool oldTool = t.getToolByID("pan");
	System.out.println(newTool);
	String builder = newTool.mousePressed(locations[0],tg);
	String builder2 = oldTool.mousePressed(locations[1],tg2);
	newTool.draw(builder,cig);
	oldTool.draw(builder2,rig);
	for(int i = 1; i < locations.length - 1; i++) {
	builder = newTool.mouseDragged(locations[i],tg);
	builder2 = newTool.mouseDragged(locations[i],tg2);
	newTool.draw(builder,cig);
	oldTool.draw(builder2,rig);
	}
	for(int x = 0; x < clientImage.getWidth(); x++) {
	for(int y = 0; y < clientImage.getHeight(); y++) {
	Assert.assertEquals(clientImage.getRGB(x,y), resultImage.getRGB(x,y));
	Assert.assertEquals(testGraphics.getRGB(x,y), testGraphics.getRGB(x,y));
	}
	}
	}
	@org.junit.Test
	public void testfillRectSerialization() {
	ClientState s = new ClientState();
	ToolManager t = ToolManager.getServerToolManager(s);
	String serializedTool = JythonBridge.serializeTool("fillRect");
	Tool newTool = JythonBridge.deserializeTool("fillRect;" + serializedTool);
	BufferedImage testGraphics = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);

	BufferedImage testGraphics2 = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
	BufferedImage clientImage = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
	BufferedImage resultImage = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
	Graphics2D tg = (Graphics2D)testGraphics.getGraphics();
	Graphics2D tg2 = (Graphics2D)testGraphics2.getGraphics();
	Graphics2D cig = (Graphics2D)clientImage.getGraphics();
	Graphics2D rig = (Graphics2D)resultImage.getGraphics();
	Tool oldTool = t.getToolByID("fillRect");
	System.out.println(newTool);
	String builder = newTool.mousePressed(locations[0],tg);
	String builder2 = oldTool.mousePressed(locations[1],tg2);
	newTool.draw(builder,cig);
	oldTool.draw(builder2,rig);
	for(int i = 1; i < locations.length - 1; i++) {
	builder = newTool.mouseDragged(locations[i],tg);
	builder2 = newTool.mouseDragged(locations[i],tg2);
	newTool.draw(builder,cig);
	oldTool.draw(builder2,rig);
	}
	for(int x = 0; x < clientImage.getWidth(); x++) {
	for(int y = 0; y < clientImage.getHeight(); y++) {
	Assert.assertEquals(clientImage.getRGB(x,y), resultImage.getRGB(x,y));
	Assert.assertEquals(testGraphics.getRGB(x,y), testGraphics.getRGB(x,y));
	}
	}
	}
	@org.junit.Test
	public void testpencilSerialization() {
	ClientState s = new ClientState();
	ToolManager t = ToolManager.getServerToolManager(s);
	String serializedTool = JythonBridge.serializeTool("pencil");
	Tool newTool = JythonBridge.deserializeTool("pencil;" + serializedTool);
	BufferedImage testGraphics = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);

	BufferedImage testGraphics2 = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
	BufferedImage clientImage = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
	BufferedImage resultImage = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
	Graphics2D tg = (Graphics2D)testGraphics.getGraphics();
	Graphics2D tg2 = (Graphics2D)testGraphics2.getGraphics();
	Graphics2D cig = (Graphics2D)clientImage.getGraphics();
	Graphics2D rig = (Graphics2D)resultImage.getGraphics();
	Tool oldTool = t.getToolByID("pencil");
	System.out.println(newTool);
	String builder = newTool.mousePressed(locations[0],tg);
	String builder2 = oldTool.mousePressed(locations[1],tg2);
	newTool.draw(builder,cig);
	oldTool.draw(builder2,rig);
	for(int i = 1; i < locations.length - 1; i++) {
	builder = newTool.mouseDragged(locations[i],tg);
	builder2 = newTool.mouseDragged(locations[i],tg2);
	newTool.draw(builder,cig);
	oldTool.draw(builder2,rig);
	}
	for(int x = 0; x < clientImage.getWidth(); x++) {
	for(int y = 0; y < clientImage.getHeight(); y++) {
	Assert.assertEquals(clientImage.getRGB(x,y), resultImage.getRGB(x,y));
	Assert.assertEquals(testGraphics.getRGB(x,y), testGraphics.getRGB(x,y));
	}
	}
	}
}
