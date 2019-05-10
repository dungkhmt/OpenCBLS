package localsearch.applications.examplebook;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Examples {
	public static void example1() {
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[5];
		for (int i = 0; i < x.length; i++) {
			x[i] = new VarIntLS(mgr, 1, 10);
			x[i].setValue(i);
		}
		IFunction s = new Sum(x);
		IConstraint c = new LessOrEqual(s, 8);
		mgr.close();

		System.out.println("s = " + s.getValue() + ", c.violations = "
				+ c.violations());
		int ds = s.getAssignDelta(x[4], 1);
		int dc = c.getAssignDelta(x[4], 1);
		x[4].setValuePropagate(1);
		System.out.println("ds = " + ds + ", dc = " + dc + ", new s = "
				+ s.getValue() + ", new violations = " + c.violations());

	}

	public static void example2() {
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[5];
		for (int i = 0; i < x.length; i++)
			x[i] = new VarIntLS(mgr, 1, 3);
		x[0].setValue(1);
		x[1].setValue(3);
		x[2].setValue(2);
		x[3].setValue(1);
		x[4].setValue(2);
		int[] w = new int[] { 2, 3, 1, 5, 4 };
		IFunction f = new ConditionalSum(x, w, 1);
		mgr.close();

		System.out.println("f = " + f.getValue());
		int d = f.getAssignDelta(x[2], 1);
		x[2].setValuePropagate(1);
		System.out.println("delta = " + d + ", new value = " + f.getValue());
	}

	public static void example3() {
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[5];
		for (int i = 0; i < x.length; i++)
			x[i] = new VarIntLS(mgr, 1, 3);
		x[0].setValue(1);
		x[1].setValue(3);
		x[2].setValue(2);
		x[3].setValue(1);
		x[4].setValue(2);
		IConstraint c = new AllDifferent(x);
		mgr.close();

		System.out.println("violations = " + c.violations());
		int d = c.getAssignDelta(x[2], 1);
		x[2].setValuePropagate(1);
		System.out.println("delta = " + d + ", violations = " + c.violations());
	}

	public static void sudoku() {
		class Move {
			int i, j1, j2;

			public Move(int i, int j1, int j2) {
				this.i = i;
				this.j1 = j1;
				this.j2 = j2;
			}
		}

		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[][] x = new VarIntLS[9][9];
		for (int i = 0; i < 9; i++){
			for (int j = 0; j < 9; j++){
				x[i][j] = new VarIntLS(mgr, 1, 9);
				x[i][j].setValue(j+1);
			}
		}
		ConstraintSystem S = new ConstraintSystem(mgr);
		for (int i = 0; i < 9; i++) {
			VarIntLS[] y = new VarIntLS[9];
			for (int j = 0; j < 9; j++)
				y[j] = x[i][j];
			S.post(new AllDifferent(y));
		}

		for (int j = 0; j < 9; j++) {
			VarIntLS[] y = new VarIntLS[9];
			for (int i = 0; i < 9; i++)
				y[i] = x[i][j];
			S.post(new AllDifferent(y));
		}

		for (int I = 0; I < 3; I++) {
			for (int J = 0; J < 3; J++) {
				VarIntLS[] y = new VarIntLS[9];
				int idx = -1;
				for (int i = 0; i < 3; i++)
					for (int j = 0; j < 3; j++) {
						idx++;
						y[idx] = x[3 * I + i][3 * J + j];
					}
				S.post(new AllDifferent(y));	
			}
		}
		mgr.close();

		ArrayList<Move> N = new ArrayList<Move>();
		Random R = new Random();
		int it = 0;
		while (it < 100000 && S.violations() > 0) {
			N.clear();
			int minDelta = Integer.MAX_VALUE;
			for (int i = 0; i < 9; i++) {
				for(int j1 = 0; j1 < 8; j1++){
					for(int j2 = j1+1; j2 < 9; j2++){
						int d = S.getSwapDelta(x[i][j1], x[i][j2]);
						if(d > 0) continue;
						if (d < minDelta) {
							N.clear();
							N.add(new Move(i, j1, j2));
							minDelta = d;
						} else if (d == minDelta) {
							N.add(new Move(i, j1, j2));
						}

					}
				}
			}
			if (N.size() > 0) {
				int idx = R.nextInt(N.size());
				Move m = N.get(idx);
				x[m.i][m.j1].swapValuePropagate(x[m.i][m.j2]);
			} else {
				// RESTART
				for (int i = 0; i < 9; i++){
					for(int k = 0; k < 20; k++){
						int j1 = R.nextInt(9);
						int j2 = R.nextInt(9);
						x[i][j1].swapValuePropagate(x[i][j2]);
					}
				}
			}
			System.out.println("Step " + it + ", S = " + S.violations());
			it++;

		}
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				System.out.print(x[i][j].getValue() + " ");
			}
			System.out.println();
		}
		
	}

	public static void queen() {
		class Move {
			int i;
			int v;

			public Move(int i, int v) {
				this.i = i;
				this.v = v;
			}
		}
		int n = 100;
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[n];
		for (int i = 0; i < n; i++)
			x[i] = new VarIntLS(mgr, 0, n - 1);

		ConstraintSystem S = new ConstraintSystem(mgr);
		S.post(new AllDifferent(x));

		IFunction[] f1 = new IFunction[n];
		for (int i = 0; i < n; i++)
			f1[i] = new FuncPlus(x[i], i);
		S.post(new AllDifferent(f1));

		IFunction[] f2 = new IFunction[n];
		for (int i = 0; i < n; i++)
			f2[i] = new FuncMinus(x[i], i);
		S.post(new AllDifferent(f2));

		mgr.close();

		ArrayList<Move> N = new ArrayList<Move>();
		Random R = new Random();
		int it = 0;
		while (it < 100000 && S.violations() > 0) {
			N.clear();
			int minDelta = Integer.MAX_VALUE;
			for (int i = 0; i < n; i++) {
				for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++)
					if (v != x[i].getValue()) {
						int d = S.getAssignDelta(x[i], v);
						if (d > 0)
							continue;
						if (d < minDelta) {
							N.clear();
							N.add(new Move(i, v));
							minDelta = d;
						} else if (d == minDelta) {
							N.add(new Move(i, v));
						}

					}
			}
			if (N.size() > 0) {
				int idx = R.nextInt(N.size());
				Move m = N.get(idx);
				x[m.i].setValuePropagate(m.v);
			} else {
				// RESTART
				for (int i = 0; i < n; i++)
					x[i].setValuePropagate(R.nextInt(n));
			}
			System.out.println("Step " + it + ", S = " + S.violations());
			it++;

		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Examples.example1();
		// Examples.example2();
		//Examples.queen();
		Examples.sudoku();
	}

}
