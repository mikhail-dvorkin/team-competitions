package challenge24.y2014.finals.b;

import java.util.List;

/**
 * Created by ab on 5/3/14.
 */
public class Iop implements Op {
	public final Op op;

	public Iop(Op op) {
		this.op = op;
	}

	@Override
	public Type type() {
		return Type.I;
	}

	@Override
	public String toFunnyString(String ident) {
		return "(I " + op.toFunnyString(ident + OpUtils.toBlanks(3)) + ")";
	}

	@Override
	public void makeReferences(List<String> ids, int[] holder) {
		op.makeReferences(ids, holder);
	}

	@Override
	public int size() {
		return 1 + op.size();
	}

	@Override
	public String toString() {
		return "I " + op;
	}
}
