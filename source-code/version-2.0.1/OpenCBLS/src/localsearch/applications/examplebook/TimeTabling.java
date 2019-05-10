package localsearch.applications.examplebook;

import org.omg.CosNaming.NamingContextPackage.NotEmpty;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.constraints.basic.NotOverLap;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;

public class TimeTabling {

	public static void tabuSearch(){
		class Pair{
			int d,s;
			public Pair(int d, int s){
				this.d = d;this.s = s;
			}
		}
		//int n;// so mon hoc
		int D = 5;// 5 ngay trong tuan
		int K = 6;// 6 tiet / 1 buoi
		int[] sl = new int[]{3,3,3,2,3,4,2,4,4,2};
		int n = sl.length;// so  mon hoc
		int[][] c = new int[][]{
		{0,1,1,1,1,1,1,1,1,1},
		{1,0,0,0,1,0,1,1,0,0},
		{1,0,0,1,0,1,0,0,1,1},
		{1,0,1,0,0,1,0,0,1,1},
		{1,1,0,0,0,0,1,1,0,0},
		{1,0,1,1,0,0,1,0,1,1},
		{1,1,0,0,1,1,0,1,0,0},
		{1,1,0,0,1,0,1,0,0,0},
		{1,0,1,1,0,1,0,0,0,1},
		{1,0,1,1,0,1,0,0,1,0}
		};
		Pair[][] f = new Pair[][]{
		new Pair[]{new Pair(0,3),new Pair(2,1),new Pair(1,1)},
		new Pair[]{new Pair(0,0),new Pair(2,0)},
		new Pair[]{new Pair(2,0)},
		new Pair[]{new Pair(1,0),new Pair(4,0)},
		new Pair[]{new Pair(3,0)},
		new Pair[]{new Pair(0,3)},
		new Pair[]{new Pair(4,0),new Pair(0,1)},
		new Pair[]{new Pair(3,0)},
		new Pair[]{new Pair(2,0),new Pair(0,1)},
		new Pair[]{new Pair(0,0),new Pair(2,0),new Pair(4,3)}
		};

		for(int i = 0; i < f.length; i++){
			for(int j = 0; j < f[i].length; j++){
				System.out.print("(" + f[i][j].d + "," + f[i][j].s + ") "); 
			}
			System.out.println();
		}
		
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] xd = new VarIntLS[n];
		VarIntLS[] xs = new VarIntLS[n];
		for(int i = 0; i < n; i++){
			xd[i] = new VarIntLS(mgr,0,D-1);
			xs[i] = new VarIntLS(mgr,0,K-sl[i]);
		}
		ConstraintSystem S = new ConstraintSystem(mgr);
		for(int i = 0; i < n-1; i++)
			for(int j = i+1; j < n; j++){
				if(c[i][j] == 1){
					S.post(new Implicate(new IsEqual(xd[i], xd[j]), 
							new NotOverLap(xs[i], sl[i], xs[j], sl[j])));
				}
			}
		for(int i = 0; i < n; i++){
			for(int j = 0; j < f[i].length; j++){
				S.post(new Implicate(new IsEqual(xd[i], f[i][j].d), new NotEqual(xs[i], f[i][j].s)));
			}
		}
		mgr.close();
		TabuSearch ts = new TabuSearch();
		ts.search(S, 30, 10, 100000, 100);
		for(int i = 0; i < n; i++){
			System.out.println("mon " + i + ": ngay " + xd[i].getValue() + ", tiet " + xs[i].getValue());
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TimeTabling T = new TimeTabling();
		T.tabuSearch();
	}

}
