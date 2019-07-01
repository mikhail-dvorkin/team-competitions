package challenge24.y2014.finals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.*;

public class AFast {
	int n;
	int[] g, h;
	long[] x, y;
	Long[] zero;
	boolean[] was_zero;
	HashMap<Long, Integer>[] sum;
	HashMap<Integer, Dist>[] v;

	int[] cycle;

	int BUBEN = 100;

	public class Dist {
		long value;
		int dist;

		public Dist(long value, int dist) {
			this.value = value;
			this.dist = dist;
		}
	}

	HashMap<Integer, HashMap<Long, TreeSet<Dist>>> cycles;

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
		long s = x[k];
		k = g[k];
		int z = 1;
		while (k != j) {
			s += x[k];
			k = g[k];
			z++;
		}

		long l = s;

		if (l == 0)
			throw new AssertionError();

		int p = normal(l);
		s = 0;
		k = j;
		Long min = null;
		int pos = -1;
		for (int t = 0; t < z; t++) {
			long a = -(value + s);

			if (a != 0 && normal(a) < p) {
				s += x[k];
				k = g[k];
				continue;
			}
			long na = normalize(a, p);
			long nl = normalize(l, p);

			//            long total = na * rev(nl, 62 - p);
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

			s += x[k];
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

	@SuppressWarnings({ "unchecked", "unlikely-arg-type" })
	public void precalc() {
		sum = new HashMap[n];
		for (int i = 0; i < n; i++) {
			sum[i] = new HashMap<>();
			int value = 0;
			int k = i;
			for (int j = 0; j < BUBEN; j++) {
				if (!sum[i].containsKey(value))
					sum[i].put(x[k], value);
				if (!v[i].containsKey(value))
					v[i].put(value, new Dist(x[k], i));
				value += x[k];
				k = g[k];
			}
		}

		boolean[] was = new boolean[n];
		cycle = new int[n];
		Arrays.fill(cycle, -1);
		cycles = new HashMap<>();
		for (int i = 0; i < n; i++) {
			if (was[i])
				continue;

			int k = i;
			while (!was[k]) {
				was[k] = true;
				k = g[k];
			}

			HashMap<Long, TreeSet<Dist>> hm = new HashMap<>();
			cycles.put(k, hm);

			int u = k;
			long len = 0;
			while (cycle[k] != u) {
				cycle[k] = u;
				len += x[k];
				k = g[k];
			}

			int p = normal(len);
			long power = p == 0 ? Long.MAX_VALUE : (1L << (63 - p)) - 1;
			len = rev(normalize(len, p), power);

			int z = 0;
			long s = 0;
			while (k != u) {
				long mod = s % (1L << p);

				if (!hm.containsKey(mod))
					hm.put(mod, new TreeSet<Dist>());

				TreeSet<Dist> d = hm.get(mod);

				long a = normalize(s, p);
				long total = a * len;

				if (p > 0)
					total &= (p == 1 ? Long.MAX_VALUE : (1L << (64 - p)) - 1);
				d.add(new Dist(total, z));

				z++;
				s += x[k];
				k = g[k];
			}
		}
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

	static String fileName = AFast.class.getSimpleName().replaceFirst("_.*", "").toUpperCase();
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
			new AFast().run();
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
