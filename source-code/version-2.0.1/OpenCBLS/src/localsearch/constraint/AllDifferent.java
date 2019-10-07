package localsearch.constraint;

import localsearch.constraint.abstract_constraint.Constraint;
import localsearch.model.IFunction;
import localsearch.model.Invariant;
import localsearch.model.variable.VarIntLS;
import localsearch.utils.CountingUtils;
import localsearch.utils.NumberUtils;

import java.util.*;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class AllDifferent extends Constraint {

    private final IFunction[] functions;
    private final HashMap<VarIntLS, Set<IFunction>> mapVarToFunction;
    private final Map<Double, Integer> mapCounter;
    private final boolean useFunction;
    private final int level;

    public AllDifferent(VarIntLS... variables) {
        this.variables = variables;
        localSearchManager = variables[0].getLocalSearchManager();
        mapVarToFunction = new HashMap<>(variables.length);
        for (VarIntLS variable : variables) {
            mapVarToFunction.put(variable, null);
        }
        useFunction = false;
        mapCounter = new TreeMap<>(NumberUtils.REAL_COMPARATOR);
        functions = null;
        level = 1;
        localSearchManager.post(this);
    }

    public AllDifferent(IFunction... functions) {
        this.functions = functions;
        localSearchManager = functions[0].getLocalSearchManager();
        mapVarToFunction = new HashMap<>();
        int maxLevel = 0;
        for (IFunction function : functions) {
            for (VarIntLS variable : function.getVariables()) {
                mapVarToFunction.computeIfAbsent(variable, k -> new HashSet<>(Math.min(16, functions.length))).add(function);
            }
            if (function.getLevel() > maxLevel) {
                maxLevel = function.getLevel();
            }
        }
        variables = mapVarToFunction.keySet().toArray(new VarIntLS[0]);
        level = maxLevel + 1;
        useFunction = true;
        mapCounter = new TreeMap<>(NumberUtils.REAL_COMPARATOR);
        localSearchManager.post(this);
    }

    @Override
    public Invariant[] getDependencyInvariants() {
        if (useFunction) {
            return functions;
        }
        return new IFunction[0];
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        Map<Double, Integer> map = new TreeMap<>(NumberUtils.REAL_COMPARATOR);
        if (!useFunction) {
            for (int i = 0; i < variables.length; ++i) {
                if (mapVarToFunction.containsKey(variables[i])) {
                    CountingUtils.update((double) variables[i].getValue(), (double) values[i], map);
                }
            }
            return this.variables.length - CountingUtils.getSize(mapCounter, map) - violation;
        } else {
            for (IFunction function : Invariant.getRefInvariants(variables, mapVarToFunction)) {
                CountingUtils.update(function.getValue(),
                        function.getValue() + function.getAssignDelta(variables, values), map);
            }
            return this.functions.length - CountingUtils.getSize(mapCounter, map) - violation;
        }
    }

    @Override
    public void propagate(Set<VarIntLS> variables) {
        if (!useFunction) {
            for (VarIntLS variable : variables) {
                if (mapVarToFunction.containsKey(variable)) {
                    CountingUtils.update((double) variable.getOldValue(), (double) variable.getValue(), mapCounter);
                }
            }
            violation = this.variables.length - mapCounter.size();
        } else {
            for (IFunction function : Invariant.getRefInvariants(variables, mapVarToFunction)) {
                CountingUtils.update(function.getOldValue(), function.getValue(), mapCounter);
            }
            violation = this.functions.length - mapCounter.size();
        }
    }

    @Override
    public void initPropagate() {
        if (!useFunction) {
            for (VarIntLS variable : variables) {
                double v = variable.getValue();
                mapCounter.merge(v, 1, Integer::sum);
            }
            violation = variables.length - mapCounter.size();
        } else {
            for (IFunction function : functions) {
                double v = function.getValue();
                mapCounter.merge(v, 1, Integer::sum);
            }
            violation = functions.length - mapCounter.size();
        }
    }

    @Override
    public int getLevel() {
        return level;
    }

}
