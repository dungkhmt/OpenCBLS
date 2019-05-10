package localsearch.applications.queen;

import localsearch.model.*;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.*;
import localsearch.search.TabuSearch;
import localsearch.selectors.*;

import java.io.PrintWriter;
import java.util.*;

class MyMove{
	int i;
	int v;
	public MyMove(int i, int v){
		this.i = i; this.v = v;
	}
}
public class Queen {
	int n;
	LocalSearchManager ls;
	ConstraintSystem S;
	VarIntLS[] x;
	Random R;
	
	public Queen(int n){
		this.n = n;
		R = new Random();
	}
	public void stateModel(){
		LocalSearchManager ls=new LocalSearchManager();
		S = new ConstraintSystem(ls);
		x = new VarIntLS[n];
		for (int i = 0; i < n; i++){
			x[i] = new VarIntLS(ls, 0, n - 1);
		}
		
		S.post(new AllDifferent(x));
		
		IFunction[] f1=new IFunction[n];
		for (int i = 0; i < n; i++) 
			f1[i] =  new FuncPlus(x[i], i);
		S.post(new AllDifferent(f1));
		
		IFunction[] f2 = new IFunction[n];
		for (int i = 0; i < n; i++) 
			f2[i] = new FuncPlus(x[i], -i);
		S.post(new AllDifferent(f2));
		
		ls.close();
		
	}
	public void search(){
		System.out.println("n = " + n + ", init S = " + S.violations());
		int it = 0;
		ArrayList<MyMove> L = new ArrayList<MyMove>();
		
		while(it < 100000 && S.violations() > 0){
			int sel_i = -1;
			int sel_v = -1;
			int min_delta = 1000000;
			L.clear();
			
			for(int i = 0; i < n; i++){
				for(int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++){
					int delta = S.getAssignDelta(x[i], v);
					if(delta < min_delta){
						min_delta = delta;
						L.clear();
						L.add(new MyMove(i,v));
					}else if(delta == min_delta){
						L.add(new MyMove(i,v));
					}
				}
			}
			int idx = R.nextInt(L.size());
			MyMove m = L.get(idx);
			sel_i = m.i;
			sel_v = m.v;
			
			x[sel_i].setValuePropagate(sel_v);// local move
			it++;
			System.out.println("Step " + it + ", S = " + S.violations());
		}
	}
	
	public void printHTML(){
		
		try{
			PrintWriter out = new PrintWriter("queen.html");
			out.println("<table border = 1>");
			for(int i = 0; i < n; i++){
				out.println("<tr>");
				for(int j = 0; j < n; j++)
					if(x[j].getValue() == i)
						out.println("<td width=20 height=20, bgcolor='red'></td>");
					else
						out.println("<td width=20 height=20, bgcolor='blue'></td>");
				out.println("</tr>");
			}
			out.println("</table>");
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public void tabuSearch(){
		TabuSearch ts = new TabuSearch();
		ts.search(S, 30, 10, 100000, 200);
		
	}
	public void printSolution(){
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Queen Q = new Queen(20);
		Q.stateModel();
		//Q.search();
		Q.tabuSearch();
		Q.printHTML();
	}

}
