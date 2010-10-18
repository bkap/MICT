package mict.server;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.Serializable;

public class Chunk implements ImageObserver, Serializable {
	public static final int WIDTH = 1024;
	public static final int HEIGHT = 1024;

	public static int getWidth() {
		return WIDTH;
	}

	public static int getHeight() {
		return HEIGHT;
	}

	public static int[] getAffectedChunks(long x, long y, long w, long h) {
		int left = (x - getWidth() + 1) / getWidth();
		int top = (y - getHeight() + 1) / getHeight();
		int right = left + (w + getWidth() - 1) / getWidth();
		int bottom = top + (h + getHeight() - 1) / getHeight();
		return new int[] {left, top, right, bottom};
	}

	public static int[] getAffectedChunks(int[] rect) {
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
	public Graphics getGraphics(long userx, long usery) {
		Graphics c =  img.getGraphics();
		c.translate(userx + x * getWidth()); // TODO PERMUTE
		c.translate(usery + y * getHeight()); // TODO PERMUTE
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