package localsearch.constraints.alldifferent;


import java.util.HashMap;

import localsearch.model.*;

public class AllDifferentVarIntLS extends AbstractInvariant implements IConstraint {

	private int 									_violations;
	private int 									_minValue;
	private int 									_maxValue;
	private int[] 									_occ;
	private VarIntLS[] 								_x;
	private LocalSearchManager 						_ls;
	private HashMap<VarIntLS, Integer>              _mapVariableIndex;
	private boolean									_posted;
		
	public AllDifferentVarIntLS(VarIntLS[] x) {
		_x = x;
		_ls = x[0].getLocalSearchManager();
		for (int i = 0; i < x.length; i++) {
			_minValue = Math.min(_minValue, x[i].getMinValue());
			_maxValue = Math.max(_maxValue, x[i].getMaxValue());
		}
		_posted = false;
		post();
		
	}
	public String name()
	{
		return "AllDifferentVarIntLS";
	}
	private void post() {
		if(_posted == true)
			return;
		_occ = new int[_maxValue - _minValue + 1];
		for (int i = 0; i < _occ.length; i++) _occ[i] = 0;
		
		_mapVariableIndex = new HashMap<VarIntLS, Integer>();
        for(int i = 0; i < _x.length; i++)
        {
       	 _mapVariableIndex.put(_x[i], i);
        }
        	
		_ls.post(this);
		_posted = true;
	}
	
	@Override
	public int violations() {
		// TODO Auto-generated method stub
		return _violations;
	}

	public int violations(VarIntLS x) {
		int v = _occ[x.getValue() - _minValue];
		return _mapVariableIndex.get(x) != null ? Math.max(0, v - 1) : 0;		
	}
	
	@Override
	public VarIntLS[] getVariables() {
		return _x;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		if ((_mapVariableIndex.get(x) == null) || (x.getValue() == val))
			return 0;
		int newV = _violations;
		int v1 = x.getValue() - _minValue;
		int v2 = val - _minValue;
		if (_occ[v1] > 1) newV--;
		if (_occ[v2] > 0) newV++;
			return newV - _violations;
	}
	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if ((_mapVariableIndex.get(x) == null) && (_mapVariableIndex.get(y) == null)) 
			return 0;
		else if ((_mapVariableIndex.get(x) != null) && (_mapVariableIndex.get(y) == null))
			return getAssignDelta(x, y.getValue());
		else if ((_mapVariableIndex.get(x) == null) && (_mapVariableIndex.get(y) != null))
			return getAssignDelta(y, x.getValue());
		else 
			return 0;			
	}
	@Override
	public void propagateInt(VarIntLS x, int val) {
		//System.out.println(name() + "::propagateInt(x[" + x.getID() + "], " + val + ")");
		if (_mapVariableIndex.get(x) == null)
			return;
		int v1 = x.getOldValue() - _minValue;
		int v2 = val - _minValue;
		if (v1 == v2) return;
		if (_occ[v1] > 1) _violations--;
		_occ[v1]--;
		if (_occ[v2] > 0) _violations++;
		_occ[v2]++;
	}
	
	@Override
	public void initPropagate() {
		//System.out.println(name() + "::initPropagate");
		_violations = 0;
		for (VarIntLS e : _x) _occ[e.getValue() - _minValue]++;
		for (int i = 0; i < _occ.length; i++) _violations += Math.max(0, _occ[i] - 1);
	}
	
	public void print(){
		for(int i = 0; i < _x.length; i++){
			System.out.println("_x[" + i + "] = " + _x[i].getValue());
		}
		for(int v = _minValue; v <= _maxValue; v++){
			System.out.println("_occ[" + v + "] = " + _occ[v]);
		}			
	}
	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int[] occ = new int[_maxValue - _minValue + 1];
		for(int i = 0; i < occ.length; i++){
			occ[i] = 0;
		}
		for(int i = 0; i < _x.length; i++){
			int v = _x[i].getValue();
			occ[v - _minValue]++;
		}
		for(int v = _minValue; v <= _maxValue; v++){
			if(_occ[v] != occ[v]){
				System.out.println(name() + "::verify failed, _occ[" + v + "] = " + _occ[v] + " differs from occ[" +
			v + "] = " + occ[v] + " by recomputation");
				return false;
			}
			
		}
		int violations = 0;
		for(int v = _minValue; v <= _maxValue; v++){
			violations += Math.max(occ[v] - 1, 0);
		}
		if(violations != _violations){
			System.out.println(name() + "::verify failed, _violations = " + _violations + " differs from violations = " 
		+ violations + " by recomputation");
		}
		return true;
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int n = 10000;
		LocalSearchManager _ls = new LocalSearchManager();
		ConstraintSystem S = new ConstraintSystem(_ls);
		VarIntLS[] x = new VarIntLS[n];
		for(int i = 0; i < n; i++)
		{
			x[i] = new VarIntLS(_ls, 0, 10000);			
		}
		AllDifferent _c = new AllDifferent(x);
		S.post(_c);
		S.close();
		_ls.close();
		localsearch.applications.Test T = new localsearch.applications.Test();
		T.test(_c,10000);
	}		
}
