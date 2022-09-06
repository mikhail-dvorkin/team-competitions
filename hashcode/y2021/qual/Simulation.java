package hashcode.y2021.qual;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

import java.io.*;
import java.util.*;

public class Simulation {
	int dur, nInter, nStreets, nCars, carBonus;
	int begin[], end[], len[];
	String name[];
	ArrayList<Integer>[] inbound;
	Map<String, Integer> streets;
	int path[][];
	int freq[];

	void input() throws Exception {
		dur = scanInt();
		nInter = scanInt();
		nStreets = scanInt();
		nCars = scanInt();
		carBonus = scanInt();
		begin = new int[nStreets];
		end = new int[nStreets];
		len = new int[nStreets];
		name = new String[nStreets];
		inbound = new ArrayList[nInter];
		for (int i = 0; i < inbound.length; i++) {
			inbound[i] = new ArrayList<>();
		}
		freq = new int[nStreets];
		streets = new HashMap<>();
		for (int i = 0; i < nStreets; i++) {
			begin[i] = scanInt();
			end[i] = scanInt();
			name[i] = scanString();
			streets.put(name[i], i);
			len[i] = scanInt();
			inbound[end[i]].add(i);
		}
		path = new int[nCars][];
		for (int i = 0; i < nCars; i++) {
			path[i] = new int[scanInt()];
			for (int j = 0; j < path[i].length; j++) {
				path[i][j] = streets.get(scanString());
				freq[path[i][j]]++;
			}
		}
	}

	long simulate() throws Exception {
		Queue<Integer> queue[] = new Queue[nStreets];
		for (int i = 0; i < nStreets; i++) {
			queue[i] = new ArrayDeque<>();
		}
		int schedule[][] = new int[nInter][];
		int n = scanInt();
		for (int i = 0; i < n; i++) {
			int ii = scanInt();
			if (schedule[ii] != null) {
				throw new AssertionError();
			}
			int cnt = scanInt();
			if (cnt < 1) {
				throw new AssertionError();
			}
			schedule[ii] = new int[2 * cnt];
			for (int j = 0; j < 2 * cnt; j += 2) {
				schedule[ii][j] = streets.get(scanString());
				int t = scanInt();
				if (t < 1 || t > dur) {
					throw new AssertionError();
				}
				schedule[ii][j + 1] = t;
			}
		}
		int schedulePos[] = new int[nInter], scheduleLeft[] = new int[nInter];
		for (int i = 0; i < nInter; i++) {
			if (schedule[i] != null) {
				scheduleLeft[i] = schedule[i][1];
			}
		}
		int time[] = new int[nCars];
		int carPos[] = new int[nCars];
		for (int i = 0; i < nCars; i++) {
			queue[path[i][0]].add(i);
		}
		long score = 0;
		for (int step = 0; step < dur; step++) {
			for (int i = 0; i < nInter; i++) {
				if (schedule[i] == null) {
					continue;
				}
				Queue<Integer> cur = queue[schedule[i][schedulePos[i]]];
				if (!cur.isEmpty() && time[cur.element()] <= step) {
					int c = cur.remove();
					++carPos[c];
					int nt = step + len[path[c][carPos[c]]];
					if (nt <= dur) {
						if (carPos[c] == path[c].length - 1) {
							score += carBonus + dur - nt;
						} else {
							time[c] = nt;
							queue[path[c][carPos[c]]].add(c);
						}
					}
				}
				if (--scheduleLeft[i] == 0) {
					schedulePos[i] += 2;
					if (schedulePos[i] == schedule[i].length) {
						schedulePos[i] = 0;
					}
					scheduleLeft[i] = schedule[i][schedulePos[i] + 1];
				}
			}
		}
		return score;
	}

	void visualize() {
		out.println("digraph City {");
		out.println("    graph[size=50];");
		out.println("    graph[node_separation=10];");
		out.println("    node[shape=point];");
		for (int i = 0; i < nInter; i++) {
			out.println("    " + i + ";");
		}
		for (int i = 0; i < nStreets; i++) {
			out.println("    " + begin[i] + " -> " + end[i] + ";");
		}
		out.println("}");
	}

	static int scanInt() throws IOException {
		return parseInt(scanString());
	}

	static long scanLong() throws IOException {
		return parseLong(scanString());
	}

	static String scanString() throws IOException {
		while (tok == null || !tok.hasMoreTokens()) {
			tok = new StringTokenizer(in.readLine());
		}
		return tok.nextToken();
	}

	static BufferedReader in;
	static PrintWriter out;
	static StringTokenizer tok;

//	public static void main(String[] args) {
//		try {
//			in = new BufferedReader(new InputStreamReader(System.in));
//			out = new PrintWriter(System.out);
////			solve();
//			in = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
//			input();
//			in = new BufferedReader(new InputStreamReader(new FileInputStream(args[1])));
//			out.println(simulate());
//			in.close();
//			out.close();
//		} catch (Throwable e) {
//			e.printStackTrace();
//			exit(1);
//		}
//	}
}
