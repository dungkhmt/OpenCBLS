package localsearch.application;

import localsearch.constraint.basic.operator.LessEqual;
import localsearch.function.conditional.ConditionalSumVarConst;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.variable.VarIntLS;
import localsearch.solver.TabuSearch;
import localsearch.solver.lns_solver.LnsSolver;
import localsearch.solver.lns_solver.implementation.ConstraintObjective;

import java.util.Random;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class MultipleKnapsack {

    private final double[] itemWeights;
    private final double[] binWeights;

    private final VarIntLS[] bin;
    private final LocalSearchManager localSearchManager;
    private final ConstraintSystem cs;

    private static Random rd = new Random();

    public MultipleKnapsack(double[] itemWeights, double[] binWeights) {
        this.itemWeights = itemWeights;
        this.binWeights = binWeights;

        localSearchManager = new LocalSearchManager();
        bin = new VarIntLS[itemWeights.length];
        for (int i = 0; i < itemWeights.length; ++i) {
            bin[i] = new VarIntLS(0, binWeights.length - 1, localSearchManager, rd);
        }
        cs = new ConstraintSystem();
        for (int i = 0; i < binWeights.length; ++i) {
            cs.post(new LessEqual(new ConditionalSumVarConst(bin, i, itemWeights), binWeights[i]));
        }
        cs.close();
        localSearchManager.close();
    }

    public void randomInit() {
        for (int i = 0; i < itemWeights.length; ++i) {
            bin[i].setValuePropagate(bin[i].getDomainArray()[rd.nextInt(bin[i].getDomainSize())]);
        }
    }

    public void printInfo() {
        System.out.println("Multiple Knapsack problem:");
        System.out.println("Num Item: " + itemWeights.length);
        System.out.println("Num Bin: " + binWeights.length);
        System.out.print("Item weight: ");
        int s = 0;
        for (double itemWeight : itemWeights) {
            System.out.print(itemWeight + " ");
            s += itemWeight;
        }
        System.out.println("\nFuncSum item weight: " + s);
        s = 0;
        System.out.print("Bin weight: ");
        for (double binWeight : binWeights) {
            System.out.print(binWeight + " ");
            s += binWeight;
        }
        System.out.println("\nFuncSum bin size: " + s);
    }

    public void solveByTabuSearch() {
        TabuSearch.getInstance().search(localSearchManager.getVariables(),
                new ConstraintObjective(cs), 10, 100, 200, rd);
//        LnsSolver lnsSolver = new LnsSolver(cs, 2, 3000, 100, rd);
////        LnsSolver solver = LnsSolver.newDefaultSolver(localSearchManager, cs);
////        solver.solve();
    }

    public void solveByLnsSolver() {
        LnsSolver solver = LnsSolver.newDefaultSolver(localSearchManager, 2, 3000, 500, 5000, rd, new ConstraintObjective(cs), "lnsSolver");
//        LnsSolver solver = LnsSolver.newDefaultSolver(localSearchManager, cs);
        solver.solve();
    }

    public void varValuePrint() {
        int[] count = new int[bin[0].getDomainArray().length];
        System.out.println("variable value: ");
        for (int i = 0; i < bin.length; ++i) {
            System.out.print(bin[i].getValue() + " ");
            count[bin[i].getValue()] += itemWeights[i];
        }
        System.out.println("\nBin size: ");
        for (int value : count) {
            System.out.print(value + " ");
        }
        System.out.println();
    }

    public void solutionPrint() {
        System.out.println("Solution: ");
        for (int i = 0; i < bin.length; ++i) {
            System.out.format("bin[%d]=%d ", i, bin[i].getValue());
        }
        System.out.println();
    }

    public static MultipleKnapsack random(int numItem, int numBins) {
        double[] itemWeights = new double[numItem];
        double[] binWeights = new double[numBins];
        for (int i = 0; i < numItem; ++i) {
            itemWeights[i] = rd.nextInt(50) + 10;
        }
        for (int i = 0; i < numBins; ++i) {
            binWeights[i] = rd.nextInt(300) + 210;
        }
        return new MultipleKnapsack(itemWeights, binWeights);
    }

    private int[] solution;

    private void backup() {
        solution = new int[bin.length];
        for (int i = 0; i < bin.length; ++i) {
            solution[i] = bin[i].getValue();
        }
    }

    private void restore() {
        localSearchManager.propagate(bin, solution);
    }

    public static void main(String[] args) {
//        MultipleKnapsack mk = random(750, 120);
        MultipleKnapsack mk = random(1000, 100);
//        MultipleKnapsack mk = random(1000, 160);
//        MultipleKnapsack mk = random(40, 10);
        mk.printInfo();
        mk.backup();

        System.out.println("Tabu search:");
        System.out.println("Start violation: " + mk.cs.getViolation());
        mk.solveByTabuSearch();
        System.out.println("Best violation: " + mk.cs.getViolation());
        mk.restore();

        System.out.println("LnsSolver");
        System.out.println("Start violation: " + mk.cs.getViolation());
        mk.solveByLnsSolver();
        System.out.println("Best violation: " + mk.cs.getViolation());
//        mk.solutionPrint();
    }
}
