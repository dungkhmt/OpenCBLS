package localsearch.solver.lns_solver;

import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.variable.VarIntLS;
import localsearch.solver.lns_solver.implementation.*;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class LnsSolver {

    public final static int NUM_THREADS = 4;

    private String name;
    private IDestroy destroy;
    private IRepair repair;
    private IStop stop;
    private IRestart restart;
    private IObjective objective;

    private final LocalSearchManager localSearchManager;
    private VarIntLS[] variables;
    private int[] bestSolution;

    private Timer timer;
    private StringBuilder sbLog;

    private LnsSolver(Builder builder) {
        name = builder.name;
        destroy = builder.destroy;
        repair = builder.repair;
        stop = builder.stop;
        restart = builder.restart;
        objective = builder.objective;
        objective.update(this);
        localSearchManager = builder.localSearchManager;
        variables = builder.variables;
        bestSolution = builder.bestSolution;
        timer = new Timer();
        sbLog = new StringBuilder();
    }

    private void startTimerPrinter() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(sbLog);
            }
        }, 1000, 1000);
    }

    public static LnsSolver newDefaultSolver(LocalSearchManager localSearchManager,
                                             int destroySize,
                                             int maxIterNotBetter,
                                             int maxIterNotBetterRestart,
                                             int maxIterCheckpoint,
                                             Random rd,
                                             IObjective objective,
                                             String name) {
        return newBuilder(localSearchManager)
                .name(name)
                .destroy(new DestroyShuffle(localSearchManager.getVariables(), destroySize, objective, rd))
                .repair(new RepairDefault(objective, rd, NUM_THREADS))
                .objective(objective)
                .stop(StopDefault.newBuilder().maxIterNotBetter(maxIterNotBetter).objective(objective).build())
                .restart(new RestartCheckpoint(objective, localSearchManager, maxIterNotBetterRestart, maxIterCheckpoint))
                .build();
    }


    public static LnsSolver newDefaultSolver(LocalSearchManager localSearchManager, ConstraintSystem cs) {
        int destroySize = 2;
        int maxIterNotBetter = 5000;
        int maxIterNotBetterRestart = 1500;
        int maxIterCheckpoint = 20000;
        Random rd = new Random(1L);
        IObjective objective = new ConstraintObjective(cs);
        String name = "LnsSolver(" + destroySize + ")";
        return newDefaultSolver(localSearchManager, destroySize, maxIterNotBetter,
                maxIterNotBetterRestart, maxIterCheckpoint, rd, objective, name);
    }

    public static Builder newBuilder(LocalSearchManager localSearchManager) {
        return new Builder(localSearchManager);
    }

    public void solve() {
        startTimerPrinter();
        saveSolution();
        while (!stop.isStop()) {
            VarIntLS[] destroyVariables = destroy.destroy();
            int[] tempSolution = repair.repair(destroyVariables);
            localSearchManager.propagate(destroyVariables, tempSolution);
            sbLog = new StringBuilder("[").append(name).append("] Step: ")
                    .append(stop.getIter()).append(", ").append(objective.currentValue());
            if (restart.isRestart()) {
                restart.restart();
            }
            if (objective.isBetter(this) < 0) {
                saveSolution();
                objective.update(this);
            }
        }
        timer.cancel();
        restoreSolution();
    }

    private void saveSolution() {
        for (int i = 0; i < bestSolution.length; ++i) {
            bestSolution[i] = variables[i].getValue();
        }
    }

    private void restoreSolution() {
        localSearchManager.propagate(variables, bestSolution);
    }

    public static final class Builder {
        private String name;
        private IDestroy destroy;
        private IRepair repair;
        private IStop stop;
        private IRestart restart;
        private IObjective objective;
        private final LocalSearchManager localSearchManager;
        private final VarIntLS[] variables;
        private int[] bestSolution;

        private Builder(LocalSearchManager localSearchManager) {
            this.localSearchManager = localSearchManager;
            this.variables = localSearchManager.getVariables();
            bestSolution = new int[variables.length];
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder destroy(IDestroy destroy) {
            this.destroy = destroy;
            return this;
        }

        public Builder repair(IRepair repair) {
            this.repair = repair;
            return this;
        }

        public Builder stop(IStop stop) {
            this.stop = stop;
            return this;
        }

        public Builder restart(IRestart restart) {
            this.restart = restart;
            return this;
        }

        public Builder objective(IObjective objective) {
            this.objective = objective;
            return this;
        }

        public LnsSolver build() {
            return new LnsSolver(this);
        }
    }
}
