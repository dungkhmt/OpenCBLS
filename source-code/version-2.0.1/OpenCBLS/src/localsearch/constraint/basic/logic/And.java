package localsearch.constraint.basic.logic;

import localsearch.constraint.abstract_constraint.ConstraintManyConstraint;
import localsearch.model.IConstraint;
import localsearch.model.Invariant;
import localsearch.model.variable.VarIntLS;
import localsearch.utils.NumberUtils;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class And extends ConstraintManyConstraint {

    private final double[] violations;
    private final HashMap<IConstraint, Integer> mapConstraintIndex;

    public And(IConstraint... constraints) {
        super(constraints);
        violations = new double[constraints.length];
        mapConstraintIndex = new HashMap<>(constraints.length);
        for (int i = 0; i < constraints.length; ++i) {
            mapConstraintIndex.put(constraints[i], i);
        }
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        double delta = 0;
        for (IConstraint constraint : Invariant.getRefInvariants(variables, mapVarToConstraint)) {
            delta += constraint.getAssignDelta(variables, values);
        }
        return delta;
    }

    @Override
    public void propagate(Set<VarIntLS> variables) {
        for (IConstraint constraint : Invariant.getRefInvariants(variables, mapVarToConstraint)) {
            int idx = mapConstraintIndex.get(constraint);
            violation += (constraint.getViolation() - violations[idx]);
            violations[idx] = constraint.getViolation();
        }
    }

    @Override
    public void initPropagate() {
        violation = 0;
        for (int i = 0; i < constraints.length; ++i) {
            violations[i] = constraints[i].getViolation();
            violation += violations[i];
        }
    }

    public void verify() {
        double s = 0;
        for (IConstraint constraint : constraints) {
            s += constraint.getViolation();
        }
        if (NumberUtils.compare(violation, s) != 0) {
            throw new RuntimeException("Wrong propagate at " + getClass().getName() + ": expect: " + s + ", actual: " + violation);
        }
    }
}
