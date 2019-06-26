package hashcode.y2019.qual;

import java.util.*;

public class HorizontalSequencer implements Changer {
	public static final int BUCKET_SIZE = 3000;
	public static final int MAX_WEIGHT = 30;

	@Override
	public Output change(Input input, Output output) {
		Random random = new Random();
		int fileName = Integer.parseInt(input.file.getName());
		System.out.println("Processing test " + fileName);
		Photo[] photos = input.photos;
		int m = 0;
		for (Photo photo : input.photos) {
			m = Math.max(m, photo.id + 1);
		}
		Map<Integer, HashSet<Photo>> byTag = new HashMap<>();
		for (Photo photo : input.photos) {
			for (int tag : photo.tags) {
				if (!byTag.containsKey(tag)) {
					byTag.put(tag, new HashSet<Photo>());
				}
				byTag.get(tag).add(photo);
			}
		}
//		for (int i = 0; i < 100; i++) {
//			System.out.print(" ");
//			System.out.print(byTag.get(i).size());
//		}
//		System.out.println();
//		System.out.println(byTag.size());
		System.out.println("Finding all pairs... ");

		Photo[] byId = new Photo[m];
		for (Photo photo : photos) {
			byId[photo.id] = photo;
		}

		@SuppressWarnings("unchecked")
		ArrayList<Edge>[] byWeight = new ArrayList[MAX_WEIGHT];
		for (int i = 0; i < byWeight.length; i++) {
			byWeight[i] = new ArrayList<>();
		}
		boolean smallNei = true;
		for (Photo photo : photos) {
			HashSet<Photo> nei = new HashSet<>();
			for (int tag : photo.tags) {
				if (byTag.get(tag).size() > 64) {
					continue;
				}
				nei.addAll(byTag.get(tag));
			}
			nei.remove(photo);
			if (nei.size() == 0) {
				smallNei = false;
				break;
			}
			for (Photo that : nei) {
				int weight = photo.score(that);
				byWeight[weight].add(new Edge(photo, that));
			}
		}

		if (smallNei) {
			System.out.println("Use smallNei trick :)");
			ArrayList<Integer> greedy = greedy(m, byWeight);
			ArrayList<Photo> list = new ArrayList<>();
			for (int index : greedy) {
				list.add(byId[index]);
			}
			photos = list.toArray(new Photo[list.size()]);
		} else {
			ArrayList<Photo> list = new ArrayList<>(Arrays.asList(photos));
			Collections.shuffle(list, random);
			photos = list.toArray(new Photo[list.size()]);
		}

		int[] bucket = new int[m];
		int bs = 0;
		int bn = 0;
		for (Photo photo : photos) {
			bucket[photo.id] = bn;
			bs++;
			if (bs == BUCKET_SIZE) {
				bs = 0;
				bn++;
			}
		}
		bn++;

		@SuppressWarnings("unchecked")
		ArrayList<Photo>[] byBucket = new ArrayList[bn];
		for (int i = 0; i < bn; i++) {
			byBucket[i] = new ArrayList<>();
		}
		for (Photo photo : photos) {
			int b = bucket[photo.id];
			byBucket[b].add(photo);
		}
		System.out.print("Finding Hamiltonian path");
		for (int i = 0; i < bn; i++) {
			ArrayList<Photo> list = byBucket[i];
			if (list.isEmpty()) {
				continue;
			}
			System.out.print(".");
			Photo[] chain = HamPath.hamPath(list.toArray(new Photo[list.size()]), random);
			for (Photo photo : chain) {
				output.add(photo);
			}
		}
		System.out.println();
		return output;
	}

	ArrayList<Integer> greedy(int m, ArrayList<Edge>[] byWeight) {
		DisjointSetUnion dsu = new DisjointSetUnion(m);
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] pathNei = new ArrayList[m];
		for (int i = 0; i < pathNei.length; i++) {
			pathNei[i] = new ArrayList<>();
		}
		for (int w = MAX_WEIGHT - 1; w >= 0; w--) {
			for (Edge edge : byWeight[w]) {
				int a = edge.a.id;
				int b = edge.b.id;
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
		boolean[] mark = new boolean[m];
		ArrayList<Integer> res = new ArrayList<>();
		for (int i = 0; i < m; i++) {
			if (mark[i]) {
				continue;
			}
			if (pathNei[i].size() != 1) {
				continue;
			}
			dfs(i, -1, mark, res, pathNei);
		}
		return res;
	}

	void dfs(int v, int parent, boolean[] mark, ArrayList<Integer> res, ArrayList<Integer>[] pathNei) {
		mark[v] = true;
		res.add(v);
		for (int u : pathNei[v]) {
			if (u == parent) {
				continue;
			}
			dfs(u, v, mark, res, pathNei);
		}
	}

	class Edge {
		Photo a, b;

		public Edge(Photo a, Photo b) {
			this.a = a;
			this.b = b;
		}
	}
}
