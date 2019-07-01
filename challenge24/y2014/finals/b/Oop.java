package challenge24.y2014.finals.b;

import java.util.List;

/**
 * Created by ab on 5/3/14.
 */
public class Oop implements Op {
    public final int ascii;
    public final Op op;

    public Oop(int ascii, Op op) {
        this.ascii = ascii;
        this.op = op;
    }

    @Override
    public Type type() {
        return Type.O;
    }

    public String prettyChar() {
        if (ascii == '\n') return "\\n";
        if ("()[]".indexOf((char)ascii) >= 0) return null;
        if (33 <= ascii && ascii <= 126) return Character.toString((char)ascii);
        return null;
    }

    @Override
    public String toFunnyString(String ident) {
        String repr = prettyChar();
        if (repr == null) {
            String prefix = "(O " + ascii + " ";
            return prefix + op.toFunnyString(ident + OpUtils.toBlanks(prefix.length())) + ")";
        } else {
            Op nop = op;
            while (nop instanceof Oop) {
                String next = ((Oop) nop).prettyChar();
                if (next == null) break;
                repr += next;
                nop = ((Oop) nop).op;
            }
            String prefix = "(O '" + repr + "' ";
            return  prefix + nop.toFunnyString(ident + OpUtils.toBlanks(prefix.length())) + ")";
        }
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
        return "O " + ascii + " " + op;
    }
}
