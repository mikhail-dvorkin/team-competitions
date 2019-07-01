package challenge24.y2014.finals;

import java.io.*;
import java.util.*;

//Buggy
public class E {
	private final Scanner in;
	private final PrintWriter out;

	public E(Scanner in, PrintWriter out) {
		this.in = in;
		this.out = out;
	}

	public static void main(String[] args) {
		for (int i = 2; i <= 2; ++i) {
			System.out.println(i);
			try (Scanner in = new Scanner(new FileReader(new File("PROBLEMSET/input/E/" + i + ".in")));
					PrintWriter out = new PrintWriter("PROBLEMSET/input/E/E" + i + ".out")) {
				new E(in, out).solve();
			} catch (Throwable t) {
				t.printStackTrace();
				System.exit(-1);
			}
		}
	}


	@SuppressWarnings("unchecked")
	private void solve() {
		M = in.nextInt();
		int F = in.nextInt();
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

		f2m = new int[F];
		Arrays.fill(f2m, -1);
		u = new int[M];
		color = 0;
		Arrays.fill(u, 0);

		m2f = new int[M];
		Arrays.fill(m2f, -1);

		for (int i = 0; i < G.length; ++i) {
			++color;
			dfs(i);
		}

		for (int i = 0; i < M; ++i) {
			if (m2f[i] != -1 && f2m[m2f[i]] != i) throw new Error();
		}

		for (int i = 0; i < F; ++i) {
			if (f2m[i] != -1 && m2f[f2m[i]] != i) throw new Error();
		}

//		System.out.println(Arrays.toString(m2f));

		dsu = new DSU(M + F);

		for (int i = 0; i < m2f.length; ++i) {
			int j = m2f[i];
			if (j != -1) {
				dsu.join(i, j + M);
			}
		}

		for (int i = 0; i < G.length; ++i) {
			for (int j : G[i]) {
//				if (j == m2f[i]) continue;

				if (dsu.get(i) == dsu.get(M + j)) continue;

				int k = f2m[j];
				if (k == -1 || m2f[i] == -1) {
					dsu.join(i, j + M);
					continue;
				}

				++color;
				if (dfs2(k, i)) {
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
				males =  Collections.emptyList();
			}
			List<Integer> females = Fmap.get(col);
			if (females == null) {
				females = Collections.emptyList();
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

	int M;

	DSU dsu;

	public boolean dfs2(int i, int f) {
		if (i == f) return true;

		u[i] = color;

		for (int j : G[i]) {
			int k = f2m[j];
			if (k == -1) {
				dsu.join(i, j + M);
				return true;
			}

			if (u[k] != color && dfs2(k, f)) {
				dsu.join(i, j + M);
				return true;
			}
		}
		return false;
	}

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
