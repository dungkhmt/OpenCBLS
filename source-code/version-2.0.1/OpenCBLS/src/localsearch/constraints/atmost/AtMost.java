package localsearch.constraints.atmost;

import localsearch.model.*;
public class AtMost extends AbstractInvariant implements IConstraint {
	private IConstraint		_c;
	
	//Semantic: At most n[v] constraints in array c[] have the number of violations  
	//          equal to v, with v in the range of 0 to the length of n[]
	public AtMost(IConstraint[] c, int[] n){
		_c = new AtmostConstraintInt(c, n);
	}
	
	//Semantic: At most n constraints in array c have the number of violations 
	//equal to val
	public AtMost(IConstraint[] c, int n, int val){
		_c = new AtmostConstraintIntInt(c, n, val);
	}
	
	//Semantic: At most n[v] functions in array f[] have value  
	//          equal to v, with v in the range of 0 to the length of n[]
	public AtMost(IFunction[] f,int[] n){
		_c = new AtmostFunInt(f, n);
	}
	
	//Semantic: At most n functions in array f[] have value equal to val
	public AtMost(IFunction[] f,int n,int val){
		_c = new AtmostFunIntInt(f, n, val);
	}
	
	//Semantic: At most n[v] variables in array x assigned to v, 
	//			with v in the range of 0 to the length of n[]
	public AtMost(VarIntLS[] x, int[] n){
		_c = new AtmostVarintInt(x, n);
	}
	
	//Semantic: At most n variables in array x assigned to val
	public AtMost(VarIntLS[] x, int n, int val){
		_c = new AtmostVarintIntInt(x, n, val);
	}
	
	@Override
	public int violations() {
		// TODO Auto-generated method stub
		return _c.violations();
	}

	@Override
	public int violations(VarIntLS x) {
		// TODO Auto-generated method stub
		return _c.violations(x);
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		return _c.getAssignDelta(x, val);
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		return _c.getSwapDelta(x, y);
	}

	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		return _c.getVariables();
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		// DO NOTHING
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _c.getLocalSearchManager();
	}

	public String name(){
		return "AtMost";
	}
	@Override
	public boolean verify(){
		return _c.verify();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
