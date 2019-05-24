package localsearch.function.math;

import localsearch.function.abstract_function.FuncOneElement;
import localsearch.function.wrapper.FuncVar;
import localsearch.model.IFunction;
import localsearch.model.variable.VarIntLS;

import java.util.Set;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class FuncAbs extends FuncOneElement {

    public FuncAbs(IFunction function) {
        super(function);
    }

    public FuncAbs(VarIntLS variable) {
        super(new FuncVar(variable));
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        for (VarIntLS variable : variables) {
            if (variableSet.contains(variable)) {
                return Math.abs(function.getValue() + function.getAssignDelta(variables, values)) - value;
            }
        }
        return 0;
    }

    @Override
    public void propagate(Set<VarIntLS> variables) {
        for (VarIntLS variable : variables) {
            if (variableSet.contains(variable)) {
                oldValue = value;
                value = Math.abs(function.getValue());
                return;
            }
        }
    }

    @Override
    public void initPropagate() {
        oldValue = value = Math.abs(function.getValue());
    }

}
