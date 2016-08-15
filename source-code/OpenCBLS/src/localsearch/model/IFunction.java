package localsearch.model;

public interface IFunction extends Invariant{
	public int getMinValue();
	public int getMaxValue();
	public int getValue();
	public int getAssignDelta(VarIntLS x, int val);
	public int getSwapDelta(VarIntLS x, VarIntLS y);
}
