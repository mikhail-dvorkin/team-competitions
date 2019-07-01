package challenge24.y2014.finals;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class A {
	int n;
	int[] g, h;
	long[] x, y;
	Long[] zero;
	boolean[] was_zero;

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
		long ans = 1;
		while (p > 0) {
			if ((p & 1) == 1)
				ans *= x;
			x *= x;
			p /= 2;
		}
		return ans;
	}

	Random rnd = new Random(239);
	long INF = rnd.nextLong();

	public long find(int j, long value) {
		if (value == 0)
			return runQueryZero(j);
		boolean[] was = new boolean[zero.length];

		while (!was[j] && j != 0) {
			was[j] = true;
			value += x[j];
			j = g[j];
			if (value == 0) {
				return runQueryZero(j);
			}
		}

		if (j == 0) {
			return value;
		}

		int k = j;
		long sum = x[k];
		k = g[k];
		int z = 1;
		while (k != j) {
			sum += x[k];
			k = g[k];
			z++;
		}

		long l = sum;

		if (l == 0)
			throw new AssertionError();

		int p = normal(l);
		sum = 0;
		k = j;
		Long min = null;
		int pos = -1;
		for (int t = 0; t < z; t++) {
			long a = -(value + sum);

			if (a != 0 && normal(a) < p) {
				sum += x[k];
				k = g[k];
				continue;
			}
			long na = normalize(a, p);
			long nl = normalize(l, p);

//			long total = na * rev(nl, 62 - p);
			long power = p == 0 ? Long.MAX_VALUE : (1L << (63 - p)) - 1;
			long total = na * rev(nl, power);

			if (p > 0)
				total &= (p == 1 ? Long.MAX_VALUE : (1L << (64 - p)) - 1);

			if (min == null) {
				min = total;
				pos = k;
			}
			if (total >= 0 && min < 0) {
				min = total;
				pos = k;
			} else if (!(total < 0 && min >= 0) && total < min) {
				min = total;
				pos = k;
			}

			sum += x[k];
			k = g[k];
		}

		if (pos == -1)
			throw new AssertionError();

		return runQueryZero(pos);
	}

	public long runQueryZero(int i) {
		if (zero[i] != null)
			return zero[i];
		if (was_zero[i])
			throw new AssertionError();
		was_zero[i] = true;
		long value = y[i];
		int j = h[i];
		if (value == 0)
			return zero[i] = runQueryZero(j);

		zero[i] = find(j, value);

		return zero[i];
	}

	void run() {
		n = in.nextInt();
		int queries = in.nextInt();
		g = new int[n];
		h = new int[n];
		x = new long[n];
		y = new long[n];
		zero = new Long[n];
		Arrays.fill(zero, null);
		for (int i = 1; i < n; i++) {
			g[i] = in.nextInt();
			h[i] = in.nextInt();
			x[i] = readLong();
			y[i] = readLong();
		}

		zero[0] = 0L;

		was_zero = new boolean[n];
		for (int i = 0; i < queries; i++) {
			try {
				writeLong(find(in.nextInt(), readLong()));
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

	static String fileName = A.class.getSimpleName().replaceFirst("_.*", "").toUpperCase();
	static MyScanner in;
	static PrintWriter out;

	public static void main(String[] args) throws IOException {
		Locale.setDefault(Locale.US);
		for (int test = 1; test <= 5; test++) {
			String inputFileName = "input/" + fileName + "/" + test + ".in";
			String outputFileName = fileName + test + ".out";
			BufferedReader br = new BufferedReader(new FileReader(inputFileName));
			out = new PrintWriter(outputFileName);
			in = new MyScanner(br);
			new A().run();
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
