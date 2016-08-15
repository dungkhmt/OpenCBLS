package localsearch.model;

public interface IFloatFunction extends Invariant {
    public double getMinValue();

    public double getMaxValue();

    public double getValue();

    public double getAssignDelta(VarIntLS x, int val);

    public double getSwapDelta(VarIntLS x, VarIntLS y);

    public double getAssignDelta(VarIntLS x, int valx, VarIntLS y, int valy);
	//public double getAsignDelta(VarIntLS x, int valx, VarIntLS y, int valy);
}
