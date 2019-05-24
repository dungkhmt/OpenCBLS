package localsearch.function.conditional;

import localsearch.function.abstract_function.FuncManyElement;
import localsearch.model.IFunction;
import localsearch.model.Invariant;
import localsearch.model.variable.VarIntLS;
import localsearch.utils.NumberUtils;

import java.util.*;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class ConditionalSum extends FuncManyElement {

    private final IFunction v;
    private final IFunction[] weights;

    private final HashMap<IFunction, Integer> mapFunctionIndex;
    private final Map<Double, HashSet<Integer>> mapCounter;

    /***
     * IF functions[i] == v then this.value += weights[i]
     * @param functions
     * @param v
     * @param weights
     */
    public ConditionalSum(IFunction[] functions, IFunction v, IFunction[] weights) {
        super(functions);
        this.v = v;
        this.weights = weights;

        mapFunctionIndex = new HashMap<>(functions.length);
        for (int i = 0; i < functions.length; ++i) {
            mapFunctionIndex.put(functions[i], i);
        }
        for (VarIntLS variable : v.getVariables()) {
            mapVarToFunctions.computeIfAbsent(variable, k -> new HashSet<>(1)).add(v);
        }
        for (IFunction function : weights) {
            for (VarIntLS variable : function.getVariables()) {
                mapVarToFunctions.computeIfAbsent(variable, k -> new HashSet<>(Math.min(16, weights.length))).add(function);
            }
        }
        mapCounter = new TreeMap<>(NumberUtils.REAL_COMPARATOR);
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        Set<IFunction> functionsRefVariables = Invariant.getRefInvariants(variables, mapVarToFunctions);
        Map<IFunction, Double> mapWeightNewValue = weightUpdate(variables, values, functionsRefVariables);
        if (!functionsRefVariables.contains(v)) {
            double delta = 0;
            for (IFunction functionRef : functionsRefVariables) {
                Integer idFunction = mapFunctionIndex.get(functionRef);
                if (idFunction != null) { // functionRef is in functions
                    double newFunctionValue = functionRef.getValue() + functionRef.getAssignDelta(variables, values);
                    Double newWeightValue = mapWeightNewValue.get(weights[idFunction]);
                    if (newWeightValue == null) {
                        if (NumberUtils.compare(functionRef.getValue(), v.getValue()) == 0) {
                            if (NumberUtils.compare(newFunctionValue, v.getValue()) != 0) {
                                delta -= weights[idFunction].getValue();
                            }
                        } else if (NumberUtils.compare(newFunctionValue, v.getValue()) != 0) {
                            delta += weights[idFunction].getValue();
                        }
                    } else {
                        if (NumberUtils.compare(functionRef.getValue(), v.getValue()) == 0) {
                            if (NumberUtils.compare(newFunctionValue, v.getValue()) == 0) {
                                delta -= weights[idFunction].getValue();
                                delta += newWeightValue;
                            } else {
                                delta -= weights[idFunction].getValue();
                            }
                        } else if (NumberUtils.compare(newFunctionValue, v.getValue()) == 0) {
                            delta += newWeightValue;
                        }
                    }
                }
            }
            return delta;
        } else {
            double newV = v.getValue() + v.getAssignDelta(variables, values);
            double newValue = 0;
            for (IFunction functionRef : functionsRefVariables) {
                Integer idFunction = mapFunctionIndex.get(functionRef);
                if (idFunction != null) {
                    double newFunctionValue = functionRef.getValue() + functionRef.getAssignDelta(variables, values);
                    Double newWeightValue = mapWeightNewValue.get(weights[idFunction]);
                    if (NumberUtils.compare(newFunctionValue, newV) == 0) {
                        newValue += newWeightValue == null ? weights[idFunction].getValue() : newWeightValue;
                    }
                }
            }
            for (Integer idFunction : mapCounter.get(newV)) {
                if (!functionsRefVariables.contains(functions[idFunction])) {
                    Double newWeightValue = mapWeightNewValue.get(weights[idFunction]);
                    newValue += newWeightValue == null ? weights[idFunction].getValue() : newWeightValue;
                }
            }
            return newValue - value;
        }
    }

    private Map<IFunction, Double> weightUpdate(VarIntLS[] variables, int[] values, Set<IFunction> functionsRefVariables) {
        HashMap<IFunction, Double> mapWeightIndexNewValue = new HashMap<>(functionsRefVariables.size());
        for (IFunction functionRef : functionsRefVariables) {
            if (!mapFunctionIndex.containsKey(functionRef) && functionRef != v) { // is in weights
                double newWeightValue = functionRef.getValue() + functionRef.getAssignDelta(variables, values);
                if (NumberUtils.compare(newWeightValue, functionRef.getValue()) != 0) {
                    mapWeightIndexNewValue.put(functionRef, newWeightValue);
                }
            }
        }
        return mapWeightIndexNewValue;
    }

    @Override
    public void propagate(Set<VarIntLS> variables) {
        for (VarIntLS variable : variables) {
            if (mapVarToFunctions.containsKey(variable)) {
                oldValue = value;
                value = 0;
                mapCounter.clear();
                for (int i = 0; i < functions.length; ++i) {
                    if (NumberUtils.compare(functions[i].getValue(), v.getValue()) == 0) {
                        value += weights[i].getValue();
                    }
                    mapCounter.computeIfAbsent(functions[i].getValue(), k -> new HashSet<>(functions.length)).add(i);
                }
                return;
            }
        }
    }

    @Override
    public void initPropagate() {
        value = 0;
        for (int i = 0; i < functions.length; ++i) {
            mapCounter.computeIfAbsent(functions[i].getValue(), k -> new HashSet<>(functions.length)).add(i);
            if (NumberUtils.compare(functions[i].getValue(), v.getValue()) == 0) {
                value += weights[i].getValue();
            }
        }
        oldValue = value;
    }

}
