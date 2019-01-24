package localsearch.applications.queen;

import java.util.ArrayList;

import localsearch.*;
import localsearch.model.*;
import localsearch.constraints.*;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.*;
import localsearch.functions.basic.*;
import localsearch.selectors.*;
import java.util.*;
public class Queen {

	/**
	 * @param args
	 */
	public void test1(){
		//java.util.Random R = new java.util.Random();
		int n=1000;
		
		LocalSearchManager ls=new LocalSearchManager();
		ConstraintSystem S=new ConstraintSystem(ls);
		
		
		HashMap<VarIntLS, Integer> map = new HashMap<VarIntLS, Integer>();
		VarIntLS[] x = new VarIntLS[n];
		for (int i = 0; i < n; i++){
			x[i] = new VarIntLS(ls, 0, n - 1);
			//int v = R.nextInt(n);
			//x[i].setValue(v);
			
			map.put(x[i], i);
		}
		
		S.post(new AllDifferent(x));
		
			
		
		IFunction[] f1=new IFunction[n];
		for (int i = 0; i < n; i++) 
			f1[i] =  new FuncPlus(x[i], i);
		//AllDifferentFunctions c1=new AllDifferentFunctions(f1);
		//S.post(new AllDifferentFunctions(f1));
		S.post(new AllDifferent(f1));
		
		IFunction[] f2 = new IFunction[n];
		for (int i = 0; i < n; i++) f2[i] = new FuncPlus(x[i], -i);
		//S.post(new AllDifferentFunctions(f2));
		S.post(new AllDifferent(f2));
		
		
		
		//S.close();
		ls.close();
		System.out.println("Init S = " + S.violations());
		MinMaxSelector mms = new MinMaxSelector(S);
		
		int it = 0;
		while(it < 10000 && S.violations() > 0){
			
			VarIntLS sel_x = mms.selectMostViolatingVariable();
			int sel_v = mms.selectMostPromissingValue(sel_x);
			
			sel_x.setValuePropagate(sel_v);
			System.out.println("Step " + it + ", x[" + map.get(sel_x) + "] := " + sel_v + ", S = " + S.violations());
			
			it++;
		}
		
		
		System.out.println(S.violations());

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Queen Q = new Queen();
		Q.test1();
	}

}
