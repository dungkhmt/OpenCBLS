package localsearch.function.element;

import localsearch.function.abstract_function.FuncManyElement;
import localsearch.model.IFunction;
import localsearch.model.variable.VarIntLS;

import java.util.Set;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class ElementFunc extends FuncManyElement {

    private final IFunction index;

    public ElementFunc(IFunction[] arr, IFunction index) {
        super(arr);
        this.index = index;
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        for (VarIntLS variable : variables) {
            if (mapVarToFunctions.containsKey(variable)) {
                int newIndex = (int) (index.getValue() + index.getAssignDelta(variables, values));
                double newValue = functions[newIndex].getValue() + functions[newIndex].getAssignDelta(variables, values);
                return newValue - value;
            }
        }
        return 0;
    }

    @Override
    public void propagate(Set<VarIntLS> variables) {
        for (VarIntLS variable : variables) {
            if (mapVarToFunctions.containsKey(variable)) {
                initPropagate();
            }
        }
    }

    @Override
    public void initPropagate() {
        oldValue = value;
        value = functions[(int) index.getValue()].getValue();
    }
}
