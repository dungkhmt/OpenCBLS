package localsearch.applications.examplebook;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.element.Element;
import localsearch.functions.occurrence.Occurrence;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;

public class MTDT {

	public void tabuSearchMTDT() {
		int n = 8;
		int m1 = 8;
		int m2 = 4;
		int r = 2;
		int k = 4;
		int lambda = 2;
		int[] s = new int[] { 0, 1, 2, 2, 4, 7, 5, 3 };
		int[][] m = new int[][] { 
				{ 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1 },
				{ 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1 },
				{ 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
				{ 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1 },
				{ 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
				{ 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1 },
				{ 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
				{ 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1 }

			};
		
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[][] xp = new VarIntLS[n][5];
		VarIntLS[] xr = new VarIntLS[n];
		VarIntLS[] xk = new VarIntLS[n];
		for(int i = 0; i < n; i++){
			xp[i][0] = new VarIntLS(mgr,m1,m1+m2-1);
			xp[i][1] = new VarIntLS(mgr,0,m1-1);
			xp[i][2] = new VarIntLS(mgr,0,m1-1);
			xp[i][3] = new VarIntLS(mgr,0,m1-1);
			xp[i][4] = new VarIntLS(mgr,m1,m1+m2-1);
			
			xr[i] = new VarIntLS(mgr,0,r-1);
			xk[i] = new VarIntLS(mgr,0,k-1);
		}
		
		ConstraintSystem S = new ConstraintSystem(mgr);
		for (int i = 0; i < n - 1; i++) {
			for (int j = i + 1; j < n; j++) {
				for (int p1 = 0; p1 < 5; p1++) {
					for (int p2 = 0; p2 < 5; p2++) {
						S.post(new Implicate(
								new IsEqual(xp[i][p1], xp[j][p2]),
								new NotEqual(xk[i], xk[j])));
					}
				}
				
				S.post(new Implicate(new IsEqual(xr[i], xr[j]), new NotEqual(xk[i],xk[j])));
			}
		}
		for(int i = 0; i < n; i++){
			VarIntLS[] y = new VarIntLS[5];
			for(int j = 0; j < 5; j++) y[j] = xp[i][j];
			S.post(new AllDifferent(y));
		
			for(int j = 0; j < 5; j++)
				S.post(new NotEqual(s[i], xp[i][j]));
		}
		
		for(int j = 0; j < m1+m2; j++){
			VarIntLS[] y0 = new VarIntLS[n];
			for(int i = 0; i < n; i++) 
				y0[i] = xp[i][0];
			S.post(new LessOrEqual(new Occurrence(y0, j),lambda));
			VarIntLS[] y1 = new VarIntLS[n];
			for(int i = 0; i < n; i++) 
				y1[i] = xp[i][1];
			S.post(new LessOrEqual(new Occurrence(y1, j),lambda));
			
		}
		
		
		IFunction[] f1 = new IFunction[n];
		IFunction[] f2 = new IFunction[n];
		
		for(int i = 0; i < n; i++){
			f1[i] = new Element(m,i,xp[i][0]);
			f2[i] = new Element(m,i,xp[i][1]);
		}
		IFunction obj = new FuncPlus(new Sum(f1), new Sum(f2));

		mgr.close();
		
		TabuSearch ts = new TabuSearch();
		ts.search(S, 30, 100, 10000, 100);
		
		ts.searchMaintainConstraintsMinimize(obj, S, 30, 100, 10000, 100);
		
		for(int ri = 0; ri < r; ri++){
			System.out.println("Room " + ri);
			for(int i = 0; i < n; i++)if(xr[i].getValue() == ri){
				System.out.print(i + ": \t" + s[i] + "\t");
				for(int j = 0; j < 5; j++)
					System.out.print(xp[i][j].getValue() + "(" + m[i][xp[i][j].getValue()] + ")\t");
				System.out.println(", kip " + xk[i].getValue());
			}
			System.out.println("-------------------------------");
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MTDT mtdt = new MTDT();
		mtdt.tabuSearchMTDT();
	}

}
