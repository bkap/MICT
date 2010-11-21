package mict.test;

import java.awt.*;
import javax.swing.*;

public class ImageTest extends JPanel {
	public static void popup(Image img) {
		JFrame jf = new JFrame("RDE::IMAGE_TEST");
		JPanel jp = new ImageTest(img);
		jf.setSize(500, 500);
		jf.getContentPane().add(jp);
		jf.setVisible(true);
		jp.repaint();
	}

	public ImageTest(Image img) {
		this.img = img;
	}

	private Image img;
	private int id = (int)(Math.random() * 1000);

	public void paint(Graphics g) {
		System.out.println("Image" + id + ": " + img);
		g.drawImage(img, 0, 0, this);
	}
}
