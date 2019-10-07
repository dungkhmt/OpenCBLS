package localsearch.model;


import localsearch.model.variable.VarIntLS;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public interface Invariant {

    public VarIntLS[] getVariables();

    public Invariant[] getDependencyInvariants();

    public double getAssignDelta(VarIntLS[] variables, int[] values);

    public default double getAssignDelta(VarIntLS variable, int value) {
        return getAssignDelta(new VarIntLS[]{variable}, new int[]{value});
    }

    public default double getSwapDelta(VarIntLS x, VarIntLS y) {
        return getAssignDelta(new VarIntLS[]{x, y}, new int[]{y.getValue(), x.getValue()});
    }

    public void propagate(Set<VarIntLS> variables);

    public void propagateConfirm(Set<VarIntLS> variables, double delta);

    public void initPropagate();

    public LocalSearchManager getLocalSearchManager();

    public int getLevel();

    public void verify();

    public static <E extends Invariant> Set<E> getRefInvariants(Set<VarIntLS> variables,
                                                                Map<VarIntLS, Set<E>> mapVarToInvariants) {
        HashSet<E> invariantsRefVariables = new HashSet<>();
        for (VarIntLS variable : variables) {
            Set<E> hs = mapVarToInvariants.get(variable);
            if (hs != null) {
                invariantsRefVariables.addAll(hs);
            }
        }
        return invariantsRefVariables;
    }

    public static <E extends Invariant> Set<E> getRefInvariants(VarIntLS[] variables,
                                                                Map<VarIntLS, Set<E>> mapVarToInvariants) {
        HashSet<E> invariantsRefVariables = new HashSet<>();
        for (VarIntLS variable : variables) {
            Set<E> hs = mapVarToInvariants.get(variable);
            if (hs != null) {
                invariantsRefVariables.addAll(hs);
            }
        }
        return invariantsRefVariables;
    }
}
