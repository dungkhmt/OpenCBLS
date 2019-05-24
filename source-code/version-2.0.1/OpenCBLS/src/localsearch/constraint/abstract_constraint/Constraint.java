package localsearch.constraint.abstract_constraint;

import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.variable.VarIntLS;
import localsearch.utils.NumberUtils;

import java.util.Set;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public abstract class Constraint implements IConstraint {

    protected double violation;
    protected VarIntLS[] variables;
    protected LocalSearchManager localSearchManager;

    @Override
    public double getViolation() {
        return violation;
    }

    @Override
    public void propagateConfirm(Set<VarIntLS> variables, double delta) {
        double oldViolation = violation;
        propagate(variables);
        if (NumberUtils.compare(violation - oldViolation, delta) != 0) {
            throw new RuntimeException("Wrong getAssignDelta at " + getClass().getName() + ": expect: " + (violation - oldViolation) + ", actual: " + delta);
        }
    }

    @Override
    public VarIntLS[] getVariables() {
        return variables;
    }

    @Override
    public LocalSearchManager getLocalSearchManager() {
        return localSearchManager;
    }

    @Override
    public void verify() {
        double propagateViolation = violation;
        initPropagate();
        double initViolation = violation;
        if (NumberUtils.compare(propagateViolation, initViolation) != 0) {
            throw new RuntimeException("Wrong propagate at " + getClass().getName() + ": expect: " + initViolation + ", actual: " + propagateViolation);
        }
    }
}
