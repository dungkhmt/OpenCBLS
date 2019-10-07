package localsearch.constraint;

import localsearch.constraint.abstract_constraint.Constraint;
import localsearch.model.ConstraintSystem;
import localsearch.model.Invariant;
import localsearch.model.LocalSearchManager;
import localsearch.model.variable.VarIntLS;
import localsearch.solver.lns_solver.LnsSolver;
import localsearch.solver.lns_solver.implementation.ConstraintObjective;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class MultipleKnapsack extends Constraint {

    private final int[] itemWeight;
    private final int[] binSize;

    private final int[] binCapacity;

    private final HashMap<VarIntLS, Integer> mapVarToIndex;

    public MultipleKnapsack(VarIntLS[] variables, int[] itemWeight, int[] binSize) {
        this.variables = variables;
        this.itemWeight = itemWeight;
        this.binSize = binSize;
        localSearchManager = variables[0].getLocalSearchManager();
        mapVarToIndex = new HashMap<>(variables.length);
        for (int i = 0; i < variables.length; ++i) {
            mapVarToIndex.put(variables[i], i);
        }
        binCapacity = new int[binSize.length];
        localSearchManager.post(this);
    }

    @Override
    public Invariant[] getDependencyInvariants() {
        return new Invariant[0];
    }

    @Override
    public double getAssignDelta(VarIntLS[] variables, int[] values) {
        TreeMap<Integer, Integer> mapChange = new TreeMap<>();
        int delta = 0;
        for (int i = 0; i < variables.length; ++i) {
            VarIntLS variable = variables[i];
            Integer id = mapVarToIndex.get(variable);
            if (id == null) {
                continue;
            }
            int oldValue = variable.getValue();
            int newValue = values[i];
            if (oldValue == newValue) {
                continue;
            }
            delta = getDelta(delta, id, oldValue, newValue, mapChange);
        }
        return delta;
    }

    private int getDelta(int delta, Integer id, int oldValue, int newValue, TreeMap<Integer, Integer> mapChange) {
        int oldBinViolation;
        int newBinViolation;
        Integer binContainOld = mapChange.get(oldValue);
        Integer binContainNew = mapChange.get(newValue);
        if (binContainOld == null) {
            binContainOld = binCapacity[oldValue];
        }
        if (binContainNew == null) {
            binContainNew = binCapacity[newValue];
        }

        oldBinViolation = binContainOld - binSize[oldValue];
        if (oldBinViolation < 0) {
            oldBinViolation = 0;
        }
        newBinViolation = binContainNew - binSize[newValue];
        if (newBinViolation < 0) {
            newBinViolation = 0;
        }
        delta -= (oldBinViolation + newBinViolation);
        binContainOld -= itemWeight[id];
        binContainNew += itemWeight[id];
        mapChange.put(oldValue, binContainOld);
        mapChange.put(newValue, binContainNew);
        oldBinViolation = binContainOld - binSize[oldValue];
        if (oldBinViolation < 0) {
            oldBinViolation = 0;
        }
        newBinViolation = binContainNew - binSize[newValue];
        if (newBinViolation < 0) {
            newBinViolation = 0;
        }
        delta += (oldBinViolation + newBinViolation);
        return delta;
    }

    private void updateViolation(Integer id, int oldValue, int newValue) {
        int oldBinViolation;
        int newBinViolation;

        oldBinViolation = binCapacity[oldValue] - binSize[oldValue];
        if (oldBinViolation < 0) {
            oldBinViolation = 0;
        }
        newBinViolation = binCapacity[newValue] - binSize[newValue];
        if (newBinViolation < 0) {
            newBinViolation = 0;
        }
        violation -= (oldBinViolation + newBinViolation);
        binCapacity[oldValue] -= itemWeight[id];
        binCapacity[newValue] += itemWeight[id];
        oldBinViolation = binCapacity[oldValue] - binSize[oldValue];
        if (oldBinViolation < 0) {
            oldBinViolation = 0;
        }
        newBinViolation = binCapacity[newValue] - binSize[newValue];
        if (newBinViolation < 0) {
            newBinViolation = 0;
        }
        violation += (oldBinViolation + newBinViolation);
    }

    @Override
    public void propagate(Set<VarIntLS> variables) {
        for (VarIntLS variable : variables) {
            Integer id = mapVarToIndex.get(variable);
            if (id == null) {
                continue;
            }
            int oldValue = variable.getOldValue();
            int newValue = variable.getValue();
            updateViolation(id, oldValue, newValue);
        }
    }

    @Override
    public void initPropagate() {
        for (int i = 0; i < variables.length; ++i) {
            binCapacity[variables[i].getValue()] += itemWeight[i];
        }
        violation = 0;
        for (int i = 0; i < binCapacity.length; ++i) {
            int v = binCapacity[i] - binSize[i];
            v = v > 0 ? v : 0;
            violation += v;
        }
    }

    @Override
    public int getLevel() {
        return 1;
    }

    public static void main(String[] args) {
        LocalSearchManager localSearchManager = new LocalSearchManager();
        int[] itemWeight = {25, 38, 47, 43, 54, 14, 24, 56, 38, 38, 89, 53, 47, 73, 82, 44, 12, 22, 16, 69, 66, 72, 50, 99, 24, 99, 98, 63, 67, 72, 35, 14, 40, 86, 33, 25, 19, 70, 55, 54, 65, 60, 17, 97, 37, 22, 40, 58, 20, 87, 92, 43, 36, 85, 76, 65, 48, 89, 84, 24, 10, 71, 26, 43, 98, 76, 48, 74, 15, 48, 29, 86, 65, 68, 82, 37, 52, 41, 91, 27, 48, 82, 81, 58, 85, 39, 39, 75, 59, 10, 37, 16, 84, 20, 63, 45, 71, 11, 36, 70, 34, 92, 64, 65, 94, 34, 28, 13, 43, 47, 77, 20, 21, 63, 67, 56, 57, 15, 80, 89, 98, 33, 30, 86, 16, 86, 91, 59, 54, 69, 48, 61, 30, 35, 25, 56, 84, 61, 46, 56, 68, 78, 61, 32, 33, 20, 59, 65, 32, 23, 54, 62, 93, 57, 31, 85, 77, 84, 27, 17, 69, 86, 37, 89, 63, 16, 31, 76, 18, 15, 77, 68, 92, 95, 46, 30, 50, 79, 45, 88, 40, 30, 45, 16, 38, 57, 68, 79, 52, 79, 41, 90, 28, 63, 75, 52, 71, 45, 39, 86, 16, 17, 73, 58, 32, 47, 32, 67, 48, 75, 74, 31, 46, 49, 38, 78, 63, 63, 44, 10, 84, 19, 79, 12, 20, 28, 20, 26, 11, 38, 86, 75, 80, 30, 86, 59, 69, 38, 94, 76, 61, 69, 38, 49, 47, 48, 18, 72, 76, 97, 89, 41, 39, 98, 18, 44, 91, 92, 69, 45, 25, 74, 66, 20, 61, 65, 78, 97, 16, 79, 83, 12, 30, 31, 23, 10, 75, 93, 42, 19, 16, 53, 88, 81, 88, 93, 15, 39, 46, 68, 81, 58, 14, 38, 66, 61, 26, 59, 43, 21, 24, 80, 15, 24, 97, 75, 51, 60, 84, 43, 75, 64, 29, 93, 73, 68, 46, 46, 33, 96, 36, 28, 98, 21, 43, 93, 39, 69, 79, 89, 52, 77, 83, 89, 79, 99, 46, 62, 40, 74, 15, 57, 63, 98, 90, 69, 76, 24, 18, 52, 73, 57, 73, 20, 99, 99, 35, 87, 69, 54, 85, 63, 47, 11, 32, 42, 68, 35, 75, 49, 69, 70, 66, 35, 52, 92, 65, 37, 57, 21, 91, 86, 93, 64, 99, 66, 39, 18, 87, 78, 77, 93, 94, 37, 94, 79, 50, 59, 54, 23, 14, 46, 53, 83, 39, 20, 31, 53, 88, 34, 38, 84, 97, 40, 95, 90, 43, 49, 11, 43, 71, 79, 34, 48, 93, 61, 10, 44, 46, 94, 92, 23, 78, 49, 54, 74, 55, 65, 35, 48, 63, 24, 28, 63, 23, 97, 98, 48, 19, 57, 53, 16, 56, 25, 70, 20, 14, 57, 57, 14, 31, 22, 61, 84, 37, 50, 82, 95, 94, 56, 29, 40, 45, 31, 40, 47, 46, 31, 79, 40, 53, 38, 83, 11, 75, 34, 14, 95, 50, 43, 38, 28, 11, 34, 38, 14, 63, 82, 97, 65, 69, 27, 74, 28, 75, 62, 66, 52, 27, 91, 81, 70, 37, 48, 95, 34, 84, 96, 67, 36, 10, 89, 19, 58, 47, 51, 29, 50, 66, 50, 59, 69, 34, 43, 66, 19, 29, 85, 19, 23, 65, 80, 83, 63, 55, 99, 62, 53, 66, 51, 39, 39, 20, 61, 98, 39, 76, 53, 77, 25, 38, 62, 17, 79, 96, 97, 99, 52, 25, 14, 66, 76, 50, 38, 71, 58, 93, 17, 18, 66, 25, 13, 69, 51, 64, 24, 17, 29, 71, 90, 86, 38, 90, 16, 67, 93, 50, 53, 36, 93, 11, 71, 98, 17, 45, 85, 74, 34, 51, 85, 47, 63, 64, 70, 52, 91, 23, 75, 24, 28, 43, 36, 75, 10, 52, 84, 56, 52, 76, 48, 97, 25, 71, 70, 69, 58, 49, 88, 23, 67, 52, 99, 48, 12, 63, 69, 69, 62, 11, 15, 20, 80, 44, 88, 97, 40, 16, 73, 88, 57, 11, 36, 44, 34, 96, 53, 69, 77, 65, 70, 30, 26, 34, 11, 47, 19, 38, 33, 84, 97, 85, 47, 98, 95, 89, 83, 46, 27, 26, 30, 23, 50, 23, 89, 13, 71, 66, 39, 68, 96, 36, 25, 54, 77, 75, 88, 86, 76, 24, 19, 90, 49, 47, 48, 60, 43, 93, 20, 76, 30, 14, 45, 76, 65, 10, 15, 85, 52, 69, 20, 91, 53, 38, 60, 58, 59, 34, 58, 80, 91, 33, 29, 32, 65, 63, 42, 36, 86, 63, 17};
        int[] binSize = {435, 292, 276, 255, 227, 432, 430, 484, 313, 244, 429, 212, 254, 492, 266, 449, 308, 270, 299, 373, 269, 293, 434, 261, 406, 216, 491, 450, 238, 418, 326, 488, 473, 200, 305, 292, 304, 438, 233, 408, 438, 227, 361, 219, 305, 499, 319, 263, 431, 459, 352, 357, 329, 444, 223, 207, 303, 349, 214, 270, 414, 319, 384, 349, 318, 395, 359, 370, 347, 383, 450, 470, 404, 319, 459, 264, 391, 258, 356, 339, 292, 233, 331, 402, 249, 251, 329, 222, 371, 432, 339, 382, 331, 461, 280, 450, 452, 364, 416, 290, 420, 402, 341, 295, 246, 273, 482, 212, 472, 285, 236, 403, 255, 367, 376, 446, 305, 272, 263, 221};

        long seed = 1L;
        Random rd = new Random(seed);
//        int numItem = 3000;
//        int numBins = 1800;
//
//        int[] itemWeight = new int[numItem];
//        int[] binSize = new int[numBins];
//
//        int sumItemWeights = 0;
//        int sumBinWeights = 0;
//
//        for (int i = 0; i < numItem; ++i) {
//            itemWeight[i] = rd.nextInt(50) + 5;
//            sumItemWeights += itemWeight[i];
//        }
//        for (int i = 0; i < numBins; ++i) {
//            binSize[i] = rd.nextInt(100) + 100;
//            sumBinWeights += binSize[i];
//        }

//        System.out.println("Sum item weights: " + sumItemWeights);
//        System.out.println("Sum bin weights: " + sumBinWeights);

        VarIntLS[] variables = new VarIntLS[itemWeight.length];
        for (int i = 0; i < variables.length; ++i) {
            variables[i] = new VarIntLS(0, binSize.length - 1, localSearchManager, rd);
        }
        ConstraintSystem cs = new ConstraintSystem();
        cs.post(new MultipleKnapsack(variables, itemWeight, binSize));
        cs.close();
        localSearchManager.close();

//        TabuSearch.getInstance().search(cs, 10, 100, 3000, rd);
        LnsSolver solver = LnsSolver.newDefaultSolver(localSearchManager, 3, 3000, 500, 50, rd, new ConstraintObjective(cs), "LnsSolver(3)");
        solver.solve();
    }
}
