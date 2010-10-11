package mict.client;

import mict.tools.Tool;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import mict.client.ClientConnection;

public class ClientState {
	public Tool activeTool;
	public BufferedImage clipboard;
	public Graphics clipboard_graphics;
	public Color selectedColor;
	public ClientConnection socket;
	public Graphics canvas_graphics;
	public long x=0, y=0;
}
