package mict.server;

import java.awt.*;
import java.awt.image.*;

/**
 * @author  bkaplan
 */
public class Chunk implements ImageObserver {
	/**
	 * @uml.property  name="wIDTH"
	 */
	public static final int WIDTH = 1024;
	/**
	 * @uml.property  name="hEIGHT"
	 */
	public static final int HEIGHT = 1024;

	/**
	 * @return
	 * @uml.property  name="wIDTH"
	 */
	public static int getWidth() {
		return WIDTH;
	}

	/**
	 * @return
	 * @uml.property  name="hEIGHT"
	 */
	public static int getHeight() {
		return HEIGHT;
	}

	public static int[] getAffectedChunks(long x, long y, long w, long h) {
		int left = (int)(x - getWidth() + 1) / getWidth();
		int top = (int)(y - getHeight() + 1) / getHeight();
		int right = left + (int)(w + getWidth() - 1) / getWidth();
		int bottom = top + (int)(h + getHeight() - 1) / getHeight();
		int[] result = new int[] {left - 1, top - 1, right + 1, bottom + 1};
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
			this.img.getGraphics().setColor(Color.WHITE);
			this.img.getGraphics().fillRect(0, 0, getWidth(), getHeight());
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
	/**
	 * @uml.property  name="x"
	 */
	private int x;
	/**
	 * @uml.property  name="y"
	 */
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
		Graphics2D c = (Graphics2D)img.getGraphics();
		c.translate((int)(-x * getWidth() + userx), (int)(-y * getHeight() + usery));
		return c;
	}

	public Image getImage() {
		return img;
	}

	public String toString() {
		return "[mict.server.Chunk: x=" + x + ", y=" + y + ", img=" + img + ']';
	}

	/**
	 * @return
	 * @uml.property  name="x"
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return
	 * @uml.property  name="y"
	 */
	public int getY() {
		return y;
	}
}
