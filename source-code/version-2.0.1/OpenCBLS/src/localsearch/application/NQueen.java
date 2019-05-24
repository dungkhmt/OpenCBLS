package localsearch.application;

import localsearch.constraint.AllDifferent;
import localsearch.function.math.FuncMinus;
import localsearch.function.math.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.variable.VarIntLS;
import localsearch.solver.lns_solver.LnsSolver;
import localsearch.solver.lns_solver.implementation.ConstraintObjective;

import java.util.Random;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class NQueen {

    private final LocalSearchManager localSearchManager;
    private final ConstraintSystem cs;
    private final VarIntLS[] variables;
    private final Random rd;
    private final int size;

    private NQueen(int size) {
        this.localSearchManager = new LocalSearchManager();
        this.cs = new ConstraintSystem();
        this.size = size;
        variables = new VarIntLS[size];
        rd = new Random();
        for (int i = 0; i < size; ++i) {
            variables[i] = new VarIntLS(0, size - 1, localSearchManager, rd);
        }
        IFunction[] addRowFunc = new IFunction[size];
        IFunction[] subRowFunc = new IFunction[size];
        for (int i = 0; i < size; ++i) {
            addRowFunc[i] = new FuncPlus(variables[i], i);
            subRowFunc[i] = new FuncMinus(variables[i], i);
        }
        cs.post(new AllDifferent(variables));
        cs.post(new AllDifferent(addRowFunc));
        cs.post(new AllDifferent(subRowFunc));
        cs.close();
        localSearchManager.close();
    }

    private void solve() {
        LnsSolver solver = LnsSolver.newDefaultSolver(localSearchManager, 2, 1500, 500, 50, rd, new ConstraintObjective(cs), "LnsSolver(2)");
        solver.solve();
    }

    private void solutionPrint() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (variables[i].getValue() == j) {
                    System.out.print("Q\t");
                } else {
                    System.out.print("-\t");
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        NQueen nQueen = new NQueen(80);
        nQueen.solve();
        nQueen.solutionPrint();
    }
}
