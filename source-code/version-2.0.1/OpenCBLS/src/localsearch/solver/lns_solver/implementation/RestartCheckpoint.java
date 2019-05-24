package localsearch.solver.lns_solver.implementation;

import localsearch.model.LocalSearchManager;
import localsearch.solver.lns_solver.ICheckpoint;
import localsearch.solver.lns_solver.IObjective;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class RestartCheckpoint extends RestartDefault {

    private final Object restartCheckpointObjectiveObject = new Object();
    private final Object refreshCheckpointObjectiveObject = new Object();
    private final Object writeToFileObjectiveObject = new Object();
    private final ArrayList<int[]> solutions;
    private final LocalSearchManager localSearchManager;
    private final ICheckpoint problem;
    private final int maxIterCheckpoint;
    private int iterCheckPoint;
    private int lastRestartPoint;
    private int checkPointId;
    private int iterToRefresh;
    private final int maxIterToRefresh;

    public RestartCheckpoint(IObjective objective,
                             LocalSearchManager localSearchManager,
                             int maxIterNotBetter,
                             int maxIterCheckpoint) {
        this(objective, localSearchManager, null, maxIterNotBetter, maxIterCheckpoint, Integer.MAX_VALUE);
    }

    public RestartCheckpoint(IObjective objective,
                             LocalSearchManager localSearchManager,
                             ICheckpoint problem,
                             int maxIterNotBetter,
                             int maxIterCheckpoint,
                             int maxIterToRefresh) {
        super(objective, maxIterNotBetter);
        this.localSearchManager = localSearchManager;
        this.problem = problem;
        this.maxIterCheckpoint = maxIterCheckpoint;
        this.maxIterToRefresh = maxIterToRefresh;
        solutions = new ArrayList<>();
        objective.update(restartCheckpointObjectiveObject);
        int[] solution = new int[localSearchManager.getVariables().length];
        for (int i = 0; i < solution.length; ++i) {
            solution[i] = localSearchManager.getVariables()[i].getValue();
        }
        solutions.add(solution);
        iterCheckPoint = 0;
        checkPointId = 1;
        iterToRefresh = 0;
    }

    @Override
    public boolean isRestart() {
        ++iterCheckPoint;
        if (iterCheckPoint >= maxIterCheckpoint) {
            iterCheckPoint = 0;
            int[] solution = new int[localSearchManager.getVariables().length];
            for (int i = 0; i < solution.length; ++i) {
                solution[i] = localSearchManager.getVariables()[i].getValue();
            }
            if (checkPointId < solutions.size()) {
                solutions.set(checkPointId, solution);
            } else {
                solutions.add(solution);
            }
            ++checkPointId;
        }
        int better = objective.isBetter(refreshCheckpointObjectiveObject);
        if (better < 0) {
            iterToRefresh = 0;
            objective.update(refreshCheckpointObjectiveObject);
        } else {
            ++iterToRefresh;
            if (iterToRefresh >= maxIterToRefresh) {
                iterToRefresh = 0;
                problem.refresh(objective);
            }
        }
        return super.isRestart();
    }

    @Override
    public void restart() {
        int better = objective.isBetter(restartCheckpointObjectiveObject);
        if (better < 0) {
            objective.update(restartCheckpointObjectiveObject);
            lastRestartPoint = checkPointId - 1;
        } else {
            --lastRestartPoint;
        }
        if (lastRestartPoint == -1) {
            lastRestartPoint = 0;
        }

        if (problem != null) {
            if (objective.isBetter(writeToFileObjectiveObject) < 0) {
                objective.update(writeToFileObjectiveObject);
                File folder = new File("solutions");
                if (!folder.exists()) {
                    folder.mkdir();
                }
                File file = new File(folder,
                        String.format("solution_%s %s.json", objective.currentValue(), SDF.format(new Date())));
                problem.writeSolutionToFile(file);
                System.out.println("Saved file: " + file.getName());
            }
        }

        localSearchManager.propagate(localSearchManager.getVariables(), solutions.get(lastRestartPoint));
        checkPointId = lastRestartPoint + 1;
        objective.update(this);
        if (lastRestartPoint == 0) {
            objective.update(restartCheckpointObjectiveObject);
        }
    }

    private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd-HHmmss");

}
