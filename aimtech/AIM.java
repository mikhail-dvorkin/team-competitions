package aimtech;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AIM {
	private int[] DX = new int[]{1, 0, -1, 0};
	private int[] DY = new int[]{0, 1, 0, -1};
	private String[] D_NAMES = new String[]{"down", "right", "up", "left"};

	private int hei, wid, area, ex = -1, ey = -1, sx, sy;
	private byte[][][] wall;
	private byte[][] key;

	private void run() {
		/* int testNum = */in.nextInt();
		/*int testWeight = */in.nextInt();
		hei = in.nextInt();
		wid = in.nextInt();
		area = hei * wid;
		sx = in.nextInt();
		sy = in.nextInt();
		int walls = in.nextInt();
		int doors = in.nextInt();
		int keys = in.nextInt();
		int exits = in.nextInt();
		wall = new byte[hei][wid][4];
		key = new byte[hei][wid];
		for (byte phase = 1; phase <= 2; phase++) {
			int max = phase == 1 ? walls : doors;
			for (int i = 0; i < max; i++) {
				int x1 = in.nextInt();
				int y1 = in.nextInt();
				int x2 = in.nextInt();
				int y2 = in.nextInt();
				if (x1 > x2) {
					int t = x1;
					x1 = x2;
					x2 = t;
				}
				if (y1 > y2) {
					int t = y1;
					y1 = y2;
					y2 = t;
				}
				if (x1 == x2) {
					wall[x1][y1][1] = wall[x2][y2][3] = phase;
				} else {
					wall[x1][y1][0] = wall[x2][y2][2] = phase;
				}
			}
		}
		for (byte phase = 1; phase <= 2; phase++) {
			int max = phase == 1 ? keys : exits;
			for (int i = 0; i < max; i++) {
				int x = in.nextInt();
				int y = in.nextInt();
				key[x][y] = phase;
				if (phase == 2) {
					ex = x;
					ey = y;
				}
			}
		}
		log.append(exits).append(" exits\t");
		log.append(keys).append(" keys\t");
		log.append(doors).append(" doors\t");
		solve();
	}

	private void solve() {
		int tx = ex;
		int ty = ey;
		int txy = tx * wid + ty;
		int[] dist = new int[hei * wid];
		int[] how = new int[hei * wid];
		String[] howString = new String[hei * wid];
		Arrays.fill(dist, -1);
		TreeMap<Integer, TreeSet<Integer>> map = new TreeMap<>();
		map.put(0, new TreeSet<>());
		map.get(0).add(sx * wid + sy);
		dist[sx * wid + sy] = 0;
		while (!map.isEmpty()) {
			int dMin = map.firstKey();
			if (map.get(dMin).isEmpty()) {
				map.remove(dMin);
				continue;
			}
			int v = map.get(dMin).iterator().next();
			map.get(dMin).remove(v);
			int vx = v / wid;
			int vy = v % wid;
			if (v == txy) {
				ArrayList<String> ans = new ArrayList<>();
				while (v != sx * wid + sy) {
					ans.add(howString[v]);
					v = how[v];
				}
				for (int i = ans.size() - 1; i >= 0; i--) {
					out.println(ans.get(i));
				}
				return;
			}
			for (int d = 0; d < 4; d++) {
				int x = vx;
				int y = vy;
				for (int steps = 1;; steps++) {
					int xx = x + DX[d];
					int yy = y + DY[d];
					if (wall[x][y][d] > 0 || xx < 0 || xx >= hei || yy < 0 || yy >= wid) {
						break;
					}
					x = xx;
					y = yy;
					int xy = x * wid + y;
					int edge = D_NAMES[d].length() + 5;
					if (steps > 1) {
						edge += 7 + ("" + steps).length();
					}
					int dd = dMin + edge;
					if (dist[xy] > dd) {
						map.get(dist[xy]).remove(xy);
					}
					if (dist[xy] > dd || dist[xy] == -1) {
						dist[xy] = dd;
						how[xy] = v;
						howString[xy] = "move-" + D_NAMES[d];
						if (steps > 1) {
							howString[xy] = "for-" + steps + " " + howString[xy] + " end";
						}
						if (!map.containsKey(dd)) {
							map.put(dd, new TreeSet<>());
						}
						map.get(dd).add(xy);
					}
				}
			}
		}
	}

	@Deprecated
	void solveWithDoors() {
//		dijkstra(sx, sy, 0, "");
		String pref = "";
		int ks = 0;
		for (int iter = 0; iter < 2; iter++) {
			nn = -1;
			findDoor = true;
			dijkstra(sx, sy, 0, pref);
			if (nn < 0) {
				break;
			}
			int nx = (nn % area) / wid;
			int ny = (nn % area) % wid;
//			int nk = nn / area;
			pref = pref + "\n" + nnString + "\ntake\n";
			sx = nx;
			sy = ny;
			ks++;
		}
		findDoor = false;
		System.out.println("ans " + ans.length());
		dijkstra(sx, sy, ks, pref);
		out.println(ans);
	}

	private int nn;
	private String nnString;
	private boolean findDoor;
	private String ans;

	@Deprecated
	void dijkstra(int sx, int sy, int ks, String pref) {
		int md0 = Math.abs(sx - ex) + Math.abs(sy - ey);
		System.out.println(md0 + " " + ks);
		ks++;
		int[] dist = new int[hei * wid * ks];
		int[] how = new int[hei * wid * ks];
		String[] howString = new String[hei * wid * ks];
		Arrays.fill(dist, -1);
		TreeMap<Integer, TreeSet<Integer>> map = new TreeMap<>();
		map.put(0, new TreeSet<>());
		int sxy = (ks - 1) * area + sx * wid + sy;
		map.get(0).add(sxy);
		dist[sxy] = 0;
		while (!map.isEmpty()) {
			int dMin = map.keySet().iterator().next();
			if (map.get(dMin).isEmpty()) {
				map.remove(dMin);
				continue;
			}
			int v = map.get(dMin).iterator().next();
			map.get(dMin).remove(v);
			int vx = (v % area) / wid;
			int vy = (v % area) % wid;
			int vk = v / area;
			if (key[vx][vy] > 0) {
				if (key[vx][vy] == 2) {
					StringBuilder sb = new StringBuilder();
					{
						ArrayList<String> list = new ArrayList<>();
						int vv = v;
						while (vv != sxy) {
							list.add(howString[vv]);
							vv = how[vv];
						}
						for (int i = list.size() - 1; i >= 0; i--) {
							sb.append(list.get(i)).append('\n');
						}
					}
					String s = pref + "\n" + sb.toString();
					if (!findDoor && (ans.equals("") || s.length() < ans.length())) {
						ans = s;
						System.out.println("BEST " + vk + " " + sb.length() + " " + sb.toString().contains("open"));
					}
					System.out.println(vk + " " + sb.length() + " " + sb.toString().contains("open"));
				} else {
					int md = Math.abs(vx - ex) + Math.abs(vy - ey);
					if (findDoor && md < md0 - 32) {
						StringBuilder sb = new StringBuilder();
						{
							ArrayList<String> list = new ArrayList<>();
							int vv = v;
							while (vv != sxy) {
								list.add(howString[vv]);
								vv = how[vv];
							}
							for (int i = list.size() - 1; i >= 0; i--) {
								sb.append(list.get(i)).append('\n');
							}
						}
						nn = v;
						nnString = sb.toString();
						return;
					}
				}
			}
			for (int d = 0; d < 4; d++) {
				int x = vx;
				int y = vy;
				for (int steps = 1;; steps++) {
					int xx = x + DX[d];
					int yy = y + DY[d];
					boolean door = false;
					if (xx < 0 || xx >= hei || yy < 0 || yy >= wid) {
						break;
					}
					if (wall[x][y][d] == 1) {
						break;
					}
					int k = vk;
					if (wall[x][y][d] == 2) {
						door = true;
						k--;
						if (k < 0) {
							break;
						}
					}
					x = xx;
					y = yy;
					int xy = k * area + x * wid + y;
					int edge = D_NAMES[d].length() + 5;
					if (steps > 1) {
						edge += 7 + ("" + steps).length();
					}
					if (door) {
						edge += D_NAMES[d].length() * 2 + 10;
					}
					int dd = dMin + edge;
					if (dist[xy] > dd) {
						map.get(dist[xy]).remove(xy);
					}
					if (dist[xy] > dd || dist[xy] == -1) {
						dist[xy] = dd;
						how[xy] = v;
						howString[xy] = "move-" + D_NAMES[d];
						if (steps > 1) {
							howString[xy] = "for-" + steps + " " + howString[xy] + " end";
						}
						if (door) {
							howString[xy] += " open-" + D_NAMES[d];
							howString[xy] += " move-" + D_NAMES[d];
						}
						if (!map.containsKey(dd)) {
							map.put(dd, new TreeSet<>());
						}
						map.get(dd).add(xy);
					}
					if (door) {
						break;
					}
				}
			}
		}
	}

	static MyScanner in;
	static PrintWriter out;
	private static StringBuilder log;

	public static void main(String[] args) throws IOException {
		boolean isSubmission = "true".equals(System.getenv("ONLINE_JUDGE"));
		File folder = new File(aimtech.AIM.class.getPackage().getName().replace(".", "/"));
		File resources = new File(folder, "res");
		File output = new File(folder, "out");
		File picsFile = new File(output, "pics.html");
		PrintWriter html = null;
		Map<Integer, Double> leaders = new HashMap<>();
		if (!isSubmission) {
			output.mkdirs();
			html = new PrintWriter(picsFile);
			File leadersFile = new File(folder, "leaders.csv");
			if (leadersFile.exists()) {
				String[] leadersStrings = new Scanner(leadersFile).nextLine().split(",");
				for (int i = 0; i < leadersStrings.length; i++) {
					leaders.put(i + 1, Double.parseDouble(leadersStrings[i]));
				}
			}
		}
		int testFrom = 1;
		int testCount = 14;
		for (int testNum = testFrom; testNum < testFrom + testCount; testNum++) {
			log = new StringBuilder(testNum + ") ");
			File inputFile = new File(resources, "input-" + testNum + ".txt");
			File outputFile = new File(output, testNum + ".out");
			File imageFile = new File(output, testNum + ".png");
			File manualFile = new File(folder, "manual-" + testNum + ".abc");
			BufferedReader br;
			if (isSubmission) {
				br = new BufferedReader(new InputStreamReader(System.in));
				out = new PrintWriter(System.out);
			} else {
				br = new BufferedReader(new FileReader(inputFile));
				out = new PrintWriter(outputFile);
			}
			in = new MyScanner(br);
			if (!isSubmission && manualFile.exists()) {
				out.close();
				Files.copy(manualFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} else {
				new AIM().run();
			}
			br.close();
			out.close();
			if (isSubmission) {
				return;
			}
			if (leaders.containsKey(testNum)) {
				log.append("TOP=").append(leaders.get(testNum)).append("\t");
			}
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec("python3 evaluate.py" +
					" --image " + imageFile.getCanonicalPath() +
					" " + inputFile.getCanonicalPath() +
					" " + outputFile.getCanonicalPath(),
					null, folder);
			Scanner errorStream = new Scanner(pr.getErrorStream());
			while (errorStream.hasNextLine()) {
				log.append(errorStream.nextLine());
			}
			System.out.println(log);
			html.println(log + "<br><img src='" + imageFile.getName() + "'><br>");
			html.flush();
		}
		Files.copy(picsFile.toPath(), new File(output, "log" + System.currentTimeMillis() + ".txt").toPath());
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
	}
}
