package localsearch.constraints.atmost;

import java.util.HashMap;

import localsearch.model.AbstractInvariant;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;


public class AtmostVarintInt extends AbstractInvariant implements IConstraint {
	private int              						_violations;
	private int[]              						_occ;
	private int[]									_n;	
	private VarIntLS[]          					_x;
	private LocalSearchManager      				_ls;
	private boolean									_posted;
	private HashMap<VarIntLS, Integer>              _mapVariableIndex;
	private int 									_minValue;
	private int										_maxValue;						

	//Semantic: At most n[v] variables in array x assigned to v, , 
	//			with v in the range of 0 to the length of n[]
	public AtmostVarintInt(VarIntLS[] x,int[] n)
	{
		_x = x;
		_ls = x[0].getLocalSearchManager();
		_n = n;
		for (int i = 0; i < x.length; i++) {
			_minValue = Math.min(_minValue, x[i].getMinValue());
			_maxValue = Math.max(_maxValue, x[i].getMaxValue());
		}
		if((_minValue < 0) || (_maxValue > n.length)){
			System.out.printf("This VarIntLS array is invalid to constraint Atmost");
			assert(false);
		}
		_minValue = 0;
		_maxValue = _n.length - 1;
		_occ = new int[_n.length];
		_posted = false;
		post();
	}
	
	public String name(){
		return "AtmostVarintInt";
	}
	
	void post()
	{
		if(_posted) return;
		_posted = true;		
		for (int i = 0; i < _occ.length; i++) 
			_occ[i] = 0;
	
		_mapVariableIndex = new HashMap<VarIntLS, Integer>();
        for(int i = 0; i < _x.length; i++)
        {
       	 _mapVariableIndex.put(_x[i], i);
        }
		
        _ls.post(this);
	}
	
	@Override
	public int violations() {
		// TODO Auto-generated method stub
		return _violations;
	}

	@Override
	public int violations(VarIntLS x) {
		if(_mapVariableIndex.get(x) == null) 
			return 0;
		int v = _occ[x.getValue()] - _n[x.getValue()];
		return Math.max(v, 0);
	}
	@Override
	public VarIntLS[] getVariables() {
		return _x;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		//System.out.println("called");
		
		if(val > _n.length){
			System.out.print("Cannot propagate a value which exceeds the length of array n or less than 0"); 
			assert(false);
		}
		if (!x.IsElement(_x) || x.getValue() == val) 
			return 0;
		int newV = _violations;
		int v1 = x.getValue();
		int v2 = val;
		if (_occ[v1] > _n[v1]) newV--;
		if (_occ[v2] >= _n[v2]) newV++;
		return newV - _violations;
			
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if((x.getValue() < _minValue)||(y.getValue() < _minValue)||
		   (x.getValue() > _maxValue)||(x.getValue() > _maxValue)){
			
			System.out.print("Error: The value of a Varint in array x exceeds the bound of array n"); 
			assert(false);
			return 0;
		}
		if(x.getValue() == y.getValue())
			return 0;
		if(!x.IsElement(_x) && !y.IsElement(_x)){
			return 0;
		}
		else if(x.IsElement(_x) && !y.IsElement(_x)){
			return getAssignDelta(x, y.getValue());
		}
		else if(!x.IsElement(_x) && y.IsElement(_x)){
			return getAssignDelta(y, x.getValue());
		}
		else{
			return 0;
		}
	}
	@Override
	public void propagateInt(VarIntLS x, int val)
	{
		if((val > _maxValue)||(val < _minValue)){
			System.out.print("Error: The value of a Varint in array x exceeds the bound of array n"); 
			assert(false);
		}
		if(!x.IsElement(_x))
			return;
		int oldVal = x.getOldValue();
		if (oldVal == val)
			return;
		if (_occ[oldVal] > _n[oldVal])
			_violations--;
		_occ[oldVal]--;
		if(_violations < 0){
			System.out.println("Error: The number of violations must be greater than 0");
			assert(false);			
		}
		if (_occ[val] >= _n[val])
			_violations++;
		_occ[val]++;
	}
	
	@Override
	public void initPropagate()
	{
		_violations = 0;
		for (VarIntLS e : _x) 
			_occ[e.getValue()]++;
		for (int i = 0; i < _occ.length; i++) 
			_violations += Math.max(0, _occ[i] - _n[i]);		
	}
		
	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int violations = 0;
		boolean result = false;
		int[] occ = new int[_occ.length];
		for(int i = 0; i < _x.length; i++)
		{
			int tmp = _x[i].getValue();
			if((tmp > _maxValue)||(tmp < _minValue)){
				System.out.print("Error: The value of a Varint in array x exceeds the bound of array n"); 
				assert(false);
			}
			else{
				occ[tmp]++; 
			}			
		}
		
		for (int i = 0; i < occ.length; i++) 
			violations += Math.max(0, occ[i] - _n[i]);
		
		//Check _violations and _occ[]
		if(violations == _violations){
			result = true;
			for(int i = 0; i < occ.length; i++){
				if(occ[i] != _occ[i]){
					result = false;
				}
			}			
		}
		return result;		
	}
	public static void main(String[] args){
		LocalSearchManager ls = new LocalSearchManager();
		ConstraintSystem S = new ConstraintSystem(ls);
		int Size = 100;
		VarIntLS[] x = new VarIntLS[Size];
		for (int i = 0; i < x.length; i++) {
			x[i] = new VarIntLS(ls, 0, Size - 1);
			x[i].setValue(0);
		}
		int[] n = new int[Size];
		for (int i = 0; i < n.length; i++) {
			n[i] = 1;
		}
		
		AtMost _c = new AtMost(x, n);
		S.post(_c);
		S.close();
		ls.close();
		localsearch.applications.Test T = new localsearch.applications.Test();
		T.test(_c,10000);		
	}
}
