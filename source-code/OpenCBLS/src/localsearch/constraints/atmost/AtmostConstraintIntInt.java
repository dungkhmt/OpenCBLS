package localsearch.constraints.atmost;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import localsearch.constraints.basic.IsEqual;
import localsearch.model.AbstractInvariant;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class AtmostConstraintIntInt extends AbstractInvariant implements
		IConstraint {
	private int         								_violations;
	private IConstraint[]     							_c;
	private int 										_n;
	private int               							_occ;
	private int                							_val;
	private LocalSearchManager     						_ls;
	private VarIntLS[]             						_x;
	private HashMap<VarIntLS, Vector<IConstraint>>    	_map;
	private HashMap<VarIntLS, Integer>     				_mapVariableIndex;
	
	//Semantic: At most n constraints in array c have the number of violations 
	//equal to val
	public AtmostConstraintIntInt(IConstraint[] c, int n, int val)
	{
		_c = c;
		_n = n;
		_val = val;
		_ls = c[0].getLocalSearchManager();
		post();		
	}
	
	public String Name(){
		return "AtmostConstraintIntInt"; 
	}
	
	void post()
	{
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		for(int i = 0; i < _c.length; i++)
		{
			VarIntLS[] f_x = _c[i].getVariables();
			if(f_x!=null)
			{
				for(int j = 0; j < f_x.length; j++)
				{
					_S.add(f_x[j]);
				}
			}
		}
		_x = new VarIntLS[_S.size()];
		int u = 0;
		for(VarIntLS e:_S)
		{
			_x[u] = e;
			u++;
		}
		_mapVariableIndex = new HashMap<VarIntLS, Integer>();
		for(int i = 0; i < _x.length; i++)
		{
			_mapVariableIndex.put(_x[i], i);
		}
		_map = new HashMap<VarIntLS, Vector<IConstraint>>();
		for(VarIntLS e:_S)
		{
			_map.put(e, new Vector<IConstraint>());
		}
		for(int i = 0; i < _c.length; i++)
		{
			VarIntLS[] s = _c[i].getVariables();
			if(s != null)
			{
				for(int j = 0; j < s.length; j++)
				{
					_map.get(s[j]).add(_c[i]);
				}
			}
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
		// TODO Auto-generated method stub
		Vector<IConstraint> Cx = _map.get(x);
		int occ = 0;
		for(IConstraint c:Cx){
			if (c.violations() == _val)
				occ++;
		}
		return Math.max(occ - _n, 0);

	}
	@Override
	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		return _x;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
        if(_mapVariableIndex.get(x) == null) return 0;
        
        int nv;
        int newocc = _occ;
        Vector<IConstraint> C = _map.get(x);
        for(IConstraint c:C)
        {
        	int ncv = c.violations() + c.getAssignDelta(x, val); 
        	if(c.violations() == _val)
        	{
        		if(ncv != _val)
        		{
        			newocc--;
        		}        		
        	}
        	else
        	{
        		if(ncv == _val)
        		{
        			newocc++;
        		}        	
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
		int newocc = _occ;
		int nv;
		if((_mapVariableIndex.get(x) == null) && (_mapVariableIndex.get(y) == null))
			return 0;
		if((_mapVariableIndex.get(x) != null) && (_mapVariableIndex.get(y) == null))
			return getAssignDelta(x, y.getValue());
		if((_mapVariableIndex.get(x) == null) && (_mapVariableIndex.get(y) != null))
			return getAssignDelta(y, x.getValue());
		
		Vector<IConstraint> Cx = _map.get(x);
		Vector<IConstraint> Cy = _map.get(y);
		HashSet<IConstraint> C = new HashSet<IConstraint>();
				
		for (IConstraint c : Cx){
			C.add(c);
		}
		
		for (IConstraint c : Cy){
			C.add(c);
		}
		
		for (IConstraint c : C){
			if((c.violations() + c.getSwapDelta(x,y)) == _val)
			{
				if(c.violations() != _val)
				{
					newocc++;
				}				
			}
			else
			{
				if(c.violations() == _val)
				{
					newocc--;
				}
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
	public void propagateInt(VarIntLS x, int val)
	{
		if(_mapVariableIndex.get(x) == null)
			return;
		int nv;
		int t = x.getOldValue();
		Vector<IConstraint> C = _map.get(x);
		for(IConstraint c:C)
		{
			if((c.violations() + c.getAssignDelta(x,t)) == _val)
			{
				if(c.violations() != _val)
				{
					_occ--;
				}				
			}
			else
			{
				if(c.violations() == _val)
				{
					_occ++;
				}
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
	public void initPropagate()
	{
		int occ = 0;
		for(int i = 0; i < _c.length; i++)
		{
			if(_c[i].violations() == _val)
			{
				occ++;
			}
			
		}
		if(occ <= _n)
		{
			_violations = 0;
			
		}
		else
		{
			_violations = occ - _n;
		}
		_occ = occ;
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
		for(int i = 0; i<_c.length; i++)
		{
			if(_c[i].violations() == _val)
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
		
		IConstraint[] c = new IConstraint[x.length];
		for(int i = 0; i < c.length; i++)
		{
			c[i] = new IsEqual(x[i], 0);			
		}
		int n = 1;
		int val = 0;
		IConstraint _c = new AtMost(c, n, val);
		S.post(_c);
		S.close();
		ls.close();
		localsearch.applications.Test T = new localsearch.applications.Test();
		T.test(_c,10000);		
	}
}
