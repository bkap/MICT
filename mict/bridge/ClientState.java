package mict.bridge;
import java.awt.Graphics;
import java.awt.Rectangle;
import mict.tools.Tool;
public abstract class ClientState {
	public Object clipboard;
	public Graphics g;
	public Tool activeTool;
	public Rectangle canvasLocation;
	public abstract void sendToServer(String s);
	
}
