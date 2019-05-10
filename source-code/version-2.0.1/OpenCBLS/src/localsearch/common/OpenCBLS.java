package localsearch.common;

import localsearch.model.VarIntLS;
import java.util.*;
public class OpenCBLS {

	public static int MAX_INT = 2147483647;
	public static Random R = new Random();
	public static int getMaxValueDomain(VarIntLS[] x){
		int max = -MAX_INT;
		for(int i = 0; i < x.length; i++){
			max = max >= x[i].getMaxValue() ? max : x[i].getMaxValue();
		}
		return max;
	}
	public static String name(){
		return "JCBLS";
	}
	public static void exit(){
		System.exit(-1);
	}
	public static void fill(int[] x, int v){
		for(int i = 0; i < x.length; i++) x[i] = v;
	}
	public static void fill(int[][] a, int v){
		for(int i = 0; i < a.length; i++)
			for(int j = 0; j < a[0].length; j++)
				a[i][j] = v;
	}
	public static int randomSelect(Set<Integer> S){
		int a = R.nextInt(S.size());
		int count = 0;
		for(int v : S){
			if(count == a) return v;count++;
		}
		System.out.println(name() + "::randomSelect EXCEPTION, set is empty"); exit();
		return 0;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
