package mict.server;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;

import mict.tools.*;

public class CanvasManager implements ImageObserver {
	public CanvasManager(DatabaseLayer database, Server parent) {
		this.database = database;
		this.parent = parent;
	}

	private Server parent;
	private DatabaseLayer database;
	private ToolManager tools = new ToolManager();
	private HashMap<Point, Chunk> cache = new HashMap<Point, Chunk>();

	public Object /*HistoryLayer*/ draw(long x, long y, String tool, String data, Waiter user) {
		System.out.println("CM: drawing a thing!");
		Tool t = tools.getToolByID(tool);
		if(t == null) {
			// TODO COMPLAIN
			return null;	
		}
		if(data.trim().equals("")) return null;
		long[] rect = t.getAffectedArea(data);
		System.out.println("rect=" + rect + " phrase=" + data);
		int[] area = Chunk.getAffectedChunks(rect);
		for(int i = area[0]; i < area[2]; i++) {
			for(int j = area[1]; j < area[3]; j++) {
				Graphics2D g = getChunk(i, j).getGraphics(x, y);
				t.draw(data, g);
			}
		}
		Waiter[] users = parent.getUsers();
		for(Waiter u : users) {
			if(!u.intersects(rect)) continue;
			if(u == user) continue;
			u.sendCanvasChange(x, y, tool, data);
		}
		return null;
	}

	public Chunk getChunk(int x, int y) {
		Chunk c = cache.get(new Point(x, y));
		if(c == null) c = load(x, y);
		return c;
	}

	private Chunk load(int x, int y) {
		Chunk c = database.getChunk(x, y);
		cache.put(new Point(x, y), c);
		// TODO if we need to remove an old chunk, do it here
		return c;
	}

	public BufferedImage getCanvasRect(long x, long y, long width, long height) {
		System.out.println("CM: Constructing a rectangular area of canvas to send to a user. How exciting!");
		BufferedImage img = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_ARGB);
		int[] area = Chunk.getAffectedChunks(x, y, width, height);
		Graphics g = img.getGraphics();
		for(int i = area[0]; i < area[2]; i++) {
			for(int j = area[1]; j < area[3]; j++) {
				Image tile = database.getChunk(i, j).getImage();
				g.drawImage(
					tile,
					(int)(i * Chunk.getWidth() - x),
					(int)(j * Chunk.getHeight() - y),
					this
				);
			}
		}
		// do I have to wait? probably. ick.
		return img;
	}

	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		// if there is more intelligent behavior than this, X.
		System.out.println("CM: imageupdate: img=" + img + " infoflags=" + infoflags + " @(" + x + ',' + y + ") at " + width + " by " + height);
		return false;
	}

	public void saveAll() {
		System.out.println("SAVING ALL CHUNKS");
		// todo lock the cache somehow
		for(Chunk c : cache.values()) {
			database.setChunk(c);
		}
	}
}
