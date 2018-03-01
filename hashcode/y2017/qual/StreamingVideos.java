package hashcode.y2017.qual;
import java.io.*;
import java.util.*;

public class StreamingVideos {
	int videos;
	int users;
	int servers;
	int capacity;
	int[] size;
	int[] toCenter;
	Map<Integer, Integer>[] toServers;
	Map<Integer, Integer>[] requests;
	Map<Integer, Integer>[] served;
	long sumAmounts;
	BitSet[] sets, ans;
	double ansScore = -1;
	int[] popularity;

	void solve(int step, double power) {
		System.out.print(step + " " + power + " ");
		sets = new BitSet[servers];
		for (int i = 0; i < servers; i++) {
			sets[i] = new BitSet();
		}
		int[] used = new int[servers];
		for (;;) {
			System.out.print("*");
			double score = 0;
			long[] latency = new long[videos];
			for (int u = 0; u < users; u++) {
				for (int v : requests[u].keySet()) {
					int amount = requests[u].get(v);
					int center = toCenter[u];
					int best = center;
					for (int s : toServers[u].keySet()) {
						if (!sets[s].get(v)) {
							continue;
						}
						best = Math.min(best, toServers[u].get(s));
					}
					served[u].put(v, best);
					latency[v] += best * (long) amount;
					score += (center - best) * amount;
				}
			}
			score *= 1000.0 / sumAmounts;
			if (score > ansScore) {
				ansScore = score;
				ans = new BitSet[servers];
				for (int s = 0; s < servers; s++) {
					ans[s] = (BitSet) sets[s].clone();
				}
			}
			Map<Double, Integer> coolVideos = new TreeMap<>();
			for (int v = 0; v < videos; v++) {
				boolean ok = false;
				for (int s = 0; s < servers; s++) {
					if (used[s] + size[v] <= capacity) {
						ok = true;
					}
				}
				if (!ok) {
					continue;
				}
				double noise = v * 1e-3 / videos;
				coolVideos.put(- latency[v] / Math.pow(size[v], power) + noise, v);
			}
			long[] temp = latency.clone();
			Arrays.sort(temp);
			boolean stop = true;
			int processed = 0;
			for (int v : coolVideos.values()) {
				if (++processed == step) {
					break;
				}
				long[] impr = new long[servers];
				for (int u = 0; u < users; u++) {
					long amount = requests[u].getOrDefault(v, 0);
					if (amount == 0) {
						continue;
					}
					int servedTime = served[u].get(v);
					for (int s : toServers[u].keySet()) {
						impr[s] += Math.max(0, servedTime - toServers[u].get(s)) * amount;
					}
				}
				int bestS = -1;
				for (int s = 0; s < servers; s++) {
					if (used[s] + size[v] > capacity) {
						continue;
					}
					if (bestS == -1 || impr[s] > impr[bestS]) {
						bestS = s;
					}
				}
				if (bestS == -1 || impr[bestS] <= 0) {
					continue;
				}
				used[bestS] += size[v];
				sets[bestS].set(v);
				stop = false;
			}
			if (stop) {
				System.out.println(score);
				break;
			}
		}
	}
	
	void run(int test) {
		readInput();
		switch (test) {
		case 1:
			solve(500, 0.74);
			break;
		case 2:
			solve(24, 1.5);
			break;
		case 3:
			solve(10000, 0.0);
			break;
		case 4:
			solve(260, 1.5);
			break;
		}
		System.out.println("=== " + (int) ansScore);
		printOutput();
	}
	
	void printOutput() {
		out.println(servers);
		for (int i = 0; i < servers; i++) {
			out.print(i);
			for (int j = 0; j < videos; j++) {
				if (ans[i].get(j)) {
					out.print(" " + j);
				}
			}
			out.println();
		}
	}

	@SuppressWarnings("unchecked")
	void readInput() {
		videos = in.nextInt();
		users = in.nextInt();
		int req = in.nextInt();
		servers = in.nextInt();
		capacity = in.nextInt();
		size = new int[videos];
		for (int i = 0; i < videos; i++) {
			size[i] = in.nextInt();
		}
		toCenter = new int[users];
		toServers = new Map[users];
		requests = new Map[users];
		served = new Map[users];
		for (int i = 0; i < users; i++) {
			toCenter[i] = in.nextInt();
			toServers[i] = new TreeMap<Integer, Integer>();
			requests[i] = new TreeMap<Integer, Integer>();
			served[i] = new TreeMap<Integer, Integer>();
			int k = in.nextInt();
			for (int j = 0; j < k; j++) {
				int id = in.nextInt();
				toServers[i].put(id, in.nextInt());
			}
		}
		popularity = new int[videos];
		for (int i = 0; i < req; i++) {
			int video = in.nextInt();
			int user = in.nextInt();
			int amount = in.nextInt();
			sumAmounts += amount;
			popularity[video] += amount;
			requests[user].put(video, amount + requests[user].getOrDefault(video, 0));
		}
	}
	
	static MyScanner in;
	static PrintWriter out;

	public static void main(String[] args) throws IOException {
		for (int test = 1; test <= 4; test++) {
			String fileName = "" + test;
			String inputFileName = fileName + ".in";
			String outputFileName = fileName + ".out";
			
			Locale.setDefault(Locale.US);
			BufferedReader br;
			br = new BufferedReader(new FileReader(inputFileName));
			out = new PrintWriter(outputFileName);
			in = new MyScanner(br);
			new StreamingVideos().run(test);
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
