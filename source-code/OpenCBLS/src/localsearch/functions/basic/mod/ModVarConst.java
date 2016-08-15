package localsearch.functions.basic.mod;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class ModVarConst extends AbstractInvariant implements IFunction {

	private int _value;
	private int _minValue;
	private int _maxValue;
	private LocalSearchManager _ls;
	private VarIntLS[] _vars;
	private VarIntLS _x;
	private int _a;
	
	public ModVarConst(VarIntLS x, int a){
		// semantic x % a
		_x = x; _a = a;
		_ls = _x.getLocalSearchManager();
		post();
	}
	private void post(){
		_vars = new VarIntLS[1];
		_vars[0] = _x;
		_minValue = 0;
		_maxValue = _a-1;
		_ls.post(this);
	}
	@Override
	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		return _vars;
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		if(x != _x) return;
		_value = val % _a;
	}

	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		_value = _x.getValue() % _a;
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return _x.getValue() % _a == _value;
	}

	@Override
	public int getMinValue() {
		// TODO Auto-generated method stub
		return _minValue;
	}

	@Override
	public int getMaxValue() {
		// TODO Auto-generated method stub
		return _maxValue;
	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return _value;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		if(x != _x) return 0;
		return val % _a - _value;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if(x == _x) return getAssignDelta(x,y.getValue());
		if(y == _x) return getAssignDelta(y,x.getValue());
		return 0;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
