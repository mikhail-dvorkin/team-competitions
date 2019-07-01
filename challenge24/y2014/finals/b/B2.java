package challenge24.y2014.finals.b;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class B2 {
	public static void main(String[] args) {
		final String fileName = "input/B/program.fl";
		final String outName = "input/B/program.out";
		try (Scanner in = new Scanner(new File(fileName));
				PrintWriter out = new PrintWriter(outName)) {
			new B2(in, out).solve();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(-1);
		}
	}

	private final Scanner in;
	private final PrintWriter out;

	public B2(Scanner in, PrintWriter out) {
		this.in = in;
		this.out = out;
	}

	private void solve() throws IOException {
		text = "";
		while (in.hasNext()) {
			text += in.nextLine();
		}

		pos = 0;

		List<Node> nodes = parseLow();
		if (nodes.size() != 1) throw new Error();

//		System.out.println(nodes.get(0));

		Op op = parseOp(nodes.get(0), new ArrayList<String>());

		op.makeReferences(new ArrayList<String>(), new int[1]);
//		System.out.println(op.toFunnyString(""));

		out.println(op);
		out.flush();

		Runner.run(op);
	}


	private Op parseOp(Node node, ArrayList<String> ids) {
		if (node.children == null) {
			int index = -1;
			for (int i = 0; i < ids.size(); ++i) {
				if (ids.get(ids.size() - 1 - i).equals(node.id)) {
					index = i;
					break;
				}
			}
			return new Rop(index);
		} else {
			Node first = node.children.get(0);
			if ("L".equals(first.id)) {
				List<Node> lets = node.children.get(1).children;
				for (Node lc : lets) {
					ids.add(lc.children.get(0).id);
				}
				Op body = parseOp(node.children.get(2), ids);
				for (int i = lets.size() - 1; i >= 0; --i) {
					Node lc = lets.get(i);
					ids.remove(ids.size() - 1);
					body = new Aop(new Fop(body), parseOp(lc.children.get(1), ids));
				}
				return body;
			} else if ("\\".equals(first.id)) {
				String[] names = node.children.get(1).id.split("\\.");

				for (String name : names) {
					ids.add(name);
				}

				Op body = parseOp(node.children.get(2), ids);

				for (int i = names.length - 1; i >= 0; --i) {
					ids.remove(ids.size() - 1);
					body = new Fop(body);
				}

				return body;
			} else if ("O".equals(first.id)) {
				String txt = node.children.get(1).id;
//				System.out.println(txt);
				if (txt.startsWith("'")) {
//					System.out.println(txt);
					txt = txt.substring(1, txt.length() - 1);
					txt = txt.replaceAll("\\\\n", "\n");
					Op body = parseOp(node.children.get(2), ids);
					for (int i = txt.length() - 1; i >= 0; --i) {
						body = new Oop(txt.charAt(i), body);
					}
					return body;
				} else {
					return new Oop(Integer.parseInt(txt), parseOp(node.children.get(2), ids));
				}
			} else if ("I".equals(first.id)) {
				return new Iop(parseOp(node.children.get(1), ids));
			}
			return new Aop(parseOp(first, ids), parseOp(node.children.get(1), ids));
		}
	}

	String text;
	int pos;

	List<Node> parseLow() {
		List<Node> result = new ArrayList<Node>();
		String id = "";
		while (true) {
			if (pos >= text.length()) return result;
			char c = text.charAt(pos++);
			if (Character.isWhitespace(c)) {
				if (id.length() > 0) {
					result.add(new Node(id));
					id = "";
				}
				continue;
			}
			if (c == ')' || c == ']') {
				if (id.length() > 0) {
					result.add(new Node(id));
					id = "";
				}
				return result;
			}
			if (c == '(' || c == '[') {
				if (id.length() > 0) {
					result.add(new Node(id));
					id = "";
				}
				result.add(new Node(parseLow()));
				continue;
			}

			id += c;
		}
	}

	static class Node {

		String id;

		List<Node> children;

		Node(List<Node> children) {
			this.children = children;
			this.id = null;
		}

		Node(String id) {
			this.children = null;
//			System.out.println(id);
			this.id = id;
		}

		@Override
		public String toString() {
			if (children != null) {
				String result = "( ";
				for (Node n : children) {
					result += n.toString() + " ";
				}
				result += ")";
				return result;
			} else {
				return id;
			}
		}
	}


}
