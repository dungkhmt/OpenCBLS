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
public class FuncVar implements IFunction {

    private double value;
    private final VarIntLS[] variables;
    private final LocalSearchManager localSearchManager;

    public FuncVar(VarIntLS variable) {
        this.variables = new VarIntLS[]{variable};
        localSearchManager = variable.getLocalSearchManager();
        localSearchManager.post(this);
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public double getOldValue() {
        return variables[0].getOldValue();
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        for (int i = 0; i < variables.length; ++i) {
            if (this.variables[0] == variables[i]) {
                return values[i] - value;
            }
        }
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
        if (variables.contains(this.variables[0])) {
            value = this.variables[0].getValue();
        }
    }


    @Override
    public void initPropagate() {
        value = variables[0].getValue();
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
