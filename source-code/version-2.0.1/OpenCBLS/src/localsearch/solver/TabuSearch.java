package localsearch.solver;

import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.variable.VarIntLS;
import localsearch.solver.lns_solver.IObjective;
import localsearch.solver.model.Move;
import localsearch.utils.NumberUtils;

import java.util.*;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class TabuSearch {

    private final static int NUM_THREADS = 4;

    public static TabuSearch getInstance() {
        if (instance == null) {
            instance = new TabuSearch();
        }
        return instance;
    }

    private static TabuSearch instance;

    private TabuSearch() {
    }

    public void search(IConstraint constraint, int tabuLen, int maxStable, int maxIterNotBetter, Random rd) {
        VarIntLS[] variables = constraint.getVariables();
        HashMap<VarIntLS, HashMap<Integer, Integer>> tabuMap = new HashMap<>(variables.length);
        int[] bestSolution = new int[variables.length];
        double bestViolation = constraint.getViolation();
        ArrayList<Move> moves = new ArrayList<>();
        int nic = 0;

        for (int i = 0; i < variables.length; ++i) {
            bestSolution[i] = variables[i].getValue();
            tabuMap.put(variables[i], new HashMap<>());
        }

        int iter = 0;
        int iterNotBetter = 0;
        while (NumberUtils.compare(constraint.getViolation(), 0) > 0) {
            final double[] minDelta = {Double.MAX_VALUE};
            moves.clear();
            Thread[] ts = new Thread[NUM_THREADS];
            for (int i = 0; i < ts.length; ++i) {
                final int iFn = i;
                final int iterFn = iter;
                final double bestViolationFn = bestViolation;
                ts[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int j = iFn; j < variables.length; j += NUM_THREADS) {
                            VarIntLS variable = variables[j];
                            for (Integer value : variable.getDomainArray()) {
                                double delta = constraint.getAssignDelta(variable, value);
                                Integer banPos = tabuMap.get(variable).get(value);
                                if (banPos == null || banPos <= iterFn || constraint.getViolation() + delta < bestViolationFn) {
                                    synchronized (this) {
                                        if (NumberUtils.compare(delta, minDelta[0]) < 0) {
                                            minDelta[0] = delta;
                                            moves.clear();
                                        }
                                        if (NumberUtils.compare(delta, minDelta[0]) <= 0) {
                                            moves.add(new Move(variable, value));
                                        }
                                    }
                                }
                            }
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (moves.isEmpty()) {
//                System.out.println("TabuSearch::restart.....");
                restartMaintainConstraint(variables, constraint, tabuMap, rd);
                if (NumberUtils.compare(constraint.getViolation(), bestViolation) < 0) {
                    bestViolation = constraint.getViolation();
                    for (int i = 0; i < variables.length; ++i) {
                        bestSolution[i] = variables[i].getValue();
                    }
                    nic = 0;
                    iterNotBetter = 0;
                } else {
                    ++iterNotBetter;
                }
            } else {
                Move move = moves.get(rd.nextInt(moves.size()));
                move.getVariable().setValuePropagate(move.getValue());
                tabuMap.get(move.getVariable()).put(move.getValue(), iter + tabuLen);
                if (NumberUtils.compare(constraint.getViolation(), bestViolation) < 0) {
                    bestViolation = constraint.getViolation();
                    for (int i = 0; i < variables.length; ++i) {
                        bestSolution[i] = variables[i].getValue();
                    }
                    nic = 0;
                    iterNotBetter = 0;
                } else {
                    ++nic;
                    ++iterNotBetter;
                    if (nic > maxStable) {
//                        System.out.println("TabuSearch::restart.....");
                        restartMaintainConstraint(variables, constraint, tabuMap, rd);
                        nic = 0;
                    }
                }
                System.out.format("Step %d: violation = %f, best = %f, minDelta= %f, nic=%d\n",
                        iter, constraint.getViolation(), bestViolation, minDelta[0], nic);
            }
            ++iter;
            if (iterNotBetter > maxIterNotBetter) {
                break;
            }
        }
        constraint.getLocalSearchManager().propagate(variables, bestSolution);
    }

    private void restartMaintainConstraint(VarIntLS[] variables, IConstraint constraint,
                                           HashMap<VarIntLS, HashMap<Integer, Integer>> tabuMap, Random rd) {
        for (VarIntLS variable : variables) {
            ArrayList<Integer> goodValue = new ArrayList<>();
            for (Integer value : variable.getDomainArray()) {
                if (NumberUtils.compare(constraint.getAssignDelta(variable, value), 0) <= 0) {
                    goodValue.add(value);
                }
            }
            variable.setValuePropagate(goodValue.get(rd.nextInt(goodValue.size())));
            tabuMap.get(variable).clear();
        }
    }


    /*______________________________ Use Objective _________________________________________*/

    public void search(VarIntLS[] variables, IObjective objective, int tabuLen, int maxStable, int maxIterNotBetter, Random rd) {
        LocalSearchManager localSearchManager = variables[0].getLocalSearchManager();
        Object globalObjectiveObject = new Object();
        objective.update(globalObjectiveObject);
        HashMap<VarIntLS, HashMap<Integer, Integer>> tabuMap = new HashMap<>(variables.length);
        int[] bestSolution = new int[variables.length];
        ArrayList<Move> moves = new ArrayList<>();
        int nic = 0;
        TabuSearchInfo info = new TabuSearchInfo();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(info.info);
            }
        }, 1000, 1000);

        for (int i = 0; i < variables.length; ++i) {
            bestSolution[i] = variables[i].getValue();
            tabuMap.put(variables[i], new HashMap<>());
        }

        int iter = 0;
        int iterNotBetter = 0;
        while (!objective.isAcceptSolution()) {
            Object stepObjectiveObject = new Object();
            objective.update(stepObjectiveObject, true);
            moves.clear();
            Thread[] ts = new Thread[NUM_THREADS];
            for (int i = 0; i < ts.length; ++i) {
                final int iFn = i;
                final int iterFn = iter;
                ts[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int j = iFn; j < variables.length; j += NUM_THREADS) {
                            VarIntLS variable = variables[j];
                            for (Integer value : variable.getDomainArray()) {
                                int better = objective.isBetter(stepObjectiveObject, variable, value);
                                Integer banPos = tabuMap.get(variable).get(value);
                                if (banPos == null || banPos <= iterFn ||
                                        objective.isBetter(stepObjectiveObject, globalObjectiveObject) == -1) {
                                    synchronized (this) {
                                        if (better < 0) {
                                            moves.clear();
                                        }
                                        if (better <= 0) {
                                            moves.add(new Move(variable, value));
                                        }
                                    }
                                }
                            }
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (moves.isEmpty()) {
//                System.out.println("TabuSearch::restart.....");
                restartMaintainConstraint(variables, objective, tabuMap, rd);
                if (objective.isBetter(globalObjectiveObject) < 0) {
                    objective.update(globalObjectiveObject);
                    for (int i = 0; i < variables.length; ++i) {
                        bestSolution[i] = variables[i].getValue();
                    }
                    nic = 0;
                    iterNotBetter = 0;
                } else {
                    ++iterNotBetter;
                }
            } else {
                Move move = moves.get(rd.nextInt(moves.size()));
                move.getVariable().setValuePropagate(move.getValue());
                tabuMap.get(move.getVariable()).put(move.getValue(), iter + tabuLen);
                if (objective.isBetter(globalObjectiveObject) < 0) {
                    objective.update(globalObjectiveObject);
                    for (int i = 0; i < variables.length; ++i) {
                        bestSolution[i] = variables[i].getValue();
                    }
                    nic = 0;
                    iterNotBetter = 0;
                } else {
                    ++nic;
                    ++iterNotBetter;
                    if (nic > maxStable) {
//                        System.out.println("TabuSearch::restart.....");
                        restartMaintainConstraint(variables, objective, tabuMap, rd);
                        nic = 0;
                    }
                }
                info.info = new StringBuilder("Step ").append(iter).append(": current = (").append(objective.currentValue())
                        .append("), best = (").append(objective.value(globalObjectiveObject))
                        .append("), nic=").append(nic);
            }
            ++iter;
            if (iterNotBetter > maxIterNotBetter) {
                break;
            }
        }
        localSearchManager.propagate(variables, bestSolution);
        timer.cancel();
    }

    private void restartMaintainConstraint(VarIntLS[] variables, IObjective objective,
                                           HashMap<VarIntLS, HashMap<Integer, Integer>> tabuMap, Random rd) {
        for (VarIntLS variable : variables) {
            ArrayList<Integer> goodValue = new ArrayList<>();
            for (Integer value : variable.getDomainArray()) {
                Object o = new Object();
                objective.update(o);
                if (objective.isBetter(o, variable, value) <= 0) {
                    goodValue.add(value);
                }
            }
            variable.setValuePropagate(goodValue.get(rd.nextInt(goodValue.size())));
            tabuMap.get(variable).clear();
        }
    }

    private static class TabuSearchInfo {
        StringBuilder info;
    }

}

