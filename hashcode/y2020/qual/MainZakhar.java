package hashcode.y2020.qual;

import java.io.*;
import java.util.*;

public class MainZakhar {

	String[] fileName = {"a_example", "b_read_on", "c_incunabula",
			"d_tough_choices", "e_so_many_books", "f_libraries_of_the_world"};

	public static class Pair {
		int x;
		int y;

		public Pair(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	class Book implements Comparable<Book> {
		int id;
		int score;

		public Book(int id, int score) {
			this.id = id;
			this.score = score;
		}

		public int newScore() {
			return score * (1 + 0 / numberBooks[id]);
		}

		@Override
		public int compareTo(Book book) {
			return book.newScore() - newScore();
		}
	}

	class Library implements Comparable<Library> {
		int id;
		Book[] books;
		int delay;
		int speed;
		int score;

		public Library(int id, Book[] books, int delay, int speed) {
			this.id = id;
			this.books = books;
			this.delay = delay;
			this.speed = speed;
			this.score = score(0);
		}

		int score(int d0) {
			int ans = 0;
			for (int i = 0; i < Math.min(books.length, (long) (nDays - delay - d0) * speed); i++) {
				ans += books[i].score;
			}
			return ans;
		}

		public int toComp(int id) {
			return delay * 30400 - score(Math.max(arr[id], 0));
		}

		@Override
		public int compareTo(Library library) {
//			if (delay != library.delay) {
//				return delay - library.delay;
//			}
//			return library.score - score;
			return toComp(id) - library.toComp(library.id);
		}
	}

	int nBooks, nLibraries, nDays;
	Book[] books;
	Library[] libraries;
	int[] numberBooks;
	int[] arr;

	public void solve(int numberOfTheTest) throws FileNotFoundException {
		Scanner in = new Scanner(new File(fileName[numberOfTheTest] + ".txt"));
		PrintWriter out = new PrintWriter(new File(fileName[numberOfTheTest] + "_out.txt"));
		nBooks = in.nextInt();
		nLibraries = in.nextInt();
		nDays = in.nextInt();

		books = new Book[nBooks];
		numberBooks = new int[nBooks];
		for (int i = 0; i < nBooks; ++i) {
			books[i] = new Book(i, in.nextInt());
		}

		libraries = new Library[nLibraries];
		for (int i = 0; i < nLibraries; ++i) {
			int n = in.nextInt();
			int delay = in.nextInt();
			int speed = in.nextInt();
			Book[] cur = new Book[n];
			for (int j = 0; j < n; ++j) {
				cur[j] = books[in.nextInt()];
				numberBooks[cur[j].id]++;
			}
			Arrays.sort(cur);
			libraries[i] = new Library(i, cur, delay, speed);
		}

		arr = new int[nLibraries];
		Arrays.sort(libraries);
		int sum = 0;
		for (int i = 0; i < nLibraries; i++) {
			arr[libraries[i].id] = Math.min(sum / 100 * 100, 100);
			sum += libraries[i].delay;
		}
		Arrays.sort(libraries);

		boolean[] usedL = new boolean[nLibraries];

		ArrayList<Library> best = new ArrayList<>();

		for (int i = 0; i < nLibraries; i++) {
			if (libraries[i].delay == 1) {
				if (libraries[i].score > 10000) {
					usedL[libraries[i].id] = true;
					best.add(libraries[i]);
				}
			} else {
				usedL[libraries[i].id] = true;
				best.add(libraries[i]);
			}
		}

		System.err.println(best.size());

		Library[] l = libraries.clone();
		int sz = 0;
		for (Library li : best) {
			l[sz++] = li;
		}

		for (int i = 0; i < nLibraries; i++) {
			if (!usedL[libraries[i].id]) {
				l[sz++] = libraries[i];
			}
		}

		libraries = l;

		int ans = 0;
		int day = 0;

		int lib = 0;
		int boo = 0;
		int dropped = 0;
		int ansDropped = 0;

		HashSet<Integer> set = new HashSet<>();
		HashMap<Integer, Integer> used = new HashMap<>();

		for (int i = 0; i < nLibraries; i++) {
			int t0 = 0;
			day += libraries[i].delay;
			int curd = day;
			int thisDay = libraries[i].speed;
//			HashSet<Integer> th = new HashSet<>();
			for (Book b : libraries[i].books) {
				if (curd < nDays) {
					if (!set.contains(b.id)) {
						boo++;
						t0 = 1;
						thisDay--;
						ans += b.score;
						set.add(b.id);
						used.put(b.id, 1);
					} else {
//						th.add(b.id);
						used.put(b.id, used.get(b.id) + 1);
						dropped++;
						ansDropped += b.score;
						continue;
					}
				}
				if (thisDay == 0) {
					thisDay = libraries[i].speed;
					curd++;
				}
			}
			lib += t0;
		}

		@SuppressWarnings("unchecked")
		HashMap<Integer, Integer> left = (HashMap<Integer, Integer>) used.clone();

		out.println(nLibraries);

		ans = 0;
		day = 0;
		lib = 0;
		boo = 0;
		dropped = 0;
		ansDropped = 0;

		set = new HashSet<>();

		ArrayList<Pair> scores = new ArrayList<>();

		for (int i = 0; i < nLibraries; i++) {
			int t0 = 0;
			int sc = 0;
			out.println(libraries[i].id + " " + libraries[i].books.length);
			day += libraries[i].delay;
			int curd = day;
			int thisDay = libraries[i].speed;
			HashSet<Integer> th = new HashSet<>();
			for (Book b : libraries[i].books) {
				if (curd < nDays) {
					if (!set.contains(b.id) &&
							!(used.containsKey(b.id) && left.get(b.id) > Math.max((used.get(b.id) - 1), 5))) {
						boo++;
						t0 = 1;
						if (sc == 0) {
							System.err.println(libraries[i].score + " " + libraries[i].delay);
						}
						sc = libraries[i].score;
						thisDay--;
						ans += b.score;
						set.add(b.id);
					} else {
						th.add(b.id);
						if (left.containsKey(b.id)) {
							left.put(b.id, left.get(b.id));
						}
						dropped++;
						ansDropped += b.score;
						continue;
					}
				}
				if (thisDay == 0) {
					thisDay = libraries[i].speed;
					curd++;
				}
				out.print(b.id + " ");
			}
			for (int j : th) {
				out.print(j + " ");
			}
			lib += t0;
			if (sc > 0) {
				scores.add(new Pair(libraries[i].delay, sc));
			}
			out.println();
		}

		System.out.println(numberOfTheTest + " " + ans + " " + lib + " " + boo + " " + dropped + " " + ansDropped);

		for (Pair i : scores) {
			System.out.println(i.x + " " + i.y + " ");
		}

		System.err.println(ans);

		in.close();
		out.close();
	}

	public static void main(String[] args) throws FileNotFoundException {
		Scanner in = new Scanner(System.in);
		for (int i = 4; i < 5; i++) {
			new MainZakhar().solve(i/*in.nextByte()*/);
		}
		in.close();
	}
}
