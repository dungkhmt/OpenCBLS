package localsearch.applications.bacp;


import java.util.Vector;

import localsearch.model.*;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.*;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.max_min.Max;
import localsearch.functions.max_min.Min;
import localsearch.search.*;

public class BACP {

	
	public static void main(String[] args) {
		
		
		int n=46;
		int m=8;
		
		  int[] credit;// credit[i] is the number of credits of the course i
		  int[] minCrd;// minC[i] is the minimum number of credits of period i
		  int[] maxCrd;// maxC[i] is the maximum number of credits of period i
		  int[] minCrs;// minCrs[i] is the minimum number of courses assigned to period i
		  int[] maxCrs;// maxCrs[i] is the maximum number of courses assigned to period i
		  int[] b;
		  int[] e; //(b[i],e[i]) is an edge of prerequisite: course b[i] must be a prerequisite of course e[i]
		//m.pp();
		 //n=46;
		 //m=8;
		credit=new int[]{1,  3,  1,  2,  4, 
				 4,  1,  5,  3,  4, 
				 4,  5, 1,  3, 3, 
				 4,  1,  1,  3,  3, 
				 3,  3,  3,  3,  1, 
				 4,  4,  3,  3, 3, 
				 2,  4,  3,  3,  3, 
				 3,  3,  3,  3,  3, 
				 3,  3,  2,  3, 3, 
				 3};
		minCrd=new int[m];
		maxCrd=new int[m];
		minCrs=new int[m];
		maxCrs=new int[m];
		
		
		for(int i=0;i<m;i++)
		{
			minCrd[i]=10;
			maxCrd[i]=24;
			minCrs[i]=2;
			maxCrs[i]=10;
		}
		b=new int[]{6,7,7,9,10,10,11,11,13,14,15,15,16,17,18,19,20,23,25,26,27,28,31,32,33,34,35,35,36,36,37,37,38,39,39,44,45,45};
		e=new int[]{0,1,5,4,4,5,7,10,8,8,9,10,6,2,13,13,14,15,13,20,21,23,25,25,29,27,27,27,29,29,35,35,29,35,35,37,31,31};
		
		LocalSearchManager mgr = new LocalSearchManager();
		ConstraintSystem S=new ConstraintSystem(mgr);
		VarIntLS[] x = new VarIntLS[n];// x[i] is the period assigned to the course i
		for(int i=0;i<x.length;i++)
		{
			x[i]=new VarIntLS(mgr, 0, m-1);
		}
		
		ConditionalSum[] s_credit = new ConditionalSum[m];
		ConditionalSum[] s_courses = new ConditionalSum[m];
		int[] one = new int[n];
		for(int i = 0; i < n; i++) one[i] = 1;
		for(int i = 0; i < m; i++){
			s_credit[i] = new ConditionalSum(x,credit,i);
			S.post(new LessOrEqual(s_credit[i], maxCrd[i]));
			S.post(new LessOrEqual(minCrd[i], s_credit[i]));
			
			s_courses[i] = new ConditionalSum(x,one,i);
			S.post(new LessOrEqual(s_courses[i], maxCrs[i]));
			S.post(new LessOrEqual(minCrs[i], s_courses[i]));
		}
		for(int j = 0; j < b.length; j++){
			//S.post(new LessOrEqual(x[b[j]],x[e[j]]));
			IFunction f=new FuncMinus(x[e[j]],x[b[j]] );
			S.post(new LessOrEqual(1,f));
			//S.post(new Implicate(new LessOrEqual(x[b[j]], x[e[j]]), new LessOrEqual(f, 2)));
			//S.post(new Implicate(new LessOrEqual(x[b[j]], x[e[j]]), new LessOrEqual(1, f)));
			
		}
		Max max=new Max(s_credit);
		Min min=new Min(s_credit);
		FuncMinus mm=new FuncMinus(max,min);
		
		S.close();
		mgr.close();
		System.out.println("max  =   "+max.getValue());
		System.out.println("min  =   "+min.getValue());
		System.out.println("mm  =   "+mm.getValue());
		
		for(int i = 0; i < x.length; i++){
			System.out.print(x[i].getValue() + ",");
		}
		System.out.println();
		for(int i = 0; i < s_credit.length; i++){
			System.out.println(s_credit[i].getValue() + ",");
		}
		System.out.println();
		//if(true) return;
		
		TabuSearch ts = new TabuSearch();
		ts.search(S, 50, 100, 100000, 20);
		
		//localsearch.applications.Test T = new localsearch.applications.Test();
		//T.test(mm, 100000);
		
		
		
		//SA_search s=new SA_search();
		//s.search(S, 2000, 0.00001, 0.9);
		
		// TODO Auto-generated method stub
		for(int i=0;i<n;i++)
			System.out.print("      +    x [ "+i+"]     =     "+x[i].getValue());
		
		System.out.println("\n");
		
		for(int i=0;i<m;i++)
			System.out.print(s_credit[i].getValue()+"     -->   ");
		
		
		
		/*
		System.out.println("\n");
		System.out.println("maxNew  =   "+max.getValue());
		System.out.println("minNew  =   "+min.getValue());
		int t=max.getValue()-min.getValue();
		int dem=0;
		while(dem<1000)
		{
			int max1=max.getValue();
			int min1=min.getValue();
			int t1=max1-min1;
			int i1=-1;
			int j1=-1;
			for(int i=0;i<s_credit.length;i++)
			{
				if(s_credit[i].getValue()==max1)
				{
					i1=i;
				}
				if(s_credit[i].getValue()==min1)
				{
					j1=i;
				}
			}
			//System.out.println("i1 =  "+i1+"   j1=     "+j1);
			Vector<Integer> s1=new Vector<Integer>();
			Vector<Integer> s2=new Vector<Integer>();
			for(int i=0;i<x.length;i++)
			{
				if(x[i].getValue()==i1)
				{
					s1.add(i);
				}
				if(x[i].getValue()==j1)
				{
					s2.add(i);
				}
			}
		
			for(int i=0;i<100;i++)
			{
				
				
				
					int kk1=(int)(Math.random()*(s1.size()));
					System.out.println("kk1 =   "+kk1);
					
					
					
					
					int k1=s1.get(kk1);
					System.out.println("k1  =   "+k1);
					
					
					
					x[k1].setValuePropagate(j1);
					int svio=S.violations();
							
						
							
					
					if(svio!=0)
					{
						
						
							x[k1].setValuePropagate(i1);
							
						
					}
					else
					{
						int u=max.getValue()-min.getValue();
						if(u<=t1)
						{
							t1=u;
						}
						else
						{
							x[k1].setValuePropagate(i1);
							//x[k2].setValuePropagate(j1);
						}
					
					
				
				
			
			}
					
			}
			
			
			dem++;
		}
		*/
		ts.searchMaintainConstraintsMinimize(mm, S, 20,100, 200,100);
		System.out.println("\n");
		System.out.println("S  =   "+S.violations());
		for(int i=0;i<m;i++)
			System.out.print(s_credit[i].getValue()+"     -->   ");
			
			
	}

}
