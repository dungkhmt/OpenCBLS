package localsearch.applications.examplebook;

import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;

public class BinPacking2D {

	public void tabuSearchBinPacking2D(){
		int W = 10;
		int H = 7;
		int n = 6;
		int[] w = new int[]{6, 5, 2, 3, 3, 2};
		int[] h = new int[]{3, 2, 4, 4, 3, 1};
		
		LocalSearchManager mgr = new LocalSearchManager();
		ConstraintSystem S = new ConstraintSystem(mgr);
		VarIntLS[] x = new VarIntLS[n];
		VarIntLS[] y = new VarIntLS[n];
		VarIntLS[] o = new VarIntLS[n];
		for (int i = 0; i < n; i++) {
			x[i] = new VarIntLS(mgr, 0, W);
			y[i] = new VarIntLS(mgr, 0, H);
			o[i] = new VarIntLS(mgr, 0, 1);
		}

		for (int i = 0; i < n; i++) {
			S.post(new Implicate(new IsEqual(o[i], 0), new LessOrEqual(
					new FuncPlus(x[i], w[i]), W)));
			S.post(new Implicate(new IsEqual(o[i], 0), new LessOrEqual(
					new FuncPlus(y[i], h[i]), H)));
			S.post(new Implicate(new IsEqual(o[i], 1), new LessOrEqual(
					new FuncPlus(x[i], h[i]), W)));
			S.post(new Implicate(new IsEqual(o[i], 1), new LessOrEqual(
					new FuncPlus(y[i], w[i]), H)));
		}

		for (int i = 0; i < n - 1; i++) {
			for (int j = i + 1; j < n; j++) {
				// o[i] = 0, o[j] = 0 (no orientation)
				IConstraint[] c = new IConstraint[4];
				c[0] = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]);
				c[1] = new LessOrEqual(new FuncPlus(x[i], w[i]), x[j]);
				c[2] = new LessOrEqual(new FuncPlus(y[i], h[i]), y[j]);
				c[3] = new LessOrEqual(new FuncPlus(y[j], h[j]), y[i]);
				S.post(new Implicate(new AND(new IsEqual(o[i], 0), new IsEqual(
						o[j], 0)), new OR(c)));

				// o[i] = o, o[j] = 1
				c = new IConstraint[4];
				c[0] = new LessOrEqual(new FuncPlus(x[j], h[j]), x[i]);
				c[1] = new LessOrEqual(new FuncPlus(x[i], w[i]), x[j]);
				c[2] = new LessOrEqual(new FuncPlus(y[i], h[i]), y[j]);
				c[3] = new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]);
				S.post(new Implicate(new AND(new IsEqual(o[i], 0), new IsEqual(
						o[j], 1)), new OR(c)));

				// o[i] = 1, o[j] = 0
				c = new IConstraint[4];
				c[0] = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]);
				c[1] = new LessOrEqual(new FuncPlus(x[i], h[i]), x[j]);
				c[2] = new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]);
				c[3] = new LessOrEqual(new FuncPlus(y[j], h[j]), y[i]);
				S.post(new Implicate(new AND(new IsEqual(o[i], 1), new IsEqual(
						o[j], 0)), new OR(c)));

				// o[i] = 1, o[j] = 1
				c = new IConstraint[4];
				c[0] = new LessOrEqual(new FuncPlus(x[j], h[j]), x[i]);
				c[1] = new LessOrEqual(new FuncPlus(x[i], h[i]), x[j]);
				c[2] = new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]);
				c[3] = new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]);
				S.post(new Implicate(new AND(new IsEqual(o[i], 1), new IsEqual(
						o[j], 1)), new OR(c)));
			}
		}
		mgr.close();

		TabuSearch ts = new TabuSearch();
		ts.search(S, 30, 10, 10000, 100);
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BinPacking2D bp = new BinPacking2D();
		bp.tabuSearchBinPacking2D();
		
	}

}
