package mict.server;

public class ChunkGraphics extends Graphics { // TODO implement the Graphics2D translation, too
	public ChunkGraphics(Graphics g, Chunk c, long userx, long usery) {
		this.g = g;
		this.c = c;
		x = userx;
		y = usery;
	}

	private Graphics g;
	private Chunk c;
	private long x, y;

	public void clearRect(int x, int y, int width, int height) {
		
	}

	public void clipRect(int x, int y, int width, int height) {
		
	}

	public void copyArea(int x, int  y, int width, int height) {
		
	}

	public Graphics create() {
		return new ChunkGraphics(g, c, x, y);
	}

	public void dispose() {
		
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		
	}

	// TODO determine if non-abstract methods need to be overwritten

	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		
	}

	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		
	}

	public boolean drawImage(Image img, int x, int ty, int width, int height, Color bgcolor, ImageObserver observer) {
		
	}

	public boolean drawImage(Image img, int x, int ty, int width, int height, Color bgcolor, ImageObserver observer) {
		
	}

	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
		
	}

	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		
	}

	// TODO DRAWLINE+
}
