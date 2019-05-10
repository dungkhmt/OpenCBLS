package localsearch.applications.examplebook;
import java.util.*;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.LessThan;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;
public class BACP {
	int n = 9;
	int p = 4;
	int a = 2;
	int b = 4;
	int c = 3;
	int d = 7;
	int[] crd = new int[]{3, 2, 2, 1, 3, 3, 1, 2, 2};
	Pair[] T = new Pair[]{	new Pair(0,1),
							new Pair(0,2),
							new Pair(1,3),
							new Pair(2,5),
							new Pair(3,6),
							new Pair(4,7),
							};
	
	class Pair{
		int i;
		int j;
		public Pair(int i, int j){
			this.i = i; this.j = j;
		}
	}
	
	public void tabuSearchBACP(){
		// DATA
		int n = 10;
		int p = 4;
		int a = 2;
		int b = 4;
		int c = 3;
		int d = 7;
		int[] crd = new int[]{3, 2, 2, 3, 3, 3, 3, 2, 2,3};
		Pair[] T = new Pair[]{	new Pair(0,1),
								new Pair(0,2),
								new Pair(1,3),
								new Pair(2,5),
								new Pair(3,6),
								new Pair(4,7),
								new Pair(9,7),
								};
		// MODEL
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[n];
		for(int i = 0; i < n; i++)
			x[i] = new VarIntLS(mgr,0,p-1);
		ConstraintSystem S = new ConstraintSystem(mgr);
		IFunction[] nbCredits = new IFunction[p];
		IFunction[] nbCourses = new IFunction[p];
		for(int i = 0; i < p; i++){
			nbCredits[i] = new ConditionalSum(x, crd, i);
			nbCourses[i] = new ConditionalSum(x, i);
			
			S.post(new LessOrEqual(c, nbCredits[i]));
			S.post(new LessOrEqual(nbCredits[i], d));
			
			S.post(new LessOrEqual(a, nbCourses[i]));
			S.post(new LessOrEqual(nbCourses[i], b));
		
		}
		
		for(int k = 0; k < T.length; k++){
			S.post(new LessThan(x[T[k].i], x[T[k].j]));
		}
		mgr.close();
		
		// SEARCH
		TabuSearch ts = new TabuSearch();
		ts.search(S, 30, 10, 100000, 100);
		
		for(int i = 0; i < p; i++){
			System.out.print("period " + i + ": ");
			for(int j = 0; j < n; j++)
				if(x[j].getValue() == i)
					System.out.print(j + " (crd " + crd[j] + "), ");
			System.out.println("nbCourses = " + nbCourses[i].getValue() + ", nbCredits = " + nbCredits[i].getValue());
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BACP bacp = new BACP();
		bacp.tabuSearchBACP();
	}

}
