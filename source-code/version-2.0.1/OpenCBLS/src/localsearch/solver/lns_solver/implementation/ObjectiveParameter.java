package localsearch.solver.lns_solver.implementation;

import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.Invariant;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class ObjectiveParameter {

    private final Invariant invariant;
    private final boolean maximize;
    private final double bestValue;
    private final String name;

    public ObjectiveParameter(Invariant invariant, boolean maximize, double bestValue, String name) {
        this.invariant = invariant;
        this.maximize = maximize;
        this.bestValue = bestValue;
        this.name = name;
    }

    public Invariant getInvariant() {
        return invariant;
    }

    public boolean isMaximize() {
        return maximize;
    }

    public double getBestValue() {
        return bestValue;
    }

    public String getName() {
        return name;
    }

    public double getInvariantValue() {
        if (invariant instanceof IConstraint) {
            return ((IConstraint) invariant).getViolation();
        }
        return ((IFunction) invariant).getValue();
    }
}
