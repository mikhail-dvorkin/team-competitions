package challenge24.y2014.finals;

import java.io.*;
import java.util.*;

public class C {
	private List<String> known = Arrays.asList("F", "A", "I", "O");

	static class MyString {
		Object[] objects;

		MyString(Object... objects) {
			this.objects = objects;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (Object object : objects) {
				sb.append(object.toString());
				sb.append(" ");
			}
//			return "(" + sb.toString().trim() + ")";
			return sb.toString().trim();
		}
	}

	void run() {
		List<String> tokens = new ArrayList<>();
		for (;;) {
			try {
				String token = in.next();
				tokens.add(token);
			} catch (Exception e) {
				break;
			}
		}
		int n = tokens.size();
		int[] value = new int[n];
		Arrays.fill(value, -1);
		for (int i = 0; i < n; i++) {
			String token = tokens.get(i);
			if (known.contains(token)) {
				continue;
			}
			value[i] = Integer.parseInt(token);
			if (value[i] < 0) {
				throw new RuntimeException();
			}
		}
		System.out.println(tokens);
		int maxDeep = 2000;
		int infty = Integer.MAX_VALUE / 3;
		System.out.println("N = " + n);
		int[][][] a = new int[n + 1][n + 1][maxDeep];
		MyString[][][] how = new MyString[n + 1][n + 1][maxDeep];
		for (int from = 0; from < a.length; from++) {
			for (int to = 0; to < a[from].length; to++) {
				Arrays.fill(a[from][to], infty);
			}
		}
		for (int len = 0; len <= n; len++) {
			System.out.println(len + " / " + n);
			for (int from = 0; from <= n; from++) {
				int to = from + len;
				if (to > n) {
					break;
				}
				for (int d = 0; d < maxDeep; d++) {
					int best = infty;
					MyString h = null;
					if (len == 0) {
						if (d > 0) {
							best = 1;
							h = new MyString("0");
						}
					} else {
						if (len == 1) {
							if (value[from] >= 0 && value[from] < d) {
								best = 0;
								h = new MyString("" + value[from]);
							}
						}
						if (tokens.get(from).equals("O")) {
							if (len >= 2 && value[from + 1] >= 0 && value[from + 1] < 128) {
								int cur = a[from + 2][to][d];
								if (cur < best) {
									best = cur;
									h = new MyString("O", "" + value[from + 1], how[from + 2][to][d]);
								}
							}
							int cur = 1 + a[from + 1][to][d];
							if (cur < best) {
								best = cur;
								h = new MyString("O", 42, how[from + 1][to][d]);
							}
						}
						if (tokens.get(from).equals("I")) {
							int cur = a[from + 1][to][d];
							if (cur < best) {
								best = cur;
								h = new MyString("I", how[from + 1][to][d]);
							}
						}
						if (tokens.get(from).equals("F") && d + 1 < maxDeep) {
							int cur = a[from + 1][to][d + 1];
							if (cur < best) {
								best = cur;
								h = new MyString("F", how[from + 1][to][d + 1]);
							}
						}
						if (tokens.get(from).equals("A")) {
							for (int i = from + 1; i <= to; i++) {
								int cur = a[from + 1][i][d] + a[i][to][d];
								if (cur < best) {
									best = cur;
									h = new MyString("A", how[from + 1][i][d], how[i][to][d]);
								}
							}
						}
						if (value[from] >= 0 && value[from] < 128) {
							int cur = 1 + a[from + 1][to][d];
							if (cur < best) {
								best = cur;
								h = new MyString("O", "" + value[from], how[from + 1][to][d]);
							}
						}
						for (int i = from; i <= to; i++) {
							int cur = 1 + a[from][i][d] + a[i][to][d];
							if (cur < best) {
								best = cur;
								h = new MyString("A", how[from][i][d], how[i][to][d]);
							}
						}
					}
					a[from][to][d] = best;
					if (d > 0 && h != null && how[from][to][d - 1] != null) {
						MyString g = how[from][to][d - 1];
						if (Arrays.equals(h.objects, g.objects)) {
							h = g;
						}
					}
					how[from][to][d] = h;
				}
			}
		}
		int best = infty;
		MyString ans = null;
		StringBuilder fs = new StringBuilder();
		for (int d = 0; d < maxDeep; d++) {
			if (a[0][n][d] + d < best) {
				best = a[0][n][d] + d;
				ans = new MyString(fs.toString().trim(), how[0][n][d]);
			}
			fs.append("F ");
		}
		System.out.println(best);
		out.println(ans);
		System.out.println(ans);
	}

	static String fileName = C.class.getSimpleName().replaceFirst("_.*", "").toUpperCase();
	static MyScanner in;
	static PrintWriter out;

	public static void main(String[] args) throws IOException {
		Locale.setDefault(Locale.US);
		for (int test = 0; test <= 10; test++) {
			String inputFileName = "input/" + fileName + "/" + test + ".in";
			String outputFileName = "output/" + fileName + test + ".out";
			BufferedReader br = new BufferedReader(new FileReader(inputFileName));
			out = new PrintWriter(outputFileName);
			in = new MyScanner(br);
			new C().run();
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
