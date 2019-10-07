package localsearch.solver.lns_solver.implementation;

import localsearch.model.variable.VarIntLS;
import localsearch.solver.lns_solver.IObjective;
import localsearch.solver.lns_solver.IRepair;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class RepairDefault implements IRepair {

    protected final Random rd;

    protected VarIntLS[] repairVariables;
    protected int[] tempSolution;
    protected ArrayList<int[]> moves;
    protected final IObjective objective;

    protected int iter;

    protected final Integer numThreads;

    public RepairDefault(IObjective objective, Random rd) {
        this.rd = rd;
        this.numThreads = null;
        this.objective = objective;
        objective.update(this);
        iter = 0;
    }

    public RepairDefault(IObjective objective, Random rd, Integer numThreads) {
        this.rd = rd;
        this.numThreads = numThreads;
        this.objective = objective;
        iter = 0;
    }

    @Override
    public int[] repair(VarIntLS[] destroyVariables) {
        ++iter;
        repairVariables = destroyVariables;
        moves = new ArrayList<>();
        objective.update(this);

        if (numThreads == null) {
            tempSolution = new int[repairVariables.length];
            recursiveSearch(0);
        } else {
            multiThreadSearch(getIdVarMaxRange());
        }

        if (moves.isEmpty()) {
            int[] values = new int[destroyVariables.length];
            for (int i = 0; i < values.length; ++i) {
                values[i] = destroyVariables[i].getValue();
            }
            int better = objective.isBetter(this, destroyVariables, values);
            if (better == 0) {
                return values;
            }
            System.out.println("Better = " + better);
            throw new RuntimeException("No valid move exception.");
        }
        return moves.get(rd.nextInt(moves.size()));
    }

    private int getIdVarMaxRange() {
        int resId = 0;
        int max = 0;
        for (int i = 0; i < repairVariables.length; ++i) {
            int range = repairVariables[i].getDomainSize();
            if (range > max) {
                max = range;
                resId = i;
            }
        }
        return resId;
    }

    private void multiThreadSearch(int idVarMaxRange) {
        final Integer[] domainArray;
        domainArray = repairVariables[idVarMaxRange].getDomainArray();
        Thread[] ts = new Thread[numThreads];
        for (int i = 0; i < ts.length; ++i) {
            final int id = i;
            ts[i] = new Thread(() -> {
                int[] tempSolution = new int[repairVariables.length];
                for (int i1 = id; i1 < domainArray.length; i1 += numThreads) {
                    tempSolution[idVarMaxRange] = domainArray[i1];
                    if (idVarMaxRange == 0) {
                        recursiveSearch(1, idVarMaxRange, tempSolution);
                    } else {
                        recursiveSearch(0, idVarMaxRange, tempSolution);
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

    private void recursiveSearch(int varId, int idVarMaxRange, int[] tempSolution) {
        if (varId == repairVariables.length) {
            tempSolutionProcess(tempSolution);
        } else {
            for (int value : repairVariables[varId].getDomainArray()) {
                tempSolution[varId] = value;
                if (varId + 1 == idVarMaxRange) {
                    recursiveSearch(varId + 2, idVarMaxRange, tempSolution);
                } else {
                    recursiveSearch(varId + 1, idVarMaxRange, tempSolution);
                }
            }
        }
    }

    protected void recursiveSearch(int varId) {
        if (varId == repairVariables.length) {
            tempSolutionProcess(tempSolution);
        } else {
            for (int value : repairVariables[varId].getDomainArray()) {
                tempSolution[varId] = value;
                recursiveSearch(varId + 1);
            }
        }
    }

    protected int[] arrayClone(int[] array) {
        int[] a = new int[array.length];
        System.arraycopy(array, 0, a, 0, a.length);
        return a;
    }

    protected void tempSolutionProcess(int[] tempSolution) {
        boolean currentSolution = true;
        for (int i = 0; i < repairVariables.length; ++i) {
            if (repairVariables[i].getValue() != tempSolution[i]) {
                currentSolution = false;
                break;
            }
        }
        int better = currentSolution ? 0 : objective.isBetter(this, repairVariables, tempSolution);
        synchronized (this) {
            if (better <= 0) {
                if (better < 0) {
                    moves.clear();
                }
                moves.add(arrayClone(tempSolution));
            }
        }
    }
}
