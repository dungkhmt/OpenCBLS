package localsearch.constraints.atmost;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import localsearch.functions.basic.FuncPlus;
import localsearch.model.AbstractInvariant;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class AtmostFunIntInt extends AbstractInvariant implements IConstraint {
	private int         							_violations;
	private IFunction[]      						_f;
	private int              						_n;
	private int               						_val;
	private VarIntLS[]         						_x;
	private LocalSearchManager      				_ls;
	private HashMap<VarIntLS, Vector<IFunction>>    _map;
	private HashMap<VarIntLS, Integer>              _mapVariableIndex;
	private boolean                  				_posted;
	private int                      				_occ;
		
	//Semantic: At most n functions in array f[] have value equal to val
	public AtmostFunIntInt(IFunction[] f,int n,int val)
	{
		_f = f;
		_n = n;
		_val = val;
		_ls = f[0].getLocalSearchManager();
		_posted = false;
		post();		
	}
	public String name(){
		return "AtmostFuncIntInt";
	}
	void post()
	{
		if(_posted) return;
		
		_posted = true;
		
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		if(_f == null){
			System.out.println(name() + "::post(), _input function f is null");
		}
		for(int i = 0; i < _f.length; i++)
		{
			VarIntLS[] f_x = _f[i].getVariables();
			if(f_x != null)
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
		
		_map=new HashMap<VarIntLS, Vector<IFunction>>();
		for(VarIntLS e:_S)
		{
			_map.put(e,new Vector<IFunction>());
		}
         for(int i=0;i<_f.length;i++)
         {
        	 VarIntLS[] s=_f[i].getVariables();
        	 if(s!=null)
        	 {
        		 for(int j=0;j<s.length;j++)
        		 {
        			 _map.get(s[j]).add(_f[i]);
        		 }
        	 }
         }
         
         _mapVariableIndex = new HashMap<VarIntLS, Integer>();
         for(int i=0;i<_x.length;i++)
         {
        	 _mapVariableIndex.put(_x[i],i);
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
		
		Vector<IFunction> Fx = _map.get(x);
		int occ = 0;
		for(IFunction f:Fx){
			if (f.getValue() == _val)
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
		// TODO Auto-generated method stub
		if(_mapVariableIndex.get(x)==null) return 0;
		int nv;
		int newocc = _occ;
		Vector<IFunction> F = _map.get(x);
		for(IFunction f:F)
		{
			int nfv = f.getValue() + f.getAssignDelta(x, val); 
			if(f.getValue()==_val)
			{
				if(nfv != _val)
				{
					newocc--;
				}			
			}
			else
			{
				if(nfv == _val)
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
		
		Vector<IFunction> Fx = _map.get(x);
		Vector<IFunction> Fy = _map.get(y);
		HashSet<IFunction> F = new HashSet<IFunction>();
		
		
		for (IFunction f : Fx){
			F.add(f);
		}
		
		for (IFunction f : Fy){
			F.add(f);
		}
		
		for (IFunction f : F){
			if(f.getValue() + f.getSwapDelta(x,y)==_val)
			{
				if(f.getValue()!=_val)
				{
					newocc++;
				}				
			}
			else
			{
				if(f.getValue()==_val)
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
	public void propagateInt(VarIntLS x,int val)
	{
		if(_mapVariableIndex.get(x)==null) return;
		int nv;
		int t = x.getOldValue();
		Vector<IFunction> F=_map.get(x);
		for(IFunction f:F)
		{
			if(f.getValue()+f.getAssignDelta(x,t)==_val)
			{
				if(f.getValue() != _val)
				{
					_occ--;
				}				
			}
			else
			{
				if(f.getValue() == _val)
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
		for(int i = 0; i < _f.length; i++)
		{
			if(_f[i].getValue() == _val)
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
		for(int i = 0; i < _f.length; i++)
		{
			if(_f[i].getValue() == _val)
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
		    	x[i].setValue(i);
		    }
		    
		    IFunction[] f = new IFunction[x.length];
		    for(int i = 0 ; i < f.length; i++)
		    {
		    	f[i] = new FuncPlus(x[i], 0);
		    }
		    
		    AtMost _c = new AtMost(f, 0, 0);
		    S.post(_c);
		    
		    S.close();
		    ls.close();
		localsearch.applications.Test T = new localsearch.applications.Test();
		T.test(_c,10000);
	
	}
}
