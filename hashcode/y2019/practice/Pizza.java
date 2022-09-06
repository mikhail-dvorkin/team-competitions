package hashcode.y2019.practice;

import java.io.*;
import java.util.*;
import hashcode.y2019.practice.Sat.*;
import hashcode.y2019.practice.SimulatedAnnealing.*;

public class Pizza {
	boolean[][] field;
	int hei, wid;
	int need;
	int maxSize;
	String filename;
	PrintWriter out;
	Settings settings = new Settings(
			3, 1 << 19, 0, Integer.MAX_VALUE, -Double.MAX_VALUE, 10000);
	Random random = new Random(566);

	void solve(boolean init) {
		System.out.println("Solving " + filename);
		int sz = 25;
		int xChunks = 40;
		int yChunks = 40;
		ArrayList<Rectangle> taken;
		if (init) {
			taken = new ArrayList<>();
			for (int xChunk = 0; xChunk < xChunks; xChunk++) {
				for (int yChunk = 0; yChunk < yChunks; yChunk++) {
					improve(xChunk * sz, yChunk * sz, sz, taken);
				}
			}
		} else {
			taken = input();
			for (int improvements = 0; improvements < 1; improvements++) {
				for (int xChunk = 0; xChunk < xChunks - 1; xChunk++) {
					for (int yChunk = 0; yChunk < yChunks; yChunk++) {
						improve(xChunk * sz + sz / 2, yChunk * sz, sz + 1, taken);
					}
				}
				for (int xChunk = 0; xChunk < xChunks; xChunk++) {
					for (int yChunk = 0; yChunk < yChunks - 1; yChunk++) {
						improve(xChunk * sz, yChunk * sz + sz / 2, sz + 1, taken);
					}
				}
				for (int xChunk = 0; xChunk < xChunks; xChunk++) {
					for (int yChunk = 0; yChunk < yChunks; yChunk++) {
						improve(xChunk * sz, yChunk * sz, sz, taken);
					}
				}
			}
		}
	}

	void improve(int xLow, int yLow, int sz, ArrayList<Rectangle> taken) {
		Rectangle rectangle = new Rectangle(xLow, yLow, sz);
		boolean improved = improve(rectangle, taken);
		if (improved) {
			output(taken);
		}
	}

	void output(ArrayList<Rectangle> taken) {
		try {
			out = new PrintWriter(outputFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		int covered = 0;
		int xMaxSeen = 0, yMaxSeen = 0;
		out.println(taken.size());
		for (Rectangle rectangle : taken) {
			out.println(rectangle);
			covered += rectangle.area();
			xMaxSeen = Math.max(xMaxSeen, rectangle.xHigh);
			yMaxSeen = Math.max(yMaxSeen, rectangle.yHigh);
		}
		double coverage = covered * 100.0 / xMaxSeen / yMaxSeen;
		out.println(String.format("\n%d %d\tCovered: %.2f",	need, maxSize, coverage));
		System.out.println(String.format("Covered: %.2f", coverage));
		char[][] c = new char[2 * xMaxSeen + 1][2 * yMaxSeen + 1];
		for (int i = 0; i < c.length; i++) {
			Arrays.fill(c[i], '#');
		}
		for (int i = 0; i < xMaxSeen; i++) {
			for (int j = 0; j < yMaxSeen; j++) {
				c[2 * i + 1][2 * j + 1] = field[i][j] ? 'T' : 'M';
			}
		}
		for (Rectangle rectangle : taken) {
			for (int i = 2 * rectangle.xLow; i <= 2 * rectangle.xHigh; i++) {
				c[i][2 * rectangle.yLow] = c[i][2 * rectangle.yHigh] = '|';
			}
			for (int j = 2 * rectangle.yLow; j <= 2 * rectangle.yHigh; j++) {
				c[2 * rectangle.xLow][j] = c[2 * rectangle.xHigh][j] = '-';
			}
			for (int i = 2 * rectangle.xLow + 1; i < 2 * rectangle.xHigh; i++) {
				for (int j = 2 * rectangle.yLow + 1; j < 2 * rectangle.yHigh; j++) {
					if (i * j % 2 == 0) {
						c[i][j] = ' ';
					}
				}
			}
		}
		for (int i = 0; i < c.length; i++) {
			out.println(new String(c[i]));
		}
		out.close();
	}

	ArrayList<Rectangle> input() {
		ArrayList<Rectangle> read = new ArrayList<>();
		Scanner in;
		try {
			in = new Scanner(outputFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		int n = in.nextInt();
		for (int i = 0; i < n; i++) {
			Rectangle rectangle = new Rectangle(in);
			read.add(rectangle);
		}
		in.close();
		return read;
	}

	File outputFile() {
		return new File(filename + ".out");
	}

//	ArrayList<Rectangle> solve(Rectangle theArea) {
//		ArrayList<Rectangle> list = new ArrayList<>();
//		list = improve(theArea, list);
//		return list;
//	}
	int mode;

	boolean improve(Rectangle theArea, ArrayList<Rectangle> previous) {
		theArea.xHigh = Math.min(theArea.xHigh, hei);
		theArea.yHigh = Math.min(theArea.yHigh, wid);
		if (theArea.xLow >= theArea.xHigh || theArea.yLow >= theArea.yHigh) {
			return false;
		}
		System.out.print("Improving " + theArea + "...  \t");
		int statOld = 0;
		ArrayList<Rectangle> result = new ArrayList<>();
		int freeArea = theArea.area();
		boolean[][] killed = new boolean[theArea.xHigh - theArea.xLow][theArea.yHigh - theArea.yLow];
		for (Rectangle rectangle : previous) {
			if (!theArea.intersects(rectangle)) {
				result.add(rectangle);
				continue;
			}
			if (theArea.contains(rectangle)) {
				statOld += rectangle.area();
				continue;
			}
			result.add(rectangle);
			for (int x = rectangle.xLow; x < rectangle.xHigh; x++) {
				for (int y = rectangle.yLow; y < rectangle.yHigh; y++) {
					if (theArea.xLow <= x && x < theArea.xHigh && theArea.yLow <= y && y < theArea.yHigh) {
						killed[x - theArea.xLow][y - theArea.yLow] = true;
						freeArea--;
					}
				}
			}
		}
		ArrayList<Rectangle> possible = new ArrayList<>();
		for (int xc = theArea.xLow; xc < theArea.xHigh; xc++) {
			for (int yc = theArea.yLow; yc < theArea.yHigh; yc++) {
				for (int xSize = 1; xSize <= maxSize; xSize++) {
					for (int ySize = 1; ySize <= maxSize; ySize++) {
						int area = xSize * ySize;
						if (area < 2 * need || area > maxSize || xc + xSize > theArea.xHigh || yc + ySize > theArea.yHigh) {
							continue;
						}
						int sum = 0;
						for (int x = xc; x < xc + xSize; x++) {
							for (int y = yc; y < yc + ySize; y++) {
								if (killed[x - theArea.xLow][y - theArea.yLow]) {
									sum = area;
								}
								sum += field[x][y] ? 1 : 0;
							}
						}
						if (sum < need || area - sum < need) {
							continue;
						}
						Rectangle rectangle = new Rectangle(xc, yc, xc + xSize, yc + ySize);
						possible.add(rectangle);
					}
				}
			}
		}
		int[] scores = new int[possible.size()];
		ArrayList<Clause> clauses = new ArrayList<>();
		for (int i = 0; i < possible.size(); i++) {
			scores[i] = possible.get(i).area();
			for (int j = 0; j < i; j++) {
				if (possible.get(i).intersects(possible.get(j))) {
					Clause clause = new Clause(0, 1, i + 1, j + 1);
					clauses.add(clause);
				}
			}
		}
		Sat satInstance = new Sat(clauses.toArray(new Clause[clauses.size()]), scores);
		settings.desiredEnergy = -freeArea;
		settings.temp0 = 5000 << random.nextInt(3);
		boolean[] sat = satInstance.solve(settings, random);
		int statNew = 0;
		for (int i = 0; i < possible.size(); i++) {
			if (sat[i]) {
				Rectangle rectangle = possible.get(i);
				result.add(rectangle);
				statNew += rectangle.area();
			}
		}
		System.out.println(((statOld >= statNew) ? "Not i" : "I") + "mproved " + statOld + " -> " + statNew);
		if (statOld > statNew) {
			return false;
		}
		previous.clear();
		previous.addAll(result);
		return statNew > statOld;
	}

	class Rectangle {
		int xLow, yLow, xHigh, yHigh;

		Rectangle(int xLow, int yLow, int xHigh, int yHigh) {
			this.xLow = xLow;
			this.yLow = yLow;
			this.xHigh = xHigh;
			this.yHigh = yHigh;
		}

		public Rectangle(int xLow, int yLow, int size) {
			this(xLow, yLow, xLow + size, yLow + size);
		}

		int area() {
			return (xHigh - xLow) * (yHigh - yLow);
		}

		boolean intersects(Rectangle that) {
			return xLow < that.xHigh && that.xLow < xHigh
					&& yLow < that.yHigh && that.yLow < yHigh;
		}

		boolean contains(Rectangle that) {
			return xLow <= that.xLow && that.xHigh <= xHigh
					&& yLow <= that.yLow && that.yHigh <= yHigh;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + xHigh;
			result = prime * result + xLow;
			result = prime * result + yHigh;
			result = prime * result + yLow;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			Rectangle that = (Rectangle) obj;
			return xHigh == that.xHigh && xLow == that.xLow && yHigh == that.yHigh && yLow == that.yLow;
		}

		@Override
		public String toString() {
			return String.format("%d %d %d %d", xLow, yLow, xHigh - 1, yHigh - 1);
		}

		public Rectangle(Scanner in) {
			this(in.nextInt(), in.nextInt(), in.nextInt() + 1, in.nextInt() + 1);
		}
	}

	static boolean segmentsIntersect(int low1, int high1, int low2, int high2) {
		return low1 < high2 && low2 < high1;
	}

	Pizza(boolean[][] field, int need, int maxSize, String filename) {
		this.field = field;
		this.need = need;
		this.maxSize = maxSize;
		this.filename = filename;
		hei = field.length;
		wid = field[0].length;
	}

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		String[] filenames = new String[] {"a_example",	"b_small", "c_medium", "d_big", "c_cut"};
		String dirName = Pizza.class.getPackage().getName().replace('.', '/');
		File dir = new File(dirName);
		boolean init = false;
		for (int fileIndex : new int[] {
//				0,
//				1,
//				2,
//				3,
				4
		}) {
			String filename = filenames[fileIndex];
			File file = new File(dir, filename + ".in");
			Scanner in;
			try {
				in = new Scanner(file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			int hei = in.nextInt();
			int wid = in.nextInt();
			int need = in.nextInt();
			int maxSize = in.nextInt();
			boolean[][] field = new boolean[hei][wid];
			for (int i = 0; i < hei; i++) {
				String s = in.next();
				for (int j = 0; j < wid; j++) {
					field[i][j] = s.charAt(j) == 'T';
				}
			}
			new Pizza(field, need, maxSize, filename).solve(init);
			in.close();
		}
	}
}
