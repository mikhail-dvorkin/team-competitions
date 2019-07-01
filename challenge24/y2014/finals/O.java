package challenge24.y2014.finals;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import challenge24.y2014.finals.video.VideoStream;

public class O {
	static final long VERSION = System.currentTimeMillis();
	static final int n = 9;
	static final int maxTicks = 20000;
	static final String server = "server.ch24.org";
	static final int portControl = 16100;
	static final int portVideo = 16200;
	static final String streamName = "http://" +  server + ":" + portVideo + "/mjpg/video.mjpg";

	static void log(String message) {
		System.out.println(message);
		log.println(message);
		log.flush();
	}

	static String read() throws IOException {
		String s = br.readLine();
		log(">>> " + s);
		return s;
	}

	static void write(String s) {
		log("<<<<< " + s);
		out.print(s + '\n');
		out.flush();
	}

	static void writeInstructions(String s) {
		s = s.trim();
		for (String t : s.split("\n")) {
			write(t);
		}
	}

	static void openConnection() throws UnknownHostException, IOException {
		socket = new Socket(InetAddress.getByName(server), portControl);
		br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
		out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
	}

	static String fileName = O.class.getSimpleName().replaceFirst("_.*", "").toUpperCase();
	static File instrFile = new File(fileName + ".txt");
	static File verFile = new File(fileName + ".ver.txt");
	static Socket socket;
	static BufferedReader br;
	static PrintWriter out;
	static PrintWriter log;
	static GUI gui;
	static boolean cameraOn = false;

	static int time;
	static int shot;
	static int ax, ay;
	static int sx, sy;
	static int vx, vy;
	static int dx, dy;
	static int wx, wy;
	static int[] hx, hy;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		Locale.setDefault(Locale.US);
		gui = new GUI();
		gui.setTitle("" + VERSION);
		gui.setVisible(true);
		int test = 2;
		log = new PrintWriter(fileName + ".log");
		log("Version: " + VERSION);
		Scanner in = new Scanner(new File("input/" + fileName + "/" + test));
		int targets = in.nextInt();
		int[] tx = new int[targets];
		int[] ty = new int[targets];
		boolean[][] forbid = new boolean[n][n];
		for (int target = 0; target < targets; target++) {
			tx[target] = in.nextInt();
			ty[target] = in.nextInt();
		}
		if (in.nextInt() != n || in.nextInt() != n) {
			throw new RuntimeException();
		}
		for (int i = 0; i < n; i++) {
			String s = in.next();
			for (int j = 0; j < n; j++) {
				forbid[j][i] = s.charAt(j) == '1';
			}
		}
		in.close();
		openConnection();
		String lastInstructions = "";
		boolean inQueue = true;
		time = 0;
		shot = 0;
		ax = -1;
		ay = -1;
		vx = 0;
		vy = 0;
		hx = new int[maxTicks];
		hy = new int[maxTicks];
		additions.clear();
		for (int line = 0;; line++) {
			String s = read();
			{
				long version = Long.MIN_VALUE;
				if (verFile.exists()) {
					in = new Scanner(verFile);
					version = in.nextLong();
					in.close();
				}
				if (VERSION < version) {
					System.exit(0);
				}
				if (VERSION > version) {
					PrintWriter pw = new PrintWriter(verFile);
					pw.println(VERSION);
					pw.close();
				}
			}
			if (s == null) {
				openConnection();
				continue;
			} else if (s.startsWith("## queue")) {
				inQueue = true;
				if (line % 16 == 0) {
					openConnection();
				}
				continue;
			} else if (s.startsWith("## GO")) {
				Toolkit.getDefaultToolkit().beep();
				gui.requestFocus();
				inQueue = false;
				continue;
			} else if (s.equals("## welcome team 4")) {
				continue;
			} else if (s.equals("camera forward for team 4")) {
				log("Starting camera");
				if (!cameraOn) {
					new Thread(new Viewer()).start();
				}
//				Runtime.getRuntime().exec("wget " + streamName);
				continue;
			} else if (s.equals("# input?")) {
				write("" + test);
				continue;
			} else if (s.equals("# timeup")) {
				break;
			} else if (s.matches("^\\d+\\s+\\d+\\s+\\d+$")) {
				in = new Scanner(s);
				time = in.nextInt();
				ax = in.nextInt();
				ay = 511 - in.nextInt();
				if (!cameraOn) {
					new Thread(new Viewer()).start();
				}
			} else if (s.startsWith("# start")) {
			} else {
				log("Unknown: " + s);
				throw new RuntimeException("Unknown: " + s);
			}
			if (inQueue) {
				continue;
			}
			String instr = instruct();
			if (!instr.equals(lastInstructions)) {
				writeInstructions(instr);
				lastInstructions = instr;
			}
		}
		socket.close();
		log.close();
		main(args);
	}

	static class Addition {
		int tFrom, tTo;
		boolean x;
		int delta;

		public Addition(int tFrom, int tTo, boolean x, int delta) {
			this.tFrom = tFrom;
			this.tTo = tTo;
			this.x = x;
			this.delta = delta;
		}
	}

	static List<Addition> additions = new ArrayList<>();
	static Properties properties;

	static String[] buttonNames = {">", "<", "v", "^", "C"};

	static int prop(String name) {
		return Integer.parseInt(properties.getProperty(name));
	}

	static double z(double x) {
		if (x > 1) {
			return 1;
		}
		if (x < -1) {
			return -1;
		}
		return x;
	}

	static double s(double x) {
		if (x > 1) {
			return 1;
		}
		if (x < -1) {
			return -1;
		}
		return 0;
	}

	static void button(int index) {
		log("Human pressed button " + index);
		if (index == 4) {
			additions.clear();
			return;
		}
		boolean x = index < 2;
		int DELTA = Integer.parseInt(properties.getProperty("delta"));
		int STEP = Integer.parseInt(properties.getProperty("step"));
		int delta = (index % 2 == 1) ? -DELTA : DELTA;
		additions.add(new Addition(time, time + STEP, x, delta));
//		additions.add(new Addition(time + STEP, time + 3 * STEP / 2, x, -delta));
	}

	static String instruct() throws IOException {
		properties = new Properties();
		FileReader fileReader = new FileReader(instrFile);
		properties.load(fileReader);
		fileReader.close();
		wx = prop("wx");
		wy = prop("wy");
		dx = prop("dx");
		dy = prop("dy");
		for (int i = additions.size() - 1; i >= 0; i--) {
			if (time > additions.get(i).tTo) {
				additions.remove(i);
			}
		}
		for (Addition addition : additions) {
			if (addition.tFrom < time && time <= addition.tTo) {
				if (addition.x) {
					wx += addition.delta;
				} else {
					wy += addition.delta;
				}
			}
		}
		if (Math.hypot(vx, vy) < 5) {
			if (Math.hypot(ax - wx, ay - wy) < 10) {
				log("STILL LIFE :))))))))))");
				if (additions.isEmpty() && prop("ap") == 1) {
					if (sx < dx - 30) {
						button(0);
					}
					if (sx > dx + 30) {
						button(1);
					}
					if (sy < dy - 30) {
						button(2);
					}
					if (sy > dy + 30) {
						button(3);
					}
				}
			}
		}
		int vxd = (int) Math.round(s((dx - sx) * 1.0 / prop("far")) * prop("vd"));
		int vyd = (int) Math.round(s((dy - sy) * 1.0 / prop("far")) * prop("vd"));
		wx -= prop("a") * (vx - vxd) / 10 + prop("b") * (sx - dx) * Math.abs(sx - dx) / 100;
		wy -= prop("a") * (vy - vyd) / 10 + prop("b") * (sy - dy) * Math.abs(sy - dy) / 100;
		log(sx + " " + sy + " " + vx + " " + vy);
		gui.label.setText(wx + " " + wy);
		wx = Math.min(501, Math.max(10, wx));
		wy = Math.min(501, Math.max(10, wy));
		log("W: " + wx + " " + wy);
		String instr = "";
		instr += "0 0 0 T% 1 report 1\n";
		instr += "1 0 0 X< " + (wx - 9) + " speedX 32\n";
		instr += "2 0 0 X~ " + (wx) + " speedX 0\n";
		instr += "3 0 0 X> " + (wx + 9) + " speedX -32\n";
		instr += "4 0 0 Y< " + ((511 - wy) - 9) + " speedY 32\n";
		instr += "5 0 0 Y~ " + ((511 - wy)) + " speedY 0\n";
		instr += "6 0 0 Y> " + ((511 - wy) + 9) + " speedY -32\n";
		return instr;
	}

	static void analyzeVideo() {
		BufferedImage bi = gui.bi;
		if (bi == null) {
			sx = -1;
			sy = -1;
			return;
		}
		int hei = bi.getHeight();
		int wid = bi.getWidth();
		int[] rgb = bi.getRGB(0, 0, wid, hei, null, 0, wid);
		List<Integer> xs = new ArrayList<Integer>();
		List<Integer> ys = new ArrayList<Integer>();
		for (int i = 0; i < rgb.length; i++) {
			int x = i % wid;
			int y = i / wid;
			if (y < 30) {
				continue;
			}
			if (x > 450) {
				continue;
			}
			int c = rgb[i];
			int b = c & 255;
			c >>= 8;
		int g = c & 255;
		c >>= 8;
		int r = c & 255;
		if (r > 160 && g > 140 && b < 95) {
//			bi.setRGB(x, y, 255);
			xs.add(x);
			ys.add(y);
		}
		}
		Collections.sort(xs);
		Collections.sort(ys);
		sx = xs.get(xs.size() / 2);
		sy = ys.get(ys.size() / 2);
		hx[shot] = sx;
		hy[shot] = sy;
		int hist = 3;
		if (shot >= hist) {
			vx = hx[shot] - hx[shot - hist];
			vy = hy[shot] - hy[shot - hist];
		}
		shot++;
		if (shot == maxTicks) {
			shot = 0;
		}
		Graphics g = bi.getGraphics();
		g.setColor(Color.MAGENTA);
		int s = 9;
		g.fillOval(sx - s, sy - s, 2 * s, 2 * s);
		g.setColor(Color.BLACK);
		g.drawLine(sx, sy, sx + 2 * vx, sy + 2 * vy);
		g.drawLine(250, 250, 250 + ax - wx, 250 + ay - wy);
		g.setColor(Color.BLUE);
		s = 5;
		g.fillOval(dx - s, dy - s, 2 * s, 2 * s);
//		System.out.println(Integer.toHexString(bi.getRGB(sx, sy)));
	}

	@SuppressWarnings("serial")
	static class GUI extends JFrame {
		int hei = 800;
		int wid = 600;
		JSlider sliderX;
		JButton[] buttons;
		JLabel label;
		BufferedImage bi;

		@Override
		public void paint(Graphics g) {
			super.paint(g);

			if (bi != null) {
				g.drawImage(bi, 10, 90, null);
			} else {
				g.drawLine(10, 90, 150, 100);
				g.drawLine(10, 90, (int) (VERSION % 500), 100);
			}
		}

		{
			setPreferredSize(new Dimension(hei, wid));
			setSize(hei, wid);
			setLayout(new FlowLayout());
			sliderX = new JSlider(0, 511);
			sliderX.setValue(0);
//			add(sliderX);
			buttons = new JButton[5];
			for (int i = 0; i < buttons.length; i++) {
				final int finalI = i;
				buttons[i] = new JButton();
				buttons[i].setText(buttonNames[i]);
				buttons[i].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(@SuppressWarnings("unused") ActionEvent e) {
						button(finalI);
					}
				});
				add(buttons[i]);
			}
			label = new JLabel("");
			add(label);
			pack();
			validate();
		}
	}

	static class Viewer implements Runnable {
		@Override
		public void run() {
			try {
//				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				log("Initializing stream...");
				cameraOn = true;
				try {
					Thread.sleep(25);
				} catch (InterruptedException e) {
				}
				VideoStream stream = new VideoStream(streamName);
				log("Inited stream");

				while (true) {
					try {
						byte[] image = stream.retrieveNextImage();
						BufferedImage bi = ImageIO.read(new ByteArrayInputStream(
								image));
						gui.bi = bi;
						analyzeVideo();
						gui.repaint();
					} catch (IOException x) {
						x.printStackTrace();
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
