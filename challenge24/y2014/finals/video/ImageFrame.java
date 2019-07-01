package challenge24.y2014.finals.video;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

@SuppressWarnings("serial")
public class ImageFrame extends JFrame {
	BufferedImage bi;

	public ImageFrame() {
		super("stream");
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		if (bi != null) {
			Dimension size = new Dimension(bi.getWidth(), bi.getHeight());
			setPreferredSize(size);
			setSize(size);
			g.drawImage(bi, 0, 0, null);
		} else {
			Dimension size = new Dimension(100, 100);
			setPreferredSize(size);
			setSize(size);
			g.drawLine(0, 0, 100, 50);
			g.drawLine(0, 0, 0, 100);
		}
	}
}
