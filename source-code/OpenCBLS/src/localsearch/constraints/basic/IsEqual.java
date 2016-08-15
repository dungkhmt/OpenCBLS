package localsearch.constraints.basic;

import java.util.HashSet;

import localsearch.functions.*;
import localsearch.functions.basic.*;
import localsearch.model.*;
import core.*;

public class IsEqual extends AbstractInvariant implements IConstraint{

	private int _violations;
	private IFunction _f1;
	private IFunction _f2;
	private VarIntLS[] _x;
	private LocalSearchManager _ls;
	
	public IsEqual(IFunction f1, IFunction f2) {
		_ls = f1.getLocalSearchManager();
		_f1 = f1;
		_f2 = f2;
		post();
	}
	
	public IsEqual(IFunction f, VarIntLS x) {
		_ls = f.getLocalSearchManager();
		_f1 = f;
		_f2 = new FuncVarConst(x);
		post();
	}
	
	public IsEqual(IFunction f, int val) {
		_ls = f.getLocalSearchManager();
		_f1 = f;
		_f2 = new FuncVarConst(_ls, val);
		post();
	}
	
	public IsEqual(int val, IFunction f) {
		_ls = f.getLocalSearchManager();
		_f1 = f;
		_f2 = new FuncVarConst(_ls, val);
		post();
	}
	
	public IsEqual(VarIntLS x, VarIntLS y) {
		_ls = x.getLocalSearchManager();
		_f1 = new FuncVarConst(x);
		_f2 = new FuncVarConst(y);
		post();
	}
	
	public IsEqual(VarIntLS x, int val) {
		_ls = x.getLocalSearchManager();
		_f1 = new FuncVarConst(x);
		_f2 = new FuncVarConst(_ls, val);
		post();
	}
	
	public IsEqual(int val, VarIntLS x) {
		_ls = x.getLocalSearchManager();
		_f1 = new FuncVarConst(x);
		_f2 = new FuncVarConst(_ls, val);
		post();
	}
	
	private void post() {
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		VarIntLS[] x1 = _f1.getVariables();
		VarIntLS[] x2 = _f2.getVariables();
		if(x1!=null)
		{
		for (int i = 0; i < _f1.getVariables().length; i++)
			_S.add(x1[i]);
		}
		if(x2!=null)
		{
		for (int i = 0; i < _f2.getVariables().length; i++)
			_S.add(x2[i]);
		}
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
		return Math.abs(v1 - v2) - _violations;
	}
	
	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		int v1 = _f1.getValue() + _f1.getSwapDelta(x, y);
		int v2 = _f2.getValue() + _f2.getSwapDelta(x, y);
		return Math.abs(v1 - v2) - _violations;
	}
	
	public void propagateInt(VarIntLS x, int val) {
		_violations = Math.abs(_f1.getValue() - _f2.getValue());
	}
	
	public void initPropagate() {
		_violations = Math.abs(_f1.getValue() - _f2.getValue());
	}
	
	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LocalSearchManager _ls = new LocalSearchManager();
		VarIntLS x = new VarIntLS(_ls,0,500);
		VarIntLS y = new VarIntLS(_ls,0,500);
		//VarIntLS z = new VarIntLS(_ls,0,500);
		//FuncMinus sum = new FuncMinus(x,y);
		FuncPlus p=new FuncPlus(x, 10);
		IConstraint c1 = new IsEqual(p, y);
		//IConstraint c2 = new IsEqual(x, z);
		x.setValue(10);
		y.setValue(20);
		//z.setValue(10);
		_ls.close();		
		System.out.println(c1.violations());
		//System.out.println(c2.getSwapDelta(x, z));
	}
}
