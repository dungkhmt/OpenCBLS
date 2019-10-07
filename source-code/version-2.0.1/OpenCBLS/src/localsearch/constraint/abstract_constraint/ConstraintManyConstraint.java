package localsearch.constraint.abstract_constraint;

import localsearch.model.IConstraint;
import localsearch.model.Invariant;
import localsearch.model.variable.VarIntLS;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public abstract class ConstraintManyConstraint extends Constraint {

    protected final IConstraint[] constraints;
    protected final HashMap<VarIntLS, Set<IConstraint>> mapVarToConstraint;
    private final int level;

    public ConstraintManyConstraint(IConstraint... constraints) {
        this.constraints = constraints;
        localSearchManager = constraints[0].getLocalSearchManager();
        mapVarToConstraint = new HashMap<>();
        int maxLevel = 0;
        for (IConstraint constraint : constraints) {
            for (VarIntLS variable : constraint.getVariables()) {
                mapVarToConstraint.computeIfAbsent(variable, k -> new HashSet<>(Math.min(16, constraints.length))).add(constraint);
            }
            if (constraint.getLevel() > maxLevel) {
                maxLevel = constraint.getLevel();
            }
        }
        variables = mapVarToConstraint.keySet().toArray(new VarIntLS[0]);
        level = maxLevel + 1;
        localSearchManager.post(this);
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Invariant[] getDependencyInvariants() {
        return constraints;
    }
}
