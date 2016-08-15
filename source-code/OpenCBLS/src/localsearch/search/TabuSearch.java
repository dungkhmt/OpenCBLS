package localsearch.search;

import java.util.*;

import localsearch.common.Utility;
import localsearch.model.*;

public class TabuSearch {

	/**
	 * @param args
	 */

	private java.util.Random rand = null;
	private double t_best;
	private double t0;
	private double t;
	
	public TabuSearch() {
		rand = new java.util.Random();
	}

	public double getTimeBest() {
		return t_best * 0.001;
	}

	public void greedySearchMinMultiObjectives(IFunction[] f, IConstraint[] S,
			int maxIter, int maxTime) {
		HashSet<VarIntLS> V = new HashSet<VarIntLS>();
		for (int i = 0; i < f.length; i++) {
			VarIntLS[] y = f[i].getVariables();
			if (y != null) {
				for (int j = 0; j < y.length; j++)
					V.add(y[j]);
			}
		}
		for (int i = 0; i < S.length; i++) {
			VarIntLS[] y = S[i].getVariables();
			if (y != null) {
				for (int j = 0; j < y.length; j++)
					V.add(y[j]);
			}
		}
		VarIntLS[] x = new VarIntLS[V.size()];
		HashMap<VarIntLS, Integer> map = new HashMap<VarIntLS, Integer>();
		int idx = -1;
		Iterator it = V.iterator();
		while (it.hasNext()) {
			VarIntLS xi = (VarIntLS) it.next();
			idx++;
			x[idx] = xi;
			map.put(xi, idx);
		}

		int iter = 0;
		maxTime = maxTime * 1000;
		double t0 = System.currentTimeMillis();
		ArrayList<Move> moves = new ArrayList<Move>();
		int deltaF[] = new int[f.length];
		int[] deltaS = new int[S.length];
		while (iter < maxIter && System.currentTimeMillis() - t0 < maxTime) {
			moves.clear();
			for (int i = 0; i < x.length; i++) {
				for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++)
					if (x[i].isValue(v)) {
						boolean ok = true;
						for (int j = 0; j < f.length; j++) {
							deltaF[j] = f[j].getAssignDelta(x[i], v);
							if (deltaF[j] > 0) {
								ok = false;
								break;
							}
						}
						if (ok)
							for (int j = 0; j < S.length; j++) {
								deltaS[j] = S[j].getAssignDelta(x[i], v);
								if (deltaS[j] > 0) {
									ok = false;
									break;
								}
							}
						if (ok) {
							ok = false;
							for (int j = 0; j < f.length; j++)
								if (deltaF[j] < 0)
									ok = true;
							for (int j = 0; j < S.length; j++)
								if (deltaS[j] < 0)
									ok = true;
						}
						if (ok) {
							moves.add(new OneVariableValueMove(
									MoveType.OneVariableValueAssignment, -1,
									x[i], v));
						}
					}
			}
			for (int i = 0; i < x.length - 1; i++) {
				for (int j = i + 1; j < x.length; j++) {
					boolean ok = true;
					for (int k = 0; k < f.length; k++) {
						deltaF[k] = f[k].getSwapDelta(x[i], x[j]);
						if (deltaF[k] > 0) {
							ok = false;
							break;
						}
					}
					for (int k = 0; k < S.length; k++) {
						deltaS[k] = S[k].getSwapDelta(x[i], x[j]);
						if (deltaS[k] > 0) {
							ok = false;
							break;
						}
					}
					if (ok) {
						ok = false;
						for (int k = 0; k < f.length; k++)
							if (deltaF[k] < 0)
								ok = true;
						for (int k = 0; k < S.length; k++)
							if (deltaS[k] < 0)
								ok = true;
					}
					if (ok) {
						moves.add(new TwoVariablesSwapMove(
								MoveType.TwoVariablesSwap, -1, x[i], x[j]));
					}
				}
			}

			if (moves.size() > 0) {
				Move move = moves.get(rand.nextInt(moves.size()));
				if (move.getType() == MoveType.OneVariableValueAssignment) {
					OneVariableValueMove m1 = (OneVariableValueMove) move;
					VarIntLS sel_x = m1.getVariable();
					int sel_v = m1.getValue();
					sel_x.setValuePropagate(sel_v);
					System.out.println(name()
							+ "::greedySearchMinMultiObjectives move assign x["
							+ sel_x.getID() + "] to " + sel_v);
				} else if (move.getType() == MoveType.TwoVariablesSwap) {
					TwoVariablesSwapMove m2 = (TwoVariablesSwapMove) move;
					VarIntLS xi = m2.getVar1();
					VarIntLS xj = m2.getVar2();
					int tmp = xi.getValue();
					xi.setValuePropagate(xj.getValue());
					xj.setValuePropagate(tmp);
					System.out.println(name()
							+ "::greedySearchMinMultiObjectives move swap x["
							+ xi.getID() + "] and x[" + xj.getID() + "]");
				} else {
					System.out
							.println("TabuSearch::greedySearchMinMultiObjectives -> exception, unknown move");
					assert (false);
				}
			} else {
				break;
			}
			iter++;
		}
	}

	
	public void searchAssignSwap(IConstraint S, int tbl, int maxIter,
			int maxTime) {
		t0 = System.currentTimeMillis();
		VarIntLS[] x = S.getVariables();
		HashMap<VarIntLS, Integer> map = new HashMap<VarIntLS, Integer>();
		for (int i = 0; i < x.length; i++)
			map.put(x[i], i);

		int n = x.length;
		boolean[][] equalDomain = new boolean[n][n];
		for(int i = 0; i < n-1; i++){
			for(int j = i+1; j <  n; j++)
				if(Utility.equalSet(x[i].getDomain(), x[j].getDomain())){
					equalDomain[i][j] = true;
					equalDomain[j][i] = true;
				}else{
					equalDomain[i][j] = false;
					equalDomain[j][i] = false;
				}
		}
		
		int iter = 0;
		maxTime = maxTime * 1000;
		double t0 = System.currentTimeMillis();
		ArrayList<Move> moves = new ArrayList<Move>();
		//int n = x.length;
		int maxV = -1000000000;
		int minV = 1000000000;
		for (int i = 0; i < n; i++) {
			if (maxV < x[i].getMaxValue())
				maxV = x[i].getMaxValue();
			if (minV > x[i].getMinValue())
				minV = x[i].getMinValue();
		}
		int D = maxV - minV;
		// System.out.println("n = " + n + ", D = " + D);
		int tabuAssign[][] = new int[n][D + 1];

		for (int i = 0; i < n; i++)
			for (int v = 0; v <= D; v++)
				tabuAssign[i][v] = -1;

		int[][] tabuSwap = new int[x.length][x.length];
		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < x.length; j++)
				tabuSwap[i][j] = -1;
		int[] x_best = new int[x.length];
		int best = S.violations();
		int it = 0;
		while (iter < maxIter && System.currentTimeMillis() - t0 < maxTime
				&& S.violations() > 0) {
			moves.clear();
			int minDelta = 1000000000;
			for (int i = 0; i < x.length; i++) {
				for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
					int d = S.getAssignDelta(x[i], v);
					if (tabuAssign[i][v] <= it || d + S.violations() < best) {
						if (minDelta > d) {
							minDelta = d;
							moves.clear();
							moves.add(new OneVariableValueMove(
									MoveType.OneVariableValueAssignment,
									minDelta, x[i], v));
						} else if (minDelta == d) {
							moves.add(new OneVariableValueMove(
									MoveType.OneVariableValueAssignment,
									minDelta, x[i], v));
						}
					}
				}
			}
			for (int i = 0; i < x.length - 1; i++) {
				for (int j = i + 1; j < x.length; j++) if(equalDomain[i][j]){
					int d = S.getSwapDelta(x[i], x[j]);
					if (tabuSwap[i][j] <= it || d + S.violations() < best) {
						if (minDelta > d) {
							minDelta = d;
							moves.clear();
							moves.add(new TwoVariablesSwapMove(
									MoveType.TwoVariablesSwap, minDelta, x[i],
									x[j]));
						} else if (minDelta == d) {
							moves.add(new TwoVariablesSwapMove(
									MoveType.TwoVariablesSwap, minDelta, x[i],
									x[j]));
						}
					}
				}
			}
			if (moves.size() > 0) {
				Move move = moves.get(rand.nextInt(moves.size()));
				if (move.getType() == MoveType.OneVariableValueAssignment) {
					OneVariableValueMove m1 = (OneVariableValueMove) move;
					VarIntLS sel_x = m1.getVariable();
					int sel_v = m1.getValue();
					sel_x.setValuePropagate(sel_v);
					tabuAssign[map.get(sel_x)][sel_v - minV] = it + tbl;
					System.out.println(name()
							+ "::searchAssignSwap move assign x["
							+ sel_x.getID() + "] to " + sel_v + ", S = "
							+ S.violations() + ", best = " + best);
				} else if (move.getType() == MoveType.TwoVariablesSwap) {
					TwoVariablesSwapMove m2 = (TwoVariablesSwapMove) move;
					VarIntLS xi = m2.getVar1();
					VarIntLS xj = m2.getVar2();
					int tmp = xi.getValue();
					xi.setValuePropagate(xj.getValue());
					xj.setValuePropagate(tmp);
					tabuSwap[map.get(xi)][map.get(xj)] = it + tbl;
					System.out.println(name()
							+ "::searchAssignSwap move swap x[" + xi.getID()
							+ "] and x[" + xj.getID() + "], S = "
							+ S.violations() + ", best = " + best);
				} else {
					System.out
							.println("TabuSearch::greedySearchMinMultiObjectives -> exception, unknown move");
					assert (false);
				}
				if (S.violations() < best) {
					best = S.violations();
					for (int i = 0; i < x.length; i++)
						x_best[i] = x[i].getValue();
				}

			} else {
				for (int i = 0; i < x.length; i++) {
					int d = x[i].getMaxValue() - x[i].getMinValue() + 1;
					int v = rand.nextInt() % d;
					if (v < 0)
						v = -v;
					v = x[i].getMinValue() + v;
					x[i].setValuePropagate(v);
				}
				for (int i = 0; i < tabuAssign.length; i++) {
					for (int j = 0; j < tabuAssign[i].length; j++)
						tabuAssign[i][j] = -1;
				}
				for (int i = 0; i < tabuSwap.length; i++) {
					for (int j = 0; j < tabuSwap[i].length; j++)
						tabuSwap[i][j] = -1;
				}
			}
			iter++;
		}

		for (int i = 0; i < x.length; i++)
			x[i].setValuePropagate(x_best[i]);
	}

	public String name() {
		return "TabuSearch";
	}

	private void searchMaintainConstraintsFunction(IFunction f1, IFunction f2,
			IConstraint S, int tabulen, int maxTime, int maxIter, int maxStable) {
		IFunction[] tf = new IFunction[1];
		tf[0] = f2;
		searchMaintainConstraintsFunction(f1, tf, S, tabulen, maxTime, maxIter,
				maxStable);
	}

	private void searchMaintainConstraintsFunction(IFunction f1, IFunction[] f2,
			IConstraint S, int tabulen, int maxTime, int maxIter, int maxStable) {

		t0 = System.currentTimeMillis();
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		/*
		 * VarIntLS[] x=S.getVariables(); HashMap<VarIntLS, Integer> map=new
		 * HashMap<VarIntLS, Integer>(); for(int i=0;i<x.length;i++) {
		 * map.put(x[i], i); }
		 */

		VarIntLS[] a = S.getVariables();
		HashMap<VarIntLS, Integer> map = new HashMap<VarIntLS, Integer>();
		for (int i = 0; i < a.length; i++) {

			_S.add(a[i]);
		}
		VarIntLS[] b = f1.getVariables();
		if (b != null) {
			for (int i = 0; i < b.length; i++) {

				_S.add(b[i]);
			}
		}

		for (int i = 0; i < f2.length; i++) {
			VarIntLS[] c = f2[i].getVariables();

			if (c != null) {
				for (int j = 0; j < c.length; j++) {
					_S.add(c[j]);

				}
			}

		}
		VarIntLS[] x;
		x = new VarIntLS[_S.size()];
		int g1 = 0;
		for (VarIntLS e : _S) {
			x[g1] = e;
			g1++;
		}
		for (int i = 0; i < x.length; i++) {
			map.put(x[i], i);
		}

		int n = x.length;
		int maxV = -1000000;
		int minV = 1000000;
		for (int i = 0; i < n; i++) {
			if (maxV < x[i].getMaxValue())
				maxV = x[i].getMaxValue();
			if (minV > x[i].getMinValue())
				minV = x[i].getMinValue();
		}
		System.out.println("minV  =   " + minV + "  maxV   =  " + maxV);
		int D = maxV - minV;

		int tabu[][] = new int[n][D + 1];
		for (int i = 0; i < n; i++)
			for (int v = 0; v <= D; v++)
				tabu[i][v] = -1;

		int it = 0;
		maxTime = maxTime * 1000;// convert into milliseconds
		double t0 = System.currentTimeMillis();
		int best = f1.getValue();
		int[] x_best = new int[x.length];
		for (int i = 0; i < x.length; i++)
			x_best[i] = x[i].getValue();

		System.out.println("TabuSearch, init S = " + S.violations());
		int nic = 0;
		ArrayList<OneVariableValueMove> moves = new ArrayList<OneVariableValueMove>();
		Random R = new Random();

		while (it < maxIter && System.currentTimeMillis() - t0 < maxTime) {
			int sel_i = -1;
			int sel_v = -1;
			int minDelta = 100000000;
			moves.clear();
			for (int i = 0; i < n; i++) {
				for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
					int deltaS = S.getAssignDelta(x[i], v);
					int[] deltaF2 = new int[f2.length];
					for (int t = 0; t < f2.length; t++) {
						deltaF2[t] = f2[t].getAssignDelta(x[i], v);
					}
					Arrays.sort(deltaF2);
					int deltaF = f1.getAssignDelta(x[i], v);

					if (deltaS <= 0 && deltaF2[f2.length - 1] <= 0)
						if (tabu[i][v - minV] <= it
								|| f1.getValue() + deltaF < best) {
							if (deltaF < minDelta) {
								minDelta = deltaF;
								sel_i = i;
								sel_v = v;
								moves.clear();
								moves.add(new OneVariableValueMove(
										MoveType.OneVariableValueAssignment,
										minDelta, x[i], v));
							} else if (deltaF == minDelta) {
								moves.add(new OneVariableValueMove(
										MoveType.OneVariableValueAssignment,
										minDelta, x[i], v));
							}
						}
				}
			}

			if (moves.size() <= 0) {
				System.out.println("TabuSearch::restart.....");
				restartMaintainConstraintFunction(x, S, f2, tabu);
				nic = 0;
			} else {
				// perform the move
				if (moves.size() > 0) {
					OneVariableValueMove m = moves.get(R.nextInt(moves.size()));

					sel_i = map.get(m.getVariable());
					sel_v = m.getValue();
					if (sel_v >= 0) {

						x[sel_i].setValuePropagate(sel_v);
					}
				}

				if (sel_v >= 0) {

					tabu[sel_i][sel_v - minV] = it + tabulen;
				}
				System.out.println("Step " + it + ", S = " + S.violations()
						+ "   f2[0]   =     " + f2[0].getValue() + ", f1 = "
						+ f1.getValue() + ", best = " + best + ", delta = "
						+ minDelta + ", nic = " + nic);
				// update best
				if (f1.getValue() < best) {
					best = f1.getValue();
					for (int i = 0; i < x.length; i++)
						x_best[i] = x[i].getValue();
					updateBest();
					t_best = System.currentTimeMillis() - t0;
				}

				//if (minDelta >= 0) {
				if(f1.getValue() >= best){
					nic++;
					if (nic > maxStable) {
						System.out.println("TabuSearch::restart.....");
						restartMaintainConstraintFunction(x, S, f2, tabu);
						nic = 0;
					}
				} else {
					nic = 0;
				}
			}
			it++;
		}
		for (int i = 0; i < x.length; i++)
			x[i].setValuePropagate(x_best[i]);

	}

	public void searchMaintainConstraintsFunctionMinimize(IFunction f1,
			IFunction f2, IConstraint S, int tabulen, int maxTime,
			int maxIter, int maxStable) {
		IFunction[] tf = new IFunction[1];
		tf[0] = f2;
		
		searchMaintainConstraintsFunctionMinimize(f1, tf, S, tabulen, maxTime, maxIter, maxStable);
	}
	
	public void searchMaintainConstraintsFunctionMinimize(IFunction f1,
			IFunction[] f2, IConstraint S, int tabulen, int maxTime,
			int maxIter, int maxStable) {

		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		/*
		 * VarIntLS[] x=S.getVariables(); HashMap<VarIntLS, Integer> map=new
		 * HashMap<VarIntLS, Integer>(); for(int i=0;i<x.length;i++) {
		 * map.put(x[i], i); }
		 */

		VarIntLS[] a = S.getVariables();
		HashMap<VarIntLS, Integer> map = new HashMap<VarIntLS, Integer>();
		for (int i = 0; i < a.length; i++) {

			_S.add(a[i]);
		}
		VarIntLS[] b = f1.getVariables();
		if (b != null) {
			for (int i = 0; i < b.length; i++) {

				_S.add(b[i]);
			}
		}

		for (int i = 0; i < f2.length; i++) {
			VarIntLS[] c = f2[i].getVariables();

			if (c != null) {
				for (int j = 0; j < c.length; j++) {
					_S.add(c[j]);

				}
			}

		}
		VarIntLS[] x;
		x = new VarIntLS[_S.size()];
		int g1 = 0;
		for (VarIntLS e : _S) {
			x[g1] = e;
			g1++;
		}
		for (int i = 0; i < x.length; i++) {
			map.put(x[i], i);
		}

		int n = x.length;
		int maxV = -1000000;
		int minV = 1000000;
		for (int i = 0; i < n; i++) {
			if (maxV < x[i].getMaxValue())
				maxV = x[i].getMaxValue();
			if (minV > x[i].getMinValue())
				minV = x[i].getMinValue();
		}
		// System.out.println("minV  =   "+minV+"  maxV   =  "+maxV);
		int D = maxV - minV;

		int tabu[][] = new int[n][D + 1];
		for (int i = 0; i < n; i++)
			for (int v = 0; v <= D; v++)
				tabu[i][v] = -1;

		int it = 0;
		maxTime = maxTime * 1000;// convert into milliseconds
		double t0 = System.currentTimeMillis();
		int best = f1.getValue();
		int[] x_best = new int[x.length];
		for (int i = 0; i < x.length; i++)
			x_best[i] = x[i].getValue();

		System.out.println(name()
				+ "::searchMaintainConstraintsFunctionMinimize, init S = "
				+ S.violations());
		int nic = 0;
		ArrayList<OneVariableValueMove> moves = new ArrayList<OneVariableValueMove>();
		Random R = new Random();

		while (it < maxIter && System.currentTimeMillis() - t0 < maxTime) {
			int sel_i = -1;
			int sel_v = -1;
			int minDelta = 100000000;
			moves.clear();
			for (int i = 0; i < n; i++) {
				for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
					int deltaS = S.getAssignDelta(x[i], v);
					int[] deltaF2 = new int[f2.length];
					for (int t = 0; t < f2.length; t++) {
						deltaF2[t] = f2[t].getAssignDelta(x[i], v);
					}
					Arrays.sort(deltaF2);
					int deltaF = f1.getAssignDelta(x[i], v);

					if (deltaS <= 0 && deltaF2[f2.length - 1] <= 0)
						if (tabu[i][v - minV] <= it
								|| f1.getValue() + deltaF < best) {
							if (deltaF < minDelta) {
								minDelta = deltaF;
								sel_i = i;
								sel_v = v;
								moves.clear();
								moves.add(new OneVariableValueMove(
										MoveType.OneVariableValueAssignment,
										minDelta, x[i], v));
							} else if (deltaF == minDelta) {
								moves.add(new OneVariableValueMove(
										MoveType.OneVariableValueAssignment,
										minDelta, x[i], v));
							}
						}
				}
			}

			if (moves.size() <= 0) {
				System.out
						.println(name()
								+ "::searchMaintainConstraintsFunctionMinimize --> restart.....");
				restartMaintainConstraintFunction(x, S, f2, tabu);
				nic = 0;
			} else {
				// perform the move
				if (moves.size() > 0) {
					OneVariableValueMove m = moves.get(R.nextInt(moves.size()));

					sel_i = map.get(m.getVariable());
					sel_v = m.getValue();
					if (sel_v >= 0) {

						x[sel_i].setValuePropagate(sel_v);
					}
				}

				if (sel_v >= 0) {

					tabu[sel_i][sel_v - minV] = it + tabulen;
				}
				System.out.println(name()
						+ "::searchMaintainConstraintsFunctionMinimize, Step "
						+ it + ", S = " + S.violations() + "   f2[0]   =     "
						+ f2[0].getValue() + ", f1 = " + f1.getValue()
						+ ", best = " + best + ", delta = " + minDelta
						+ ", nic = " + nic);
				// update best
				if (f1.getValue() < best) {
					best = f1.getValue();
					for (int i = 0; i < x.length; i++)
						x_best[i] = x[i].getValue();
					updateBest();
					t_best = System.currentTimeMillis() - t0;
				}

				//if (minDelta >= 0) {
				if(f1.getValue() >= best){
					nic++;
					if (nic > maxStable) {
						System.out
								.println(name()
										+ "::searchMaintainConstraintsFunctionMinimize --> restart.....");
						restartMaintainConstraintFunction(x, S, f2, tabu);
						nic = 0;
					}
				} else {
					nic = 0;
				}
			}
			it++;
		}
		for (int i = 0; i < x.length; i++)
			x[i].setValuePropagate(x_best[i]);

	}

	public void updateBest(){
		
	}
	public void searchMaintainConstraintsFunctionMaximize(IFunction f1,
			IFunction f2, IConstraint S, int tabulen, int maxTime,
			int maxIter, int maxStable) {
		IFunction[] tf = new IFunction[1];
		tf[0] = f2;
		searchMaintainConstraintsFunctionMaximize(f1, tf, S, tabulen, maxTime, maxIter, maxStable);
	}
	
	public void searchMaintainConstraintsFunctionMaximize(IFunction f1,
			IFunction[] f2, IConstraint S, int tabulen, int maxTime,
			int maxIter, int maxStable) {
		
		t0 = System.currentTimeMillis();
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		/*
		 * VarIntLS[] x=S.getVariables(); HashMap<VarIntLS, Integer> map=new
		 * HashMap<VarIntLS, Integer>(); for(int i=0;i<x.length;i++) {
		 * map.put(x[i], i); }
		 */

		VarIntLS[] a = S.getVariables();
		HashMap<VarIntLS, Integer> map = new HashMap<VarIntLS, Integer>();
		for (int i = 0; i < a.length; i++) {

			_S.add(a[i]);
		}
		VarIntLS[] b = f1.getVariables();
		if (b != null) {
			for (int i = 0; i < b.length; i++) {

				_S.add(b[i]);
			}
		}

		for (int i = 0; i < f2.length; i++) {
			VarIntLS[] c = f2[i].getVariables();

			if (c != null) {
				for (int j = 0; j < c.length; j++) {
					_S.add(c[j]);
				}
			}

		}
		VarIntLS[] x;
		x = new VarIntLS[_S.size()];
		int g1 = 0;
		for (VarIntLS e : _S) {
			x[g1] = e;
			g1++;
		}
		for (int i = 0; i < x.length; i++) {
			map.put(x[i], i);
		}

		int n = x.length;
		int maxV = -1000000;
		int minV = 1000000;
		for (int i = 0; i < n; i++) {
			if (maxV < x[i].getMaxValue())
				maxV = x[i].getMaxValue();
			if (minV > x[i].getMinValue())
				minV = x[i].getMinValue();
		}
		// System.out.println("minV  =   "+minV+"  maxV   =  "+maxV);
		int D = maxV - minV;

		int tabu[][] = new int[n][D + 1];
		for (int i = 0; i < n; i++)
			for (int v = 0; v <= D; v++)
				tabu[i][v] = -1;

		int it = 0;
		maxTime = maxTime * 1000;// convert into milliseconds
		double t0 = System.currentTimeMillis();
		int best = f1.getValue();
		int[] x_best = new int[x.length];
		for (int i = 0; i < x.length; i++)
			x_best[i] = x[i].getValue();

		System.out.println(name()
				+ "::searchMaintainConstraintsFunctionMaximize, init S = "
				+ S.violations());
		int nic = 0;
		ArrayList<OneVariableValueMove> moves = new ArrayList<OneVariableValueMove>();
		Random R = new Random();

		while (it < maxIter && System.currentTimeMillis() - t0 < maxTime) {
			int sel_i = -1;
			int sel_v = -1;
			int maxDelta = -100000000;
			moves.clear();
			for (int i = 0; i < n; i++) {
				for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
					int deltaS = S.getAssignDelta(x[i], v);
					int[] deltaF2 = new int[f2.length];
					for (int t = 0; t < f2.length; t++) {
						deltaF2[t] = f2[t].getAssignDelta(x[i], v);
					}
					// Arrays.sort(deltaF2);
					boolean deltaF2NotChange = true;
					for (int ii = 0; ii < deltaF2.length; ii++)
						if (deltaF2[ii] != 0)
							deltaF2NotChange = false;

					int deltaF = f1.getAssignDelta(x[i], v);

					if (deltaS <= 0 && deltaF2NotChange)
						if (tabu[i][v - minV] <= it
								|| f1.getValue() + deltaF > best) {
							if (deltaF > maxDelta) {
								maxDelta = deltaF;
								sel_i = i;
								sel_v = v;
								moves.clear();
								moves.add(new OneVariableValueMove(
										MoveType.OneVariableValueAssignment,
										maxDelta, x[i], v));
							} else if (deltaF == maxDelta) {
								moves.add(new OneVariableValueMove(
										MoveType.OneVariableValueAssignment,
										maxDelta, x[i], v));
							}
						}
				}
			}

			if (moves.size() <= 0) {
				System.out
						.println(name()
								+ "::searchMaintainConstraintsFunctionMaximize --> restart.....");
				restartMaintainConstraintFunction(x, S, f2, tabu);
				nic = 0;
			} else {
				// perform the move
				if (moves.size() > 0) {
					OneVariableValueMove m = moves.get(R.nextInt(moves.size()));

					sel_i = map.get(m.getVariable());
					sel_v = m.getValue();
					if (sel_v >= 0) {

						x[sel_i].setValuePropagate(sel_v);
					}
				}

				if (sel_v >= 0) {

					tabu[sel_i][sel_v - minV] = it + tabulen;
				}
				System.out.println(name()
						+ "::searchMaintainConstraintsFunctionMaximize, Step "
						+ it + ", S = " + S.violations() + "   f2[0]   =     "
						+ f2[0].getValue() + ", f1 = " + f1.getValue()
						+ ", best = " + best + ", delta = " + maxDelta
						+ ", nic = " + nic);
				// update best
				if (f1.getValue() > best) {
					best = f1.getValue();
					for (int i = 0; i < x.length; i++)
						x_best[i] = x[i].getValue();
					updateBest();
					t_best = System.currentTimeMillis() - t0;
				}

				//if (maxDelta < 0) {
				if(f1.getValue() <= best){
					nic++;
					if (nic > maxStable) {
						System.out
								.println(name()
										+ "::searchMaintainConstraintsFunctionMaximize --> restart.....");
						restartMaintainConstraintFunction(x, S, f2, tabu);
						nic = 0;
					}
				} else {
					nic = 0;
				}
			}
			it++;
		}
		for (int i = 0; i < x.length; i++)
			x[i].setValuePropagate(x_best[i]);

	}

	public void searchMaintainConstraintsMinimize(IFunction f, IConstraint S,
			int tabulen, int maxTime, int maxIter, int maxStable) {
		double t0 = System.currentTimeMillis();

		VarIntLS[] x = S.getVariables();
		HashMap<VarIntLS, Integer> map = new HashMap<VarIntLS, Integer>();
		for (int i = 0; i < x.length; i++)
			map.put(x[i], i);

		int n = x.length;
		int maxV = -1000000000;
		int minV = 100000000;
		for (int i = 0; i < n; i++) {
			if (maxV < x[i].getMaxValue())
				maxV = x[i].getMaxValue();
			if (minV > x[i].getMinValue())
				minV = x[i].getMinValue();
		}
		int D = maxV - minV;
		int tabu[][] = new int[n][D + 1];
		for (int i = 0; i < n; i++)
			for (int v = 0; v <= D; v++)
				tabu[i][v] = -1;

		int it = 0;
		maxTime = maxTime * 1000;// convert into milliseconds

		int best = f.getValue();
		int[] x_best = new int[x.length];
		for (int i = 0; i < x.length; i++)
			x_best[i] = x[i].getValue();

		System.out.println(name()
				+ "::searchMaintainConstraintsMinimize, init S = "
				+ S.violations());
		int nic = 0;
		ArrayList<OneVariableValueMove> moves = new ArrayList<OneVariableValueMove>();
		Random R = new Random();

		while (it < maxIter && System.currentTimeMillis() - t0 < maxTime) {
			int sel_i = -1;
			int sel_v = -1;
			int minDelta = 10000000;
			moves.clear();
			for (int i = 0; i < n; i++) {
				for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
					int deltaS = S.getAssignDelta(x[i], v);
					int deltaF = f.getAssignDelta(x[i], v);
					// System.out.println("min  =   "+x[i].getMinValue()+"   max =     "+x[i].getMaxValue());
					/*
					 * Accept moves that are not tabu or they are better than
					 * the best solution found so far (best)
					 */
					if (deltaS <= 0)
						if (tabu[i][v - minV] <= it
								|| f.getValue() + deltaF < best) {
							if (deltaF < minDelta) {
								minDelta = deltaF;
								sel_i = i;
								sel_v = v;
								moves.clear();
								moves.add(new OneVariableValueMove(
										MoveType.OneVariableValueAssignment,
										minDelta, x[i], v));
							} else if (deltaF == minDelta) {
								moves.add(new OneVariableValueMove(
										MoveType.OneVariableValueAssignment,
										minDelta, x[i], v));
							}
						}
				}
			}

			// perform the move
			if (moves.size() <= 0) {
				System.out
						.println(name()
								+ "::searchMaintainConstraintsMinimize --> restart.....");
				restartMaintainConstraint(x, S, tabu);
				nic = 0;
			} else {
				OneVariableValueMove m = moves.get(R.nextInt(moves.size()));
				sel_i = map.get(m.getVariable());
				sel_v = m.getValue();
				x[sel_i].setValuePropagate(sel_v);
				tabu[sel_i][sel_v - minV] = it + tabulen;
				System.out.println(name()
						+ "::searchMaintainConstraintsMinimize, Step " + it
						+ ", S = " + S.violations() + ", f = " + f.getValue()
						+ ", best = " + best + ", delta = " + minDelta
						+ ", nic = " + nic);
				// update best
				if (f.getValue() < best) {
					best = f.getValue();
					for (int i = 0; i < x.length; i++)
						x_best[i] = x[i].getValue();
					updateBest();
					t_best = System.currentTimeMillis() - t0;
				}

				//if (minDelta >= 0) {
				if(f.getValue() >= best){
					nic++;
					if (nic > maxStable) {
						System.out
								.println(name()
										+ "::searchMaintainConstraintsMinimize  -> restart.....");
						restartMaintainConstraint(x, S, tabu);
						nic = 0;
					}
				} else {
					nic = 0;
				}
			}
			it++;
		}
		for (int i = 0; i < x.length; i++)
			x[i].setValuePropagate(x_best[i]);

	}

	public void searchMaintainConstraintsMaximize(IFunction f, IConstraint S,
			int tabulen, int maxTime, int maxIter, int maxStable) {
		double t0 = System.currentTimeMillis();

		VarIntLS[] x = S.getVariables();
		HashMap<VarIntLS, Integer> map = new HashMap<VarIntLS, Integer>();
		for (int i = 0; i < x.length; i++)
			map.put(x[i], i);

		int n = x.length;
		int maxV = -1000000000;
		int minV = 100000000;
		for (int i = 0; i < n; i++) {
			if (maxV < x[i].getMaxValue())
				maxV = x[i].getMaxValue();
			if (minV > x[i].getMinValue())
				minV = x[i].getMinValue();
		}
		int D = maxV - minV;
		int tabu[][] = new int[n][D + 1];
		for (int i = 0; i < n; i++)
			for (int v = 0; v <= D; v++)
				tabu[i][v] = -1;

		int it = 0;
		maxTime = maxTime * 1000;// convert into milliseconds

		int best = f.getValue();
		int[] x_best = new int[x.length];
		for (int i = 0; i < x.length; i++)
			x_best[i] = x[i].getValue();

		System.out.println(name()
				+ "::searchMaintainConstraintsMaximize, init S = "
				+ S.violations());
		int nic = 0;
		ArrayList<OneVariableValueMove> moves = new ArrayList<OneVariableValueMove>();
		Random R = new Random();

		while (it < maxIter && System.currentTimeMillis() - t0 < maxTime) {
			int sel_i = -1;
			int sel_v = -1;
			int maxDelta = -100000000;
			moves.clear();
			for (int i = 0; i < n; i++) {
				for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
					int deltaS = S.getAssignDelta(x[i], v);
					int deltaF = f.getAssignDelta(x[i], v);
					// System.out.println("min  =   "+x[i].getMinValue()+"   max =     "+x[i].getMaxValue());
					/*
					 * Accept moves that are not tabu or they are better than
					 * the best solution found so far (best)
					 */
					if (deltaS <= 0)
						if (tabu[i][v - minV] <= it
								|| f.getValue() + deltaF > best) {
							if (deltaF > maxDelta) {
								maxDelta = deltaF;
								sel_i = i;
								sel_v = v;
								moves.clear();
								moves.add(new OneVariableValueMove(
										MoveType.OneVariableValueAssignment,
										maxDelta, x[i], v));
							} else if (deltaF == maxDelta) {
								moves.add(new OneVariableValueMove(
										MoveType.OneVariableValueAssignment,
										maxDelta, x[i], v));
							}
						}
				}
			}

			// perform the move
			if (moves.size() <= 0) {
				System.out
						.println(name()
								+ "::searchMaintainConstraintsMaximize --> restart .....");
				restartMaintainConstraint(x, S, tabu);
				nic = 0;
			} else {
				OneVariableValueMove m = moves.get(R.nextInt(moves.size()));
				sel_i = map.get(m.getVariable());
				sel_v = m.getValue();
				x[sel_i].setValuePropagate(sel_v);
				tabu[sel_i][sel_v - minV] = it + tabulen;
				System.out.println(name()
						+ "::searchMaintainConstraintsMaximize, Step " + it
						+ ", S = " + S.violations() + ", f = " + f.getValue()
						+ ", best = " + best + ", delta = " + maxDelta
						+ ", nic = " + nic);
				// update best
				if (f.getValue() > best) {
					best = f.getValue();
					for (int i = 0; i < x.length; i++)
						x_best[i] = x[i].getValue();
					updateBest();
					t_best = System.currentTimeMillis() - t0;
				}

				//if (maxDelta <= 0) {
				if(f.getValue() <= best){
					nic++;
					if (nic > maxStable) {
						System.out
								.println(name()
										+ "::searchMaintainConstraintsMaximize --> restart.....");
						restartMaintainConstraint(x, S, tabu);
						nic = 0;
					}
				} else {
					nic = 0;
				}
			}
			it++;
		}
		for (int i = 0; i < x.length; i++)
			x[i].setValuePropagate(x_best[i]);
	}

	private void searchMaintainConstraints(IFunction f, IConstraint S,
			int tabulen, int maxTime, int maxIter, int maxStable) {
		double t0 = System.currentTimeMillis();

		VarIntLS[] x = S.getVariables();
		HashMap<VarIntLS, Integer> map = new HashMap<VarIntLS, Integer>();
		for (int i = 0; i < x.length; i++)
			map.put(x[i], i);

		int n = x.length;
		int maxV = -1000000000;
		int minV = 100000000;
		for (int i = 0; i < n; i++) {
			if (maxV < x[i].getMaxValue())
				maxV = x[i].getMaxValue();
			if (minV > x[i].getMinValue())
				minV = x[i].getMinValue();
		}
		int D = maxV - minV;
		int tabu[][] = new int[n][D + 1];
		for (int i = 0; i < n; i++)
			for (int v = 0; v <= D; v++)
				tabu[i][v] = -1;

		int it = 0;
		maxTime = maxTime * 1000;// convert into milliseconds

		int best = f.getValue();
		int[] x_best = new int[x.length];
		for (int i = 0; i < x.length; i++)
			x_best[i] = x[i].getValue();

		System.out.println("TabuSearch, init S = " + S.violations());
		int nic = 0;
		ArrayList<OneVariableValueMove> moves = new ArrayList<OneVariableValueMove>();
		Random R = new Random();

		while (it < maxIter && System.currentTimeMillis() - t0 < maxTime) {
			int sel_i = -1;
			int sel_v = -1;
			int minDelta = 10000000;
			moves.clear();
			for (int i = 0; i < n; i++) {
				for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
					int deltaS = S.getAssignDelta(x[i], v);
					int deltaF = f.getAssignDelta(x[i], v);
					// System.out.println("min  =   "+x[i].getMinValue()+"   max =     "+x[i].getMaxValue());
					/*
					 * Accept moves that are not tabu or they are better than
					 * the best solution found so far (best)
					 */
					if (deltaS <= 0)
						if (tabu[i][v - minV] <= it
								|| f.getValue() + deltaF < best) {
							if (deltaF < minDelta) {
								minDelta = deltaF;
								sel_i = i;
								sel_v = v;
								moves.clear();
								moves.add(new OneVariableValueMove(
										MoveType.OneVariableValueAssignment,
										minDelta, x[i], v));
							} else if (deltaF == minDelta) {
								moves.add(new OneVariableValueMove(
										MoveType.OneVariableValueAssignment,
										minDelta, x[i], v));
							}
						}
				}
			}

			// perform the move
			if (moves.size() <= 0) {
				System.out.println("TabuSearch::restart.....");
				restartMaintainConstraint(x, S, tabu);
				nic = 0;
			} else {
				OneVariableValueMove m = moves.get(R.nextInt(moves.size()));
				sel_i = map.get(m.getVariable());
				sel_v = m.getValue();
				x[sel_i].setValuePropagate(sel_v);
				tabu[sel_i][sel_v - minV] = it + tabulen;
				System.out.println("Step " + it + ", S = " + S.violations()
						+ ", f = " + f.getValue() + ", best = " + best
						+ ", delta = " + minDelta + ", nic = " + nic);
				// update best
				if (f.getValue() < best) {
					best = f.getValue();
					for (int i = 0; i < x.length; i++)
						x_best[i] = x[i].getValue();
					updateBest();
					t_best = System.currentTimeMillis() - t0;
				}

				//if (minDelta >= 0) {
				if(f.getValue() >= best){
					nic++;
					if (nic > maxStable) {
						System.out.println("TabuSearch::restart.....");
						restartMaintainConstraint(x, S, tabu);
						nic = 0;
					}
				} else {
					nic = 0;
				}
			}
			it++;
		}
		for (int i = 0; i < x.length; i++)
			x[i].setValuePropagate(x_best[i]);
	}

	public void search(IConstraint S, int tabulen, int maxTime, int maxIter,
			int maxStable) {
		double t0 = System.currentTimeMillis();

		VarIntLS[] x = S.getVariables();
		HashMap<VarIntLS, Integer> map = new HashMap<VarIntLS, Integer>();
		for (int i = 0; i < x.length; i++)
			map.put(x[i], i);

		int n = x.length;
		int maxV = -1000000000;
		int minV = 1000000000;
		for (int i = 0; i < n; i++) {
			if (maxV < x[i].getMaxValue())
				maxV = x[i].getMaxValue();
			if (minV > x[i].getMinValue())
				minV = x[i].getMinValue();
		}
		int D = maxV - minV;
		// System.out.println("n = " + n + ", D = " + D);
		int tabu[][] = new int[n][D + 1];
		for (int i = 0; i < n; i++)
			for (int v = 0; v <= D; v++)
				tabu[i][v] = -1;

		int it = 0;
		maxTime = maxTime * 1000;// convert into milliseconds

		int best = S.violations();
		int[] x_best = new int[x.length];
		for (int i = 0; i < x.length; i++)
			x_best[i] = x[i].getValue();

		System.out.println("TabuSearch, init S = " + S.violations());
		int nic = 0;
		ArrayList<OneVariableValueMove> moves = new ArrayList<OneVariableValueMove>();
		Random R = new Random();
		while (it < maxIter && System.currentTimeMillis() - t0 < maxTime
				&& S.violations() > 0) {
			int sel_i = -1;
			int sel_v = -1;
			int minDelta = 10000000;
			moves.clear();
			for (int i = 0; i < n; i++) {
				for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
					int delta = S.getAssignDelta(x[i], v);
					// System.out.println("min  =   "+x[i].getMinValue()+"   max =     "+x[i].getMaxValue());
					/*
					 * Accept moves that are not tabu or they are better than
					 * the best solution found so far (best)
					 */
					if (tabu[i][v - minV] <= it
							|| S.violations() + delta < best) {
						if (delta < minDelta) {
							minDelta = delta;
							sel_i = i;
							sel_v = v;
							moves.clear();
							moves.add(new OneVariableValueMove(
									MoveType.OneVariableValueAssignment,
									minDelta, x[i], v));
						} else if (delta == minDelta) {
							moves.add(new OneVariableValueMove(
									MoveType.OneVariableValueAssignment,
									minDelta, x[i], v));
						}
					}
				}
			}

			if (moves.size() <= 0) {
				System.out.println("TabuSearch::restart.....");
				restartMaintainConstraint(x, S, tabu);
				if(S.violations() == 0){
				best = S.violations();
				for (int i = 0; i < x.length; i++)
					x_best[i] = x[i].getValue();
				}
				// restart(x,tabu);
				nic = 0;
			} else {
				// perform the move
				OneVariableValueMove m = moves.get(R.nextInt(moves.size()));
				sel_i = map.get(m.getVariable());
				sel_v = m.getValue();
				x[sel_i].setValuePropagate(sel_v);
				tabu[sel_i][sel_v - minV] = it + tabulen;

				System.out.println("Step " + it + ", S = " + S.violations()
						+ ", best = " + best + ", delta = " + minDelta
						+ ", nic = " + nic);
				// update best
				if (S.violations() < best) {
					best = S.violations();
					for (int i = 0; i < x.length; i++)
						x_best[i] = x[i].getValue();
					updateBest();
					t_best = System.currentTimeMillis() - t0;
				}

				//if (minDelta >= 0) {
				if(S.violations() >= best){
					nic++;
					if (nic > maxStable) {
						System.out.println("TabuSearch::restart.....");
						restartMaintainConstraint(x, S, tabu);
						nic = 0;
					}
				} else {
					nic = 0;
				}
			}
			it++;
		}
		for (int i = 0; i < x.length; i++)
			x[i].setValuePropagate(x_best[i]);
	}

	private void restart(VarIntLS[] x, int[][] tabu) {

		for (int i = 0; i < x.length; i++) {
			int d = x[i].getMaxValue() - x[i].getMinValue() + 1;
			int v = rand.nextInt() % d;
			if (v < 0)
				v = -v;
			v = x[i].getMinValue() + v;
			x[i].setValuePropagate(v);
		}
		for (int i = 0; i < tabu.length; i++) {
			for (int j = 0; j < tabu[i].length; j++)
				tabu[i][j] = -1;
		}
	}

	private void restartMaintainConstraint(VarIntLS[] x, IConstraint S,
			int[][] tabu) {

		for (int i = 0; i < x.length; i++) {
			java.util.ArrayList<Integer> L = new java.util.ArrayList<Integer>();
			for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
				if (S.getAssignDelta(x[i], v) <= 0)
					L.add(v);
			}
			int idx = rand.nextInt(L.size());
			int v = L.get(idx);
			x[i].setValuePropagate(v);
		}
		for (int i = 0; i < tabu.length; i++) {
			for (int j = 0; j < tabu[i].length; j++)
				tabu[i][j] = -1;
		}
		
	}
	private void restartMaintainConstraintFunction(VarIntLS[] x, IConstraint S, IFunction[] f,
			int[][] tabu) {

		
		for (int i = 0; i < x.length; i++) {
			java.util.ArrayList<Integer> L = new java.util.ArrayList<Integer>();
			for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
				boolean ok = true;
				for(int j = 0; j < f.length; j++) if(f[j].getAssignDelta(x[i], v) != 0){
					ok = false;break;
				}
				
				if (S.getAssignDelta(x[i], v) <= 0 && ok)
					L.add(v);
			}
			int idx = rand.nextInt(L.size());
			int v = L.get(idx);
			x[i].setValuePropagate(v);
		}
		for (int i = 0; i < tabu.length; i++) {
			for (int j = 0; j < tabu[i].length; j++)
				tabu[i][j] = -1;
		}
	}
	private void restartMaintainConstraintFunction(VarIntLS[] x, IConstraint S, IFunction f,
			int[][] tabu) {

		for (int i = 0; i < x.length; i++) {
			java.util.ArrayList<Integer> L = new java.util.ArrayList<Integer>();
			for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
				boolean ok = f.getAssignDelta(x[i], v) == 0;
				
				if (S.getAssignDelta(x[i], v) <= 0 && ok)
					L.add(v);
			}
			int idx = rand.nextInt(L.size());
			int v = L.get(idx);
			x[i].setValuePropagate(v);
		}
		for (int i = 0; i < tabu.length; i++) {
			for (int j = 0; j < tabu[i].length; j++)
				tabu[i][j] = -1;
		}
	}
	//public double getTimeBest(){
	//	return t_best*0.001;
	//}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
