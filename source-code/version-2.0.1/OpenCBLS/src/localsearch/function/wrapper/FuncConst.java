package localsearch.function.wrapper;

import localsearch.model.IFunction;
import localsearch.model.Invariant;
import localsearch.model.LocalSearchManager;
import localsearch.model.variable.VarIntLS;
import localsearch.utils.NumberUtils;

import java.util.Set;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class FuncConst implements IFunction {

    private double value;
    private final LocalSearchManager localSearchManager;
    private final VarIntLS[] variables;

    public FuncConst(double value, LocalSearchManager localSearchManager) {
        this.value = value;
        variables = new VarIntLS[0];
        this.localSearchManager = localSearchManager;
        localSearchManager.post(this);
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public double getOldValue() {
        return value;
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        return 0;
    }

    @Override
    public VarIntLS[] getVariables() {
        return variables;
    }

    @Override
    public Invariant[] getDependencyInvariants() {
        return new Invariant[0];
    }

    @Override
    public void propagateConfirm(Set<VarIntLS> variables, double delta) {
        propagate(variables);
        if (NumberUtils.compare(value - getOldValue(), delta) != 0) {
            throw new RuntimeException("Wrong getAssignDelta at " + getClass().getName() + ": expect: " + (value - getOldValue()) + ", actual: " + delta);
        }
    }

    @Override
    public void propagate(Set<VarIntLS> variables) {
    }

    @Override
    public void initPropagate() {
    }

    @Override
    public LocalSearchManager getLocalSearchManager() {
        return localSearchManager;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public void verify() {
    }
}
