package mict.server;

public class ChunkGraphics extends Graphics { // TODO implement the Graphics2D translation, too
	public ChunkGraphics(Graphics g, Chunk c, long userx, long usery, CanvasManager parent) {
		this.g = g;
		this.c = c;
		dx = c.getX() * c.getWidth() - userx ;
		dy = c.getY() * c.getHeight() - usery ;
		original = true;
		this.parent = parent;
	}

	private CanvasManager parent;
	private Graphics g;
	private Chunk c;
	private long dx, dy;
	private original;

	/** returns an array of Graphics contexts that overlap with the given rectangle
	 *
	 * @param x the x-coordinate of the upper left-hand corner of the rectangle, in terms of this chunk
	 * @param y the y-coordinate of the upper left-hand corner of the rectangle, in terms of this chunk
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 */
	public ChunkGraphics[][] extend(int x, int y, int w, int h) { // TODO fix diagonal duplication
		if(!original) return new ChunkGraphics[0][0];
		int cleft = c.getX() + (x - c.getWidth() + 1) / c.getWidth();
		int ctop = c.getY() + (x - c.getHeight() + 1) / c.getHeight();
		int cwidth = (w + c.getWidth() - 1) / c.getWidth();
		int cheight = (w + c.getHeight() - 1) / c.getHeight();
		ChunkGraphics[][] result = new ChunkGraphics[cwidth][cheight];
		for(int i = 0; i < cwidth; i++) {
			for(int j = 0; j < cheight; j++) {
				Chunk c = parent.getChunk(i, j);
				result[i][j] = new ChunkGraphics(
					c.getGraphics(),
					c,
					dx - this.c.getX() * this.c.getWidth() + c.getX() * c.getWidth(),
					dy - this.c.getY() * this.c.getHeight() + c.getY() * g.getHeight(),
					parent
				);
				result[i][j].original = false;
			}
		}
		return result;
	}

	public void clearRect(int x, int y, int width, int height) {
		g.clearRect(x + dx, y + dy, width, height);
		// todo does this intersect with any adjacent chunk
	}

	public void clipRect(int x, int y, int width, int height) {
		g.clipRect(x + dx, y + dy, width, height);
		// todo does this intersect with any adjacent chunk
	}

	public void copyArea(int x, int  y, int width, int height) {
		g.copyRect(x + dx, y + dy, width, height);
		// todo does this intersect with any adjacent chunk
	}

	public Graphics create() {
		return new ChunkGraphics(g, c, x, y);
	}

	public void dispose() {
		// todo does anything go here?
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		g.drawArc(x + dx, y + dy, width, height, startAngle, arcAngle);
	}

	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		return g.drawImage(img, x + dx, y + dy, bgcolor, observer);
	}

	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		return g.drawImage(img, x + dx, y + dy, observer);
	}

	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		return g.drawImage(img, x + dx, y + dy, width, height, bgcolor, observer);
	}

	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		return g.drawImage(img, x + dx, y + dy, width, height, bgcolor, observer);
	}

	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
		return g.drawImage(img, dx1 + dx, dy1 + dy, dx2 + dx, dy2 + dy, sx1, sy1, sx2, sy2, bgcolor, observer);
	}

	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		return g.drawImage(img, dx1 + dx, dy1 + dy, dx2 + dx, dy2 + dy, sx1, sy1, sx2, sy2, observer);
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		g.drawLine(x1 + dx, y1 + dy, x2 + dx, y2 + dy);
	}

	public void drawOval(int x, int y, int width, int height) {
		g.drawOval(x + dx, y + dy, width, height);
	}

	public void drawPolygon(int[] xpoints, int[] ypoints, int npoints) {
		int[] dxpoints = new int[npoints];
		int[] dypoints = new int[npoints];
		for(int i = 0; i < npoints; i++) {
			dxpoints[i] = xpoints[i] + dx;
			dypoints[i] = ypoints[i] + dy;
		}
		g.drawPolygon(dxpoints, dypoints, npoints);
	}

	public void drawPolyline(int[] xpoints, int[] ypoints, int npoints) {
		int[] dxpoints = new int[npoints];
		int[] dypoints = new int[npoints];
		for(int i = 0; i < npoints; i++) {
			dxpoints[i] = xpoints[i] + dx;
			dypoints[i] = ypoints[i] + dy;
		}
		g.drawPolyline(dxpoints, dypoints, npoints);
	}

	public void drawRoundRect(int x, int y, int width, int height, int arcwidth, int archeight) {
		g.drawRoundRect(x + dx, y + dy, width, height, arcwidth, archeight);
	}

	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		g.drawString(iterator, x + dx, y + dy);
	}

	public void drawString(String str, int x, int y) {
		g.drawString(str, x + dx, y + dy);
	}

	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		g.fillArc(x + dx, y + dy, width, height, startAngle, arcAngle);
	}

	public void fillOval(int x, int y, int width, int height) {
		g.fillOval(x + dx, y + dy, width, height);
	}

	public void fillPolygon(int[] xpoints, int[] ypoints, int npoints) {
		int[] dxpoints = new int[npoints];
		int[] dypoints = new int[npoints];
		for(int i = 0; i < npoints; i++) {
			dxpoints[i] = xpoints[i] + dx;
			dypoints[i] = ypoints[i] + dy;
		}
		g.fillPolygon(dxpoints, dypoints, npoints);
	}

	public void fillRect(int x, int y, int width, int height) {
		g.fillRect(x + dx, y + dy, width, height);
	}

	public void fillRoundRect(int x, int y, int width, int height) {
		g.fillRoundRect(x + dx, y + dy, width, height);
	}

	public Shape getClip() {
		// TODO
	}

	public Rectangle getClipBounds() {
		// TODO
	}

	public Color getColor() {
		return g.getColor();
	}

	public Font getFont() {
		return g.getFont();
	}

	public FontMetric getFontMetric(Font f) {
		return g.getFontMetric(f);
	}

	public void setClip(int x, int y, int width, int height) {
		g.setClip(x + dx, y + dy, width, height);
	}

	public void setClip(Shape clip) {
		g.setClip(clip); // TODO translate
	}

	public void setColor(Color c) {
		g.setColor(c);
	}

	public void setFont(Font f) {
		g.setFont(f);
	}

	public void setPaintMode() {
		g.setPaintMode();
	}

	public void setXORMode(Color c) {
		g.setXORMode(c);
	}

	public String toString() {
		return g.toString();
	}

	public void translate(int x, int y) {
		g.translate(x, y); // TODO do I need to translate this?
		// also: ``Dude, I heard you like to translate, so I put some Graphics in your ChunkGraphics so you can translate while you translate
	}
}
