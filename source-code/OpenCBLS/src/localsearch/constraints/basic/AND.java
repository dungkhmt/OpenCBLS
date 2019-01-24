package localsearch.constraints.basic;

import localsearch.model.AbstractInvariant;
import localsearch.model.IConstraint;
import localsearch.model.VarIntLS;
import localsearch.model.LocalSearchManager;
import localsearch.constraints.alldifferent.*;

import java.util.*;

public class AND extends AbstractInvariant implements IConstraint {
	private int _violations;
	private IConstraint[] _cstr;
	private VarIntLS[] _x;
	private LocalSearchManager _ls;
	private int[] _vx;
	private HashMap<VarIntLS, Integer> _map;
	private HashMap<VarIntLS, HashSet<IConstraint>> _mapVarIntLSConstraints = null;
	private int[] _vc;// _vc[i] is the violations of constraint _cstr[i]
	private HashMap<IConstraint, Integer> _mapC;
	
	public AND(IConstraint[] c){
		_cstr = new IConstraint[c.length];
		for(int i = 0; i < c.length; i++)
			_cstr[i] = c[i];
		
		post();
	}
	private void post(){
		_ls = _cstr[0].getLocalSearchManager();
		_mapC = new HashMap<IConstraint, Integer>();
		for(int i = 0; i < _cstr.length; i++)
			_mapC.put(_cstr[i], i);
		_vc = new int[_cstr.length];
		
		HashSet<VarIntLS> X = new HashSet<VarIntLS>();
		for(int i = 0; i < _cstr.length; i++){
			VarIntLS[] xi = _cstr[i].getVariables();
			for(int j = 0; j < xi.length; j++)
				X.add(xi[j]);
		}
		
		_x = new VarIntLS[X.size()];
		_map = new HashMap<VarIntLS,Integer>();
		_mapVarIntLSConstraints = new HashMap<VarIntLS, HashSet<IConstraint>>();
		
		
		//_x = (VarIntLS[])X.toArray();//new VarIntLS[X.size()];
		int idx = -1;
		Iterator it = X.iterator();
		while(it.hasNext()){
			VarIntLS y = (VarIntLS)it.next();
			idx++;
			_x[idx] = y;
			_map.put(_x[idx], idx);
			_mapVarIntLSConstraints.put(_x[idx],new HashSet<IConstraint>());
		}
		
		for(int i = 0; i < _cstr.length; i++){
			VarIntLS[] y = _cstr[i].getVariables();
			for(int j = 0; j < y.length; j++){
				_mapVarIntLSConstraints.get(y[j]).add(_cstr[i]);
			}
		}
		
		_vx = new int[_x.length];
		
		
		_ls.post(this);
	}
	public AND(IConstraint c1, IConstraint c2){
		_cstr = new IConstraint[2];
		_cstr[0] = c1;
		_cstr[1] = c2;
		
		post();
	}
	@Override
	public int violations() {
		// TODO Auto-generated method stub
		return _violations;
	}

	@Override
	public int violations(VarIntLS x) {
		// TODO Auto-generated method stub
		if(_map.get(x) == null) return 0;
		return _vx[_map.get(x)];
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		int delta = 0;
		HashSet<IConstraint> C = _mapVarIntLSConstraints.get(x);
		if(C == null) return 0;
		Iterator<IConstraint> it = C.iterator();
		while(it.hasNext()){
			IConstraint c = (IConstraint)it.next();
			delta += c.getAssignDelta(x, val);
		}
		return delta;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		int delta = 0;
		//HashSet<IConstraint> C = mapVarIntLSConstraints.get(x);
		HashSet<IConstraint> C = new HashSet<IConstraint>();
		HashSet<IConstraint> Cx = _mapVarIntLSConstraints.get(x);
		HashSet<IConstraint> Cy = _mapVarIntLSConstraints.get(y);
		
		if((Cx == null) && (Cy == null)){
			//System.out.println(name() + "::getSwapDelta, Error:: 2 variables not in Constraint System ");
			return 0;
		}
		
		if(Cx != null){
			for (IConstraint c : Cx) {
				C.add(c);
			}
		}
		
		if(Cy != null){
			for (IConstraint c : Cy) {
				C.add(c);
			}
		}
		
		Iterator<IConstraint> it = C.iterator();
		while(it.hasNext()){
			IConstraint c = (IConstraint)it.next();
			delta += c.getSwapDelta(x, y);
		}
		return delta;

	}

	public VarIntLS[] getVariables(){
		return _x;
	}
	public LocalSearchManager getLocalSearchManager() {
		return _ls;
	}
	
	@Override
	public void propagateInt(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		HashSet<IConstraint> C = _mapVarIntLSConstraints.get(x);
		if(C == null) return;
		Iterator<IConstraint> it = C.iterator();
		while(it.hasNext()){
			IConstraint c = (IConstraint)it.next();
			int idc = _mapC.get(c);
			_violations += (c.violations() - _vc[idc]);
			_vc[idc] = c.violations();
		}
		
		for(int i = 0; i < _x.length; i++){
			_vx[i] = 0;
			HashSet<IConstraint> Cx = _mapVarIntLSConstraints.get(_x[i]);
			if(Cx == null);
			Iterator itc = Cx.iterator();
			while(itc.hasNext()){
				IConstraint ci = (IConstraint)itc.next();
				_vx[i] += ci.violations();
			}
		}
	}

	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		_violations = 0;
		for(int i = 0; i < _cstr.length; i++){
			_vc[i] = _cstr[i].violations();
			_violations += _vc[i];
		}
		for(int i = 0; i < _vx.length; i++){
			_vx[i] = 0;
			HashSet<IConstraint> C = _mapVarIntLSConstraints.get(_x[i]);
			if(C == null) continue;
			Iterator it = C.iterator();
			while(it.hasNext()){
				IConstraint ci = (IConstraint)it.next();
				_vx[i] += ci.violations();
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LocalSearchManager ls = new LocalSearchManager();
		int n = 5;
		VarIntLS[] x = new VarIntLS[n];
		for(int i = 0; i < n; i++)
			x[i] = new VarIntLS(ls,1,n);
		IConstraint[] c = new IConstraint[3];
		c[0] = new LessOrEqual(x[0], x[1]);
		c[1] = new NotEqual(x[1],x[3]);
		c[2] = new AllDifferent(x);
		
		IConstraint cc = new IsEqual(x[1],x[2]);
		
		IConstraint oc = new OR(c[0],cc);
		
		ls.close();
		for(int i = 0; i < n; i++)
			System.out.println("x[" + i + "] = " + x[i].getValue());
		System.out.print("c[0] = "+ c[0].violations() + ", c[1] = " + c[1].violations() + 
				", c[2] = " + c[2].violations() + ", oc = " + oc.violations());
		
		int d = oc.getAssignDelta(x[0], 3);
		x[0].setValuePropagate(3);
		for(int i = 0; i < n; i++)
			System.out.println("x[" + i + "] = " + x[i].getValue());
		System.out.print("c[0] = "+ c[0].violations() + ", c[1] = " + c[1].violations() + 
				", c[2] = " + c[2].violations() + ", oc = " + oc.violations() + ", delta = " + d);
	}

}
