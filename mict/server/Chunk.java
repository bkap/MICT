package mict.server;

import java.awt.*;
import java.awt.image.*;

public class Chunk implements ImageObserver {
	public static final int WIDTH = 1024;
	public static final int HEIGHT = 1024;

	public static int getWidth() {
		return WIDTH;
	}

	public static int getHeight() {
		return HEIGHT;
	}

	public static int[] getAffectedChunks(long x, long y, long w, long h) {
		int left = (int)(x - getWidth() + 1) / getWidth();
		int top = (int)(y - getHeight() + 1) / getHeight();
		int right = left + (int)(w + getWidth() - 1) / getWidth();
		int bottom = top + (int)(h + getHeight() - 1) / getHeight();
		int[] result = new int[] {left, top, right, bottom};
		return result;
	}

	public static int[] getAffectedChunks(long[] rect) {
		return getAffectedChunks(rect[0], rect[1], rect[2], rect[3]);
	}

	public Chunk(int x, int y, Image img) {
		this.x = x;
		this.y = y;
		if(img instanceof BufferedImage) {
			this.img = (BufferedImage)img;
			loading = false;
		} else {
			this.img = new BufferedImage(img.getWidth(this), img.getHeight(this), BufferedImage.TYPE_INT_ARGB);
			this.img.getGraphics().drawImage(img, 0, 0, this);
			loading = true;
			try {
				while(loading) {
					wait();
				}
			} catch(InterruptedException e) {
				System.out.println("RDEINFO: in Chunk(int, int, ImageObserver), interrupted wait and got loading=" + (loading ? "true" : "false"));
			}
		}
	}

	private boolean loading;
	private int x;
	private int y;
	private BufferedImage img;

	public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
		loading = false;
		notify();
		return true;
	}

	/** returns the graphics context for the internal image representation, translated for tiling correction
	 */
	public Graphics2D getGraphics(long userx, long usery) {
		Graphics2D c =  (Graphics2D)img.getGraphics();
		c.translate((int)(userx + x * getWidth()), (int)(usery + y * getHeight())); // TODO PERMUTE
		return c;
	}

	public Image getImage() {
		return img;
	}

	public String toString() {
		return "[mict.server.Chunk: x=" + x + ", y=" + y + ", img=" + img + ']';
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
