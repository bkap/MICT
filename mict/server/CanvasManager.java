package mict.server;
import java.util.HashMap;
import java.awt.Point;

public class CanvasManager {
	public CanvasManager(DatabaseLayer database, Server parent) {
		this.database = database;
		this.parent = parent;
	}

	private server parent;
	private DatabaseLayer database;
	private ToolManager tools;
	private HashMap<Point, Chunk> cache = new HashMap<Point, Chunk>();

	public HistoryLayer draw(long x, long y, String tool, String data, Waiter user) {
		Tool t = tools.getToolByID(tool);
		if(t == null) {
			// TODO COMPLAIN
			return;	
		}
		int[] area = Chunk.getAffectedChunks(t.getAffectedArea(data));
		for(int i = area[0]; i < area[2]; i++) {
			for(int j = area[1]; j < area[3]; j++) {
				Graphics g = getChunk(i, j).getGraphics(x, y);
				t.draw(data, g);
			}
		}
		List<Waiter> users = parent.getUsers();
		for(Waiter u : users) {
			if(!u.intersects(area)) continue;
			if(u == user) continue;
			u.sendCanvasChange(x, y, tool, data);
		}
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
