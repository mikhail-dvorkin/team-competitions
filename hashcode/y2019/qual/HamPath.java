package hashcode.y2019.qual;

import java.util.*;

public class HamPath {
	static Photo[] hamPath(Photo[] photos, Random random) {
		int n = photos.length;
		int[][] e = new int[n][n];
		int noise = 64;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < i; j++) {
				int w = photos[i].score(photos[j]);
				e[i][j] = e[j][i] = w;
			}
		}

		int score = -1;
		int[] best = new int[n];
		for (int iter = 4; iter > 0; iter--) {
			int[] p = new int[n];
			@SuppressWarnings("unchecked")
			ArrayList<Edge>[] byWeight = new ArrayList[HorizontalSequencer.MAX_WEIGHT * noise];
			for (int i = 0; i < byWeight.length; i++) {
				byWeight[i] = new ArrayList<>();
			}
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < i; j++) {
					int w = e[i][j];
					byWeight[w * noise + random.nextInt(noise)].add(new Edge(i, j));
				}
			}
			DisjointSetUnion dsu = new DisjointSetUnion(n);
			@SuppressWarnings("unchecked")
			ArrayList<Integer>[] pathNei = new ArrayList[n];
			for (int i = 0; i < pathNei.length; i++) {
				pathNei[i] = new ArrayList<>();
			}
			for (int w = byWeight.length - 1; w >= 0; w--) {
				Collections.shuffle(byWeight[w], random);
				for (Edge edge : byWeight[w]) {
					int a = edge.a;
					int b = edge.b;
					if (dsu.get(a) == dsu.get(b)) {
						continue;
					}
					if (pathNei[a].size() == 2 || pathNei[b].size() == 2) {
						continue;
					}
					pathNei[a].add(b);
					pathNei[b].add(a);
					dsu.unite(a, b);
				}
			}
			boolean[] mark = new boolean[n];
			ArrayList<Integer> res = new ArrayList<>();
			for (int i = 0; i < n; i++) {
				if (mark[i]) {
					continue;
				}
				if (pathNei[i].size() != 1) {
					continue;
				}
				dfs(i, -1, mark, res, pathNei);
			}
			for (int i = 0; i < n; i++) {
				if (mark[i]) {
					continue;
				}
				mark[i] = true;
				res.add(i);
			}
			for (int i = 0; i < n; i++) {
				p[i] = res.get(i);
			}
			int s = score(p, e);
			if (s > score) {
				score = s;
				best = p;
			}
		}

		int[] path = best;
		Photo[] result = new Photo[n];
		for (int i = 0; i < n; i++) {
			result[i] = photos[path[i]];
		}
		return result;
	}

	static int score(int[] p, int[][] e) {
		int res = 0;
		for (int i = 0; i < p.length - 1; i++) {
			res += e[p[i]][p[i + 1]];
		}
		return res;
	}

	static void dfs(int v, int parent, boolean[] mark, ArrayList<Integer> res, ArrayList<Integer>[] pathNei) {
		mark[v] = true;
		res.add(v);
		for (int u : pathNei[v]) {
			if (u == parent) {
				continue;
			}
			if (mark[u]) {
				throw new RuntimeException();
			}
			dfs(u, v, mark, res, pathNei);
		}
	}

	static class Edge {
		int a, b;

		public Edge(int a, int b) {
			this.a = a;
			this.b = b;
		}
	}
}
