package localsearch.function;

import localsearch.function.abstract_function.FuncManyElement;
import localsearch.function.wrapper.FuncVar;
import localsearch.model.IFunction;
import localsearch.model.Invariant;
import localsearch.model.variable.VarIntLS;
import localsearch.utils.CountingUtils;
import localsearch.utils.NumberUtils;

import java.util.Set;
import java.util.TreeMap;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class FuncMax extends FuncManyElement {

    private final TreeMap<Double, Integer> maxMap;

    public FuncMax(IFunction... functions) {
        super(functions);
        maxMap = new TreeMap<>(NumberUtils.REAL_COMPARATOR_REVERSE);
    }

    public FuncMax(VarIntLS... variables) {
        super(new IFunction[variables.length]);
        for (int i = 0; i < functions.length; ++i) {
            functions[i] = new FuncVar(variables[i]);
        }
        maxMap = new TreeMap<>(NumberUtils.REAL_COMPARATOR_REVERSE);
    }


    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        TreeMap<Double, Integer> map = new TreeMap<>(NumberUtils.REAL_COMPARATOR_REVERSE);
        for (IFunction function : Invariant.getRefInvariants(variables, mapVarToFunctions)) {
            double newFunctionValue = function.getValue() + function.getAssignDelta(variables, values);
            CountingUtils.update(function.getValue(), newFunctionValue, map);
        }
        return CountingUtils.getFirst(maxMap, map, NumberUtils.REAL_COMPARATOR_REVERSE) - value;
    }

    @Override
    public void propagate(Set<VarIntLS> variables) {
        for (IFunction function : Invariant.getRefInvariants(variables, mapVarToFunctions)) {
            CountingUtils.update(function.getOldValue(), function.getValue(), maxMap);
        }
        oldValue = value;
        value = maxMap.firstKey();
    }

    @Override
    public void initPropagate() {
        for (IFunction function : functions) {
            maxMap.merge(function.getValue(), 1, Integer::sum);
        }
        oldValue = value = maxMap.firstKey();
    }
}
