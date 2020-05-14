package hashcode.y2020.qual;

import java.io.*;
import java.util.*;

public class MainNikita {
	static class Book {
		int id;
		int score;

		public Book(int id, int score) {
			this.id = id;
			this.score = score;
		}
	}

	class Library implements Comparable<Library> {
		Book[] books;
		int id;
		int delay;
		int speed;
		int totalScore;

		public Library(Book[] books, int id, int delay, int speed) {
			this.id = id;
			this.books = books;
			this.delay = delay;
			this.speed = speed;
			this.totalScore = Arrays.stream(books).mapToInt(x -> x.score).sum();
		}

		int calcScore() {
			int ans = 0;
			for (Book b : books) {
				ans += b.score / occ[b.id];
			}
			return ans;
		}

		@Override
		public int compareTo(Library o) {
			return -Double.compare(
					1.0 * totalScore / delay, 1.0 * o.totalScore / o.delay);
		}
	}

	int nBooks, nLibraries, nDays;
	Book[] books;
	Library[] libraries;
	PrintWriter out;

	int score(Book[] taken) {
		int ans = 0;
		for (Book b : taken) {
			ans += b.score;
		}
		return ans;
	}

	void printLib(Library l, PrintWriter to) {
		to.println(l.id + " " + l.books.length);
		Book[] books = l.books.clone();
		Arrays.sort(books, Comparator.comparingInt(x -> -x.score));
		for (Book b : books) {
			to.print(b.id + " ");
		}
		to.println();
	}

	int calcScore(Library[] libs) {
		List<Book> books = new ArrayList<>();
		boolean[] used = new boolean[nBooks];
		int curDay = 0;
		for (Library lib : libs) {
			int takes = lib.delay;
			if (curDay + takes > nDays) {
				continue;
			}
			curDay += takes;
			for (Book b : lib.books) {
				if (!used[b.id]) {
					used[b.id] = true;
					books.add(b);
				}
			}
		}
		return score(books.toArray(new Book[0]));
	}

	@Deprecated
	int calcScore(Collection<Library> coll) {
		return calcScore(coll.toArray(new Library[0]));
	}

	Library[] naiveGreedy() {
		Library[] cp = libraries.clone();
		Arrays.sort(cp);
		return cp;
	}

	@Deprecated
	Library[] calcDp() {
		int[][] dp = new int[nLibraries + 1][nDays + 1];
		boolean[][] shouldTake = new boolean[nLibraries + 1][nDays + 1];
		for (int i = 0; i < nLibraries; ++i) {
			dp[i + 1] = dp[i].clone();
			Library cur = libraries[i];
			int sc = cur.calcScore();
			for (int can = 0; can + cur.delay <= nDays; ++can) {
				int newScore = dp[i][can] + sc;
				if (newScore > dp[i + 1][can + cur.delay]) {
					dp[i + 1][can + cur.delay] = newScore;
					shouldTake[i + 1][can + cur.delay] = true;
				}
			}
		}

		int cur = 0;
		for (int i = 1; i <= nDays; ++i) {
			if (dp[nLibraries][i] > dp[nLibraries][cur]) {
				cur = i;
			}
		}
		if (!shouldTake[nLibraries][cur]) {
			System.err.println("wtf?");
		}

		List<Library> ans = new ArrayList<>();

		int curDays = cur;
		int curLib = nLibraries - 1;
		boolean[] used = new boolean[nLibraries];

		while (curDays > 0) {
			if (shouldTake[curLib + 1][curDays]) {
				ans.add(libraries[curLib]);
				curDays -= libraries[curLib].delay;
				used[curLib] = true;
			}
			--curLib;
		}
		Collections.reverse(ans);
		for (int i = 0; i < nLibraries; ++i) {
			if (!used[i]) {
				ans.add(libraries[i]);
			}
		}
		return ans.toArray(new Library[0]);
	}

	int[] occ;

	<T> void swap(T[] arr, int i, int j) {
		T k = arr[i];
		arr[i] = arr[j];
		arr[j] = k;
	}

	void dump(Library[] libs, String name) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(name));
		pw.println(libs.length);
		for (Library lib : libs) {
			printLib(lib, pw);
		}
		pw.println();
		pw.close();
	}

	public void run() throws FileNotFoundException {
		Scanner in = new Scanner(new File("c_incunabula.txt"));
		out = new PrintWriter(new File("c_out.txt"));
		nBooks = in.nextInt();
		nLibraries = in.nextInt();
		nDays = in.nextInt();

		books = new Book[nBooks];
		for (int i = 0; i < nBooks; ++i) {
			books[i] = new Book(i, in.nextInt());
		}
		occ = new int[nBooks];

		libraries = new Library[nLibraries];
		for (int i = 0; i < nLibraries; ++i) {
			int n = in.nextInt();
			int delay = in.nextInt();
			int speed = in.nextInt();
			Book[] cur = new Book[n];
			for (int j = 0; j < n; ++j) {
				cur[j] = books[in.nextInt()];
				occ[cur[j].id]++;
			}
			libraries[i] = new Library(cur, i, delay, speed);
		}

		Library[] cur = naiveGreedy();
		int sc = calcScore(cur);

		Random rnd = new Random(566);
//		int nImprove = 0;
		//noinspection InfiniteLoopStatement
		while (true) {
			Library[] nxt = cur.clone();
			int i = rnd.nextInt(nLibraries);
			int j = rnd.nextInt(nLibraries);
			swap(nxt, i, j);
			int new_sc = calcScore(nxt);
			if (new_sc > sc) {
				System.err.println(new_sc);
				cur = nxt;
				sc = new_sc;
				dump(cur, new_sc + "");
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		new MainNikita().run();
	}
}
