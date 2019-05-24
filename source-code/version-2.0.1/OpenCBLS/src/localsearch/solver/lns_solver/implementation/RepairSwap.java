package localsearch.solver.lns_solver.implementation;

import localsearch.model.variable.VarIntLS;
import localsearch.solver.lns_solver.IObjective;

import java.util.*;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class RepairSwap extends RepairDefault {

    private HashSet<Integer>[] domains;
    private final HashMap<Integer, Integer> valueCounter;
    private int numRepair;

    public RepairSwap(IObjective objective, Random rd, int numThreads) {
        super(objective, rd, numThreads);
        valueCounter = new HashMap<>();
    }

    @Override
    public int[] repair(VarIntLS[] destroyVariables) {
        numRepair = destroyVariables.length;
        repairVariables = destroyVariables;
        moves = new ArrayList<>();
        valueCounter.clear();
        domainInit(destroyVariables);
        for (VarIntLS destroyVariable : destroyVariables) {
            valueCounter.merge(destroyVariable.getValue(), 1, Integer::sum);
        }
        objective.update(this);
        multiThreadSearch();
        if (moves.isEmpty()) {
            return null;
        }
        return moves.get(rd.nextInt(moves.size()));
    }

    private void multiThreadSearch() {
        Thread[] ts = new Thread[numThreads];
        for (int i = 0; i < ts.length; ++i) {
            final int id = i;
            ts[i] = new Thread(() -> {
                int[] tempSolution = new int[numRepair];
                Integer[] values = valueCounter.keySet().toArray(new Integer[0]);
                Integer[] count = valueCounter.values().toArray(new Integer[0]);
                for (int i1 = id; i1 < values.length; i1 += numThreads) {
                    if (domains[0].contains(values[i1])) {
                        tempSolution[0] = values[i1];
                        --count[i1];
                        recursiveSearch(1, tempSolution, values, count);
                        ++count[i1];
                    }
                }
            });
        }
        for (Thread t : ts) {
            t.start();
        }
        try {
            for (Thread t : ts) {
                t.join();
            }
        } catch (InterruptedException ignored) {
        }
    }

    private void recursiveSearch(int varId, int[] tempSolution, Integer[] values, Integer[] count) {
        if (varId == numRepair) {
            tempSolutionProcess(tempSolution);
        } else {
            for (int i = 0; i < values.length; ++i) {
                if (count[i] > 0 && domains[varId].contains(values[i])) {
                    tempSolution[varId] = values[i];
                    --count[i];
                    recursiveSearch(varId + 1, tempSolution, values, count);
                    ++count[i];
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void domainInit(VarIntLS[] destroyVariables) {
        domains = new HashSet[numRepair];
        for (int i = 0; i < numRepair; ++i) {
            domains[i] = new HashSet<>();
            Collections.addAll(domains[i], destroyVariables[i].getDomainArray());
        }
    }
}
