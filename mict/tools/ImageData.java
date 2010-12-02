package mict.tools;

import java.awt.image.BufferedImage;
/** A simple structure used to store an image and the location to draw it.
 * 
 * 
 * @author bkaplan
 * @since 120110
 */
public class ImageData {
	public int x;
	public int y;
	public BufferedImage img;
	public ImageData(int x, int y, BufferedImage img) {
		this.x = x;
		this.y = y;
		this.img = img;
	}
}
