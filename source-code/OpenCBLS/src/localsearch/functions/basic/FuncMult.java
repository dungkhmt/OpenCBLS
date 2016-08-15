package localsearch.functions.basic;

import java.util.HashMap;
import java.util.HashSet;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class FuncMult extends AbstractInvariant implements IFunction {
	
	private int _value;
	private int _minValue;
	private int _maxValue;
	private IFunction _f1;
	private IFunction _f2;
	private VarIntLS[] _x;
	private LocalSearchManager _ls;
	private HashMap<VarIntLS, Integer>    _map;
	
	public FuncMult(IFunction f1, IFunction f2) {
		this._f1 = f1;
		this._f2 = f2;
		_ls = f1.getLocalSearchManager();
		post();
	}
	
	public FuncMult(IFunction f, VarIntLS x) {
		_f1 = f;
		_f2 = new FuncVarConst(x);
		_ls = f.getLocalSearchManager();
		post();
	}
	
	public FuncMult(IFunction f, int val) {
		_f1 = f;
		_f2 = new FuncVarConst(f.getLocalSearchManager(), val);
		_ls = f.getLocalSearchManager();
		post();
	}
	
	public FuncMult(VarIntLS x, VarIntLS y) {
		_f1 = new FuncVarConst(x);
		_f2 = new FuncVarConst(y);
		_ls = x.getLocalSearchManager();
		post();
	}
	
	public FuncMult(VarIntLS x, int val) {
		_f1 = new FuncVarConst(x);
		_f2 = new FuncVarConst(x.getLocalSearchManager(),val);
		_ls = x.getLocalSearchManager();
		post();
	}
	
	void post()
	{
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		VarIntLS[] x1 = _f1.getVariables();
		VarIntLS[] x2 = _f2.getVariables();
		if(x1!=null)
		{
		for (int i = 0; i < x1.length; i++)
			_S.add(x1[i]);
		}
		if(x2!=null)
		{
		for (int i = 0; i < x2.length; i++)
			_S.add(x2[i]);
		}
		_x = new VarIntLS[_S.size()];
		int u = 0;
		for (VarIntLS e : _S){
			_x[u] = e;
			u++;
		}		
		
		_map=new HashMap<VarIntLS, Integer>();
		
		for(int i=0;i<_x.length;i++)
		{
			_map.put(_x[i],i);
		}
		
		int a1=_f1.getMinValue();
		int a2=_f2.getMinValue();
		int b1=_f1.getMaxValue();
		int b2=_f2.getMaxValue();
		
		int m1=Math.min(a1*a2, b1*b2);
		int m2=Math.min(a1*b2, a2*b1);
		_minValue=Math.min(m1, m2);
		
		int ma1=Math.max(a1*a2, b1*b2);
		int ma2=Math.max(a1*b2, a2*b1);
		_maxValue=Math.max(ma1, ma2);
		
		
		_ls.post(this);
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
	public VarIntLS[] getVariables() {
		return _x;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		if(_map.get(x)==null) return 0;
		
		int a=_f1.getAssignDelta(x, val)+_f1.getValue();
		int b=_f2.getAssignDelta(x, val)+_f2.getValue();
		int c=a*b-_f1.getValue()*_f2.getValue();
		
		
		
		
		return c;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if(_map.get(x)==null&&_map.get(y)==null) return 0;
		if(_map.get(x)!=null&&_map.get(y)==null) return getAssignDelta(x,y.getValue());
		if(_map.get(x)==null&&_map.get(y)!=null) return getAssignDelta(y, x.getValue());
		
		
		int a=_f1.getValue()+_f1.getSwapDelta(x, y);
		int b=_f2.getValue()+_f2.getSwapDelta(x, y);
		int c=a*b-_f1.getValue()*_f2.getValue();
		
		return c;
	}
	@Override
	public void propagateInt(VarIntLS x, int val) {
		
		_value=_f1.getValue()*_f2.getValue();
		
		
	}

	@Override
	public void initPropagate() {
		_value=_f1.getValue()*_f2.getValue();
	
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}
	
	public String name(){
		return "FuncMult";
	}
	
	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		
		
		return true;
	}

	
	public static void main(String[] args)
	{
		LocalSearchManager _ls = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[10];
		for(int i=0;i<x.length;i++)
		{
			x[i]=new VarIntLS(_ls, 0, 100);
		}
		x[1].setValue(1);
		x[3].setValue(10);
		IFunction[] f=new IFunction[x.length];
		for(int i=0;i<f.length;i++)
		{
			f[i]=new FuncMult(x[i], 10);
		}
		FuncMult ff=new FuncMult(f[1], f[3]);
		
		_ls.close();
		
		
		System.out.println(ff.getValue());
		
		System.out.println(ff.getAssignDelta(x[3], 5));
		
	}
	
	
}
