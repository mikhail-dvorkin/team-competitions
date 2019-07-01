package challenge24.y2014.finals.b;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class B {
	public static void main(String[] args) {
		final String fileName = "input/B/program.prg";
		final String outName = "input/B/program.fl";
		try (Scanner in = new Scanner(new File(fileName));
				PrintWriter out = new PrintWriter(outName)) {
			new B(in, out).solve();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(-1);
		}
	}

	private final Scanner in;
	private final PrintWriter out;

	public B(Scanner in, PrintWriter out1) {
		this.in = in;
		this.out = out1;
	}

	private void solve() {
		final Op op = parse(in);

		op.makeReferences(new ArrayList<String>(), new int[1]);
		System.out.println(op.toFunnyString(""));

		out.println(op.toFunnyString(""));
//		run(new Operation(op), Environment.EMPTY);
	}

	Op parse(Scanner scanner) {
		String s = scanner.next();
		switch (s) {
		case "F":
			return new Fop(parse(scanner));
		case "A":
			return new Aop(parse(scanner), parse(scanner));
		case "O":
			return new Oop(scanner.nextInt(), parse(scanner));
		case "I":
			return new Iop(parse(scanner));
		default:
			return new Rop(Integer.parseInt(s));
		}
	}
}
