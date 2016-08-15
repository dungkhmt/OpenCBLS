package localsearch.functions.basic;



import java.util.HashSet;

import localsearch.model.*;

public class FuncMinus extends AbstractInvariant implements IFunction {

	private int _value;
	private int _minValue;
	private int _maxValue;
	private IFunction _f1;
	private IFunction _f2;
	private VarIntLS[] _x;
	private LocalSearchManager _ls;
	
	public FuncMinus(int val, IFunction f){
		_f1 = new FuncVarConst(f.getLocalSearchManager(),val);
		_f2 = f;
		_ls = f.getLocalSearchManager();
		post();
	}
	public FuncMinus(IFunction f1, IFunction f2) {
		this._f1 = f1;
		this._f2 = f2;
		_ls = f1.getLocalSearchManager();
		post();
	}
	
	public FuncMinus(IFunction f, VarIntLS x) {
		_f1 = f;
		_f2 = new FuncVarConst(x);
		_ls = f.getLocalSearchManager();
		post();
	}
	
	public FuncMinus(IFunction f, int val) {
		_f1 = f;
		_f2 = new FuncVarConst(f.getLocalSearchManager(), val);
		_ls = f.getLocalSearchManager();
		post();
	}
	
	public FuncMinus(VarIntLS x, VarIntLS y) {
		_f1 = new FuncVarConst(x);
		_f2 = new FuncVarConst(y);
		_ls = x.getLocalSearchManager();
		post();
	}
	
	public FuncMinus(VarIntLS x, int val) {
		_f1 = new FuncVarConst(x);
		_f2 = new FuncVarConst(x.getLocalSearchManager(),val);
		_ls = x.getLocalSearchManager();
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
		_value = _f1.getValue() - _f2.getValue();
		_maxValue = _f1.getMaxValue() - _f2.getMinValue();
		_minValue = _f1.getMinValue() - _f2.getMaxValue();
		_ls.post(this);
	}
	
	@Override
	public int getMinValue() {
		return _minValue;
	}

	@Override
	public int getMaxValue() {
		return _maxValue;
	}

	@Override
	public int getValue() {
		return _value;
	}

	@Override
	public VarIntLS[] getVariables() {
		return _x;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		return (!(x.IsElement(_x))) ? 0 : _f1.getAssignDelta(x, val) - _f2.getAssignDelta(x, val);
	}
	
	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if ((!(x.IsElement(_x))) && (!(y.IsElement(_x))))
			return 0;
		return _f1.getSwapDelta(x, y) - _f2.getSwapDelta(x, y);	
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		_value = _f1.getValue() - _f2.getValue();
	}

	@Override
	public void initPropagate() {
		_value = _f1.getValue() - _f2.getValue();
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}
	
	public String name(){
		return "FuncMinus";
	}
	
	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LocalSearchManager _ls = new LocalSearchManager();
		VarIntLS x = new VarIntLS(_ls,0,500);
		VarIntLS y = new VarIntLS(_ls,0,500);
		FuncMinus sum = new FuncMinus(x,y);
		x.setValue(10);
		y.setValue(15);
		_ls.close();		
		System.out.println(sum.getSwapDelta(x,y));		
	}		
}
