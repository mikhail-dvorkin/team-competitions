package challenge24.y2014.finals;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class L {
	static final double R = 6371.0;
	static final double P = Math.PI / 180;
	static boolean gzip = true;

	void run() throws IOException {
		int idA = readId(in.next());
		int idB = readId(in.next());
		long maxId = 0;
		double latA = 0, lonA = 0, latB = 0, lonB = 0;
		{
			BufferedReader nodes;
			if (gzip) {
				nodes = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream("input/input_nodes.txt.gz"))));
			} else {
				nodes = new BufferedReader(new FileReader("input/input_nodes.txt"));
			}
			for (int line = 0;; line++) {
				if (line % 4000000 == 0) {
					System.out.println("10) Line " + line);
				}
				String s = nodes.readLine();
				if (s == null) {
					break;
				}
				StringTokenizer st = new StringTokenizer(s);
				int id = readId(st.nextToken());
				maxId = Math.max(maxId, id);
				if (id == idA || id == idB) {
					float lat = Float.parseFloat(st.nextToken());
					float lon = Float.parseFloat(st.nextToken());
					if (id == idA) {
						latA = lat; lonA = lon;
					} else {
						latB = lat; lonB = lon;
					}
					System.out.println("Found A/B :-)");
				}
			}
			nodes.close();
		}
		System.out.println("READ :-)");
		System.out.println("MaxID = " + maxId);
		double xA = Math.cos(latA * P) * Math.cos(lonA * P);
		double yA = Math.cos(latA * P) * Math.sin(lonA * P);
		double zA = Math.sin(latA * P);
		double xB = Math.cos(latB * P) * Math.cos(lonB * P);
		double yB = Math.cos(latB * P) * Math.sin(lonB * P);
		double zB = Math.sin(latB * P);
		double dAB = distance(xA, yA, zA, xB, yB, zB);
		System.out.println("Distance: " + dAB);
		Set<Integer> indices = new HashSet<>();
		{
			BufferedReader nodes;
			if (gzip) {
				nodes = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream("input/input_nodes.txt.gz"))));
			} else {
				nodes = new BufferedReader(new FileReader("input/input_nodes.txt"));
			}
			for (int line = 0;; line++) {
				if (line % 4000000 == 0) {
					System.out.println("20) Line " + line + ", Index: " + indices.size());
				}
				String s = nodes.readLine();
				if (s == null) {
					break;
				}
				StringTokenizer st = new StringTokenizer(s);
				int id = readId(st.nextToken());
				double lat = Float.parseFloat(st.nextToken());
				double lon = Float.parseFloat(st.nextToken());
				double x = Math.cos(lat * P) * Math.cos(lon * P);
				double y = Math.cos(lat * P) * Math.sin(lon * P);
				double z = Math.sin(lat * P);
				double d = distance(xA, yA, zA, x, y, z) + distance(xB, yB, zB, x, y, z);
				if (d < 1.08 * dAB + 1) {
					indices.add(id);
				}
			}
			nodes.close();
		}
		System.out.println("READ :-)  Index: " + indices.size());
		Map<Integer, List<Integer>> nei = new HashMap<>();
		{
			int goodWays = 0;
			BufferedReader ways;
			if (gzip) {
				ways = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream("input/input_ways.txt.gz"))));
			} else {
				ways = new BufferedReader(new FileReader("input/input_ways.txt"));
			}
			List<Integer> nice = new ArrayList<>();
			for (int line = 0;; line++) {
				if (line % 4000000 == 0) {
					System.out.println("30) Line " + line + ", Good ways: " + goodWays);
					if (dfs(nei, new HashSet<Integer>(), idB, idA)) {
						ways.close();
						return;
					}
				}
				String s = ways.readLine();
				if (s == null) {
					break;
				}
				StringTokenizer st = new StringTokenizer(s);
//				long wayId = Long.parseLong(st.nextToken());
				st.nextToken();
				int len = Integer.parseInt(st.nextToken());
				for (int k = 0; k < len; k++) {
					int id = readId(st.nextToken());
					if (indices.contains(id)) {
						nice.add(id);
					}
				}
				if (nice.size() > 0) {
					if (nice.size() > 1) {
						goodWays++;
						for (int i = 1; i < nice.size(); i++) {
							int a = nice.get(i);
							int b = nice.get(0);
							if (!nei.containsKey(a)) {
								nei.put(a, new ArrayList<Integer>());
							}
							nei.get(a).add(b);
							if (!nei.containsKey(b)) {
								nei.put(b, new ArrayList<Integer>());
							}
							nei.get(b).add(a);
						}
					}
					nice.clear();
				}
			}
			ways.close();
		}
		System.out.println(nei);
		System.out.println("READ :-)");
		System.out.println(dfs(nei, new HashSet<Integer>(), idB, idA));
	}

	boolean dfs(Map<Integer, List<Integer>> nei, Set<Integer> mark, int v, int dest) {
		if (v == dest) {
			printId(v);
			out.print(" ");
			return true;
		}
		mark.add(v);
		List<Integer> list = nei.get(v);
		if (list == null) {
			return false;
		}
		for (int u : list) {
			if (!nei.containsKey(u)) {
				continue;
			}
			if (mark.contains(u)) {
				continue;
			}
			if (dfs(nei, mark, u, dest)) {
				out.print(v + " ");
				return true;
			}
		}
		return false;
	}

	static int readId(String s) {
		return (int) Long.parseLong(s);
	}

	static void printId(int id) {
		long v = id;
		if (v < 0) {
			v += 1L << 32;
		}
		out.print(v);
	}

	static double distance(double x0, double y0, double z0, double x1, double y1, double z1) {
		double x2 = y0 * z1 - z0 * y1;
		double y2 = z0 * x1 - x0 * z1;
		double z2 = x0 * y1 - y0 * x1;
		double c = x0 * x1 + y0 * y1 + z0 * z1;
		double s = Math.sqrt(x2 * x2 + y2 * y2 + z2 * z2);
		return R * Math.atan2(s, c);
	}


	static String fileName = L.class.getSimpleName().replaceFirst("_.*", "").toUpperCase();
	static MyScanner in;
	static PrintWriter out;

	public static void main(String[] args) throws IOException {
		Locale.setDefault(Locale.US);
		for (int test = 1; test <= 10; test++) {
			System.out.println("Test " + test);
			String inputFileName = "input/" + fileName + "/" + test + ".in";
			String outputFileName = "output/" + fileName + test + ".out";
			BufferedReader br = new BufferedReader(new FileReader(inputFileName));
			out = new PrintWriter(outputFileName);
			in = new MyScanner(br);
			new L().run();
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
}
