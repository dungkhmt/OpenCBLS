package localsearch.constraint;

import localsearch.constraint.abstract_constraint.Constraint;
import localsearch.model.IFunction;
import localsearch.model.Invariant;
import localsearch.model.variable.VarIntLS;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class NotOverlap2D extends Constraint {

    private final IFunction x1;
    private final IFunction y1;
    private final IFunction xLen1;
    private final IFunction yLen1;
    private final IFunction x2;
    private final IFunction y2;
    private final IFunction xLen2;
    private final IFunction yLen2;
    private final HashSet<VarIntLS> variableSet;
    private final int level;

    public NotOverlap2D(IFunction x1, IFunction y1, IFunction xLen1, IFunction yLen1, IFunction x2, IFunction y2, IFunction xLen2, IFunction yLen2) {
        this.x1 = x1;
        this.y1 = y1;
        this.xLen1 = xLen1;
        this.yLen1 = yLen1;
        this.x2 = x2;
        this.y2 = y2;
        this.xLen2 = xLen2;
        this.yLen2 = yLen2;
        localSearchManager = x1.getLocalSearchManager();
        variableSet = new HashSet<>();
        Collections.addAll(variableSet, x1.getVariables());
        Collections.addAll(variableSet, y1.getVariables());
        Collections.addAll(variableSet, xLen1.getVariables());
        Collections.addAll(variableSet, yLen1.getVariables());
        Collections.addAll(variableSet, x2.getVariables());
        Collections.addAll(variableSet, y2.getVariables());
        Collections.addAll(variableSet, xLen2.getVariables());
        Collections.addAll(variableSet, yLen2.getVariables());
        variables = variableSet.toArray(new VarIntLS[0]);
        int maxLevel = 0;
        if (x1.getLevel() > maxLevel) maxLevel = x1.getLevel();
        if (y1.getLevel() > maxLevel) maxLevel = y1.getLevel();
        if (xLen1.getLevel() > maxLevel) maxLevel = xLen1.getLevel();
        if (yLen1.getLevel() > maxLevel) maxLevel = yLen1.getLevel();
        if (x2.getLevel() > maxLevel) maxLevel = x2.getLevel();
        if (y2.getLevel() > maxLevel) maxLevel = y2.getLevel();
        if (xLen2.getLevel() > maxLevel) maxLevel = xLen2.getLevel();
        if (yLen2.getLevel() > maxLevel) maxLevel = yLen2.getLevel();
        level = maxLevel + 1;
        localSearchManager.post(this);
    }

    @Override
    public Invariant[] getDependencyInvariants() {
        return new IFunction[]{x1, y1, xLen1, yLen1, x2, y2, xLen2, yLen2};
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        for (VarIntLS variable : variables) {
            if (variableSet.contains(variable)) {
                int x1New = (int) (x1.getValue() + x1.getAssignDelta(variables, values));
                int xlen1New = (int) (xLen1.getValue() + xLen1.getAssignDelta(variables, values));
                int y1New = (int) (y1.getValue() + y1.getAssignDelta(variables, values));
                int ylen1New = (int) (yLen1.getValue() + yLen1.getAssignDelta(variables, values));
                int x2New = (int) (x2.getValue() + x2.getAssignDelta(variables, values));
                int xlen2New = (int) (xLen2.getValue() + xLen2.getAssignDelta(variables, values));
                int y2New = (int) (y2.getValue() + y2.getAssignDelta(variables, values));
                int ylen2New = (int) (yLen2.getValue() + yLen2.getAssignDelta(variables, values));
                int newViolation;
                int dx = Math.min(x1New + xlen1New, x2New + xlen2New) - Math.max(x1New, x2New);
                int dy = Math.min(y1New + ylen1New, y2New + ylen2New) - Math.max(y1New, y2New);
                if (dx > 0 && dy > 0) {
                    newViolation = dx * dy;
                } else {
                    newViolation = 0;
                }
                return newViolation - violation;
            }
        }
        return 0;
    }

    @Override
    public void propagate(Set<VarIntLS> variables) {
        for (VarIntLS variable : variables) {
            if (variableSet.contains(variable)) {
                initPropagate();
                return;
            }
        }
    }

    @Override
    public void initPropagate() {
        int dx = (int) (Math.min(x1.getValue() + xLen1.getValue(), x2.getValue() + xLen2.getValue()) - Math.max(x1.getValue(), x2.getValue()));
        int dy = (int) (Math.min(y1.getValue() + yLen1.getValue(), y2.getValue() + yLen2.getValue()) - Math.max(y1.getValue(), y2.getValue()));
        if (dx > 0 && dy > 0) {
            violation = dx * dy;
        } else {
            violation = 0;
        }
    }

    @Override
    public int getLevel() {
        return level;
    }
}
