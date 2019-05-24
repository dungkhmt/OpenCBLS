package localsearch.function.math;

import localsearch.function.abstract_function.FuncTwoElement;
import localsearch.function.wrapper.FuncConst;
import localsearch.function.wrapper.FuncVar;
import localsearch.model.IFunction;
import localsearch.model.variable.VarIntLS;

import java.util.Set;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class FuncMinus extends FuncTwoElement {

    public FuncMinus(IFunction f1, IFunction f2) {
        super(f1, f2);
    }

    public FuncMinus(IFunction function, VarIntLS variable) {
        super(function, new FuncVar(variable));
    }

    public FuncMinus(IFunction function, double c) {
        super(function, new FuncConst(c, function.getLocalSearchManager()));
    }

    public FuncMinus(VarIntLS var1, VarIntLS var2) {
        super(new FuncVar(var1), new FuncVar(var2));
    }

    public FuncMinus(VarIntLS variable, double c) {
        super(new FuncVar(variable), new FuncConst(c, variable.getLocalSearchManager()));
    }

    public FuncMinus(VarIntLS variable, IFunction function) {
        super(new FuncVar(variable), function);
    }

    public FuncMinus(double c, IFunction function) {
        super(new FuncConst(c, function.getLocalSearchManager()), function);
    }

    public FuncMinus(double c, VarIntLS variable) {
        super(new FuncConst(c, variable.getLocalSearchManager()), new FuncVar(variable));
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        for (VarIntLS variable : variables) {
            if (variableSet.contains(variable)) {
                return f1.getAssignDelta(variables, values) - f2.getAssignDelta(variables, values);
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
        value = f1.getValue() - f2.getValue();
    }
}
