package localsearch.functions.sum;


import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.basic.*;
import localsearch.functions.basic.FuncVarConst;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.*;
public class Sum extends AbstractInvariant implements IFunction {
	
	private IFunction _f;
	
	/*
	public Sum(VarIntLS[] x, IConstraint[] c){
		// semantic: \sum_{i in f.rng(): c[i].violations() == 0}x[i]
		_f = new SumVarConstraints(x,c);
	}
	*/
	/*
	public Sum(IFunction[] f, IConstraint[] c){
		_f = new SumFunConstraints(f, c);
	}
	*/
	public Sum(IFunction[] f){
		_f = new SumFun(f);
	}
	public Sum(VarIntLS[] x){
		_f = new SumVar(x);
	}
	@Override
	public int getMinValue() {
		// TODO Auto-generated method stub
		return _f.getMinValue();
	}

	@Override
	public int getMaxValue() {
		// TODO Auto-generated method stub
		return _f.getMaxValue();
	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return _f.getValue();
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		return _f.getAssignDelta(x,val);
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		return _f.getSwapDelta(x, y);
	}

	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		return _f.getVariables();
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	public boolean verify(){
		return _f.verify();
	}
	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _f.getLocalSearchManager();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LocalSearchManager ls = new LocalSearchManager();
		int n = 1000;
		VarIntLS[] x = new VarIntLS[n];
		for (int i = 0; i < x.length; i++) {
			x[i] = new VarIntLS(ls, 0, n);
			x[i].setValue(i);
		}
		IFunction[] f = new IFunction[n];
		for(int i = 0; i < n; i++)
			f[i] = new FuncMinus(x[i],i);
		IConstraint[] c = new IConstraint[n];
		for(int i = 0; i < n; i++){
			c[i] = new LessOrEqual(x[i], 10);
		}
		Sum s = new Sum(f);

		ls.close();
		
		localsearch.applications.Test T = new localsearch.applications.Test();
		T.test(s,100000);
		
	}

}
