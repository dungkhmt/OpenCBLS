package localsearch.constraint.basic.logic;

import localsearch.constraint.abstract_constraint.ConstraintManyConstraint;
import localsearch.model.IConstraint;
import localsearch.model.Invariant;
import localsearch.model.variable.VarIntLS;
import localsearch.utils.CountingUtils;
import localsearch.utils.NumberUtils;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class Or extends ConstraintManyConstraint {

    private final TreeMap<Double, Integer> mapCounter;
    private final HashMap<IConstraint, Double> mapViolation;

    public Or(IConstraint... constraints) {
        super(constraints);
        mapCounter = new TreeMap<>(NumberUtils.REAL_COMPARATOR);
        mapViolation = new HashMap<>(constraints.length);
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        TreeMap<Double, Integer> map = new TreeMap<>(NumberUtils.REAL_COMPARATOR);
        for (IConstraint constraint : Invariant.getRefInvariants(variables, mapVarToConstraint)) {
            double nextViolation = constraint.getViolation() + constraint.getAssignDelta(variables, values);
            CountingUtils.update(constraint.getViolation(), nextViolation, map);
        }
        return CountingUtils.getFirst(mapCounter, map, NumberUtils.REAL_COMPARATOR) - violation;
    }

    @Override
    public void propagate(Set<VarIntLS> variables) {
        for (IConstraint constraint : Invariant.getRefInvariants(variables, mapVarToConstraint)) {
            CountingUtils.update(mapViolation.get(constraint), constraint.getViolation(), mapCounter);
            mapViolation.put(constraint, constraint.getViolation());
        }
        violation = mapCounter.firstKey();
    }

    @Override
    public void initPropagate() {
        violation = Double.MAX_VALUE;
        for (IConstraint constraint : constraints) {
            mapCounter.merge(constraint.getViolation(), 1, Integer::sum);
            mapViolation.put(constraint, constraint.getViolation());
            if (NumberUtils.compare(constraint.getViolation(), violation) < 0) {
                violation = constraint.getViolation();
            }
        }
    }

    public void verify() {
        double s = Double.MAX_VALUE;
        for (IConstraint constraint : constraints) {
            if (NumberUtils.compare(constraint.getViolation(), s) < 0) {
                s = constraint.getViolation();
            }
        }
        if (NumberUtils.compare(violation, s) != 0) {
            throw new RuntimeException("Wrong propagate at " + getClass().getName() + ": expect: " + s + ", actual: " + violation);
        }
    }
}
