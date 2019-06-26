package hashcode.y2019.qual;

import java.util.*;

public class DisjointSetUnion {
	int[] p;
	Random r = new Random(566);

	public DisjointSetUnion(int n) {
		p = new int[n];
		clear();
	}

	void clear() {
		for (int i = 0; i < p.length; i++) {
			p[i] = i;
		}
	}

	int get(int v) {
		if (p[v] == v) {
			return v;
		}
		p[v] = get(p[v]);
		return p[v];
	}

	void unite(int v, int u) {
		v = get(v);
		u = get(u);
		if (r.nextBoolean()) {
			p[v] = u;
		} else {
			p[u] = v;
		}
	}
}
