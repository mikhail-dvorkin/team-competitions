package hashcode.y2022.qual;

import static java.lang.Integer.parseInt;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.System.exit;
import static java.util.Arrays.fill;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {

	static class HopcroftKarpFast {

		static int n, m, len;
		static long edges[][];
		static int matchingL[], matchingR[];
		static int hkQueue[];
		static long hkSet[], hkSet2[][];

		static void init() {
			len = (m + 63) >> 6;
			edges = new long[n][len];
			matchingL = new int[n];
			matchingR = new int[m];
			hkQueue = new int[2 * n];
			hkSet = new long[len];
			hkSet2 = new long[min(n, m + 1)][len];
		}

		static final int BIT_INDEX[] = {
			0, 1, 2, 7, 3, 13, 8, 19, 4, 25, 14, 28, 9, 34, 20, 40,
			5, 17, 26, 38, 15, 46, 29, 48, 10, 31, 35, 54, 21, 50, 41, 57,
			63, 6, 12, 18, 24, 27, 33, 39, 16, 37, 45, 47, 30, 53, 49, 56,
			62, 11, 23, 32, 36, 44, 52, 55, 61, 22, 43, 51, 60, 42, 59, 58};

		static int bitIndex(long x) {
			return BIT_INDEX[(int) ((x * 0x218a392cd3d5dbfL) >>> 58)];
		}

		static int hopcroftKarp() {
			fill(matchingL, -1);
			fill(matchingR, -1);
			for (long a[]: hkSet2) {
				fill(a, 0);
			}
			int size = 0;
			while (true) {
				fill(hkSet, -1L);
				int queueHead = 0, queueTail = 0;
				for (int i = 0; i < n; i++) {
					if (matchingL[i] < 0) {
						hkQueue[queueTail++] = i;
						hkQueue[queueTail++] = 0;
					}
				}
				int resDist = -1;
				while (queueHead < queueTail) {
					int cur = hkQueue[queueHead++];
					int cdist = hkQueue[queueHead++];
					if ((cdist + Integer.MIN_VALUE) > (resDist + Integer.MIN_VALUE)) {
						break;
					}
					long ecur[] = edges[cur], curSet2[] = hkSet2[cdist];
					for (int nextBlock = 0; nextBlock < len; nextBlock++) {
						long v = hkSet[nextBlock], w = ecur[nextBlock] & v;
						if (w == 0) {
							continue;
						}
						hkSet[nextBlock] = v ^ w;
						curSet2[nextBlock] |= w;
						do {
							long x = w & -w;
							w ^= x;
							int next = (nextBlock << 6) + bitIndex(x);
							int next2 = matchingR[next];
							if (next2 < 0) {
								resDist = cdist;
							} else {
								hkQueue[queueTail++] = next2;
								hkQueue[queueTail++] = cdist + 1;
							}
						} while (w != 0);
					}
				}
				if (resDist < 0) {
					return size;
				}
				for (int cur = 0; cur < n; cur++) {
					if (matchingL[cur] < 0 && hopcroftKarpDfs(cur, 0)) {
						++size;
					}
				}
				for (int i = 0; i <= resDist; i++) {
					fill(hkSet2[i], 0);
				}
			}
		}

		static boolean hopcroftKarpDfs(int cur, int cdist) {
			long ecur[] = edges[cur], curSet2[] = hkSet2[cdist];
			for (int nextBlock = 0; nextBlock < len; nextBlock++) {
				long v = curSet2[nextBlock], w = ecur[nextBlock] & v;
				if (w == 0) {
					continue;
				}
				v ^= w;
				do {
					long x = w & -w;
					w ^= x;
					int next = (nextBlock << 6) + bitIndex(x);
					int next2 = matchingR[next];
					if (next2 < 0 || hopcroftKarpDfs(next2, cdist + 1)) {
						matchingR[next] = cur;
						matchingL[cur] = next;
						curSet2[nextBlock] = v ^ w;
						return true;
					}
				} while (w != 0);
				curSet2[nextBlock] = v;
			}
			return false;
		}
	}

	interface IntComparator {
		public int compare(int o1, int o2);
	}

	static class FreeTime implements Comparable<FreeTime> {
		final int time, ansPos;

		FreeTime(int time, int ansPos) {
			this.time = time;
			this.ansPos = ansPos;
		}

		public int compareTo(FreeTime o) {
			return time - o.time;
		}
	}

	static void sort(int a[], int n, IntComparator cmp) {
		if (n == 0) {
			return;
		}
		for (int i = 1; i < n; i++) {
			int j = i;
			int ca = a[i];
			do {
				int nj = (j - 1) >> 1;
				int na = a[nj];
				if (cmp.compare(na, ca) >= 0) {
					break;
				}
				a[j] = na;
				j = nj;
			} while (j != 0);
			a[j] = ca;
		}
		int ca = a[0];
		for (int i = n - 1; i > 0; i--) {
			int j = 0;
			while ((j << 1) + 2 + Integer.MIN_VALUE < i + Integer.MIN_VALUE) {
				j <<= 1;
				j += (cmp.compare(a[j + 1], a[j + 2]) < 0) ? 2 : 1;
			}
			if ((j << 1) + 2 == i) {
				j = (j << 1) + 1;
			}
			int na = a[i];
			a[i] = ca;
			ca = na;
			while (j != 0 && cmp.compare(a[j], ca) < 0) {
				j = (j - 1) >> 1;
			}
			while (j != 0) {
				na = a[j];
				a[j] = ca;
				ca = na;
				j = (j - 1) >> 1;
			}
		}
		a[0] = ca;
	}

	public static void main(String[] args) {
		String task = args[0];
		try (BufferedReader in = new BufferedReader(new FileReader(task + ".in"))) {
			StringTokenizer tok = new StringTokenizer(in.readLine());
			int n = parseInt(tok.nextToken());
			int m = parseInt(tok.nextToken());
			String cNames[] = new String[n];
			int cLevels[][] = new int[n][];
			Map<String, Integer> skills = new HashMap<>();
			for (int i = 0; i < n; i++) {
				tok = new StringTokenizer(in.readLine());
				cNames[i] = tok.nextToken();
				int nn = parseInt(tok.nextToken());
				cLevels[i] = new int[2 * nn];
				for (int j = 0; j < nn; j++) {
					tok = new StringTokenizer(in.readLine());
					String s = tok.nextToken();
					Integer ss = skills.get(s);
					if (ss == null) {
						skills.put(s, ss = skills.size());
					}
					cLevels[i][2 * j] = ss;
					cLevels[i][2 * j + 1] = parseInt(tok.nextToken());
				}
			}
			String pNames[] = new String[m];
			final int pDur[] = new int[m];
			final int pScore[] = new int[m];
			final int pBest[] = new int[m];
			int pLevels[][] = new int[m][];
			int maxRoles = 0;
			for (int i = 0; i < m; i++) {
				tok = new StringTokenizer(in.readLine());
				pNames[i] = tok.nextToken();
				pDur[i] = parseInt(tok.nextToken());
				pScore[i] = parseInt(tok.nextToken());
				pBest[i] = parseInt(tok.nextToken());
				int nn = parseInt(tok.nextToken());
				pLevels[i] = new int[2 * nn];
				for (int j = 0; j < nn; j++) {
					tok = new StringTokenizer(in.readLine());
					String s = tok.nextToken();
					Integer ss = skills.get(s);
					if (ss == null) {
						skills.put(s, ss = skills.size());
					}
					pLevels[i][2 * j] = ss;
					pLevels[i][2 * j + 1] = parseInt(tok.nextToken());
				}
				maxRoles = max(maxRoles, nn);
			}
			int nSkills = skills.size();
			System.out.println("Contributors: " + n);
			int cnt[] = new int[nSkills + 1];
			for (int i = 0; i < n; i++) {
				++cnt[cLevels[i].length >> 1];
			}
			for (int i = 0; i < cnt.length; i++) {
				if (cnt[i] != 0) {
					System.out.println("    with " + i + " skills: " + cnt[i]);
				}
			}
			System.out.println("Projects: " + m);
			cnt = new int[maxRoles + 1];
			for (int i = 0; i < m; i++) {
				++cnt[pLevels[i].length >> 1];
			}
			for (int i = 0; i < cnt.length; i++) {
				if (cnt[i] != 0) {
					System.out.println("    with " + i + " roles: " + cnt[i]);
				}
			}
			System.out.println("Total skills: " + nSkills);
			boolean have[] = new boolean[skills.size()];
			int nDup = 0;
			for (int i = 0; i < m; i++) {
				boolean dup = false;
				for (int j = 0; j < pLevels[i].length; j += 2) {
					if (have[pLevels[i][j]]) {
						dup = true;
					}
					have[pLevels[i][j]] = true;
				}
				for (int j = 0; j < pLevels[i].length; j += 2) {
					have[pLevels[i][j]] = false;
				}
				if (dup) {
					++nDup;
				}
			}
			System.out.println("Project with duplicate skills: " + nDup);
			HopcroftKarpFast.n = maxRoles;
			HopcroftKarpFast.m = n;
			HopcroftKarpFast.init();
			long can[][][] = new long[m][][];
			for (int i = 0; i < m; i++) {
				can[i] = new long[pLevels[i].length >> 1][(n + 63) >> 6];
				for (int j = 0; j < pLevels[i].length >> 1; j++) {
					int skill = pLevels[i][2 * j], level = pLevels[i][2 * j + 1];
					for (int k = 0; k < n; k++) {
						for (int l = 0; l < cLevels[k].length >> 1; l++) {
							if (cLevels[k][2 * l] == skill && cLevels[k][2 * l + 1] >= level) {
								can[i][j][k >> 6] |= 1L << k;
								break;
							}
						}
					}
				}
			}
			int order[] = new int[m];
			for (int i = 0; i < m; i++) {
				order[i] = i;
			}
			long free[] = new long[(n + 63) >> 6];
			fill(free, -1L);
			if ((n & 63) != 0) {
				free[n >> 6] &= -1L >>> -n;
			}
			boolean done[] = new boolean[m];
			int time = 0;
			PriorityQueue<FreeTime> freeTime = new PriorityQueue<>();
			int ansProj[] = new int[m];
			int ansAss[][] = new int[m][];
			int ansCnt = 0;
			long score = 0;
			main: while (true) {
				final int finalTime = time;
				sort(order, m, new IntComparator() {
					public int compare(int o1, int o2) {
						int score1 = max(pScore[o1] + min(0, pBest[o1] - pDur[o1] - finalTime), 0);
						int score2 = max(pScore[o2] + min(0, pBest[o2] - pDur[o2] - finalTime), 0);
						return Long.compare((long) score2 * pDur[o1], (long) score1 * pDur[o2]);
//						return pBest[o1] - pBest[o2];
					}
				});
				for (int i: order) {
					if (done[i]) {
						continue;
					}
					int nRoles = pLevels[i].length >> 1;
					HopcroftKarpFast.n = nRoles;
					int len = (n + 63) >> 6;
					for (int j = 0; j < nRoles; j++) {
						for (int k = 0; k < len; k++) {
							HopcroftKarpFast.edges[j][k] = can[i][j][k] & free[k];
						}
					}
					if (HopcroftKarpFast.hopcroftKarp() == nRoles) {
						ansProj[ansCnt] = i;
						ansAss[ansCnt] = Arrays.copyOf(HopcroftKarpFast.matchingL, nRoles);
						done[i] = true;
						for (int j = 0; j < nRoles; j++) {
							int cur = HopcroftKarpFast.matchingL[j];
							free[cur >> 6] &= ~(1L << cur);
						}
						int endTime = time + pDur[i];
						freeTime.add(new FreeTime(endTime, ansCnt));
						score += max(pScore[i] + min(0, pBest[i] - endTime), 0);
						++ansCnt;
						continue main;
					}
				}
				if (freeTime.isEmpty()) {
					break;
				}
				FreeTime ft = freeTime.remove();
				for (int cur: ansAss[ft.ansPos]) {
					free[cur >> 6] |= 1L << cur;
				}
				time = ft.time;
			}
			System.out.println("Score: " + score);
			try (PrintWriter out = new PrintWriter(task + ".out")) {
				out.println(ansCnt);
				for (int i = 0; i < ansCnt; i++) {
					out.println(pNames[ansProj[i]]);
					for (int j: ansAss[i]) {
						out.print(cNames[j] + " ");
					}
					out.println();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			exit(1);
		}
	}
}