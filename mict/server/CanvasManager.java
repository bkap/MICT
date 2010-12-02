package mict.server;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.imageio.*;

import mict.tools.*;

/**
 * @author rde
 */
public class CanvasManager implements ImageObserver {
	public CanvasManager(DatabaseLayer database, Server parent) {
		this.database = database;
		this.parent = parent;
	}

	/**
	 * @uml.property name="parent"
	 * @uml.associationEnd  
	 */
	private Server parent;
	/**
	 * @uml.property name="database"
	 * @uml.associationEnd  
	 */
	private DatabaseLayer database;
	/**
	 * @uml.property name="tools"
	 * @uml.associationEnd  
	 */
	private ToolManager tools = ToolManager.getServerToolManager();
	private HashMap<Point, Chunk> cache = new HashMap<Point, Chunk>();

	public Object /*HistoryLayer*/ draw(long x, long y, String tool, String data, Waiter user, BufferedImage alt) {
		System.out.println("CM: drawing a thing!");
		Tool t = tools.getToolByID(tool);
		long[] rect;
		if(t == null) {
			if(tool.equals("imgrect")) {
				rect = new long[] { x, y, alt.getWidth(null), alt.getHeight(null) };
			} else {
				// TODO COMPLAIN
				return null;	
			}
		} else {
			if(data.trim().equals("")) return null;
			rect = t.getAffectedArea(data);
		}
		System.out.println("rect=" + rect + " phrase=" + data);
		rect[0] += x;
		rect[1] += y;
		int[] area = Chunk.getAffectedChunks(rect);
		for(int i = area[0]; i < area[2]; i++) {
			for(int j = area[1]; j < area[3]; j++) {
				Graphics2D g = getChunk(i, j).getGraphics(x, y);
				if(t != null)
					t.draw(data, g);
				else
					g.drawImage(alt, (int)x, (int)y, this);
			}
		}
		Waiter[] users = parent.getUsers();
		for(Waiter u : users) {
			System.out.println("Sending to user " + u.getUserName() + "? " + (u.intersects(rect) ? "intersects" : "does not intersect"));
			if(!u.intersects(rect)) continue;
			if(t != null)
				u.sendCanvasChange(x, y, tool, data);
			else
				u.sendCanvasRectangle(x, y, alt);
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
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, (int)width, (int)height);
		for(int i = area[0]; i < area[2]; i++) {
			for(int j = area[1]; j < area[3]; j++) {
				Image tile = getChunk(i, j).getImage();
				g.drawImage(
					tile,
					(int)(i * Chunk.getWidth() - x),
					(int)(j * Chunk.getHeight() - y),
					this
				);
			}
		}
		// TODO ensure that the image has finished loading, somehow
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

	class Autosaver extends Thread {
		public Autosaver() {
			setDaemon(true);
		}

		private int timeout = 5*60000;

		public void run() {
			while(true) {
				while(parent.getUserCount() > 0) {
					try { Thread.sleep(timeout); } catch(InterruptedException e) {}
					saveAll();
				}
				try { Thread.sleep(60000); } catch(InterruptedException e) {}
			}
		}
	}
}
