package challenge24.y2014.finals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.*;

public class AStupido {
	long time;
	int n;
	int[] g, h;
	long[] x, y;

	public static int normal(long x) {
		int k = 0;
		while (x % 2 == 0) {
			x /= 2;
			k += 1;
		}
		return k;
	}

	public static long normalize(long x, int p) {
		for (int i = 0; i < p; i++)
			x /= 2;
		return x;
	}

	public static long rev(long x, long p) {
//		for (int i = 0; i < p; i++)
//			x *= x;
//		if (1 == 1)
//			return x;

		long ans = 1;
		while (p > 0) {
			if ((p & 1) == 1)
				ans *= x;
			x *= x;
			p >>= 1;
		}
		return ans;
	}

	Random rnd = new Random(239);
	long INF = rnd.nextLong();

	public long find(int j, long value) {
		time = System.currentTimeMillis();
		while (j != 0 && System.currentTimeMillis() - time < 10000) {
			if (value == 0) {
				value += y[j];
				j = h[j];
			} else {
				value += x[j];
				j = g[j];
			}
		}

		if (System.currentTimeMillis() - time > 10000)
			return -1;

		return value;
	}

	void run() {
		n = in.nextInt();
		int queries = in.nextInt();
		g = new int[n];
		h = new int[n];
		x = new long[n];
		y = new long[n];
		for (int i = 1; i < n; i++) {
			g[i] = in.nextInt();
			h[i] = in.nextInt();
			x[i] = readLong();
			y[i] = readLong();
		}
		for (int i = 0; i < queries; i++) {
			try {
				System.err.println(i);
				writeLong(find(in.nextInt(), readLong()));
				out.flush();
			} catch (AssertionError e) {
				out.println(-1);
			}
		}
	}

	ArrayList<Long> run2(int n_, int queries, int[] g_, int[] h_, long[] x_, long[] y_, int[] qx, int[] qv) {
		this.n = n_;
		this.g = g_;
		this.h = h_;
		this.x = x_;
		this.y = y_;
		ArrayList<Long> ans = new ArrayList<>();

		for (int i = 0; i < queries; i++) {
			try {
				ans.add(find(qx[i], qv[i]));
			} catch (AssertionError e) {
				ans.add(-1L);
			}
		}
		return ans;
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

	static String fileName = AStupido.class.getSimpleName().replaceFirst("_.*", "").toUpperCase();
	static MyScanner in;
	static PrintWriter out;

	public static void main(String[] args) throws IOException {
		Locale.setDefault(Locale.US);
		for (int test = 1; test <= 1; test++) {
			String inputFileName = "input/A" + "/" + test + ".in";
			String outputFileName = fileName + test + ".out";
			BufferedReader br = new BufferedReader(new FileReader(inputFileName));
			out = new PrintWriter(outputFileName);
			in = new MyScanner(br);
			new AStupido().run();
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
