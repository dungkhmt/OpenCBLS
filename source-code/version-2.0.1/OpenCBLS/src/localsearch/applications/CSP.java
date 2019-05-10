package localsearch.applications;

import org.omg.CosNaming.NamingContextPackage.NotEmpty;

import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.LessThan;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;
import localsearch.selectors.MinMaxSelector;

public class CSP {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS X0 = new VarIntLS(mgr,3,6);
		VarIntLS X1 = new VarIntLS(mgr,0,5);
		VarIntLS X2 = new VarIntLS(mgr,2,4);
		VarIntLS X3 = new VarIntLS(mgr,1,6);
		
		ConstraintSystem S = new ConstraintSystem(mgr);
		
		S.post(new LessThan(X0, X1));
		S.post(new LessThan(X2, X0));
		S.post(new IsEqual(new FuncPlus(X0, X3),new FuncPlus(X1, X2)));
		S.post(new LessOrEqual(new FuncPlus(X2, X3), new FuncPlus(X0, X1)));
		S.post(new IsEqual(new FuncPlus(X0, X2), 6));
		mgr.close();
	
		MinMaxSelector mms = new MinMaxSelector(S);
		for(int it = 0; it <= 10000 && S.violations() > 0; it++){
			VarIntLS y = mms.selectMostViolatingVariable();
			int v = mms.selectMostPromissingValue(y);
			y.setValuePropagate(v);
		}
		
		TabuSearch ts = new TabuSearch();
		ts.search(S, 30, 10, 100000, 100);
		
		System.out.println("X0 = " + X0.getValue());
		System.out.println("X1 = " + X1.getValue());
		System.out.println("X2 = " + X2.getValue());
		System.out.println("X3 = " + X3.getValue());
		mgr.close();
		
	}

	
}
