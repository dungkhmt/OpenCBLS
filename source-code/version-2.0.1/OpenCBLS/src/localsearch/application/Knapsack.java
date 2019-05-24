package localsearch.application;

import localsearch.constraint.basic.operator.LessEqual;
import localsearch.function.conditional.ConditionalSumVarConst;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.variable.VarIntLS;
import localsearch.solver.TabuSearch;
import localsearch.solver.lns_solver.IObjective;
import localsearch.solver.lns_solver.LnsSolver;
import localsearch.solver.lns_solver.implementation.*;

import java.util.Random;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class Knapsack {

    private static int[] backup(VarIntLS[] variables) {
        int[] solution = new int[variables.length];
        for (int i = 0; i < solution.length; ++i) {
            solution[i] = variables[i].getValue();
        }
        return solution;
    }

    public static void main(String[] args) {
        int size = 5000;
        Random rd = new Random();
//        double[] w = {0.335, 0.581, 0.620, 0.190, 0.325, 0.142, 0.883, 0.089, 0.617, 0.884, 0.344, 0.384, 0.935, 0.377, 0.258, 0.905, 0.787, 0.090, 0.226, 0.072, 0.680, 0.828, 0.277, 0.428, 0.638, 0.772, 0.291, 0.971, 0.909, 0.300, 0.278, 0.098, 0.004, 0.078, 0.280, 0.255, 0.475, 0.103, 0.925, 0.619, 0.057, 0.671, 0.896, 0.401, 0.091, 0.464, 0.827, 0.585, 0.350, 0.227, 0.421, 0.213, 0.679, 0.730, 0.770, 0.521, 0.090, 0.978, 0.192, 0.521, 0.199, 0.877, 0.064, 0.863, 0.157, 0.808, 0.444, 0.052, 0.472, 0.820, 0.501, 0.355, 0.355, 0.399, 0.009, 0.983, 0.494, 0.020, 0.433, 0.375, 0.404, 0.932, 0.386, 0.667, 0.730, 0.898, 0.452, 0.685, 0.172, 0.830, 0.673, 0.031, 0.902, 0.542, 0.193, 0.109, 0.641, 0.061, 0.856, 0.157};
//        double[] v = {0.435, 0.513, 0.056, 0.524, 0.113, 0.760, 0.173, 0.940, 0.220, 0.318, 0.855, 0.068, 0.676, 0.404, 0.827, 0.131, 0.367, 0.123, 0.267, 0.923, 0.333, 0.231, 0.295, 0.591, 0.633, 0.956, 0.449, 0.170, 0.827, 0.593, 0.710, 0.247, 0.236, 0.887, 0.866, 0.358, 0.419, 0.418, 0.263, 0.994, 0.539, 0.490, 0.259, 0.310, 0.528, 0.269, 0.916, 0.921, 0.261, 0.131, 0.741, 0.386, 0.128, 0.493, 0.519, 0.242, 0.703, 0.591, 0.547, 0.709, 0.429, 0.947, 0.813, 0.430, 0.492, 0.526, 0.923, 0.843, 0.315, 0.804, 0.832, 0.070, 0.346, 0.250, 0.665, 0.425, 0.060, 0.948, 0.201, 0.903, 0.362, 0.357, 0.798, 0.265, 0.807, 0.444, 0.826, 0.590, 0.318, 0.672, 0.901, 0.392, 0.325, 0.596, 0.474, 0.679, 0.500, 0.034, 0.576, 0.561};

        double[] w = new double[size];
        double[] v = new double[size];

        for (int i = 0; i < w.length; ++i) {
            w[i] = rd.nextDouble();
        }
        for (int i = 0; i < v.length; ++i) {
            v[i] = rd.nextDouble();
        }

        double maxW = 10;


        LocalSearchManager localSearchManager = new LocalSearchManager();
        ConstraintSystem cs = new ConstraintSystem();

        VarIntLS[] x = new VarIntLS[size];
        for (int i = 0; i < size; ++i) {
            x[i] = new VarIntLS(0, 1, localSearchManager, rd);
        }
        cs.post(new LessEqual(new ConditionalSumVarConst(x, 1, w), maxW));
        IFunction sumValue = new ConditionalSumVarConst(x, 1, v);

        cs.close();
        localSearchManager.close();

        System.out.format("Num variables: %d\nNum invariants: %d\n",
                localSearchManager.getNumVariables(), localSearchManager.getNumInvariants());

        int[] initSolution = backup(x);

        IObjective objective = new MultiObjective(
                new ObjectiveParameter(cs, false, 0, "violation"),
                new ObjectiveParameter(sumValue, true, Double.MAX_VALUE, "value")
        );

        TabuSearch.getInstance().search(localSearchManager.getVariables(), objective, 20, 100, 10000, rd);
        double sumW = 0;
        double sumV = 0;
        System.out.println("Solution: ");
        for (int i = 0; i < size; ++i) {
            if (x[i].getValue() == 1) {
                sumW += w[i];
                sumV += v[i];
            }
        }
        System.out.println();
        System.out.println("Sum weight = " + sumW);
        System.out.println("Sum value = " + sumV);

        localSearchManager.propagate(x, initSolution);

        LnsSolver solver = LnsSolver.newBuilder(localSearchManager).name("LnsSolver(10)")
                .destroy(new DestroyShuffle(cs.getVariables(), 10, objective, rd))
                .repair(new RepairDefault(objective, rd, 2))
                .objective(objective)
                .restart(new RestartCheckpoint(objective, localSearchManager, 5000, 30000))
                .stop(StopDefault.newBuilder().maxIterNotBetter(50000).maxTime(60).objective(objective).build())
                .build();
        solver.solve();

        sumW = 0;
        sumV = 0;
        System.out.println("Solution: ");
        for (int i = 0; i < size; ++i) {
            if (x[i].getValue() == 1) {
                sumW += w[i];
                sumV += v[i];
            }
        }
        System.out.println();
        System.out.println("Sum weight = " + sumW);
        System.out.println("Sum value = " + sumV);
    }

}
