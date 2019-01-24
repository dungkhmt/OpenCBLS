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

public class AtmostFunInt extends AbstractInvariant implements IConstraint {
	private int              						_violations;
	private IFunction[]      						_f;
	private int[]              						_occ;
	private int[]									_n;
	private VarIntLS[]          					_x;
	private LocalSearchManager      				_ls;
	private HashMap<VarIntLS,Vector<IFunction>>   	_map;
	private HashMap<VarIntLS, Integer>              _mapVariableIndex;
	private boolean									_posted;
	private int										_minValue;
	private int										_maxValue;

	
	//Semantic: At most n[v] functions in array f[] have value  
	//          equal to v, with v in the range of 0 to the length of n[]
	public AtmostFunInt(IFunction[] f,int[] n)
	{
		_f = f;
		_n = n;
		_ls = f[0].getLocalSearchManager();
		_posted = false;
		post();
	}
	public String name(){
		return "AtmostConstraintInt";
	}
	
	void post()
	{
		if(_posted) return;
		_posted = true;
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		for(int i = 0; i < _f.length; i++)
		{
			VarIntLS[] f_x = _f[i].getVariables();
			if(f_x != null)
			{
				for(int j = 0; j < f_x.length; j++)
				_S.add(f_x[j]);
			}
		}
		
		_x = new VarIntLS[_S.size()];
		int u = 0;
		for(VarIntLS e:_S)
		{
			_x[u] = e;
			u++;
		}
		_map = new HashMap<VarIntLS, Vector<IFunction>>();
		for(VarIntLS e:_S)
		{
			_map.put(e, new Vector<IFunction>());			
		}
		for(int i = 0; i < _f.length; i++)
		{
			VarIntLS[] s = _f[i].getVariables();
			if(s!=null)
			{
				for(int j = 0; j < s.length; j++)
				{
					_map.get(s[j]).add(_f[i]);
				}
			}
		}
		_mapVariableIndex = new HashMap<VarIntLS, Integer>();
		for(int i = 0; i < _x.length; i++)
		{
			_mapVariableIndex.put(_x[i], i);
		}
		
		_minValue = 0;
		_maxValue = _n.length - 1; 
		_occ = new int[_n.length];
		for (int i = 0; i < _occ.length; i++) _occ[i] = 0;
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
		int violations = 0;
		Vector<IFunction> Fx = _map.get(x);
		for(IFunction f:Fx)
		{
			int tmp = f.getValue();
			if((tmp < _minValue)||(tmp > _maxValue)){
				System.out.printf("Error: The value of a function in array f exceeds the bound of array n" );
				assert(false);
			}
			else{
				violations += Math.max(0, _occ[tmp] - _n[tmp]);				 
			}			
		}
		return violations;
	
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
		Vector<IFunction> Fx = _map.get(x);
		int nv = _violations;
		for(IFunction f : Fx){
			if((f.getValue() < 0)||(f.getValue() > _occ.length))
			{
				System.out.printf("Error: The value of a function in array f exceeds the bound of array n" );
				assert(false);
			}
			else {
				int nfv = f.getValue() + f.getAssignDelta(x, val);
				if ((nfv < 0)||(nfv > _occ.length)){
					System.out.printf("Error: The value of a function in array f exceeds the bound of array n" );
					assert(false);
				}
				else{
					if(_occ[f.getValue()] > _n[f.getValue()])
						nv--;
					_occ[f.getValue()]--;
					if(_occ[nfv] >= _n[nfv])
						nv++;
					_occ[nfv]++;
				}
			}			
		}
		for(IFunction f : Fx){
			int nfv = f.getValue() + f.getAssignDelta(x, val);
			_occ[nfv]--;
			_occ[f.getValue()]++;
		} 
		
		return nv - _violations;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if((_mapVariableIndex.get(x) == null) && (_mapVariableIndex.get(y) == null)) 
			return 0;
		else if (_mapVariableIndex.get(y) == null)
			return getAssignDelta(x, y.getValue());
		else if (_mapVariableIndex.get(x) == null)
			return getAssignDelta(y, x.getValue());
		else{
			int nv = _violations;
			Vector<IFunction> Fx = _map.get(x);
			Vector<IFunction> Fy = _map.get(y);
			HashSet<IFunction> h = new HashSet<IFunction>();
			for (IFunction f : Fx){
				h.add(f);
			}
			for (IFunction f : Fy){
				h.add(f);
			}
			for (IFunction f : h){
				if((f.getValue() < 0)||(f.getValue() > _occ.length))
				{
					System.out.printf("Error: The value of a function in array f exceeds the bound of array n" );
					assert(false);
				}
				else {
					int nfv = f.getValue() + f.getSwapDelta(x, y);
					if ((nfv < 0)||(nfv > _occ.length)){
						System.out.printf("Error: The value of a function in _f exceeds the bound of array _n" );
						assert(false);
					}
					else{
						if(_occ[f.getValue()] > _n[f.getValue()])
							nv--;
						_occ[f.getValue()]--;
						if(_occ[nfv] >= _n[nfv])
							nv++;
						_occ[nfv]++;
					}
				}
			}
			for (IFunction f : h){
				int nfv = f.getValue() + f.getSwapDelta(x, y);
				_occ[f.getValue()]++;
				_occ[nfv]--;				
			}
			return nv - _violations;
		}		
	}
	@Override
	public void propagateInt(VarIntLS x,int val)
	{	
		if(_mapVariableIndex.get(x) == null) return;
		Vector<IFunction> Fx = _map.get(x);
		int nv = _violations;
		for(IFunction f:Fx){
			if((f.getValue() < 0)||(f.getValue() > _occ.length))
			{
				System.out.printf("Error: The value of a function in array f exceeds the bound of array n" );
				assert(false);
			}
			else {
				int oldVal = x.getOldValue();
				int ofv = f.getValue() + f.getAssignDelta(x, oldVal);
				if ((ofv < 0)||(ofv > _occ.length)){
					System.out.printf("Error: The value of a function in array f exceeds the bound of array n" );
					assert(false);
				}
				else{
					if(_occ[ofv] > _n[ofv]){
						//System.out.println("_occ["+ocv+"] = "+_occ[ocv]+ "\n_n["+ocv+"] = " + _n[ocv] + "\nnv--");
						nv--;
					}
					_occ[ofv]--;
					if(_occ[f.getValue()] >= _n[f.getValue()]){
						//System.out.println("_occ["+c.violations()+"] = "+_occ[c.violations()]+ "\n_n["+c.violations()+"] = " + _n[c.violations()] + "\nnv++");
						nv++;
					}
					_occ[f.getValue()]++;
					
				}
			}			
		}
		
		_violations = nv;
	}
	
	@Override
	public void initPropagate()
	{
		for(int i = 0; i < _f.length; i++)
		{
			int tmp = _f[i].getValue();
			if((tmp < _minValue)||(tmp > _maxValue)){
				System.out.printf("Error: The value of a function in array f exceeds the bound of array n" );
				assert(false);
			}
			else{
				_occ[tmp]++; 
			}			
		}
		
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
		for(int i = 0; i < _f.length; i++)
		{
			int tmp = _f[i].getValue();
			if((tmp > _maxValue)||(tmp < _minValue)){
				System.out.printf("Error: The value of a function in array f exceeds the bound of array n" );
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
	public static void main(String[] args)
	{
		LocalSearchManager ls=new LocalSearchManager();
		ConstraintSystem S=new ConstraintSystem(ls);
		VarIntLS[] x=new VarIntLS[1000];
		for(int i = 0; i < x.length; i++)
		{
			x[i]=new VarIntLS(ls, 0, 1000);
			x[i].setValue(0);
		}
		
		IFunction[] f = new IFunction[x.length];
		for(int i = 0; i < f.length; i++)
		{
			f[i] = new FuncPlus(x[i], i);
			
		}
		int n[] = new int[10000];
		for(int i = 0; i < 10000; i++){
			n[i] = 1;
		}		
		IConstraint _c = new AtMost(f, n);
		S.post(_c);
		S.close();
		ls.close();
		localsearch.applications.Test T = new localsearch.applications.Test();
		T.test(_c,10000);		
	}
}
