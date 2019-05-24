package localsearch.model;

import localsearch.model.variable.VarIntLS;

import java.util.*;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class LocalSearchManager {

    private final ArrayList<Invariant> allInvariants;
    private final HashMap<VarIntLS, Set<Invariant>> mapVarToInvariant;
    private VarIntLS[] variables;
    private boolean closed;

    private static final Comparator<Invariant> PROPAGATE_ORDER_COMPARATOR = Comparator.comparingInt(Invariant::getLevel);

    public LocalSearchManager() {
        allInvariants = new ArrayList<>();
        mapVarToInvariant = new HashMap<>();
    }

    public void post(Invariant invariant) {
        allInvariants.add(invariant);
    }

    public void close() {
        if (!closed) {
            for (Invariant invariant : allInvariants) {
                for (VarIntLS x : invariant.getVariables()) {
                    mapVarToInvariant.computeIfAbsent(x,
                            k -> new HashSet<>(Math.min(16, allInvariants.size()))).add(invariant);
                }
            }
            variables = mapVarToInvariant.keySet().toArray(new VarIntLS[0]);
            closed = true;
        }
        initPropagate();
    }

    public void initPropagate() {
        ArrayList<Invariant> invariants = new ArrayList<>(allInvariants);
        invariants.sort(PROPAGATE_ORDER_COMPARATOR);
        for (Invariant invariant : invariants) {
            invariant.initPropagate();
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public void propagate(VarIntLS x) {
        HashSet<VarIntLS> variableSet = new HashSet<>(1);
        variableSet.add(x);
        ArrayList<Invariant> invariants = new ArrayList<>(mapVarToInvariant.get(x));
        invariants.sort(PROPAGATE_ORDER_COMPARATOR);
        for (Invariant invariant : invariants) {
            invariant.propagate(variableSet);
        }
    }

    public void propagate(VarIntLS[] variables, int[] values) {
        ArrayList<Invariant> invariants = new ArrayList<>(Invariant.getRefInvariants(variables, mapVarToInvariant));
        invariants.sort(PROPAGATE_ORDER_COMPARATOR);

//        for verify
//        HashMap<Invariant, Double> mapInvariantDelta = getAssignDeltaVerify(invariants, variables, values);

        for (int i = 0; i < variables.length; ++i) {
            variables[i].setValue(values[i]);
        }
        HashSet<VarIntLS> variableSet = new HashSet<>(variables.length);
        Collections.addAll(variableSet, variables);
        for (Invariant invariant : invariants) {
//            invariant.propagateConfirm(variableSet, mapInvariantDelta.get(invariant));
            invariant.propagate(variableSet);
        }
//        propagateVerify(invariants);
    }

    private void propagateVerify(Collection<Invariant> invariants) {
        for (Invariant invariant : invariants) {
            invariant.verify();
        }
    }

    private HashMap<Invariant, Double> getAssignDeltaVerify(Collection<Invariant> invariants,
                                                            VarIntLS[] variables,
                                                            int[] values) {
        HashMap<Invariant, Double> mapInvariantDelta = new HashMap<>();
        for (Invariant invariant : invariants) {
            mapInvariantDelta.put(invariant, invariant.getAssignDelta(variables, values));
        }
        return mapInvariantDelta;
    }

    public VarIntLS[] getVariables() {
        return variables;
    }

    public int getNumVariables() {
        return variables.length;
    }

    public int getNumInvariants() {
        return allInvariants.size();
    }
}
