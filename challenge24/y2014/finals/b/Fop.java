package challenge24.y2014.finals.b;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ab on 5/3/14.
 */
public class Fop implements Op {

	public final Op op;
	public String id;

	int size;

	public Fop(Op op) {
		this.op = op;
	}

	@Override
	public Type type() {
		return Type.F;
	}

	@Override
	public String toFunnyString(String ident) {
		String result = "(\\ " + id;
		Op nop = op;
		while (nop instanceof Fop) {
			Fop fop = ((Fop)nop);
			result += "." + fop.id;
			nop = fop.op;
		}

		return result +  " " + nop.toFunnyString(ident + OpUtils.toBlanks(result.length() + 1)) + ")";
	}

	@SuppressWarnings("serial")
	public static final Map<String, String> predefined = new HashMap<String, String>(){{
		put("b", "YC");
		put("i", "#f");
		put("j", "#t");
		put("m", "PC");
		put("w", "FC");
		put("x", "PF");
		put("kd", "MainLoop");
		put("kb", "Menu");
		put("ka", "ReadCmd");
		put("k", "#s_t");
		put("do", "SelfTests");
		put("dc", "Exit");
		put("jb", "Numbers");
		put("jh", "Circle");
		put("jy", "Calc");
		put("it", "Binary");
		put("ix", "Sqrt");
		put("jz", "Ascii");
		put("jc", "Bit");
		put("iz", "Sort");
		put("o", "WT");
		put("n", "WF");
	}};

	@Override
	public void makeReferences(List<String> ids, int[] holder) {
		id = toString(++holder[0]);
		if (predefined.containsKey(id)) {
			id = predefined.get(id);
		}
		ids.add(id);
		op.makeReferences(ids, holder);
		ids.remove(ids.size() - 1);
	}

	@Override
	public int size() {
		return size == 0 ? 1 + op.size() : size;
	}

	public final int BASE = 26;

	public int fromString(String s) {
		int result = 0;
		for (char c : s.toCharArray()) {
			result = result * BASE + (c - 'a');
		}
		return result;
	}

	public String toString(int i) {
		if (i == 0) return "a";
		String result = "";
		while (i > 0) {
			result = ((char) ('a' + (i % BASE))) + result;
			i /= BASE;
		}
		return result;
	}

	@Override
	public String toString() {
		return "F " + op;
	}
}
