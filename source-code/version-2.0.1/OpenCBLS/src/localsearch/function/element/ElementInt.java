package localsearch.function.element;

import localsearch.function.abstract_function.FuncOneElement;
import localsearch.function.wrapper.FuncVar;
import localsearch.model.IFunction;
import localsearch.model.variable.VarIntLS;

import java.util.Set;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class ElementInt extends FuncOneElement {

    private final int[] arr;

    public ElementInt(int[] arr, IFunction index) {
        super(index);
        this.arr = arr;
    }

    public ElementInt(int[] arr, VarIntLS index) {
        super(new FuncVar(index));
        this.arr = arr;
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        for (VarIntLS variable : variables) {
            if (variableSet.contains(variable)) {
                return arr[(int) (function.getValue() + function.getAssignDelta(variables, values))] - value;
            }
        }
        return 0;
    }

    @Override
    public void propagate(Set<VarIntLS> variables) {
        initPropagate();
    }

    @Override
    public void initPropagate() {
        oldValue = value;
        value = arr[(int) function.getValue()];
    }
}
