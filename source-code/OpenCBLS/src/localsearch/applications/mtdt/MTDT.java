package localsearch.applications.mtdt;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.max_min.Max;
import localsearch.functions.max_min.Min;
import localsearch.functions.occurrence.Occurrence;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.functions.element.*;
import core.*;

public class MTDT {
	public int[][] match;
	public int n;
	public int[] sup;// sup[i] is the supervisor of student i
	public int[] rawsup;
	public int m1;
	public int m2;
	public int[] intp;
	public int[] extp;
	public int nbSlots;
	public int nbRooms;
	public HashMap<Integer, Integer> map;
	public int lambda = 3;// max number of times each professor is a examiner
	// Model
	LocalSearchManager ls;
	ConstraintSystem S;
	VarIntLS[][] x_p;
	VarIntLS[] x_s;
	VarIntLS[] x_r;
	IFunction obj1;
	IFunction obj2;
	IFunction[] occ;
	IFunction[] match1;
	IFunction[] match2;
	public void readData(String filename) {
		try {
			File file;
			file = new File(filename);
			Scanner scan = new Scanner(file);
			String line = scan.nextLine();
			// line = scan.nextLine();
			System.out.println("Line = " + line);
			n = scan.nextInt();
			line = scan.nextLine();
			line = scan.nextLine();
			System.out.println("Line = " + line);
			map = new HashMap<Integer, Integer>();
			sup = new int[n];
			rawsup = new int[n];
			for (int i = 0; i < n; i++) {
				int sid = scan.nextInt();// student id
				rawsup[i] = scan.nextInt();
				System.out.print(sid + " " + rawsup[i] + " ");
				for (int j = 0; j < 7; j++) {
					int v = scan.nextInt();// do not used
					System.out.print(v + " ");
				}
				System.out.println();
			}
			line = scan.nextLine();
			line = scan.nextLine();
			m1 = scan.nextInt();
			intp = new int[m1];// internal professors
			for (int i = 0; i < m1; i++)
				intp[i] = scan.nextInt();

			line = scan.nextLine();
			line = scan.nextLine();
			m2 = scan.nextInt();
			extp = new int[m2];// external professors
			for (int i = 0; i < m2; i++)
				extp[i] = scan.nextInt();

			for (int i = 0; i < m1; i++)
				map.put(intp[i], i);
			for (int i = 0; i < m2; i++)
				map.put(extp[i], m1 + i);
			for(int i = 0; i < n; i++)
				sup[i] = map.get(rawsup[i]);
			
			line = scan.nextLine();
			line = scan.nextLine();
			match = new int[n][m1 + m2];

			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m1 + m2; j++) {
					scan.nextInt();
					int u = scan.nextInt();
					int v = scan.nextInt();
					//System.out.println("read match, u = " + u + ", v = " + v);
					if (map.get(u) == null)
						continue;
					int u1 = map.get(u);
					match[i][u1] = v;
					//System.out.println("match[" + i + "][" + u1 + "] = " + match[i][u1]);
				}

			}
			line = scan.nextLine();
			line = scan.nextLine();
			nbSlots = scan.nextInt();
			nbRooms = scan.nextInt();

			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public VarIntLS[] copyTo1DArray(VarIntLS[][] x){
		int r = x.length;
		int c = x[0].length;
		//System.out.println("r = " + r + ", c = " + c); System.exit(-1);
		
		VarIntLS[] y = new VarIntLS[r*c];
		for(int i = 0; i < r; i++)
			for(int j = 0; j < c; j++)
				y[i*c+j] = x[i][j];
		return y;
	}
	public VarIntLS[] copyColumn(VarIntLS[][] x, int c){
		
		VarIntLS[] y = new VarIntLS[x.length];
		for(int i = 0; i < y.length; i++)
			y[i] = x[i][c];
		return y;
	}
	public VarIntLS[] copyRow(VarIntLS[][] x, int r){
		VarIntLS[] y = new VarIntLS[x[0].length];
		for(int i = 0; i < y.length; i++)
			y[i] = x[r][i];
		return y;
	}
	public void stateModel() {
		ls = new LocalSearchManager();
		S = new ConstraintSystem(ls);
		x_p = new VarIntLS[n][5];
		x_s = new VarIntLS[n];
		x_r = new VarIntLS[n];
		
		for (int i = 0; i < n; i++) {
			x_p[i][0] = new VarIntLS(ls, m1, m1 + m2 - 1);
			x_p[i][1] = new VarIntLS(ls, 0, m1 - 1);
			x_p[i][2] = new VarIntLS(ls, 0, m1 - 1);
			x_p[i][3] = new VarIntLS(ls, 0, m1 - 1);
			x_p[i][4] = new VarIntLS(ls, m1, m1 + m2 - 1);
		}

		for (int i = 0; i < n; i++) {
			x_s[i] = new VarIntLS(ls, 0, nbSlots - 1);
			x_r[i] = new VarIntLS(ls, 0, nbRooms - 1);

		}
		for (int i = 0; i < n - 1; i++) {
			for (int j = i + 1; j < n; j++) {
				for (int p1 = 0; p1 < 5; p1++) {
					for (int p2 = 0; p2 < 5; p2++) {
						S.post(new Implicate(
								new IsEqual(x_p[i][p1], x_p[j][p2]),
								new NotEqual(x_s[i], x_s[j])));
					}
				}
			}
		}
		for (int i = 0; i < n; i++) {
			VarIntLS[] y = copyRow(x_p,i);
			S.post(new AllDifferent(y));
			//for (int p1 = 0; p1 < 4; p1++) {
				//for (int p2 = p1 + 1; p2 < 5; p2++) {
				//	S.post(new NotEqual(x_p[i][p1], x_p[i][p2]));
				//}
			//}
		}

		for (int i = 0; i < n - 1; i++) {
			for (int j = i + 1; j < n; j++) {
				S.post(new Implicate(new IsEqual(x_r[i], x_r[j]), 
						new NotEqual(x_s[i], x_s[j])));
			}
		}

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < 5; j++) {
				S.post(new NotEqual(x_p[i][j], sup[i]));
			}
		}
		
		VarIntLS[] y = copyColumn(x_p, 1);		
		for(int i = 0; i < m1; i++){
			IFunction o = new Occurrence(y, i);
			S.post(new LessOrEqual(o,lambda));
		}
		
		y = copyColumn(x_p,0);
		for(int i = m1; i < m1+m2; i++){
			IFunction o = new Occurrence(y, i);
			S.post(new LessOrEqual(o,lambda));
		}
		
		y = copyTo1DArray(x_p);
		occ = new IFunction[m1+m2];
		for(int i = 0; i < m1+m2; i++)
			occ[i] = new Occurrence(y, i);
		IFunction oMax = new Max(occ);
		IFunction oMin = new Min(occ);
		
		obj1 = new FuncMinus(oMax,oMin);
		
		match1 = new IFunction[n];
		match2 = new IFunction[n];
		
		for(int i = 0; i < n; i++){
			match1[i] = new Element(match,i,x_p[i][0]);
			match2[i] = new Element(match,i,x_p[i][1]);
		}
		obj2 = new FuncMinus(0,new FuncPlus(new Sum(match1), new Sum(match2)));
		

		ls.close();

		
	}

	public void solve() {
		localsearch.search.TabuSearch ts = new localsearch.search.TabuSearch();
		ts.search(S, 20, 300, 1000000, 200);
		
		ts.searchMaintainConstraintsMinimize(obj1, S, 20, 30, 100000, 200);
		IFunction[] tf = new IFunction[1];
		tf[0] = obj1;
		ts.searchMaintainConstraintsFunctionMinimize(obj2, tf, S, 20, 30, 100000, 200);
		for (int i = 0; i < n; i++) {
			System.out.println(i + " : " + rawsup[i] + " "
					+ extp[x_p[i][0].getValue() - m1] + " "
					+ intp[x_p[i][1].getValue()] + " "
					+ intp[x_p[i][2].getValue()] + " "
					+ intp[x_p[i][3].getValue()] + " "
					+ extp[x_p[i][4].getValue() - m1] + " " + x_s[i].getValue()
					+ " " + x_r[i].getValue() + ", match1 = " + match1[i].getValue() + ", match2 = " + match2[i].getValue());
		}
		for(int i = 0; i < m1; i++){
			System.out.println("Occ[" + intp[i] + "] = " + occ[i].getValue());
		}
		for(int i = m1; i < m1+m2; i++){
			System.out.println("Occ[" + extp[i-m1] + "] = " + occ[i].getValue());
		}
		System.out.println("obj1 = " + obj1.getValue() + ", obj2 = " + obj2.getValue());
	}

	public void testBatch(String filename, int nbTrials) {
		MTDT A = new MTDT();
		A.readData(filename);
		double[] t = new double[nbTrials];
		double avg_t = 0;
		for (int k = 0; k < nbTrials; k++) {
			double t0 = System.currentTimeMillis();
			A.stateModel();
			A.solve();
			t[k] = (System.currentTimeMillis() - t0)*0.001;
			avg_t += t[k];
		}
		avg_t = avg_t*1.0/nbTrials;
		System.out.println("Time = " + avg_t);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		MainApp A = new MainApp();
		A.readData("data\\MasterThesisDefenseTimeTabling\\jury-data-8-4-2.txt");
		double t0 = System.currentTimeMillis();
		A.stateModel();
		A.solve();
		double t = System.currentTimeMillis() - t0;
		t = t * 0.001;
		System.out.println("Time = " + t);
		*/
		MTDT A = new MTDT();
		A.testBatch("data\\MTDT\\jury-data-19-4-5.txt",1);
	}

}
