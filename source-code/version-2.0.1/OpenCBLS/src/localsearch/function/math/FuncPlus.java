package localsearch.function.math;

import localsearch.function.abstract_function.FuncTwoElement;
import localsearch.function.wrapper.FuncConst;
import localsearch.function.wrapper.FuncVar;
import localsearch.model.IFunction;
import localsearch.model.variable.VarIntLS;
import localsearch.utils.NumberUtils;

import java.util.Set;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class FuncPlus extends FuncTwoElement {

    public FuncPlus(IFunction f1, IFunction f2) {
        super(f1, f2);
    }

    public FuncPlus(IFunction function, VarIntLS variable) {
        super(function, new FuncVar(variable));
    }

    public FuncPlus(IFunction function, double c) {
        super(function, new FuncConst(c, function.getLocalSearchManager()));
    }

    public FuncPlus(VarIntLS var1, VarIntLS var2) {
        super(new FuncVar(var1), new FuncVar(var2));
    }

    public FuncPlus(VarIntLS variable, double c) {
        super(new FuncVar(variable), new FuncConst(c, variable.getLocalSearchManager()));
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        for (VarIntLS variable : variables) {
            if (variableSet.contains(variable)) {
                return f1.getAssignDelta(variables, values) + f2.getAssignDelta(variables, values);
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
        super.value = f1.getValue() + f2.getValue();
    }

    public void verify() {
        if (NumberUtils.compare(value, f1.getValue() + f2.getValue()) != 0) {
            throw new RuntimeException("Wrong propagate at " + getClass().getName() + "");
        }
    }
}
