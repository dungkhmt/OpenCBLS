package localsearch.function.math;

import localsearch.model.IFunction;
import localsearch.model.Invariant;
import localsearch.model.LocalSearchManager;
import localsearch.model.variable.VarIntLS;
import localsearch.utils.NumberUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class FuncSum implements IFunction {

    private double value;
    private double oldValue;
    private final VarIntLS[] variables;
    private final LocalSearchManager localSearchManager;
    private final HashMap<VarIntLS, Set<IFunction>> mapVarToFunctions;

    private final IFunction[] functions;
    private final boolean useFunction;

    private final int level;

    public FuncSum(VarIntLS... variables) {
        this.variables = variables;
        localSearchManager = variables[0].getLocalSearchManager();
        mapVarToFunctions = new HashMap<>(variables.length);
        for (VarIntLS variable : variables) {
            mapVarToFunctions.put(variable, null);
        }
        functions = null;
        useFunction = false;
        level = 1;
        localSearchManager.post(this);
    }

    public FuncSum(IFunction... functions) {
        this.functions = functions;
        localSearchManager = functions[0].getLocalSearchManager();
        mapVarToFunctions = new HashMap<>();
        int maxLevel = 0;
        for (IFunction function : functions) {
            for (VarIntLS variable : function.getVariables()) {
                mapVarToFunctions.computeIfAbsent(variable, k -> new HashSet<>(Math.min(16, functions.length))).add(function);
            }
            if (function.getLevel() > maxLevel) {
                maxLevel = function.getLevel();
            }
        }
        level = maxLevel + 1;
        variables = mapVarToFunctions.keySet().toArray(new VarIntLS[0]);
        useFunction = true;
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
        if (!useFunction) {
            for (int i = 0; i < variables.length; ++i) {
                if (mapVarToFunctions.containsKey(variables[i])) {
                    delta += values[i] - variables[i].getValue();
                }
            }
        } else {
            for (IFunction function : Invariant.getRefInvariants(variables, mapVarToFunctions)) {
                delta += function.getAssignDelta(variables, values);
            }
        }
        return delta;
    }

    @Override
    public void propagateConfirm(Set<VarIntLS> variables, double delta) {
        propagate(variables);
        if (NumberUtils.compare(value - oldValue, delta) != 0) {
            throw new RuntimeException("Wrong getAssignDelta at " + getClass().getName() + ": expect: " + (value - oldValue) + ", actual: " + delta);
        }
    }

    @Override
    public VarIntLS[] getVariables() {
        return variables;
    }

    @Override
    public Invariant[] getDependencyInvariants() {
        if (useFunction) {
            return functions;
        }
        return new Invariant[0];
    }

    @Override
    public void propagate(Set<VarIntLS> variables) {
        double delta = 0;
        if (!useFunction) {
            for (VarIntLS variable : variables) {
                if (mapVarToFunctions.containsKey(variable)) {
                    delta += (variable.getValue() - variable.getOldValue());
                }
            }
        } else {
            for (IFunction function : Invariant.getRefInvariants(variables, mapVarToFunctions)) {
                delta += (function.getValue() - function.getOldValue());
            }
        }
        oldValue = value;
        value += delta;
    }

    @Override
    public void initPropagate() {
        value = 0;
        if (!useFunction) {
            for (VarIntLS variable : variables) {
                value += variable.getValue();
            }
        } else {
            for (IFunction function : functions) {
                value += function.getValue();
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
        return level;
    }

    @Override
    public void verify() {
    }
}
