package challenge24.y2014.ec;

import java.io.*;
import java.util.*;

public class E {
	private static String fileName = E.class.getSimpleName().replaceFirst("_.*", "");
	private static Scanner in;
	private static PrintWriter out;
	public static final double R = 6371;

	public void run() {
		int n = in.nextInt();
		City[] cities = new City[n];
		for (int i = 0; i < n; i++) {
			cities[i] = new City(in.nextDouble(), in.nextDouble(), i);
		}
		Arrays.sort(cities);
		double[][] dist = new double[n][n];
		for (int i = 0; i < n; i++) {
			City ci = cities[i];
			for (int j = 0; j < n; j++) {
				if (i == j) {
					continue;
				}
				City cj = cities[j];
				double d = Math.hypot(ci.x - cj.x, Math.hypot(ci.y - cj.y, ci.z - cj.z));
				double dl = cj.longitude - ci.longitude;
				if (dl < 0) {
					dl += 2 * Math.PI;
				}
				if (dl <= Math.PI) {
					dist[i][j] = 2 * Math.asin(d / 2);
				} else {
					dist[i][j] = 2 * (Math.PI - Math.asin(d / 2));
					double throughPole = Math.min(Math.PI - ci.latitude - cj.latitude, Math.PI + ci.latitude + cj.latitude);
					dist[i][j] = Math.min(dist[i][j], throughPole);
				}
			}
		}
		double bestDist = 0;
		int[] bestPrev = new int[n];
		for (int i = 0; i < n; i++) {
			bestDist += dist[i][(i + 1) % n];
			bestPrev[(i + 1) % n] = i;
		}
		double inf = 1e9;
		for (int f = 1; f < n; f++) {
			double[][] a = new double[n][n];
			int[][] prev0 = new int[n][n];
			int[][] prev1 = new int[n][n];
			for (int i = 0; i < n; i++) {
				Arrays.fill(a[i], inf);
				Arrays.fill(prev0[i], -1);
				Arrays.fill(prev1[i], -1);
			}
			double d = 0;
			for (int i = 1; i < f; i++) {
				d += dist[i - 1][i];
			}
			a[f - 1][f] = d;
			for (int i = 1; i < n - 1; i++) {
				for (int j = 0; j < i; j++) {
					if (a[i][j] < inf) {
						double cur = a[i][j] + dist[i][i + 1];
						if (cur < a[i + 1][j]) {
							a[i + 1][j] = cur;
							prev0[i + 1][j] = i;
							prev1[i + 1][j] = j;
						}
						cur = a[i][j] + dist[j][i + 1];
						if (cur < a[i][i + 1]) {
							a[i][i + 1] = cur;
							prev0[i][i + 1] = i;
							prev1[i][i + 1] = j;
						}
					}
					if (a[j][i] < inf) {
						double cur = a[j][i] + dist[i][i + 1];
						if (cur < a[j][i + 1]) {
							a[j][i + 1] = cur;
							prev0[j][i + 1] = j;
							prev1[j][i + 1] = i;
						}
						cur = a[j][i] + dist[j][i + 1];
						if (cur < a[i + 1][i]) {
							a[i + 1][i] = cur;
							prev0[i + 1][i] = j;
							prev1[i + 1][i] = i;
						}
					}
				}
			}
			for (int t = f; t < n - 1; t++) {
				if (a[t][n - 1] < inf) {
					double cur = a[t][n - 1] + dist[n - 1][0] + dist[t][f];
					if (cur < bestDist) {
						bestDist = cur;
						Arrays.fill(bestPrev, -1);
						for (int i = 1; i < f; i++) {
							bestPrev[i] = i - 1;
						}
						int i = t;
						int j = n - 1;
						while (prev0[i][j] >= 0) {
							int ii = prev0[i][j];
							int jj = prev1[i][j];
							if ((i != ii) == (j != jj)) {
								throw new RuntimeException();
							}
							if (i != ii) {
								bestPrev[i] = ii;
							} else {
								bestPrev[j] = jj;
							}
							i = ii;
							j = jj;
						}
						bestPrev[f] = t;
						bestPrev[0] = n - 1;
					}
				}
				if (a[n - 1][t] < inf) {
					double cur = a[n - 1][t] + dist[n - 1][f] + dist[t][0];
					if (cur < bestDist) {
						bestDist = cur;
						Arrays.fill(bestPrev, -1);
						for (int i = 1; i < f; i++) {
							bestPrev[i] = i - 1;
						}
						int i = n - 1;
						int j = t;
						while (prev0[i][j] >= 0) {
							int ii = prev0[i][j];
							int jj = prev1[i][j];
							if ((i != ii) == (j != jj)) {
								throw new RuntimeException();
							}
							if (i != ii) {
								bestPrev[i] = ii;
							} else {
								bestPrev[j] = jj;
							}
							i = ii;
							j = jj;
						}
						bestPrev[f] = n - 1;
						bestPrev[0] = t;
//						System.out.println(Arrays.toString(bestPrev));
//						double s = 0;
//						for (i = 0; i < n; i++) {
//							s += dist[i][bestPrev[i]];
//						}
//						System.out.println(R * s);
					}
				}
			}
		}
		out.println(R * bestDist);
		int[] order = new int[n];
		Arrays.fill(order, -1);
		order[0] = 0;
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n; j++) {
				if (bestPrev[j] == order[i]) {
					order[i + 1] = j;
					break;
				}
			}
		}
		for (int i = 0; i < n; i++) {
			out.print(cities[order[i]].id + " ");
		}
		out.println(cities[order[0]].id);
	}

	class City implements Comparable<City> {
		double latitude, longitude;
		int id;
		double x, y, z;

		City(double latitude, double longitude, int id) {
			this.latitude = latitude * Math.PI / 180;
			this.longitude = longitude * Math.PI / 180;
			this.id = id;
			z = Math.sin(this.latitude);
			x = Math.cos(this.latitude) * Math.cos(this.longitude);
			y = Math.cos(this.latitude) * Math.sin(this.longitude);
		}

		@Override
		public int compareTo(City that) {
			return Double.compare(longitude, that.longitude);
		}
	}

	public static void main(String[] args) throws IOException {
		Locale.setDefault(Locale.US);
		for (int t = 0; t <= 10; t++) {
			in = new Scanner(new File("PROBLEMSET/input/" + fileName + "/" + t + ".in"));
			out = new PrintWriter(fileName + t + ".out");
			new E().run();
			in.close();
			out.close();
		}
	}
}
