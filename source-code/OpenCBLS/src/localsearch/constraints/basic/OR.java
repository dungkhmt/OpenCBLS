package localsearch.constraints.basic;

import localsearch.model.AbstractInvariant;
import localsearch.model.IConstraint;
import localsearch.model.VarIntLS;
import localsearch.model.LocalSearchManager;
import localsearch.constraints.alldifferent.*;
import java.util.*;

public class OR extends AbstractInvariant implements IConstraint {
	private int _violations;
	private IConstraint[] _cstr;
	private VarIntLS[] _x;
	private LocalSearchManager _ls;
	private int[] _vx;
	private HashMap<VarIntLS, Integer> _map;
	public OR(IConstraint[] c){
		_cstr = new IConstraint[c.length];
		for(int i = 0; i < c.length; i++)
			_cstr[i] = c[i];
		
		post();
	}
	private void post(){
		_ls = _cstr[0].getLocalSearchManager();
		HashSet<VarIntLS> X = new HashSet<VarIntLS>();
		for(int i = 0; i < _cstr.length; i++){
			VarIntLS[] xi = _cstr[i].getVariables();
			for(int j = 0; j < xi.length; j++)
				X.add(xi[j]);
		}
		
		_x = new VarIntLS[X.size()];
		_map = new HashMap<VarIntLS,Integer>();
		//_x = (VarIntLS[])X.toArray();//new VarIntLS[X.size()];
		int idx = -1;
		Iterator it = X.iterator();
		while(it.hasNext()){
			VarIntLS y = (VarIntLS)it.next();
			idx++;
			_x[idx] = y;
			_map.put(_x[idx], idx);
		}
		_vx = new int[_x.length];
		
		
		_ls.post(this);
	}
	public OR(IConstraint c1, IConstraint c2){
		_cstr = new IConstraint[2];
		_cstr[0] = c1;
		_cstr[1] = c2;
		_ls = c1.getLocalSearchManager();
		if(_ls == null){
			System.out.println(name() + "::post, _ls is NULL BUS?????????");
			
		}
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
		int newV = 100000000;
		for(int i = 0; i < _cstr.length; i++){
			int nvi = _cstr[i].getAssignDelta(x, val) + _cstr[i].violations();
			newV = newV < nvi ? newV : nvi;
		}
		
		return newV - _violations;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		int newV = 100000000;
		for(int i = 0; i < _cstr.length; i++){
			int nvi = _cstr[i].getSwapDelta(x, y) + _cstr[i].violations();
			newV = newV < nvi ? newV : nvi;
		}
		return newV - _violations;
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
		initPropagate();
	}

	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		_violations = 100000000;
		for(int i = 0; i < _cstr.length; i++)
			_violations = _violations < _cstr[i].violations() ?
					_violations : _cstr[i].violations();
		for(int i = 0; i < _vx.length; i++){
			_vx[i] = _violations;//1000000000;
			
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
