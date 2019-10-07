package localsearch.constraint.abstract_constraint;

import localsearch.model.IConstraint;
import localsearch.model.Invariant;
import localsearch.model.variable.VarIntLS;

import java.util.Collections;
import java.util.HashSet;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public abstract class ConstraintTwoConstraint extends Constraint {

    protected final IConstraint c1;
    protected final IConstraint c2;
    protected final HashSet<VarIntLS> variableSet;
    private final int level;

    public ConstraintTwoConstraint(IConstraint c1, IConstraint c2) {
        this.c1 = c1;
        this.c2 = c2;
        variableSet = new HashSet<>(c1.getVariables().length + c2.getVariables().length);
        Collections.addAll(variableSet, c1.getVariables());
        Collections.addAll(variableSet, c2.getVariables());
        variables = variableSet.toArray(new VarIntLS[0]);
        localSearchManager = c1.getLocalSearchManager();
        if (c1.getLevel() > c2.getLevel()) {
            level = c1.getLevel() + 1;
        } else {
            level = c2.getLevel() + 1;
        }
        localSearchManager.post(this);
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Invariant[] getDependencyInvariants() {
        return new IConstraint[]{c1, c2};
    }
}
