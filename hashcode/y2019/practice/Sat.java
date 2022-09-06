package hashcode.y2019.practice;

import java.math.BigInteger;
import java.util.*;

import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.pb.ObjectiveFunction;
import org.sat4j.pb.PseudoOptDecorator;
import org.sat4j.pb.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IOptimizationProblem;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;

import hashcode.y2019.practice.SimulatedAnnealing.*;

class Sat {
	Clause[] clauses;
	int[] scores;
	int maxScore;
	Clause[][] withVar;

	Sat(Clause[] clauses, int[] scores) {
		this.clauses = clauses;
		this.scores = scores;
		for (int score : scores) {
			maxScore = Math.max(maxScore, score);
		}
		int[] deg = new int[scores.length];
		for (Clause clause : clauses) {
			for (int var : clause.vars) {
				deg[Math.abs(var) - 1]++;
			}
		}
		withVar = new Clause[scores.length][];
		for (int i = 0; i < scores.length; i++) {
			withVar[i] = new Clause[deg[i]];
		}
		for (Clause clause : clauses) {
			for (int var : clause.vars) {
				var = Math.abs(var) - 1;
				withVar[var][--deg[var]] = clause;
			}
		}
	}

	boolean[] solve(Settings settings, Random random) {
		if (false) {
			return solveSat4j();
		}
		SatItem item = new SatItem(this, random);
		item = (SatItem) SimulatedAnnealing.simulatedAnnealing(item, settings, random);
		return item.sat;
	}

	boolean[] solveSat4j() {
		try {
			System.out.print("Hi");
			PseudoOptDecorator solver = new PseudoOptDecorator(SolverFactory.newBothStar());
			for (Clause clause : clauses) {
				IVecInt literals = new VecInt(clause.vars);
				solver.addAtLeast(literals, clause.min);
				solver.addAtMost(literals, clause.max);
			}
			System.out.print(".");
			IVecInt vars = new VecInt(scores.length);
			IVec<BigInteger> coeffs = new Vec<>(scores.length);
			for (int i = 0; i < scores.length; i++) {
				vars.push(i + 1);
				coeffs.push(BigInteger.valueOf(-scores[i]));
			}
			System.out.print(".");
			ObjectiveFunction objectiveFunction = new ObjectiveFunction(vars, coeffs);
			solver.setObjectiveFunction(objectiveFunction);
			IOptimizationProblem problem = (IOptimizationProblem) solver;
			solver.setTimeout(6 * 60 * 60);
//			problem.setTimeoutForFindingBetterSolution(20);
			System.out.print("Run Sat4j ");
			int[] model = null;
			for (int i = 0;; i++) {
				try {
					System.out.print(problem.admitABetterSolution() + " ");
					model = solver.model();
					System.out.print(problem.getObjectiveValue() + " ");
					problem.discardCurrentSolution();
				} catch (TimeoutException e) {
					break;
				}
			}
			System.out.print("Ran Sat4j ");
			boolean[] result = new boolean[scores.length];
			for (int v : model) {
				if (v > 0) {
					result[v - 1] = true;
				}
			}
			return result;
		} catch (ContradictionException e) {
			throw new RuntimeException(e);
		}
	}

	static class Clause {
		int[] vars;
		int min, max;

		public Clause(int min, int max, int... vars) {
			this.vars = vars;
			this.min = min;
			this.max = max;
		}
	}

	static class SatItem implements AnnealableWithStepBack {
		Sat satInstance;
		boolean[] sat;
		int penaltyForBadClause;
		int energy;

		public SatItem(Sat satInstance, Random random) {
			this.satInstance = satInstance;
			sat = new boolean[satInstance.scores.length];
			for (int i = 0; i < sat.length; i++) {
				sat[i] = random.nextBoolean();
			}
			penaltyForBadClause = satInstance.maxScore + 1;
			calcEnergy();
		}

		public SatItem(SatItem that) {
			sat = that.sat.clone();
			energy = that.energy;
		}

		void calcEnergy() {
			energy = 0;
			for (int i = 0; i < sat.length; i++) {
				if (sat[i]) {
					energy -= satInstance.scores[i];
				}
			}
			for (Clause clause : satInstance.clauses) {
				energy += clauseEnergy(clause);
			}
		}

		int clauseEnergy(Clause clause) {
			int truths = 0;
			for (int x : clause.vars) {
				if ((x > 0) == (sat[x - 1])) {
					truths++;
				}
			}
			int diff = Math.max(truths - clause.max, clause.min - truths);
			diff = Math.max(diff, 0);
			return penaltyForBadClause * diff * diff;
		}

		@Override
		public double energy() {
			return energy;
		}

		@Override
		public SatItem randomInstance(Random random) {
			return new SatItem(satInstance, random);
		}

		int localEnergy(int x) {
			int result = sat[x] ? -satInstance.scores[x] : 0;
			for (Clause clause : satInstance.withVar[x]) {
				result += clauseEnergy(clause);
			}
			return result;
		}

		void flip(int x) {
			energy -= localEnergy(x);
			sat[x] ^= true;
			energy += localEnergy(x);
		}

		int vary1, vary2;

		@Override
		public void vary(Random random) {
			if (random.nextInt(4) == 0) {
				vary1 = random.nextInt(sat.length);
				vary2 = -1;
				flip(vary1);
			} else {
				while (true) {
					vary1 = random.nextInt(sat.length);
					Clause[] clauses = satInstance.withVar[vary1];
					if (clauses.length == 0) {
						continue;
					}
					Clause clause = clauses[random.nextInt(clauses.length)];
					while (true) {
						vary2 = Math.abs(clause.vars[random.nextInt(clause.vars.length)]) - 1;
						if (vary2 != vary1) {
							break;
						}
					}
					break;
				}
				flip(vary1);
				flip(vary2);
			}
		}

		@Override
		public void undo() {
			flip(vary1);
			if (vary2 >= 0) {
				flip(vary2);
			}
		}

		@Override
		public SatItem cloneAnswer() {
			return new SatItem(this);
		}
	}
}
