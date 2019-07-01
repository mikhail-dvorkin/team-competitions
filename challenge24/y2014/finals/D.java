package challenge24.y2014.finals;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class D {
	int m;
	int[][] g;
	int[] k;
	HashMap<Long, Boolean> dp;

	public int recalc(int v, long mask) {
		int total = k[v];
		for (int u : g[v]) {
			total += recalc(u, mask);
		}
		if (v == 0 || (mask & (1L << (v - 1))) > 0) {
			if (total > m)
				throw new AssertionError();
			return 1;
		} else {
			return total;
		}
	}

	public boolean calc(long mask) {
		if (dp.containsKey(mask))
			return dp.get(mask);

		for (int remove = 0; remove < k.length - 1; remove++) {
			if ((mask & (1L << remove)) == 0)
				continue;
			try {
				recalc(0, mask ^ (1L << remove));
				if (calc((mask ^ (1L << remove))))
					continue;
				dp.put(mask, true);
				return true;
			} catch (AssertionError e) {

			}
		}
		dp.put(mask, false);
		return false;
	}

	public void solve() {
		int n = in.nextInt();

		g = new int[n][];
		k = new int[n];
		for (int i = 0; i < g.length; i++) {
			k[i] = in.nextInt();
			int k2 = in.nextInt();
			g[i] = new int[k2];
			for (int j = 0; j < k2; j++) {
				g[i][j] = in.nextInt();
			}
		}

		System.err.println(n);

		dp = new HashMap<>();

		if (calc((1L << (n - 1)) - 1)) {
			out.println(1);
		} else {
			out.println(2);
		}
		out.flush();
	}

	void run() {
		int t = in.nextInt();
		m = in.nextInt();
		for (int i = 0; i < t; i++) {
			solve();
		}
	}

	static long readLong() {
		BigInteger x = new BigInteger(in.next());
		return x.longValue();
	}

	static void writeLong(long val) {
		BigInteger x = BigInteger.valueOf(val);
		if (x.signum() < 0) {
			x = x.add(BigInteger.ONE.shiftLeft(64));
		}
		out.println(x);
	}

	static String fileName = D.class.getSimpleName().replaceFirst("_.*", "").toUpperCase();
	static MyScanner in;
	static PrintWriter out;

	public static void main(String[] args) throws IOException {
		Locale.setDefault(Locale.US);
		for (int test = 4; test <= 4; test++) {
			System.err.println(test);
			String inputFileName = "input/" + fileName + "/" + test + ".in";
			String outputFileName = fileName + test + ".out";
			BufferedReader br = new BufferedReader(new FileReader(inputFileName));
			out = new PrintWriter(outputFileName);
			in = new MyScanner(br);
			new D().run();
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
