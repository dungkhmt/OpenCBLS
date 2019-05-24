package localsearch.function;

import localsearch.function.abstract_function.FuncManyElement;
import localsearch.model.IFunction;
import localsearch.model.Invariant;
import localsearch.model.variable.VarIntLS;
import localsearch.utils.CountingUtils;
import localsearch.utils.NumberUtils;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class FuncDifferentCount extends FuncManyElement {

    private final Map<Double, Integer> mapCounter;

    public FuncDifferentCount(IFunction... functions) {
        super(functions);
        mapCounter = new TreeMap<>(NumberUtils.REAL_COMPARATOR);
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        TreeMap<Double, Integer> map = new TreeMap<>(NumberUtils.REAL_COMPARATOR);
        for (IFunction function : Invariant.getRefInvariants(variables, mapVarToFunctions)) {
            double oldValue = function.getValue();
            double newValue = function.getValue() + function.getAssignDelta(variables, values);
            CountingUtils.update(oldValue, newValue, map);
        }
        return CountingUtils.getSize(mapCounter, map) - value;
    }

    @Override
    public void propagate(Set<VarIntLS> variables) {
        for (IFunction function : Invariant.getRefInvariants(variables, mapVarToFunctions)) {
            CountingUtils.update(function.getOldValue(), function.getValue(), mapCounter);
        }
        oldValue = value;
        value = mapCounter.size();
    }

    @Override
    public void initPropagate() {
        for (IFunction function : functions) {
            mapCounter.merge(function.getValue(), 1, Integer::sum);
        }
        value = mapCounter.size();
    }
}
