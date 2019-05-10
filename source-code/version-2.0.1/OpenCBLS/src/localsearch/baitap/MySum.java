package localsearch.baitap;

import java.util.HashMap;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.model.AbstractInvariant;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class MySum extends AbstractInvariant implements IFunction {
	private VarIntLS[] _x;
	private LocalSearchManager _mgr;
	private int _min;
	private int _max;
	private int _value;
	private HashMap<VarIntLS, Integer> _map;
	
	MySum(VarIntLS[] x){
		this._x = x;
		this._mgr = _x[0].getLocalSearchManager();
		
		post();
	}
	private void post(){
		_map = new HashMap<VarIntLS, Integer>();
		for(int i = 0; i < _x.length; i++){
			_map.put(_x[i], i);
		}
		_mgr.post(this);
	}
	public int getMinValue(){
		return _min;
	}
	public int getMaxValue(){
		return _max;
	}
	public int getValue(){
		return _value;
	}
	public int getAssignDelta(VarIntLS x, int val){
		if(_map.get(x) == null) return 0;
		return val - x.getValue();
	}
	public int getSwapDelta(VarIntLS x, VarIntLS y){
		if(_map.get(x) == null && _map.get(y) == null) return 0;
		if(_map.get(x) != null && _map.get(y) != null) return 0;
		if(_map.get(x) == null && _map.get(y) != null) 
			return getAssignDelta(y,x.getValue());
		else
			return getAssignDelta(x,y.getValue());
	}

	public VarIntLS[] getVariables(){
		return _x;
	}
	public void propagateInt(VarIntLS x, int val){		
		if(_map.get(x) == null) return;
		_value = _value - x.getOldValue() + x.getValue(); 
	}
	public void initPropagate(){
		// khoi tao du lieu
		_min = 0;
		_max = 0;
		_value = 0;
		for(int i = 0; i < _x.length; i++){
			_min += _x[i].getMinValue();
			_max += _x[i].getMaxValue();
			_value += _x[i].getValue();
		}
	}
	public LocalSearchManager getLocalSearchManager(){
		return _mgr;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[5];
		for(int i = 0; i < x.length; i++){
			x[i] = new VarIntLS(mgr,1,10);
			x[i].setValue(i);
		}
		MySum f = new MySum(x);
		IConstraint c = new LessOrEqual(f, 6);
		mgr.close();
		System.out.println("f = " + f.getValue() + ", c = " + c.violations());
		int d = f.getAssignDelta(x[4], 1);
		x[4].setValuePropagate(1);
		System.out.println("delta = " + d + ", new f = " + f.getValue());
		
		
	}

}
