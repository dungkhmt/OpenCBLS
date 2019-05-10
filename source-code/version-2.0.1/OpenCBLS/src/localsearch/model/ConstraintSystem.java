package localsearch.model;

import java.util.*;

public class ConstraintSystem extends AbstractInvariant implements IConstraint {
	LocalSearchManager _mgr = null;
	private ArrayList<IConstraint> _constraints = new ArrayList<IConstraint>() ;
	private VarIntLS[] _x;
	private int _totalviolations;
	
	// auxiliary data structures
	private HashMap<VarIntLS, HashSet<IConstraint>> mapVarIntLSConstraints = null;// mapVarIntLSConstraints.get(x) is the set of 
																					// constraints defined over x
	private int[] _violations;
	private HashMap<IConstraint, Integer> mapC;
	
	public void post(IConstraint c){
		_constraints.add(c);		
	}
	public String name(){ return "ConstraintSystem";}
	
	public void close(){
		HashSet<VarIntLS> S = new HashSet<VarIntLS>();
		mapVarIntLSConstraints = new HashMap<VarIntLS, HashSet<IConstraint>>();
		for(int i = 0; i < _constraints.size(); i++){
			IConstraint c = _constraints.get(i);
			VarIntLS[] x = c.getVariables();
			for(int j = 0; j < x.length; j++)
				S.add(x[j]);
		}
		_x = new VarIntLS[S.size()];
		Iterator<VarIntLS> it = S.iterator();
		int idx = -1;
		while(it.hasNext()){
			VarIntLS x = (VarIntLS)it.next();
			idx++;
			_x[idx] = x;
			mapVarIntLSConstraints.put(x, new HashSet<IConstraint>());
		}
		for(int i = 0; i < _constraints.size(); i++){
			IConstraint c = _constraints.get(i);
			VarIntLS[] x = c.getVariables();
			for(int j = 0; j < x.length; j++){
				mapVarIntLSConstraints.get(x[j]).add(c);
			}
				
		}
		mapC = new HashMap<IConstraint, Integer>();
		for(int i = 0; i < _constraints.size(); i++)
			mapC.put(_constraints.get(i), i);
		_violations = new int[_constraints.size()];
	}
	public ConstraintSystem(LocalSearchManager mgr){
		//super(-1);
		this._mgr = mgr;
		mgr.post(this);
	}
	@Override
	public int violations() {
		// TODO Auto-generated method stub
		return _totalviolations;
	}

	@Override
	public int violations(VarIntLS x) {
		// TODO Auto-generated method stub
		HashSet<IConstraint> C = mapVarIntLSConstraints.get(x);
		if(C == null) return 0;
		int v = 0;
		Iterator<IConstraint> it = C.iterator();
		while(it.hasNext()){
			IConstraint c = (IConstraint)it.next();
			v += c.violations(x);
		}
		return v;
	}

	@Override
	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		return _x;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		int delta = 0;
		HashSet<IConstraint> C = mapVarIntLSConstraints.get(x);
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
		HashSet<IConstraint> Cx = mapVarIntLSConstraints.get(x);
		HashSet<IConstraint> Cy = mapVarIntLSConstraints.get(y);
		
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

	@Override
	public void propagateInt(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		//System.out.println(name() + "::propagateInt(x[" + x.getID() + "], " + val + ")");
		HashSet<IConstraint> C = mapVarIntLSConstraints.get(x);
		if(C == null) return;
		Iterator<IConstraint> it = C.iterator();
		while(it.hasNext()){
			IConstraint c = (IConstraint)it.next();
			int idc = mapC.get(c);
			_totalviolations += (c.violations() - _violations[idc]);
			_violations[idc] = c.violations();
		}
	}

	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		//System.out.println(name() + "::initPropagate");
		_totalviolations = 0;
		for(int i = 0; i < _constraints.size(); i++){
			_violations[i] = _constraints.get(i).violations();
			_totalviolations += _violations[i];
		}
	}
	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _mgr;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		for(int i = 0; i < _constraints.size(); i++){
			if(!_constraints.get(i).verify()) return false;
		}
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
