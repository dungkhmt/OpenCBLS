package localsearch.constraints.atmost;

import java.util.HashMap;
import localsearch.model.AbstractInvariant;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class AtmostVarintIntInt extends AbstractInvariant implements IConstraint{

	private int         							_violations;
	private int              						_n;
	private int               						_val;
	private VarIntLS[]         						_x;
	private LocalSearchManager      				_ls;
	private boolean                  				_posted;
	private int                      				_occ;
	private HashMap<VarIntLS, Integer>              _mapVariableIndex;
		
	//Semantic: At most n variables in array x assigned to val
	public AtmostVarintIntInt(VarIntLS[] x, int n, int val)
	{
		_x = x;
		_n = n;
		_val = val;
		_ls = x[0].getLocalSearchManager();
		_posted = false;
		post();		
	}
	public String name(){
		return "AtmostFuncIntInt";
	}
	void post()
	{
		if(_posted) return;
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

	@Override
	public int violations(VarIntLS x) {
		if(_mapVariableIndex.get(x) == null) 
			return 0;
		int occ = 1;		
		return Math.max(occ - _n, 0);
	}

	@Override
	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		return _x;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		if(_mapVariableIndex.get(x) == null) return 0;
		int nv;
		int newocc = _occ;
		if(x.getValue() == _val)
		{
			if(val != _val)
			{
				newocc--;
			}			
		}
		else
		{
			if(val == _val)
			{
				newocc++;
			}
		}
		if(newocc <= _n)
		{
			nv = 0;
		}
		else
		{
			nv = newocc - _n;
		}
		
		return nv - _violations;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if((_mapVariableIndex.get(x) == null) && (_mapVariableIndex.get(y) == null))
			return 0;
		if((_mapVariableIndex.get(x) != null) && (_mapVariableIndex.get(y) == null))
			return getAssignDelta(x, y.getValue());
		if((_mapVariableIndex.get(x) == null) && (_mapVariableIndex.get(y) != null))
			return getAssignDelta(y, x.getValue());
		return 0;
	}
	@Override
	public void propagateInt(VarIntLS x, int val)
	{
		if(_mapVariableIndex.get(x) == null) 
			return;
		int nv;
		int t = x.getOldValue();
		if(t == _val)
		{
			if(val != _val)
			{
				_occ--;
			}
		}
		else 
		{
			if(val == _val)
			{
				_occ++;
			}			
		}
		if(_occ <= _n)
		{
			nv = 0;
		}
		else
		{
			nv = _occ - _n;
		}
		_violations = nv;
	}
	
	@Override
	public void initPropagate()
	{
		int occ = 0;
		for(int i = 0; i < _x.length; i++)
		{
			if(_x[i].getValue() == _val)
			{
				occ++;
			}
		}
		_occ = occ;
		_violations = Math.max(0, _occ - _n);		
	}	
	
	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int occ = 0, violations = 0;
		for(int i = 0; i < _x.length; i++)
		{
			if(_x[i].getValue() == _val)
			{
				occ++;
			}
		}
		violations = Math.max(0, occ - _n);
		if((violations == _violations) && (occ == _occ))
			return true;
		return false;
	}
	
	public static void main(String[] args)
	{
		LocalSearchManager ls = new LocalSearchManager();
		ConstraintSystem S = new ConstraintSystem(ls);
		VarIntLS[] x = new VarIntLS[10000];
		for(int i = 0; i < x.length; i++)
		{
		  	x[i] = new VarIntLS(ls, 0, 10000);
		   	x[i].setValue(0);
		}    
		AtMost _c = new AtMost(x, 1, 0);
		S.post(_c);
		S.close();
		ls.close();
		localsearch.applications.Test T = new localsearch.applications.Test();
		T.test(_c,10000);
	}	
}
