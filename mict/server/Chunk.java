package mict.server;

public class Chunk implements ImageObserver {
	public Chunk(int x, int y, Image img) {
		this.x = x;
		this.y = y;
		if(img instanceof BufferedImage)
			this.img = (BufferedImage)img;
			loading = false;
		else {
			this.img = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
			this.img.getGraphics().drawImage(img, 
			loading = true;
			try {
				while(loading) {
					wait();
				}
			} catch(InterruptedException e) {
				System.out.println("RDEINFO: in Chunk(int, int, ImageObserver), interrupted wait and got loading=" + loading ? "true" : "false");
			}
		}
	}

	private boolean loading;
	private int x;
	private int y;
	private BufferedImage img;

	public boolean updateImage(Image img, int flags, int x, int y, int w, int h) {
		loading = false;
		notify();
		return true;
	}

	/** returns the graphics context for the internal image representation
	 *
	 * WARNING: does not return a ChunkGraphics object. This method is not magic!
	 */
	public Graphics getGraphics() {
		return img.getGraphics();
	}
}
