package localsearch.constraint.basic.logic;

import localsearch.constraint.abstract_constraint.ConstraintTwoConstraint;
import localsearch.model.IConstraint;
import localsearch.model.variable.VarIntLS;
import localsearch.utils.NumberUtils;

import java.util.Set;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class Implicate extends ConstraintTwoConstraint {

    public Implicate(IConstraint c1, IConstraint c2) {
        super(c1, c2);
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        for (VarIntLS variable : variables) {
            if (variableSet.contains(variable)) {
                if (NumberUtils.compare(c1.getViolation() + c1.getAssignDelta(variables, values), 0) > 0) {
                    return -violation;
                }
                return c2.getViolation() + c2.getAssignDelta(variables, values) - violation;
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
        violation = NumberUtils.compare(c1.getViolation(), 0) > 0 ? 0 : c2.getViolation();
    }

    public void verify() {
        if (violation != (NumberUtils.compare(c1.getViolation(), 0) > 0 ? 0 : c2.getViolation())) {
            throw new RuntimeException("Wrong propagate at " + getClass().getName() + "");
        }
    }
}
