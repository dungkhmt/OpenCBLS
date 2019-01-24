package localsearch.model;

public interface IConstraint extends Invariant{
	public int violations();
	public int violations(VarIntLS x);
	public int getAssignDelta(VarIntLS x, int val);
	public int getSwapDelta(VarIntLS x, VarIntLS y);
	
}
