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
public class LessEqual extends ConstraintTwoFunction {

    public LessEqual(IFunction f1, IFunction f2) {
        super(f1, f2);
    }

    public LessEqual(VarIntLS var1, VarIntLS var2) {
        this(new FuncVar(var1), new FuncVar(var2));
    }

    public LessEqual(VarIntLS variable, double c) {
        this(new FuncVar(variable), new FuncConst(c, variable.getLocalSearchManager()));
    }

    public LessEqual(double c, VarIntLS variable) {
        this(new FuncConst(c, variable.getLocalSearchManager()), new FuncVar(variable));
    }

    public LessEqual(IFunction function, VarIntLS variable) {
        this(function, new FuncVar(variable));
    }

    public LessEqual(VarIntLS variable, IFunction function) {
        this(new FuncVar(variable), function);
    }

    public LessEqual(IFunction function, double c) {
        this(function, new FuncConst(c, function.getLocalSearchManager()));
    }

    public LessEqual(double c, IFunction function) {
        this(new FuncConst(c, function.getLocalSearchManager()), function);
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        for (VarIntLS variable : variables) {
            if (variableSet.contains(variable)) {
                double v1 = f1.getValue() + f1.getAssignDelta(variables, values);
                double v2 = f2.getValue() + f2.getAssignDelta(variables, values);
                double newViolation = NumberUtils.compare(v1, v2) <= 0 ? 0 : v1 - v2;
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
        double v1 = f1.getValue();
        double v2 = f2.getValue();
        violation = NumberUtils.compare(v1, v2) <= 0 ? 0 : v1 - v2;
    }

    public void verify() {
        double v1 = f1.getValue();
        double v2 = f2.getValue();
        double expectViolation = NumberUtils.compare(v1, v2) <= 0 ? 0 : v1 - v2;
        if (NumberUtils.compare(violation, expectViolation) != 0) {
            throw new RuntimeException("Wrong propagate at " + getClass().getName() + ". Expect: " + expectViolation + ", actual: " + violation);
        }
    }
}
