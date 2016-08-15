package localsearch.applications;
import localsearch.constraints.*;
import localsearch.constraints.basic.*;
import localsearch.search.*;
import localsearch.model.*;
public class Test {

	/**
	 * @param args
	 */
	public void test(IConstraint c, int maxIter){
	//public void test(AllDifferent c){
		VarIntLS[] x = c.getVariables();
		int n = x.length;
		int oldV = c.violations();
		System.out.println("init violations = oldV = " + oldV);
		//c.print();
		java.util.Random R = new java.util.Random();
		for(int it = 0; it < maxIter; it++){
			int delta = 0;
			int choice = R.nextInt(2);
			if(choice == 0){
				
				int i = R.nextInt(n);
				int d = x[i].getMaxValue() - x[i].getMinValue() + 1;
				int v = R.nextInt(d);
				v = x[i].getMinValue() + v;
				delta = c.getAssignDelta(x[i], v);
				x[i].setValuePropagate(v);
				System.out.println("Step " + it + ", x[" + i + "] = " + v + ", violations = " + c.violations());
			}else if(choice == 1){
				int i = R.nextInt(n);
				int j = R.nextInt(n);
				delta = c.getSwapDelta(x[i], x[j]);
				int vi = x[i].getValue();
				int vj = x[j].getValue();
				
				x[i].setValuePropagate(vj);
				x[j].setValuePropagate(vi);
				System.out.println("Step " + it + ", swap x[" + i + "] and x[" + j + "], violations = " + c.violations());
			}
			
			if(oldV + delta != c.violations()){
				System.out.println(it + ", move --> failed oldV = " + oldV + ", delta = " + delta + 
						" while violations after move = " + c.violations());
				break;
			}
			if(!c.verify()){
				break;
			}
			System.out.println("Step " + it + ", move --> OK");
			oldV = c.violations();
		}

	}

	public void test(IFunction f, int maxIter){
	//public void test(AllDifferent c){
		VarIntLS[] x = f.getVariables();
		int n = x.length;
		int oldV = f.getValue();
		System.out.println("init value = oldV = " + oldV);
		//c.print();
		java.util.Random R = new java.util.Random();
		for(int it = 0; it < maxIter; it++){
			int delta = 0;
			int choice = R.nextInt(2);
			System.out.println("Choice = " + choice);
			if(choice == 0){
				
				int i = R.nextInt(n);
				int d = x[i].getMaxValue() - x[i].getMinValue() + 1;
				int v = R.nextInt(d);
				v = x[i].getMinValue() + v;
				delta = f.getAssignDelta(x[i], v);
				x[i].setValuePropagate(v);
				System.out.println("Step " + it + ", x[" + i + "] = " + v + ", value = " + f.getValue());
			}else if(choice == 1){
				int i = R.nextInt(n);
				int j = R.nextInt(n);
				delta = f.getSwapDelta(x[i], x[j]);
				int vi = x[i].getValue();
				int vj = x[j].getValue();
				
				x[i].setValuePropagate(vj);
				x[j].setValuePropagate(vi);
				
				System.out.println("Step " + it + ", swap x[" + i + "] and x[" + j + "], value = " + f.getValue());
			}
			
			if(oldV + delta != f.getValue()){
				System.out.println(it + ", move --> failed oldV = " + oldV + ", delta = " + delta + 
						" while value after move = " + f.getValue());
				break;
			}
			if(!f.verify()){
				break;
			}
			System.out.println("Step " + it + ", move --> OK");
			oldV = f.getValue();
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] a = {1,2,3,4,5,2,3,4,3,5,5};
		int[] b = {0,0,0,0,0,1,1,1,2,2,3};
		LocalSearchManager mgr = new LocalSearchManager();
		int n = 6;
		VarIntLS[] x = new VarIntLS[n];
		for(int i = 0; i < n; i++)
			x[i] = new VarIntLS(mgr,1,n);
		ConstraintSystem S = new ConstraintSystem(mgr);
		for(int i = 0; i < a.length; i++){
			S.post(new NotEqual(x[a[i]],x[b[i]]));
		}
		mgr.close();
		TabuSearch ts = new TabuSearch();
		ts.search(S, 5, 2, 10000, 100);
		for(int i = 0; i < x.length; i++)
			System.out.println(x[i].getValue());
		for(int i = 0; i < a.length; i++){
			System.out.println(x[a[i]].getValue() + " != " + x[b[i]].getValue());
		}
	}

}
