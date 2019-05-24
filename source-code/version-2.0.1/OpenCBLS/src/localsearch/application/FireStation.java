package localsearch.application;

import localsearch.constraint.basic.operator.LessEqual;
import localsearch.function.math.FuncSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.variable.VarIntLS;
import localsearch.solver.lns_solver.LnsSolver;
import localsearch.solver.lns_solver.implementation.MultiObjective;
import localsearch.solver.lns_solver.implementation.ObjectiveParameter;

import java.util.Random;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class FireStation {

    /**
     * description: https://imgur.com/YySljBZ
     */

    private final LocalSearchManager localSearchManager;
    private final ConstraintSystem cs;
    private final VarIntLS[] variables;
    private final Random rd;
    private final IFunction objectiveFunction;

    public FireStation() {
        this.localSearchManager = new LocalSearchManager();
        this.variables = new VarIntLS[16];
        rd = new Random(1L);
        for (int i = 0; i < variables.length; ++i) {
            variables[i] = new VarIntLS(0, 1, localSearchManager, rd);
        }

        int[][] neighborhoods = {
                {0, 1, 3, 4},
                {0, 1, 2, 4, 5},
                {1, 2, 5, 6},
                {0, 3, 4, 7, 9, 10},
                {0, 1, 3, 4, 5, 7},
                {1, 2, 4, 5, 6, 7, 8},
                {2, 5, 6, 8, 12},
                {3, 4, 5, 7, 8, 10, 11},
                {5, 6, 7, 8, 11, 12},
                {3, 9, 10, 13},
                {3, 7, 9, 10, 11, 13},
                {7, 8, 10, 11, 12, 14},
                {6, 8, 11, 12, 14, 15},
                {9, 10, 13, 14},
                {11, 12, 13, 14, 15},
                {12, 14, 15}
        };

        // app
        cs = new ConstraintSystem();
        for (int[] neighborhood : neighborhoods) {
            VarIntLS[] vars = new VarIntLS[neighborhood.length];
            for (int i = 0; i < neighborhood.length; ++i) {
                vars[i] = variables[neighborhood[i]];
            }
            cs.post(new LessEqual(1, new FuncSum(vars)));
        }
        objectiveFunction = new FuncSum(variables);
        cs.close();
        localSearchManager.close();
    }

    public void solve() {
        LnsSolver solver = LnsSolver.newDefaultSolver(localSearchManager, 3, 1500, 500, 50, rd,
                new MultiObjective(
                        new ObjectiveParameter(cs, false, 0.0, "violation"),
                        new ObjectiveParameter(objectiveFunction, false, 3, "numStations")
                ), "fireStationSearch");
        solver.solve();
    }

    public void solutionPrint() {
        System.out.print("Solution: [");
        for (int i = 0; i < variables.length; ++i) {
            if (variables[i].getValue() == 1) {
                System.out.print((i + 1) + " ");
            }
        }
        System.out.println("]");
    }

    public static void main(String[] args) {
        FireStation fs = new FireStation();
        fs.solve();
        fs.solutionPrint();
    }
}
