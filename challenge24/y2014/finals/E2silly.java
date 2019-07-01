package challenge24.y2014.finals;

import java.io.*;
import java.util.*;

public class E2silly {
	private final Scanner in;
	private final PrintWriter out;

	public E2silly(Scanner in, PrintWriter out) {
		this.in = in;
		this.out = out;
	}

	public static void main(String[] args) {
		for (int i = 0; i <= 10; ++i) {
			System.out.println(i);
			try (Scanner in = new Scanner(new FileReader(new File("PROBLEMSET/input/E/" + i + ".in")));
					PrintWriter out = new PrintWriter("PROBLEMSET/input/E/E" + i + ".out")) {
				new E2silly(in, out).solve();
			} catch (Throwable t) {
				t.printStackTrace();
				System.exit(-1);
			}
		}
	}


	@SuppressWarnings("unchecked")
	private void solve() {
		M = in.nextInt();
		F = in.nextInt();
		int C = in.nextInt();
		int Q = in.nextInt();

		G = new ArrayList[M];
		for (int i = 0; i < G.length; ++i) {
			G[i] = new ArrayList<>();
		}

		for (int i = 0; i < C; ++i) {
			int a = in.nextInt();
			int b = in.nextInt();
			G[a].add(b);
		}

		rM = rF = -1;
		int maxSize = maxMatchingSize();


//		System.out.println(Arrays.toString(m2f));

		dsu = new DSU(M + F);

		for (int i = 0; i < G.length; ++i) {
			for (int j : G[i]) {
				rM = i;
				rF = j;
				if (maxMatchingSize() + 1 == maxSize) {
					dsu.join(i, j + M);
				}
			}
		}

		Map<Integer, List<Integer>> Mmap = new HashMap<>();
		for (int i = 0; i < M; ++i) {
			int col = dsu.get(i);
			List<Integer> list = Mmap.get(col);
			if (list == null) {
				list = new ArrayList<>();
				Mmap.put(col, list);
			}
			list.add(i);
		}

		Map<Integer, List<Integer>> Fmap = new HashMap<>();
		for (int i = 0; i < F; ++i) {
			int col = dsu.get(M + i);
			List<Integer> list = Fmap.get(col);
			if (list == null) {
				list = new ArrayList<>();
				Fmap.put(col, list);
			}
			list.add(i);
		}

		for (int i = 0; i < Q; ++i) {
			String s = in.next();
			int j = in.nextInt();
			if (s.startsWith("F")) {
				j += M;
			}

			int col = dsu.get(j);

			List<Integer> males = Mmap.get(col);
			if (males == null) {
				males =  Collections.<Integer>emptyList();
			}
			List<Integer> females = Fmap.get(col);
			if (females == null) {
				females = Collections.<Integer>emptyList();
			}

			out.println(males.size() + " " + females.size());

			Collections.sort(males);
			Collections.sort(females);

			for (int k : males) {
				out.print(k + " ");
			}

			for (int k : females) {
				out.print(k + " ");
			}

			out.println();
		}
	}

	int rM, rF;

	int maxMatchingSize() {
		f2m = new int[F];
		Arrays.fill(f2m, -1);
		u = new int[M];
		color = 0;
		Arrays.fill(u, 0);

		m2f = new int[M];
		Arrays.fill(m2f, -1);

		for (int i = 0; i < G.length; ++i) {
			if (i == rM) continue;
			++color;
			dfs(i);
		}

		for (int i = 0; i < M; ++i) {
			if (m2f[i] != -1 && f2m[m2f[i]] != i) throw new Error();
		}

		for (int i = 0; i < F; ++i) {
			if (f2m[i] != -1 && m2f[f2m[i]] != i) throw new Error();
		}

		int size = 0;
		for (int i : m2f) {
			if (i != -1) ++size;
		}
		return size;
	}

	int M, F;



	DSU dsu;

	class DSU {

		int[] p;

		public DSU(int n) {
			p = new int[n];
			for (int i = 0; i < p.length; ++i) {
				p[i] = i;
			}
		}

		int get(int i) {
			if (p[i] != i) {
				p[i] = get(p[i]);
			}
			return p[i];
		}

		void join(int i, int j) {
			p[get(i)] = get(j);
		}
	}

	ArrayList<Integer>[] G;
	int[] f2m, m2f;
	int[] u;
	int color;

	boolean dfs(int i) {
		u[i] = color;
		for (int j : G[i]) {
			if (j == rF) continue;
			int k = f2m[j];
			if (k == -1 || u[k] != color && dfs(k)) {
				m2f[i] = j;
				f2m[j] = i;
				return true;
			}
		}
		return false;
	}
}
