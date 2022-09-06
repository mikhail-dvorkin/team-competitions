package hashcode.y2021.qual;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.System.arraycopy;
import static java.lang.System.exit;
import static java.util.Arrays.fill;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main_old {
	static int dur, nInter, nStreets, nCars, carBonus;
	static int begin[], end[], len[];
	static String name[];
	static Map<String, Integer> streets;
	static int path[][];

	static void readInput() throws Exception {
		dur = scanInt();
		nInter = scanInt();
		nStreets = scanInt();
		nCars = scanInt();
		carBonus = scanInt();
		begin = new int[nStreets];
		end = new int[nStreets];
		len = new int[nStreets];
		name = new String[nStreets];
		streets = new HashMap<>();
		for (int i = 0; i < nStreets; i++) {
			begin[i] = scanInt();
			end[i] = scanInt();
			name[i] = scanString();
			streets.put(name[i], i);
			len[i] = scanInt();
		}
		path = new int[nCars][];
		for (int i = 0; i < nCars; i++) {
			path[i] = new int[scanInt()];
			for (int j = 0; j < path[i].length; j++) {
				path[i][j] = streets.get(scanString());
			}
		}
	}

	static int schedule[][];

	static void readOutput() throws Exception {
		schedule = new int[nInter][];
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
	}

	static void printOutput() {
		int n = 0;
		for (int i = 0; i < nInter; i++) {
			if (schedule[i] != null) {
				n++;
			}
		}
		out.println(n);
		for (int i = 0; i < nInter; i++) {
			if (schedule[i] != null) {
				out.println(i);
				out.println(schedule[i].length / 2);
				for (int j = 0; j < schedule[i].length; j += 2) {
					out.println(name[schedule[i][j]] + " " + schedule[i][j + 1]);
				}
			}
		}
	}

	static class Entry {
		final int car, time;
		Entry(int car, int time) {
			this.car = car;
			this.time = time;
		}
	}
	static List<Entry> isectSchedules[];

	static void solve() {
		int load[] = new int[nStreets];
		for (int i = 0; i < nCars; i++) {
			for (int j: path[i]) {
				++load[j];
			}
		}
		schedule = new int[nInter][];
		List<Integer> incoming[] = new List[nInter];
		for (int i = 0; i < nInter; i++) {
			incoming[i] = new ArrayList<>();
		}
		for (int i = 0; i < nStreets; i++) {
			incoming[end[i]].add(i);
		}
		int inLoad[][] = new int[nInter][];
		int inLoad2[][] = new int[nInter][];
		int curSchedule[] = new int[dur];
		int inSchedPos[] = new int[nStreets];
		for (int i = 0; i < nInter; i++) {
			int cn = incoming[i].size();
			inLoad[i] = new int[cn];
			int csum = 0;
			for (int j = 0; j < cn; j++) {
				inLoad[i][j] = load[incoming[i].get(j)];
				csum += inLoad[i][j];
			}
			inLoad2[i] = new int[cn];
			final int MAX = 50;
			if (csum > MAX) {
				for (int j = 0; j < cn; j++) {
					inLoad2[i][j] = inLoad[i][j] * MAX / csum;
				}
			} else {
				arraycopy(inLoad[i], 0, inLoad2[i], 0, cn);
			}
			int cnt = 0;
			for (int j = 0; j < cn; j++) {
				if (inLoad2[i][j] != 0) {
					++cnt;
				}
			}
			if (cnt == 0) {
				continue;
			}
			schedule[i] = new int[2 * cnt];
			for (int j = 0, jj = 0; j < cn; j++) {
				if (inLoad2[i][j] != 0) {
					schedule[i][jj++] = incoming[i].get(j);
					schedule[i][jj++] = inLoad2[i][j];
				}
			}
		}
		simulate();
		for (int i = 0; i < nInter; i++) {
			int cn = incoming[i].size();
			int csum = 0;
			for (int j = 0; j < cn; j++) {
				csum += inLoad[i][j];
			}
			int best = Integer.MAX_VALUE, bestLen = -1;
			for (int c = 1; c <= 100 && c <= csum; c++) {
				int cnt = 0;
				for (int j = 0; j < cn; j++) {
					inLoad2[i][j] = inLoad[i][j] * c / csum;
					if (inLoad2[i][j] != 0) {
						++cnt;
					}
				}
				if (cnt == 0) {
					continue;
				}
				int scheduleLen = 0;
				for (int j = 0; j < cn; j++) {
					for (int jj = 0; jj < inLoad2[i][j]; jj++) {
						inSchedPos[curSchedule[scheduleLen++] = incoming[i].get(j)] = 0;
					}
				}
				int cur = 0;
				for (int step = 0; step < dur; step++) {
					int str = curSchedule[step % scheduleLen];
					List<Entry> sched = isectSchedules[str];
					int schedPos = inSchedPos[str];
					if (schedPos < sched.size() && sched.get(schedPos).time <= step) {
						cur += step - sched.get(schedPos).time;
						++inSchedPos[str];
					}
				}
				for (int j = 0; j < cn; j++) {
					int str = incoming[i].get(j);
					List<Entry> sched = isectSchedules[str];
					cur += carBonus * (sched.size() - inSchedPos[str]);
				}
				if (cur < best) {
					best = cur;
					bestLen = c;
				}
			}
			if (bestLen > 0) {
				int cnt = 0;
				for (int j = 0; j < cn; j++) {
					inLoad2[i][j] = inLoad[i][j] * bestLen / csum;
					if (inLoad2[i][j] != 0) {
						++cnt;
					}
				}
				if (cnt == 0) {
					continue;
				}
				schedule[i] = new int[2 * cnt];
				for (int j = 0, jj = 0; j < cn; j++) {
					if (inLoad2[i][j] != 0) {
						schedule[i][jj++] = incoming[i].get(j);
						schedule[i][jj++] = inLoad2[i][j];
					}
				}
			}
		}
	}

	static long simulate() {
		Queue<Integer> queue[] = new Queue[nStreets];
		for (int i = 0; i < nStreets; i++) {
			queue[i] = new ArrayDeque<>();
		}
		int schedulePos[] = new int[nInter], scheduleLeft[] = new int[nInter];
		for (int i = 0; i < nInter; i++) {
			if (schedule[i] != null) {
				scheduleLeft[i] = schedule[i][1];
			}
		}
		int time[] = new int[nCars];
		isectSchedules = new List[nStreets];
		for (int i = 0; i < nStreets; i++) {
			isectSchedules[i] = new ArrayList<>();
		}
		int carPos[] = new int[nCars];
		for (int i = 0; i < nCars; i++) {
			queue[path[i][0]].add(i);
			isectSchedules[path[i][0]].add(new Entry(i, 0));
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
							isectSchedules[path[c][carPos[c]]].add(new Entry(c, nt));
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

	static void visualize() {
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

	public static void main(String[] args) {
		for (int i = 0; i < 6; i++) {
			String letter = "" + (char) ('a' + i);
			main1(new String[] {"solve", letter + ".txt", letter + ".out"});
		}
	}

	public static void main1(String[] args) {
		try {
			switch (args[0]) {
			case "solve":
				in = new BufferedReader(new InputStreamReader(new FileInputStream(args[1])));
				readInput();
				solve();
				out = new PrintWriter(new FileOutputStream(args[2]));
				printOutput();
				out.close();
				System.err.println(simulate());
				break;
			case "simulate":
				in = new BufferedReader(new InputStreamReader(new FileInputStream(args[1])));
				readInput();
				in = new BufferedReader(new InputStreamReader(new FileInputStream(args[2])));
				readOutput();
				System.err.println(simulate());
				break;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			exit(1);
		}
	}
}
