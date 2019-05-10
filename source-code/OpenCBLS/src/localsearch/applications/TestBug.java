package localsearch.applications;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class TestBug {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] X = new VarIntLS[5];
		for(int i = 0; i < X.length; i++)
			X[i] = new VarIntLS(mgr, 1, 5);
		X[0].setValue(2); X[1].setValue(3); X[2].setValue(3);
		X[3].setValue(2); X[4].setValue(5);
		int[] w = new int[]{1, 2, 2, 3, 1};
		ConstraintSystem S = new ConstraintSystem(mgr);
		S.post(new LessOrEqual(new ConditionalSum(X, w, 2), 2));
		S.post(new AllDifferent(X));
		mgr.close();
		System.out.println("violations = " + S.violations());
		System.out.println("delta = " + S.getAssignDelta(X[1], 4));
		X[1].setValuePropagate(4);
		System.out.println("new violations = " + S.violations());

	}

}
