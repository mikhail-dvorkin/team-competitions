package challenge24.y2014.finals.b;

import java.util.List;

/**
 * Created by ab on 5/3/14.
 */
public class Aop implements Op {

	public final Op func_op, arg_op;

	int size;

	public Aop(Op func_op, Op arg_op) {
		this.func_op = func_op;
		this.arg_op = arg_op;
	}

	@Override
	public Type type() {
		return Type.A;
	}

	public boolean lettable() {
		return func_op instanceof Fop;
	}

	@Override
	public String toFunnyString(String ident) {
		if (size() > 20 && lettable()) {

			final String letHeader = "(L [";
			final String letShift =  "    ";

			String prefix = letHeader;

			prefix += "(" + ((Fop)func_op).id + " " + arg_op.toFunnyString(ident + SHIFT) + ")";

			Op aOp = ((Fop)func_op).op;

			while (aOp instanceof Aop && ((Aop)aOp).lettable()) {
				Fop funcOp = (Fop)((Aop)aOp).func_op;
				Op argOp = ((Aop)aOp).arg_op;
				prefix += "\n" + ident + letShift + "(" + funcOp.id + " " + argOp.toFunnyString(ident + letShift + OpUtils.toBlanks(1 + funcOp.id.length() + 1)) + ")";
				aOp = funcOp.op;
			}
			prefix += "]\n" + ident + letShift + SHIFT;

			return prefix + aOp.toFunnyString(ident + letShift + SHIFT) + ")";

		} else {
			String inBetween = size() < 20 ? " " : "\n" + ident + " ";

			return
					"(" +
					func_op.toFunnyString(ident + " ") + inBetween +
					arg_op.toFunnyString(ident + " ") + ")";
		}
	}

	@Override
	public void makeReferences(List<String> ids, int[] holder) {
		func_op.makeReferences(ids, holder);
		arg_op.makeReferences(ids, holder);
	}

	@Override
	public int size() {
		return size == 0 ? size = 1 + func_op.size() + arg_op.size() : size;
	}

	@Override
	public String toString() {
		return "A " + func_op + " " + arg_op;
	}
}
