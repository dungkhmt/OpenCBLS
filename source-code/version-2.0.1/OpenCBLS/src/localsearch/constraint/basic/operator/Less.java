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
public class Less extends ConstraintTwoFunction {

    public Less(IFunction f1, IFunction f2) {
        super(f1, f2);
    }

    public Less(IFunction function, VarIntLS variable) {
        super(function, new FuncVar(variable));
    }

    public Less(IFunction function, double c) {
        super(function, new FuncConst(c, function.getLocalSearchManager()));
    }

    public Less(VarIntLS var1, VarIntLS var2) {
        super(new FuncVar(var1), new FuncVar(var2));
    }

    public Less(VarIntLS variable, double c) {
        super(new FuncVar(variable), new FuncConst(c, variable.getLocalSearchManager()));
    }

    public Less(VarIntLS variable, IFunction function) {
        super(new FuncVar(variable), function);
    }

    public Less(double c, IFunction function) {
        super(new FuncConst(c, function.getLocalSearchManager()), function);
    }

    public Less(double c, VarIntLS variable) {
        super(new FuncConst(c, variable.getLocalSearchManager()), new FuncVar(variable));
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        for (VarIntLS variable : variables) {
            if (variableSet.contains(variable)) {
                double v1 = f1.getValue() + f1.getAssignDelta(variables, values);
                double v2 = f2.getValue() + f2.getAssignDelta(variables, values);
                double newViolation = NumberUtils.compare(v1, v2) < 0 ? 0 : v1 - v2 + 1;
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
        violation = NumberUtils.compare(v1, v2) < 0 ? 0 : v1 - v2 + 1;
    }
}
