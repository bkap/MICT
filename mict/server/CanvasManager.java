package mict.server;
import java.util.HashMap;
import java.awt.Point;

public class CanvasManager {
	public CanvasManager(DatabaseLayer database) {
		this.database = database;
	}

	private DatabaseLayer database;
	private ToolManager tools;
	private HashMap<Point, Chunk> cache = new HashMap<Point, Chunk>();

	public HistoryLayer draw(long x, long y, String tool, String data, Waiter user) {
		Tool t = tools.getToolByID(tool);
		if(t == null) {
			// TODO COMPLAIN
			return;	
		}
		List<ChunkGraphics> gs = null // TODO implement
		// do the d[r]ew
		// holla back
		
	}

	public Chunk getChunk(int x, int y) {
		Chunk c = cache.get(new Point(x, y));
		if(c == null) c = load(x, y);
		return c;
	}

	private Chunk load(int x, int y) {
		Chunk c = database.get(x, y);
		cache.add(new Point(x, y), c);
		// TODO if we need to remove an old chunk, do it here
	}
}
