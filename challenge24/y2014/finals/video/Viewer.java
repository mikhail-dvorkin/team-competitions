package challenge24.y2014.finals.video;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class Viewer implements Runnable {
	String streamName;

	public Viewer(String streamName) {
		this.streamName = streamName;
	}

	@Override
	public void run() {
		try {
			ImageFrame frame = new ImageFrame();
//			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			VideoStream stream = new VideoStream(streamName);

			frame.pack();
			frame.setVisible(true);

			while (true) {
				try {
					byte[] image = stream.retrieveNextImage();
					BufferedImage bi = ImageIO.read(new ByteArrayInputStream(
							image));
					frame.bi = bi;
					frame.repaint();
				} catch (IOException x) {
					x.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Viewer("http://repo.hu/mjpg/video.mjpg").run();
	}
}
