package challenge24.y2014.finals;

/**
 * Created with IntelliJ IDEA.
 * User: Aksenov239
 * Date: 03.05.14
 * Time: 23:39
 * To change this template use File | Settings | File Templates.
 */

import javax.imageio.ImageIO;
import java.util.*;
import java.io.*;
import java.awt.image.*;

public class F {
	HashMap<Integer, Integer>[] rows;
	HashMap<Integer, Integer>[] cols;
	int total = 0;
	double C = 0.2;

	public class Query {
		int x1, y1, x2, y2, w, h;

		public Query(int x1, int y1, int x2, int y2, int w, int h) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			this.w = w;
			this.h = h;
		}
	}

	public static void add(HashMap<Integer, Integer> hm, int x) {
		if (!hm.containsKey(x))
			hm.put(x, 0);
		hm.put(x, hm.get(x) + 1);
	}

	public static void remove(HashMap<Integer, Integer> hm, int x) {
		hm.put(x, hm.get(x) - 1);
		if (hm.get(x) == 0) {
			hm.remove(x);
		}
	}

	public static int get(HashMap<Integer, Integer> hm, int x) {
		return hm.containsKey(x) ? hm.get(x) : 0;
	}

	public void statistics() {
		System.err.println(total);
		for (int i = 0; i < rows.length; i++) {
			System.err.println(cols.length - rows[i].size());
		}
		for (int i = 0; i < cols.length; i++) {
			System.err.println(rows.length - cols[i].size());
		}
	}

	public static boolean check(int[][] a) {
		for (int i = 0; i < a.length; i++) {
			HashSet<Integer> hs = new HashSet<>();
			for (int x : a[i]) {
				hs.add(x);
			}
			if (hs.size() != a[i].length)
				return false;
		}

		for (int j = 0; j < a[0].length; j++) {
			HashSet<Integer> hs = new HashSet<>();
			for (int i = 0; i < a.length; i++) {
				hs.add(a[i][j]);
			}
			if (hs.size() != a.length)
				return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public void setup(int[][] im) {
		total = 0;
		rows = new HashMap[im.length];
		for (int i = 0; i < im.length; i++) {
			rows[i] = new HashMap<>();
			for (int x : im[i]) {
				add(rows[i], x);
			}
			total += im[0].length - rows[i].size();
		}

		cols = new HashMap[im[0].length];
		for (int i = 0; i < im[0].length; i++) {
			cols[i] = new HashMap<>();
			for (int j = 0; j < im.length; j++) {
				add(cols[i], im[j][i]);
			}
			total += im.length - cols[i].size();
		}
	}

	public void recalc(int[][] a, Query q) {
		for (int i = 0; i < q.w; i++) {
			for (int j = 0; j < q.h; j++) {
				total += rows[q.x1 + i].size() + rows[q.x2 + i].size() + cols[q.y1 + j].size() + cols[q.y2 + j].size();

				remove(rows[q.x1 + i], a[q.x1 + i][q.y1 + j]);
				add(rows[q.x1 + i], a[q.x2 + i][q.y2 + j]);
				remove(rows[q.x2 + i], a[q.x2 + i][q.y2 + j]);
				add(rows[q.x2 + i], a[q.x1 + i][q.y1 + j]);

				remove(cols[q.y1 + j], a[q.x1 + i][q.y1 + j]);
				add(cols[q.y1 + j], a[q.x2 + i][q.y2 + j]);
				remove(cols[q.y2 + j], a[q.x2 + i][q.y2 + j]);
				add(cols[q.y2 + j], a[q.x1 + i][q.y1 + j]);

				int p = a[q.x1 + i][q.y1 + j];
				a[q.x1 + i][q.y1 + j] = a[q.x2 + i][q.y2 + j];
				a[q.x2 + i][q.y2 + j] = p;

				total -= rows[q.x1 + i].size() + rows[q.x2 + i].size() + cols[q.y1 + j].size() + cols[q.y2 + j].size();
			}
		}
	}

	public ArrayList<Query> start(int[][] a) {
		setup(a);

		System.err.println("Started from " + total);

		Random rnd = new Random(239);

		int it = 0;
		ArrayList<Query> ans = new ArrayList<>();
		while (1L * total * rows.length * cols.length > 100000 && it < 108) {
			System.err.println(total);
			for (int w = 30; w >= 2; w--) {
//				for (int w = Math.min(a.length, a[0].length); w >= 2; w--) {
				System.err.println(it + " " + w);
				boolean found = true;
				while (found) {
					found = false;
					for (int t = 0; t < 100; t++) {
						Query q = new Query(rnd.nextInt(a.length / w) * w, rnd.nextInt(a[0].length / w) * w,
								rnd.nextInt(a.length / w) * w, rnd.nextInt(a[0].length / w) * w, w, w);
						int dt = total;
						recalc(a, q);
						if (dt - total >= w * w * C && dt - total > 3) {
							System.out.println(dt + " " + total);
							System.out.println("Difference " + (dt - total));
							ans.add(q);
							found = true;
							break;
						}
						recalc(a, q);
					}
				}
			}
			it++;
		}
		while (total > 0) {
			System.err.println(total);
			ArrayList<Integer> x = new ArrayList<>();
			ArrayList<Integer> y = new ArrayList<>();
			for (int i = 0; i < rows.length; i++) {
				for (int j = 0; j < a[0].length; j++) {
					if (rows[i].get(a[i][j]) >= 2) {
						x.add(i);
						y.add(j);
					}
				}
			}
			for (int i = 0; i < a.length; i++) {
				for (int j = 0; j < cols.length; j++) {
					if (cols[j].get(a[i][j]) >= 2) {
						x.add(i);
						y.add(j);
					}
				}
			}

//			if (1L * total * rows.length * cols.length > 100000000) {
			boolean option = true;
			if (option) {
				u:
					for (int i = 0; i < x.size(); i++) {
						int xx = x.get(i);
						int yy = y.get(i);
						if ((rows[xx].size() != a[0].length && get(rows[xx], a[xx][yy]) >= 2) ||
								(cols[yy].size() != a.length && get(cols[yy], a[xx][yy]) >= 2)) {
							for (int t = 0; t < 10; t++) {
								int dt = total;
								int ttt = rnd.nextInt(x.size());
								Query q = new Query(xx, yy, x.get(ttt), y.get(ttt), 1, 1);
								recalc(a, q);
								if (total < dt) {
									ans.add(q);
									continue u;
								}
								recalc(a, q);
							}
							for (int t = 0; t < 10; t++) {
								int dt = total;
								Query q = new Query(xx, yy, rnd.nextInt(a.length), rnd.nextInt(a[0].length), 1, 1);
								recalc(a, q);
								if (total < dt) {
									ans.add(q);
									break;
								}
								recalc(a, q);
							}
						}
					}
			} else {
				System.err.println("trying " + x.size());
				u:
					for (int i = 0; i < x.size(); i++) {
						int xx = x.get(i);
						int yy = y.get(i);

						if ((rows[xx].size() != a[0].length && get(rows[xx], a[xx][yy]) >= 2) ||
								(cols[yy].size() != a.length && get(cols[yy], a[xx][yy]) >= 2)) {
							int best_x = -1;
							int best_y = -1;
							int md = 0;
							for (int x1 = 0; x1 < a.length; x1++) {
								for (int y1 = 0; y1 < a.length; y1++) {
									int dt = total;
									Query q = new Query(xx, yy, x1, y1, 1, 1);
									recalc(a, q);
									if (dt - total > md) {
										md = dt - total;
										best_x = x1;
										best_y = y1;
										ans.add(q);
										continue u;
									}
									recalc(a, q);
								}
							}
							Query q = new Query(xx, yy, best_x, best_y, 1, 1);
							ans.add(q);
							recalc(a, q);
						}
					}
			}

			it++;
		}
		return ans;
	}

	public void run() {
		for (int t = 10; t <= 10; t++) {
			try {
				BufferedImage image = ImageIO.read(new File("input/" + t + ".png"));

				int[][] im = new int[image.getHeight()][image.getWidth()];
				System.err.println(im.length + " " + im[0].length);
				for (int i = 0; i < im.length; i++) {
					for (int j = 0; j < im[i].length; j++) {
						im[i][j] = image.getRGB(j, i);
					}
				}

				PrintWriter out = new PrintWriter("F" + t + ".out");

				ArrayList<Query> queries = start(im);

				out.println(queries.size());
				for (Query q : queries) {
//					out.println(q.x1 + " " + q.y1 + " " + q.x2 + " " + q.y2 + " " + q.w + " " + q.h);
					out.println(q.y1 + " " + q.x1 + " " + q.y2 + " " + q.x2 + " " + q.w + " " + q.h);
				}
				out.close();
			} catch (IOException x) {
				x.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new F().run();
	}
}
