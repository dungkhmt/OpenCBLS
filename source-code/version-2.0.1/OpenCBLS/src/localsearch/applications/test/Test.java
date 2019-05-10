package localsearch.applications.test;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;

public class Test {

	public static void test1(){
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS X1 = new VarIntLS(mgr,1,5);
		VarIntLS X2 = new VarIntLS(mgr,1,5);
		VarIntLS X3 = new VarIntLS(mgr,1,5);
		VarIntLS X4 = new VarIntLS(mgr,1,5);
		
		ConstraintSystem S = new ConstraintSystem(mgr);
		
		IFunction f1 = new FuncPlus(X2,X4);
		IFunction f2 = new FuncPlus(X1,X3);
		S.post(new IsEqual(f1, f2));
		
		IFunction f3 = new FuncPlus(new FuncPlus(X1,X2),X3);
		S.post(new IsEqual(f3, 9));
		
		S.post(new LessOrEqual(X3, X1));
		
		IConstraint c1 = new LessOrEqual(X2, X1);
		IConstraint c2 = new IsEqual(new FuncPlus(X3,X4), 7);
		S.post(new Implicate(c1, c2));
		
		IConstraint c3 = new LessOrEqual(X3, X4);
		
		S.post(new OR(c1,c3));
		
		mgr.close();// construct dependency graph
		
		
		TabuSearch ts = new TabuSearch();
		ts.search(S, 30, 10, 100000, 200);
		
		System.out.println("X1 = " + X1.getValue());
		System.out.println("X2 = " + X2.getValue());
		System.out.println("X3 = " + X3.getValue());
		System.out.println("X4 = " + X4.getValue());
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Test.test1();
	}

}
