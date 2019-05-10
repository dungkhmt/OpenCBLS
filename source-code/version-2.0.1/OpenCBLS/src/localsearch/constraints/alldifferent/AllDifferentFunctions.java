package localsearch.constraints.alldifferent;


import java.util.*;

import core.*;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.*;

public class AllDifferentFunctions extends AbstractInvariant implements IConstraint {

	private int 									_violations;
	private int 									_minValue;
	private int 									_maxValue;
	private int[] 									_occ;
	private IFunction[] 							_f;
	private VarIntLS[] 								_x;
	private LocalSearchManager 						_ls;
	private HashMap<VarIntLS, Vector<IFunction>> 	_map;
	private HashMap<VarIntLS, Integer>              _mapVariableIndex;
	private boolean									_posted;
		
	public AllDifferentFunctions(IFunction[] f) {
		_f = f;
		_ls = f[0].getLocalSearchManager();
		_posted = false;
		post();
	}
	
	private void post() {
		if(_posted == true)
			return;		
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		for (int i = 0; i < _f.length; i++){
			VarIntLS[] f_x = _f[i].getVariables();
			for (int j = 0; j < f_x.length; j++)
				_S.add(f_x[j]);
		}
		_map = new HashMap<VarIntLS, Vector<IFunction>>();
		int count = 0;
		_x = new VarIntLS[_S.size()];
		for (VarIntLS e : _S){
			_x[count++] = e;
			_map.put(e, new Vector<IFunction>());
		}
		
		for (int i = 0; i < _f.length; i++) {
			VarInt[] s = _f[i].getVariables();
			for (int j = 0; j < s.length; j++) 
				_map.get(s[j]).add(_f[i]);
		}
		
		_minValue = _f[0].getMinValue();
		_maxValue = _f[0].getMaxValue();
		for (int i = 1; i < _f.length; i++) {
			_minValue = Math.min(_minValue, _f[i].getMinValue());
			_maxValue = Math.max(_maxValue, _f[i].getMaxValue());
		}
		
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
		return _violations;
	}

	public int violations(VarInt x) {
		if (_mapVariableIndex.get(x) == null)
			return 0;
		
		Vector<IFunction> F = _map.get(x);
		HashSet<Integer> h = new HashSet<Integer>();
		for (IFunction f : F) {
			int v = f.getValue() - _minValue;
			h.add(v);
		}		
		int violations = 0;
		for (Integer e : h) violations += Math.max(0, _occ[e] - 1); 
		return violations;
	}
	
	@Override
	public VarIntLS[] getVariables() {
		return _x;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		if (_mapVariableIndex.get(x) == null)
			return 0;
		
		int nv = _violations;
		Vector<IFunction> F = _map.get(x);
		for (IFunction f : F) {
			int v = f.getValue() - _minValue; 
			if (_occ[v] > 1) nv--;
			_occ[v]--;
		}
		
		for (IFunction f : F) {
			int v = f.getValue() + f.getAssignDelta(x, val) - _minValue;
			if (_occ[v] > 0) nv++;
			_occ[v]++;
		}
		
		for (IFunction f : F) {
			int v1 = f.getValue() - _minValue;
			int v2 = f.getValue() + f.getAssignDelta(x, val) - _minValue;
			_occ[v1]++;
			_occ[v2]--;
		}		
		return nv - _violations;
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
		{	
		
			int nv = _violations;
			Vector<IFunction> Fx = _map.get(x);
			Vector<IFunction> Fy = _map.get(y);
			HashSet<IFunction> F = new HashSet<IFunction>();
		
			if(Fx != null){
				for (IFunction f : Fx) {
					F.add(f);
				}
			}
			
			if(Fy != null){
				for (IFunction f : Fy) {
					F.add(f);
				}
			}
			
			for (IFunction f : F) {
				int v = f.getValue() - _minValue; 
				if (_occ[v] > 1) nv--;
				_occ[v]--;
			}
			
			for (IFunction f : F) {
				int v = f.getValue() + f.getSwapDelta(x, y) - _minValue;
				if (_occ[v] > 0) nv++;
				_occ[v]++;
			}
			
			for (IFunction f : F) {
				int v1 = f.getValue() - _minValue;
				int v2 = f.getValue() + f.getSwapDelta(x, y) - _minValue;
				_occ[v1]++;
				_occ[v2]--;
			}		
			return nv - _violations;
		}
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		if (_mapVariableIndex.get(x) == null) 
			return;
		
		int oldVal = x.getOldValue();
		Vector<IFunction> F = _map.get(x);
		
		for (IFunction f : F) {
			int v = f.getValue() + f.getAssignDelta(x, oldVal) - _minValue;
			if (_occ[v] > 1) _violations--;
			_occ[v]--;
		}
		
		for (IFunction f : F) {
			int v = f.getValue() - _minValue; 
			if (_occ[v] > 0) _violations++;
			_occ[v]++;
		}		
	}
	
	@Override
	public void initPropagate() {
		_violations = 0;
		for (int i = 0; i < _f.length; i++){ 
			_occ[_f[i].getValue() - _minValue]++;			
		}
		for (int i = 0; i < _occ.length; i++){ 
			_violations += Math.max(0, _occ[i] - 1);
		}
	}
	
	@Override
	public int violations(VarIntLS x) {
		int violations = 0;
		Vector<IFunction> F = _map.get(x);
		for (IFunction f : F) {
			int v = f.getValue() - _minValue; 
			violations += (_occ[v] - 1);			
		}
		return violations;					
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}	
	
	public String name(){ 
		return "AllDiferentFunctions";
	}
	
	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int[] occ = new int[_maxValue - _minValue + 1];
		for(int i = 0; i < occ.length; i++){
			occ[i] = 0;
		}
		for(int i = 0; i < _f.length; i++){
			int v = _f[i].getValue();
			occ[v - _minValue]++;
		}
		for(int v = 0; v <= (_maxValue - _minValue); v++){
			if(_occ[v] != occ[v]){
				System.out.println(name() + "::verify failed, _occ[" + v + "] = " + _occ[v] + " differs from occ[" +
			v + "] = " + occ[v] + " by recomputation");
				return false;
			}
			
		}
		int violations = 0;
		for(int v = 0; v <= (_maxValue - _minValue); v++){
			violations += Math.max(occ[v] - 1, 0);
		}
		if(violations != _violations){
			System.out.println(name() + "::verify failed, _violations = " + _violations + " differs from violations = " 
		+ violations + " by recomputation");
		}
		return true;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int n = 1000;		
		java.util.Random R = new java.util.Random();
		
		LocalSearchManager ls = new LocalSearchManager();
		ConstraintSystem S = new ConstraintSystem(ls);
		VarIntLS[] x = new VarIntLS[n];
		for(int i = 0; i < n; i++){
			x[i] = new VarIntLS(ls,0,n-1);
			x[i].setValue(R.nextInt(n));
		}
		IFunction[] f = new IFunction[n];
		for(int i = 0; i < n; i++){
			f[i] = new FuncPlus(x[i], i);			
		}
		
		IConstraint _c = new AllDifferent(f);
		S.post(_c);
		S.close();
		ls.close();
		localsearch.applications.Test T = new localsearch.applications.Test();
		T.test(_c,100000);
	}	
}
