package localsearch.constraints.basic;

import java.util.HashSet;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncVarConst;
import localsearch.model.*;
import core.*;

public class NotEqual extends AbstractInvariant implements IConstraint{

	private int _violations;
	private IFunction _f1;
	private IFunction _f2;
	private VarIntLS[] _x;
	private LocalSearchManager _ls;
	
	public NotEqual(IFunction f1, IFunction f2) {
		_ls = f1.getLocalSearchManager();
		_f1 = f1;
		_f2 = f2;
		post();
	}
	
	public NotEqual(IFunction f, VarIntLS x) {
		_ls = f.getLocalSearchManager();
		_f1 = f;
		_f2 = new FuncVarConst(x);
		post();
	}
	
	public NotEqual(IFunction f, int val) {
		_ls = f.getLocalSearchManager();
		_f1 = f;
		_f2 = new FuncVarConst(_ls, val);
		post();
	}
	
	public NotEqual(int val, IFunction f) {
		_ls = f.getLocalSearchManager();
		_f1 = f;
		_f2 = new FuncVarConst(_ls, val);
		post();
	}
	
	public NotEqual(VarIntLS x, VarIntLS y) {
		_ls = x.getLocalSearchManager();
		_f1 = new FuncVarConst(x);
		_f2 = new FuncVarConst(y);
		post();
	}
	
	public NotEqual(VarIntLS x, int val) {
		_ls = x.getLocalSearchManager();
		_f1 = new FuncVarConst(x);
		_f2 = new FuncVarConst(_ls, val);
		post();
	}
	
	public NotEqual(int val, VarIntLS x) {
		_ls = x.getLocalSearchManager();
		_f1 = new FuncVarConst(x);
		_f2 = new FuncVarConst(_ls, val);
		post();
	}
	
	private void post() {
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		VarIntLS[] x1 = _f1.getVariables();
		VarIntLS[] x2 = _f2.getVariables();
		if(x1 != null)
			for (int i = 0; i < _f1.getVariables().length; i++)
				_S.add(x1[i]);
		if(x2 != null)
			for (int i = 0; i < _f2.getVariables().length; i++)
				_S.add(x2[i]);
		_x = new VarIntLS[_S.size()];
		int i = 0;
		for (VarIntLS e : _S){
			_x[i] = e;
			i++;
		}		
		_ls.post(this);
	}
	
	@Override
	public int violations() {
		return _violations;
	}

	public int violations(VarIntLS x) {
		if (_violations != 0)
			return (x.IsElement(_x)) ? _violations : 0;
		else
			return 0;
	}
	
	public VarIntLS[] getVariables() {
		return _x;
	}
	
	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		if (!x.IsElement(_x)) return 0;
		int v1 = _f1.getValue() + _f1.getAssignDelta(x, val);
		int v2 = _f2.getValue() + _f2.getAssignDelta(x, val);
		int d;
		if(v1 == v2)
			d = 1;
		else d = 0;
		return 
			d - _violations;
	}
	
	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		int v1 = _f1.getValue() + _f1.getSwapDelta(x, y);
		int v2 = _f2.getValue() + _f2.getSwapDelta(x, y);
		int d;
		if(v1 == v2)
			d = 1;
		else d = 0;
		return 
			d - _violations;
	}
	
	public void propagateInt(VarIntLS x, int val) {
		if (_f1.getValue() == _f2.getValue())
			_violations = 1;
		else
			_violations = 0;
	}
	
	public void initPropagate() {
		if (_f1.getValue() == _f2.getValue())
			_violations = 1;
		else
			_violations = 0;
	}	

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static void main(String[] args) {
		LocalSearchManager ls = new LocalSearchManager();
		int n = 5;
		VarIntLS[] x = new VarIntLS[n];
		for(int i = 0; i < n; i++)
			x[i] = new VarIntLS(ls,0,n-1);
		
		ConstraintSystem S = new ConstraintSystem(ls);
		S.post(new NotEqual(x[0],4));
		S.post(new NotEqual(1,x[3]));
		S.post(new NotEqual(2,x[4]));
		S.post(new AllDifferent(x));
		ls.close();
		
		localsearch.search.TabuSearch s = new localsearch.search.TabuSearch();
		s.search(S, 10, 1, 1000, 100);
		
		for(int i = 0; i < n; i++)
			System.out.print(x[i].getValue() + " ");
		System.out.println();
	}
}
