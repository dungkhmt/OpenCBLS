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
public class FuncMin extends FuncManyElement {

    private final TreeMap<Double, Integer> minMap;

    public FuncMin(IFunction... functions) {
        super(functions);
        minMap = new TreeMap<>(NumberUtils.REAL_COMPARATOR);
    }

    public FuncMin(VarIntLS... variables) {
        super(new IFunction[variables.length]);
        for (int i = 0; i < functions.length; ++i) {
            functions[i] = new FuncVar(variables[i]);
        }
        minMap = new TreeMap<>(NumberUtils.REAL_COMPARATOR);
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        TreeMap<Double, Integer> map = new TreeMap<>(NumberUtils.REAL_COMPARATOR);
        for (IFunction function : Invariant.getRefInvariants(variables, mapVarToFunctions)) {
            double newFunctionValue = function.getValue() + function.getAssignDelta(variables, values);
            CountingUtils.update(function.getValue(), newFunctionValue, map);
        }
        return CountingUtils.getFirst(minMap, map, NumberUtils.REAL_COMPARATOR) - value;
    }

    @Override
    public void propagate(Set<VarIntLS> variables) {
        for (IFunction function : Invariant.getRefInvariants(variables, mapVarToFunctions)) {
            CountingUtils.update(function.getOldValue(), function.getValue(), minMap);
        }
        oldValue = value;
        value = minMap.firstKey();
    }

    @Override
    public void initPropagate() {
        for (IFunction function : functions) {
            minMap.merge(function.getValue(), 1, Integer::sum);
        }
        oldValue = value = minMap.firstKey();
    }
}
