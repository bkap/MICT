package mict.client;
import mict.tools.Tool;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Graphics;
import mict.client.ClientConnection;
public class ClientState {
	public Tool ActiveTool;
	public Object clipboard;
	public String lastAction;
	public Color selectedColor;
	public Rectangle viewingArea;
	public Graphics bufferedScreen;
	public java.awt.Point bufferedScreenCorner;
	public ClientConnection socket;
	
}
