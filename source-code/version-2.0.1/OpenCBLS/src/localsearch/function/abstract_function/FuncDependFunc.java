package localsearch.function.abstract_function;

import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.variable.VarIntLS;
import localsearch.utils.NumberUtils;

import java.util.Set;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public abstract class FuncDependFunc implements IFunction {

    protected double value;
    protected double oldValue;
    protected VarIntLS[] variables;
    protected LocalSearchManager localSearchManager;

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public double getOldValue() {
        return oldValue;
    }

    @Override
    public VarIntLS[] getVariables() {
        return variables;
    }

    @Override
    public LocalSearchManager getLocalSearchManager() {
        return localSearchManager;
    }

    @Override
    public void propagateConfirm(Set<VarIntLS> variables, double delta) {
        propagate(variables);
        if (NumberUtils.compare(value - oldValue, delta) != 0) {
            throw new RuntimeException("Wrong getAssignDelta at " + getClass().getName() + ": expect: " + (value - oldValue) + ", actual: " + delta);
        }
    }

    @Override
    public void verify() {

    }
}
