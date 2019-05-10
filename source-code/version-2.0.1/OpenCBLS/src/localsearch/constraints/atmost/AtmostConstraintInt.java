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

public class AtmostConstraintInt extends AbstractInvariant implements IConstraint {
	private int              						_violations;
	private IConstraint[]      						_c;
	private int[]              						_occ;
	private int[]									_n;
	private VarIntLS[]          					_x;
	private LocalSearchManager      				_ls;
	private HashMap<VarIntLS,Vector<IConstraint>>   _map;
	private HashMap<VarIntLS, Integer>              _mapVariableIndex;
	private boolean									_posted;
	private int										_minValue;
	private int										_maxValue;

	
	//Semantic: At most n[v] constraints in array c[] have the number of violations  
	//          equal to v, with v in the range of 0 to the length of n[]
	public AtmostConstraintInt(IConstraint[] c,int[] n)
	{
		_c = c;
		_n = n;
		_ls = c[0].getLocalSearchManager();
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
		for(int i = 0; i < _c.length; i++)
		{
			VarIntLS[] f_x = _c[i].getVariables();
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
		_map = new HashMap<VarIntLS, Vector<IConstraint>>();
		for(VarIntLS e:_S)
		{
			_map.put(e, new Vector<IConstraint>());			
		}
		for(int i = 0; i < _c.length; i++)
		{
			VarIntLS[] s = _c[i].getVariables();
			if(s!=null)
			{
				for(int j = 0; j < s.length; j++)
				{
					_map.get(s[j]).add(_c[i]);
				}
			}
		}
		_mapVariableIndex = new HashMap<VarIntLS, Integer>();
		for(int i = 0; i < _x.length; i++)
		{
			_mapVariableIndex.put(_x[i],i);
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
		Vector<IConstraint> Cx = _map.get(x);
		for(IConstraint c:Cx)
		{
			int tmp = c.violations();
			if(tmp > _maxValue){
				System.out.printf("Error: The number of violations of a constraint in array c exceeds the bound of array n" );
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
		Vector<IConstraint> Cx = _map.get(x);
		int nv = _violations;
		for(IConstraint c : Cx){
			if((c.violations() < 0)||(c.violations() > _occ.length))
			{
				System.out.printf("Error: The number of violations of a constraint in array c exceeds the bound of array n" );
				assert(false);
			}
			else {
				int ncv = c.violations() + c.getAssignDelta(x, val);
				if ((ncv < 0)||(ncv > _occ.length)){
					System.out.println("Error: The number of violations of a constraint in c exceed the bound of array n");
					assert(false);
				}
				else{
					if(_occ[c.violations()] > _n[c.violations()])
						nv--;
					_occ[c.violations()]--;
					if(_occ[ncv] >= _n[ncv])
						nv++;
					_occ[ncv]++;
				}
			}			
		}
		for(IConstraint c : Cx){
			int ncv = c.violations() + c.getAssignDelta(x, val);
			_occ[ncv]--;
			_occ[c.violations()]++;
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
			Vector<IConstraint> Cx = _map.get(x);
			Vector<IConstraint> Cy = _map.get(y);
			HashSet<IConstraint> h = new HashSet<IConstraint>();
			for (IConstraint c : Cx){
				h.add(c);
			}
			for (IConstraint c : Cy){
				h.add(c);
			}
			for (IConstraint c : h){
				if((c.violations() < 0)||(c.violations() > _occ.length))
				{
					System.out.printf("Error: The number of violations of a constraint in array c exceeds the bound of array n" );
					assert(false);
				}
				else {
					int ncv = c.violations() + c.getSwapDelta(x, y);
					if ((ncv < 0)||(ncv > _occ.length)){
						System.out.printf("Error: The number of violations of a constraint in array c exceeds the bound of array n" );
						assert(false);
					}
					else{
						if(_occ[c.violations()] > _n[c.violations()])
							nv--;
						_occ[c.violations()]--;
						if(_occ[ncv] >= _n[ncv])
							nv++;
						_occ[ncv]++;
					}
				}
			}
			for (IConstraint c : h){
				int ncv = c.violations() + c.getSwapDelta(x, y);
				_occ[c.violations()]++;
				_occ[ncv]--;				
			}
			return nv - _violations;
		}		
	}
	@Override
	public void propagateInt(VarIntLS x,int val)
	{	
		if(_mapVariableIndex.get(x) == null) return;
		Vector<IConstraint> Cx = _map.get(x);
		int nv = _violations;
		for(IConstraint c:Cx){
			if((c.violations() < 0)||(c.violations() > _occ.length))
			{
				System.out.printf("Error: The number of violations of a constraint in array c exceeds the bound of array n" );
				assert(false);
			}
			else {
				int oldVal = x.getOldValue();
				int ocv = c.violations() + c.getAssignDelta(x, oldVal);
				if ((ocv < 0)||(ocv > _occ.length)){
					System.out.printf("Error: The number of violations of a constraint in array c exceeds the bound of array n" );
					assert(false);
				}
				else{
					if(_occ[ocv] > _n[ocv]){
						//System.out.println("_occ["+ocv+"] = "+_occ[ocv]+ "\n_n["+ocv+"] = " + _n[ocv] + "\nnv--");
						nv--;
					}
					_occ[ocv]--;
					if(_occ[c.violations()] >= _n[c.violations()]){
						//System.out.println("_occ["+c.violations()+"] = "+_occ[c.violations()]+ "\n_n["+c.violations()+"] = " + _n[c.violations()] + "\nnv++");
						nv++;
					}
					_occ[c.violations()]++;
					
				}
			}			
		}
		
		_violations = nv;
	}
	
	@Override
	public void initPropagate()
	{
		for(int i = 0; i < _c.length; i++)
		{
			int tmp = _c[i].violations();
			if(tmp > _maxValue){
				System.out.printf("Error: The number of violations of a constraint in array c exceeds the bound of array n" );
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
		for(int i = 0; i < _c.length; i++)
		{
			int tmp = _c[i].violations();
			if((tmp > _maxValue)||(tmp < _minValue)){
				System.out.printf("Error: The number of violations of a constraint in array c exceeds the bound of array n" );
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
		VarIntLS[] x=new VarIntLS[10000];
		for(int i = 0; i < x.length; i++)
		{
			x[i]=new VarIntLS(ls, 0, 9999);
			x[i].setValue(0);
		}
		
		IConstraint[] c=new IConstraint[x.length];
		for(int i = 0; i < c.length; i++)
		{
			c[i] = new IsEqual(x[i], 0);
			
		}
		int n[] = new int[c.length];
		for(int i = 0; i < c.length; i++){
			n[i] = 1;
		}		
		IConstraint _c = new AtMost(c, n);
		S.post(_c);
		S.close();
		ls.close();
		localsearch.applications.Test T = new localsearch.applications.Test();
		T.test(_c,10000);		
	}
}
