package localsearch.constraint.basic.operator;

import localsearch.constraint.abstract_constraint.ConstraintTwoFunction;
import localsearch.function.wrapper.FuncConst;
import localsearch.function.wrapper.FuncVar;
import localsearch.model.IFunction;
import localsearch.model.variable.VarIntLS;
import localsearch.utils.NumberUtils;

import java.util.Set;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class NotEqual extends ConstraintTwoFunction {

    public NotEqual(IFunction f1, IFunction f2) {
        super(f1, f2);
    }

    public NotEqual(IFunction function, VarIntLS variable) {
        super(function, new FuncVar(variable));
    }

    public NotEqual(IFunction function, double c) {
        super(function, new FuncConst(c, function.getLocalSearchManager()));
    }

    public NotEqual(VarIntLS var1, VarIntLS var2) {
        super(new FuncVar(var1), new FuncVar(var2));
    }

    public NotEqual(VarIntLS variable, double c) {
        super(new FuncVar(variable), new FuncConst(c, variable.getLocalSearchManager()));
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        for (VarIntLS variable : variables) {
            if (variableSet.contains(variable)) {
                double v1 = f1.getValue() + f1.getAssignDelta(variables, values);
                double v2 = f2.getValue() + f2.getAssignDelta(variables, values);
                double newViolation = NumberUtils.compare(v1, v2) == 0 ? 1 : 0;
                return newViolation - violation;
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
        if (NumberUtils.compare(f1.getValue(), f2.getValue()) == 0) {
            violation = 1;
        } else {
            violation = 0;
        }
    }

    @Override
    public void verify() {
        if (NumberUtils.compare(violation, 0) == 0) {
            if (NumberUtils.compare(f1.getValue(), f2.getValue()) == 0) {
                throw new RuntimeException("Wrong propagate at " + getClass().getName() + ". Expect: 1, actual: 0");
            }
        } else if (NumberUtils.compare(f1.getValue(), f2.getValue()) != 0) {
            throw new RuntimeException("Wrong propagate at " + getClass().getName() + ". Expect: 1, actual: 0");
        }
    }
}
