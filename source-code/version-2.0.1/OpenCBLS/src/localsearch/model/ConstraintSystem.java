package localsearch.model;

import localsearch.constraint.basic.logic.And;
import localsearch.model.variable.VarIntLS;

import java.util.ArrayList;
import java.util.Set;


/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class ConstraintSystem implements IConstraint {

    private final ArrayList<IConstraint> allConstraints;
    private boolean closed;
    private And and;

    public ConstraintSystem() {
        allConstraints = new ArrayList<>();
        closed = false;
    }

    public void post(IConstraint constraint) {
        allConstraints.add(constraint);
    }

    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        and = new And(allConstraints.toArray(new IConstraint[0]));
    }

    public VarIntLS[] getVariables() {
        return and.getVariables();
    }

    @Override
    public Invariant[] getDependencyInvariants() {
        return and.getDependencyInvariants();
    }

    @Override
    public void propagate(Set<VarIntLS> variables) {
        and.propagate(variables);
    }

    @Override
    public void initPropagate() {
        and.initPropagate();
    }

    @Override
    public LocalSearchManager getLocalSearchManager() {
        return and.getLocalSearchManager();
    }

    @Override
    public int getLevel() {
        return and.getLevel();
    }

    @Override
    public void verify() {
    }

    public String getName() {
        return "ConstraintSystemLS";
    }

    @Override
    public double getViolation() {
        return and.getViolation();
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        return and.getAssignDelta(variables, values);
    }

    @Override
    public void propagateConfirm(Set<VarIntLS> variables, double delta) {
        and.propagateConfirm(variables, delta);
    }
}
