package challenge24.y2014.finals;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

//import javax.imageio.ImageIO;
//import javax.imageio.ImageWriter;

public class N {
	final int S = 350;
	double maxC;
	public static final int[] dx = new int[]{1, 0, -1, 0};
	public static final int[] dy = new int[]{0, 1, 0, -1};
	ColorMaster colorMaster = new ColorMaster();

	int imgCoord(double x) {
		int xc = (int) (S / 2 * (1 + x / maxC));
		if (xc < 0 || xc >= S) {
			throw new RuntimeException("" + x);
		}
		return xc;
	}

	void run() {
		int slices = in.nextInt();
		double[][][] vx = new double[slices][][];
		double[][][] vy = new double[slices][][];
		maxC = 0;
		for (int slice = 0; slice < slices; slice++) {
			int circles = in.nextInt();
			vx[slice] = new double[circles][];
			vy[slice] = new double[circles][];
			for (int circle = 0; circle < circles; circle++) {
				int m = in.nextInt();
				vx[slice][circle] = new double[m + 1];
				vy[slice][circle] = new double[m + 1];
				for (int i = 0; i < m; i++) {
					vx[slice][circle][i] = in.nextDouble();
					vy[slice][circle][i] = in.nextDouble();
					maxC = Math.max(maxC, Math.abs(vx[slice][circle][i]));
					maxC = Math.max(maxC, Math.abs(vy[slice][circle][i]));
				}
				vx[slice][circle][m] = vx[slice][circle][0];
				vy[slice][circle][m] = vy[slice][circle][0];
			}
		}
		maxC *= 1.01;
//		System.out.println("MaxC = " + maxC);
		List<Circle> circles = new ArrayList<Circle>();
		for (int slice = 0; slice < slices; slice++) {
			BufferedImage img = new BufferedImage(S, S, BufferedImage.TYPE_INT_RGB);
			Graphics g = img.getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, S, S);
			g.setColor(Color.BLACK);
			for (int circle = 0; circle < vx[slice].length; circle++) {
				for (int i = 0; i < vx[slice][circle].length - 1; i++) {
					g.drawLine(imgCoord(vx[slice][circle][i]), imgCoord(vy[slice][circle][i]), imgCoord(vx[slice][circle][i + 1]), imgCoord(vy[slice][circle][i + 1]));
				}
			}
//			ImageWriter iw = ImageIO.getImageWritersByFormatName("png").next();
//			try {
//				iw.setOutput(ImageIO.createImageOutputStream(new File(imgName + "_" + slice + ".png")));
//				iw.write(img);
//			} catch (IOException e) {
//				throw new RuntimeException(e);
//			}
			boolean[][] border = new boolean[S][S];
			for (int i = 0; i < S; i++) {
				for (int j = 0; j < S; j++) {
					border[i][j] = (img.getRGB(i, j) & 0xFFFFFF) == 0;
				}
			}
			boolean[][] outside = new boolean[S][S];
			dfs(0, 0, border, outside);
			for (int i = 0; i < S; i++) {
				for (int j = 0; j < S; j++) {
					outside[i][j] |= border[i][j];
					border[i][j] = outside[i][j];
				}
			}
			boolean[][] mark = new boolean[S][S];
			for (int i = 0; i < S; i++) {
				for (int j = 0; j < S; j++) {
					if (outside[i][j] || mark[i][j]) {
						continue;
					}
					sumx = 0;
					sumy = 0;
					count = 0;
					dfs(i, j, outside, mark);
					Circle circle = new Circle(sumx / count, sumy / count, slice, count);
					circles.add(circle);
				}
			}
//			ImageWriter iw = ImageIO.getImageWritersByFormatName("png").next();
//			try {
//				iw.setOutput(ImageIO.createImageOutputStream(new File(imgName + "_" + slice + ".png")));
//				iw.write(img);
//			} catch (IOException e) {
//				throw new RuntimeException(e);
//			}
		}
		int colors = 0;
		Set<Circle> mark = new HashSet<>();
		List<Bubble> preliminaryBubbles = new ArrayList<>();
		for (Circle circle : circles) {
			if (!mark.contains(circle)) {
				count = 0;
				largest = 0;
				sumx = 0;
				sumy = 0;
				sumz = 0;
				dfs(circle, circles, mark, colors);
				Bubble bubble = new Bubble(sumx / count, sumy / count, sumz / count, count);
				preliminaryBubbles.add(bubble);
				colors++;
			}
		}
//		for (int slice = 0; slice < slices; slice++) {
//			BufferedImage img = new BufferedImage(S, S, BufferedImage.TYPE_INT_RGB);
//			Graphics g = img.getGraphics();
//			g.setColor(Color.WHITE);
//			g.fillRect(0, 0, S, S);
//			g.setColor(Color.BLACK);
//			for (int circle = 0; circle < x[slice].length; circle++) {
//				for (int i = 0; i < x[slice][circle].length - 1; i++) {
//					g.drawLine(imgCoord(x[slice][circle][i]), imgCoord(y[slice][circle][i]), imgCoord(x[slice][circle][i + 1]), imgCoord(y[slice][circle][i + 1]));
//				}
//			}
//			for (Circle circle : circles) {
//				if (circle.z != slice) {
//					continue;
//				}
//				g.setColor(colorMaster.color(circle.color));
//				int r = (int) Math.pow(bubbles.get(circle.color).count, 1.0/3);
//				g.fillOval((int) circle.x - r, (int) circle.y - r / 2, 2 * r, r);
//			}
//			ImageWriter iw = ImageIO.getImageWritersByFormatName("png").next();
//			try {
//				iw.setOutput(ImageIO.createImageOutputStream(new File(imgName + "_" + slice + ".png")));
//				iw.write(img);
//			} catch (IOException e) {
//				throw new RuntimeException(e);
//			}
//		}
		double[] sizes = new double[preliminaryBubbles.size()];
		for (int i = 0; i < sizes.length; i++) {
			sizes[i] = preliminaryBubbles.get(i).count;
		}
		Arrays.sort(sizes);
		double maxSize = sizes[sizes.length - 1];
		List<Bubble> bubbles = new ArrayList<>();
		List<Bubble> fidu = new ArrayList<>();
//		for (int i = 0; i < sizes.length; i++) {
//			sizes[i] /= maxSize;
//		}
//		System.out.println(Arrays.toString(sizes));
		for (Bubble oldBubble : preliminaryBubbles) {
			Bubble bubble = new Bubble(oldBubble.x, oldBubble.y, oldBubble.z, 0);
			double size = oldBubble.count / maxSize;
			if (size > 0.9) {
				bubble.count = 2;
				fidu.add(bubble);
				continue;
			}
			if (size > 0.34 && size < 0.6) {
				bubble.count = 1;
				bubbles.add(bubble);
				continue;
			}
			if (size > 0.048 && size < 0.14) {
				bubble.count = 0;
				bubbles.add(bubble);
				continue;
			}
			if (size < 0.02) {
				continue;
			}
			throw new RuntimeException("" + size);
		}
		System.out.println(bubbles.size() + " bubbles");
		double[] d = new double[3];
		double bestQual = Double.POSITIVE_INFINITY;
		double bestCoef = -1;
		for (double coef = 0.01; coef <= 10; coef += 0.001) {
			for (int i = 0; i < 3; i++) {
				Bubble p = fidu.get((i + 1) % 3);
				Bubble q = fidu.get((i + 2) % 3);
				d[i] = Math.hypot(coef * (p.z - q.z), Math.hypot(p.x - q.x, p.y - q.y));
			}
			Arrays.sort(d);
			double qual = Math.abs(d[2] / d[0] - Math.sqrt(2)) +  Math.abs(d[2] / d[1] - Math.sqrt(2)) + 1e-9 * Math.abs(coef - 1);
			if (qual < bestQual) {
				bestQual = qual;
				bestCoef = coef;
			}
		}
		System.out.println("Coef: " + bestCoef);
		for (Bubble bubble : bubbles) {
			bubble.z *= bestCoef;
		}
		for (Bubble bubble : fidu) {
			bubble.z *= bestCoef;
		}
		double maxD = -1;
		int v = -1;
		for (int i = 0; i < 3; i++) {
			Bubble p = fidu.get((i + 1) % 3);
			Bubble q = fidu.get((i + 2) % 3);
			d[i] = Math.hypot(p.z - q.z, Math.hypot(p.x - q.x, p.y - q.y));
			if (d[i] > maxD) {
				maxD = d[i];
				v = i;
			}
		}
		Bubble f0 = fidu.get(v);
		Bubble f1 = fidu.get((v + 1) % 3);
		Bubble f2 = fidu.get((v + 2) % 3);
		Bubble f3;
		{
			double len = maxD / Math.sqrt(2);
			double x1 = (f1.x - f0.x) / len;
			double x2 = (f2.x - f0.x) / len;
			double y1 = (f1.y - f0.y) / len;
			double y2 = (f2.y - f0.y) / len;
			double z1 = (f1.z - f0.z) / len;
			double z2 = (f2.z - f0.z) / len;
			double x3 = y1 * z2 - z1 * y2;
			double y3 = z1 * x2 - x1 * z2;
			double z3 = x1 * y2 - y1 * x2;
			f3 = new Bubble(f0.x + x3 * len, f0.y + y3 * len, f0.z + z3 * len, 2);
		}
		double[] xs = new double[bubbles.size()];
		double[] ys = new double[bubbles.size()];
		double[] zs = new double[bubbles.size()];
		double sumZ = 0;
		for (int i = 0; i < bubbles.size(); i++) {
			xs[i] = scalar(f0, f1, bubbles.get(i));
			ys[i] = scalar(f0, f2, bubbles.get(i));
			double z = scalar(f0, f3, bubbles.get(i));
			sumZ += z;
			zs[i] = Math.abs(z);
		}
		int[] xo = integer(xs);
		int[] yo = integer(ys);
		int[] zo = integer(zs);
		if (sumZ < 0) {
			int[] t = xo; xo = yo; yo = t;
		}
		int maxX = 0;
		int maxY = 0;
		int maxZ = 0;
		for (int i = 0; i < bubbles.size(); i++) {
			bubbles.get(i).x = xo[i];
			bubbles.get(i).y = yo[i];
			bubbles.get(i).z = zo[i];
			maxX = Math.max(maxX, xo[i]);
			maxY = Math.max(maxY, yo[i]);
			maxZ = Math.max(maxZ, zo[i]);
		}
		int[][][] a = new int[maxX][maxY][maxZ];
		for (int i = 0; i < bubbles.size(); i++) {
			a[xo[i] - 1][yo[i] - 1][zo[i] - 1] += 2 + bubbles.get(i).count;
		}
		boolean stopped = false;
		for (int z = 0; z < maxZ; z++) {
			for (int y = 0; y < maxY; y++) {
				for (int x = 0; x < maxX; x++) {
					int b = a[x][y][z];
					if (b == 0) {
						stopped = true;
						continue;
					}
					if (b < 2 || b > 3) {
						throw new RuntimeException("" + b);
					}
					if (stopped) {
						throw new RuntimeException("stopped");
					}
					out.print(b - 2);
				}
				if (!stopped) {
					out.println();
				}
			}
			if (!stopped) {
				out.println();
			}
		}
	}

	static int[] integer(double[] xs) {
		int n = xs.length;
		double[] x = xs.clone();
		Arrays.sort(x);
		for (int i = 0; i < n - 1; i++) {
			x[i] = x[i + 1] - x[i];
		}
		x = Arrays.copyOf(x, n - 1);
		Arrays.sort(x);
		double maxDiff = x[x.length - 1];
		x = xs.clone();
		Arrays.sort(x);
		Map<Double, Integer> map = new TreeMap<>();
		int t = 1;
		map.put(x[0], t);
		for (int i = 1; i < n; i++) {
			double d = (x[i] - x[i - 1]) / maxDiff;
			if (d > 0.2 && d < 0.8) {
				throw new RuntimeException("" + d);
			}
			if (d > 0.5) {
				t++;
			}
			map.put(x[i], t);
		}
		int[] result = new int[n];
		for (int i = 0; i < n; i++) {
			result[i] = map.get(xs[i]);
		}
		return result;
	}

	static double scalar(Bubble o, Bubble a, Bubble b) {
		return (a.x - o.x) * (b.x - o.x) + (a.y - o.y) * (b.y - o.y) + (a.z - o.z) * (b.z - o.z);
	}

	void dfs(Circle v, List<Circle> circles, Set<Circle> mark, int color) {
		mark.add(v);
		v.color = color;
		count += v.count;
		sumx += v.x * v.count;
		sumy += v.y * v.count;
		sumz += v.z * v.count;
		largest = Math.max(largest, v.count);
		for (Circle u : circles) {
			if (v == u) {
				continue;
			}
			if (mark.contains(u)) {
				continue;
			}
			if (Math.abs(v.z - u.z) > 1) {
				continue;
			}
			double dist = Math.hypot(v.x - u.x, v.y - u.y);
			if (dist > 10) {
				continue;
			}
			dfs(u, circles, mark, color);
		}
	}

	static class Bubble {
		double x, y, z;
		int count;

		public Bubble(double x, double y, double z, int count) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.count = count;
		}

		public double dist(Bubble that) {
			return Math.hypot(z - that.z, Math.hypot(x - that.x, y - that.y));
		}

		@Override
		public String toString() {
			return x + " " + y + " " + z;
		}
	}

	static class Circle {
		double x, y, z;
		int count;
		int color;

		public Circle(double x, double y, int z, int count) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.count = count;
		}
	}

	double sumx, sumy, sumz;
	int count, largest;

	void dfs(int i, int j, boolean[][] border, boolean[][] mark) {
		mark[i][j] = true;
		sumx += i;
		sumy += j;
		count++;
		for (int d = 0; d < 4; d++) {
			int ii = i + dx[d];
			int jj = j + dy[d];
			if (ii < 0 || jj < 0 || ii >= S || jj >= S) {
				continue;
			}
			if (mark[ii][jj]) {
				continue;
			}
			if (border[ii][jj]) {
				continue;
			}
			dfs(ii, jj, border, mark);
		}
	}

	static String fileName = N.class.getSimpleName().replaceFirst("_.*", "").toUpperCase();
	static MyScanner in;
	static PrintWriter out;
	static String imgName;

	public static void main(String[] args) throws IOException {
		Locale.setDefault(Locale.US);
		for (int test = 0; test <= 10; test++) {
			String inputFileName = "input/" + fileName + "/" + test + ".in";
			String outputFileName = "output/" + fileName + test + ".out";
			imgName = "output/img/" + fileName + test;
			BufferedReader br = new BufferedReader(new FileReader(inputFileName));
			out = new PrintWriter(outputFileName);
			in = new MyScanner(br);
			new N().run();
			System.out.println("Test " + test + " processed");
			br.close();
			out.close();
		}
	}

	static class MyScanner {
		BufferedReader br;
		StringTokenizer st;

		MyScanner(BufferedReader br) {
			this.br = br;
		}

		void findToken() {
			while (st == null || !st.hasMoreTokens()) {
				try {
					st = new StringTokenizer(br.readLine());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

		String next() {
			findToken();
			return st.nextToken();
		}

		int nextInt() {
			return Integer.parseInt(next());
		}

		long nextLong() {
			return Long.parseLong(next());
		}

		double nextDouble() {
			return Double.parseDouble(next());
		}
	}

	static class ColorMaster {
		int from, to;
		long seed;

		public ColorMaster(int from, int to, long seed) {
			this.from = from;
			this.to = to;
			this.seed = seed;
		}

		public ColorMaster() {
			this(50, 255, 394243585);
		}

		public Color color(long id) {
			id *= seed;
			int range = to - from;
			int[] color = new int[3];
			for (int i = 0; i < color.length; i++) {
				color[i] = (int) (to - 1 - id % range);
				id /= range;
			}
			return new Color(color[0], color[1], color[2]);
		}
	}
}
