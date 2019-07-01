package challenge24.y2014.finals.b;

import java.io.IOException;

public class Runner {

	public static void run(Op op) throws IOException {
		new Runner().run(new Operation(op), Environment.EMPTY);
	}

	public Closure run(Val val, Environment env) throws IOException {
		// maybe check val here
		while (true) {
			Entity result = step(val, env.copy());

			if (result instanceof Thunk) {
//				if (((Thunk) result).id != null /*&& Character.isUpperCase(((Thunk) result).id.charAt(0))*/) System.err.println(((Thunk) result).id);
				val = ((Thunk)result).val;
				env = ((Thunk)result).env;
			} else {
				val = (Val)result;
			}

			if (val instanceof Closure) {
				return (Closure)val;
			}
		}
	}

	public Entity step(Val val, Environment env) throws IOException {
		if (val instanceof Operation) {
			Op op = ((Operation)val).op;

//			System.out.println("OP:");
//			System.out.println(op.toFunnyString(""));
//
//			System.out.println("Environment");
//			System.out.println(env);

			switch (op.type()) {
			case F: return new Closure(new Operation(((Fop)op).op), env);
			case R: {
				int index = ((Rop)op).index;
				Entity result = env.get(index);

				if (result instanceof Thunk) {
					//					if (((Thunk) result).id != null /*&& Character.isUpperCase(((Thunk) result).id.charAt(0))*/) System.err.println(((Thunk) result).id);
					result = step(((Thunk)result).val, ((Thunk)result).env);
					env.put(index, result);
				}

				return result;
			}
			case A: {
				Op funcOp = ((Aop)op).func_op;

				String id = null;
				if (funcOp instanceof Fop) {
					id = ((Fop) funcOp).id;
				}

				Closure func = run(new Operation(funcOp), env.copy());
				Thunk arg = new Thunk(new Operation(((Aop)op).arg_op), env, id);

				Environment func_env = func.env;
				Val func_val = func.val;

				Environment new_env = func_env.prepend(arg);

				return new Thunk(func_val, new_env);
			}
			case O: {
				System.out.print((char)((Oop)op).ascii);
				return new Thunk(new Operation(((Oop)op).op), env);
			}
			case I: {
				int c = System.in.read();
				if (c < 0 || c > 127) System.exit(0);

				Op tOp = new Fop(new Fop(new Rop(1)));
				Op fOp = new Fop(new Fop(new Rop(0)));

				Op newOp = ((Iop)op).op;
				for (int t = 64; t > 0; t >>= 1) {
					if ((c & t) != 0) {
						newOp = new Aop(newOp, tOp);
					} else {
						newOp = new Aop(newOp, fOp);
					}
				}

				return new Thunk(new Operation(newOp), env);
			}
			default:
				throw new Error("Shouldn't happen");
			}
		} else {
			return new Thunk(val, env);
		}
	}

	public static interface Entity {
	}

	public static interface Val extends Entity {
	}

	public static class Operation implements Val {
		public final Op op;

		public Operation(Op op) {
			this.op = op;
		}
	}

	public static class Closure implements Val {
		public final Val val;
		public final Environment env;

		public Closure(Val val, Environment env) {
			this.val = val;
			this.env = env.copy();
		}
	}

	public static class Thunk implements Entity {
		public final Val val;
		public final Environment env;

		public String id;

		public Thunk(Val val, Environment env) {
			this(val, env, null);
		}

		public Thunk(Val val, Environment env, String id) {
			this.val = val;
			this.env = env.copy();
			this.id = id;
		}
	}

	public static class Environment {

		static class Node {
			Entity entity;
			final Node next;

			Node(Entity entity, Node next) {
				this.entity = entity;
				this.next = next;
			}

			@Override
			public String toString() {
				String result = "" + entity.getClass() + "\n";
				if (entity instanceof Operation) {
					result += ((Operation)entity).op.toFunnyString("");
				} else if (entity instanceof Thunk) {
					result += ((Operation)(((Thunk) entity).val)).op.toFunnyString("");
				}

				return result;
			}
		}

		Node root = null;

		public static Environment EMPTY = new Environment(null);

		public Environment(Node root) {
			this.root = root;
		}

		public Entity get(int index) {
			Node n = root;
			while (index > 0) {
				n = n.next;
				--index;
			}
			return n.entity;
		}

		public void put(int index, Entity entity) {
			Node n = root;
			while (index > 0) {
				n = n.next;
				--index;
			}
			n.entity = entity;
		}

		public Environment prepend(Entity entity) {
			Node newRoot = new Node(entity, root);
			return new Environment(newRoot);
		}

		public Environment copy() {
			return new Environment(root);
		}

		@Override
		public String toString() {
			String ans = "";
			Node n = root;
			while (n != null) {
				ans += n.toString() + "\n\n";
				n = n.next;
			}
			return ans;
		}
	}

}
