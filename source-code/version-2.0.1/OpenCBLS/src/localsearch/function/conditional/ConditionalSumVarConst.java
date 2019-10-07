package localsearch.function.conditional;

import localsearch.model.IFunction;
import localsearch.model.Invariant;
import localsearch.model.LocalSearchManager;
import localsearch.model.variable.VarIntLS;
import localsearch.utils.NumberUtils;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class ConditionalSumVarConst implements IFunction {

    private double value;
    private double oldValue;

    private final VarIntLS[] variables;
    private final Double v;
    private final double[] weights;

    private final LocalSearchManager localSearchManager;
    private final HashMap<VarIntLS, Integer> mapVarToIndex;


    public ConditionalSumVarConst(VarIntLS[] variables, double v, double[] weights) {
        this.variables = variables;
        this.v = v;
        this.weights = weights;
        localSearchManager = variables[0].getLocalSearchManager();
        mapVarToIndex = new HashMap<>(variables.length);
        for (int i = 0; i < variables.length; ++i) {
            mapVarToIndex.put(variables[i], i);
        }
        localSearchManager.post(this);
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public double getOldValue() {
        return oldValue;
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        double delta = 0;
        for (int i = 0; i < variables.length; ++i) {
            Integer id = mapVarToIndex.get(variables[i]);
            if (id != null) {
                if (variables[i].getValue() == v.intValue()) {
                    if (values[i] != v.intValue()) {
                        delta -= weights[id];
                    }
                } else if (values[i] == v.intValue()) {
                    delta += weights[id];
                }
            }
        }
        return delta;
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
        if (NumberUtils.compare(value - oldValue, delta) != 0) {
            throw new RuntimeException("Wrong getAssignDelta at " + getClass().getName() + ": expect: " + (value - oldValue) + ", actual: " + delta);
        }
    }

    @Override
    public void propagate(Set<VarIntLS> variables) {
        double delta = 0;
        for (VarIntLS variable : variables) {
            Integer id = mapVarToIndex.get(variable);
            if (id != null) {
                if (variable.getOldValue() == v.intValue()) {
                    if (variable.getValue() != v.intValue()) {
                        delta -= weights[id];
                    }
                } else if (variable.getValue() == v.intValue()) {
                    delta += weights[id];
                }
            }
        }
        oldValue = value;
        value += delta;
    }

    @Override
    public void initPropagate() {
        value = 0;
        for (int i = 0; i < variables.length; ++i) {
            if (variables[i].getValue() == v.intValue()) {
                value += weights[i];
            }
        }
        oldValue = value;
    }

    @Override
    public LocalSearchManager getLocalSearchManager() {
        return localSearchManager;
    }

    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public void verify() {
    }

}
