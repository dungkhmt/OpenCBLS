package localsearch.model;

import localsearch.model.variable.VarIntLS;
import localsearch.utils.ClassUtils;

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
            for (VarIntLS var : variables) {
                if (var.getLocalSearchManager() != this) {
                    throw new RuntimeException("Wrong modelling!!");
                }
            }
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
        Set<VarIntLS> varChanges = new HashSet<>();
        for (int i = 0; i < variables.length; ++i) {
            if (variables[i].getValue() != values[i]) {
                varChanges.add(variables[i]);
            }
        }
        ArrayList<Invariant> invariants = new ArrayList<>(Invariant.getRefInvariants(varChanges, mapVarToInvariant));
        invariants.sort(PROPAGATE_ORDER_COMPARATOR);

//        for verify
//        HashMap<Invariant, Double> mapInvariantDelta = getAssignDeltaVerify(invariants, variables, values);

        for (int i = 0; i < variables.length; ++i) {
            variables[i].setValue(values[i]);
        }
        for (Invariant invariant : invariants) {
//            invariant.propagateConfirm(variableSet, mapInvariantDelta.get(invariant));
            invariant.propagate(varChanges);
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

    private String dotString = null;

    private void buildDot() {
        Map<String, List<Object>> mapSameTypes = new HashMap<>();
        Map<Object, String> mapNames = new HashMap<>();
        Map<Integer, List<Invariant>> mapSameLevels = new HashMap<>();
        for (VarIntLS var : variables) {
            mapSameTypes.computeIfAbsent(ClassUtils.getClassName(var), k -> new ArrayList<>()).add(var);
        }
        for (Invariant invariant : allInvariants) {
            mapSameTypes.computeIfAbsent(ClassUtils.getClassName(invariant), k -> new ArrayList<>()).add(invariant);
            mapSameLevels.computeIfAbsent(invariant.getLevel(), k -> new ArrayList<>()).add(invariant);
        }
        mapSameTypes.forEach((k, v) -> {
            int i = 0;
            for (Object o : v) {
                mapNames.put(o, k + i);
                ++i;
            }
        });
        StringBuilder sb = new StringBuilder();
        sb.append("digraph DG {\n\nnode [shape = record];\nranksep = 3.0; size = \"10,10\";\n\n");
        for (VarIntLS var : variables) {
            sb.append(mapNames.get(var)).append("  [shape = circle]\n");
        }
        sb.append("\n");
        for (List<Invariant> invariants : mapSameLevels.values()) {
            sb.append("{ rank = same ");
            for (Invariant invariant : invariants) {
                sb.append(mapNames.get(invariant)).append(" ");
            }
            sb.append("}\n");
        }
        sb.append("\n");
        for (Invariant invariant : allInvariants) {
            if (invariant.getDependencyInvariants().length == 0) {
                for (VarIntLS var : invariant.getVariables()) {
                    sb.append(mapNames.get(var)).append(" -> ").append(mapNames.get(invariant)).append(";\n");
                }
            }
        }
        sb.append("\n");
        for (Invariant invariant : allInvariants) {
            for (Invariant childInvariant : invariant.getDependencyInvariants()) {
                sb.append(mapNames.get(childInvariant)).append(" -> ").append(mapNames.get(invariant)).append(";\n");
            }
        }
        sb.append("}");
        dotString = sb.toString();
    }

    public String getDotString() {
        if (dotString == null) {
            buildDot();
        }
        return dotString;
    }
}
