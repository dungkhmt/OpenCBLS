package localsearch.constraint.abstract_constraint;

import localsearch.model.IFunction;
import localsearch.model.Invariant;
import localsearch.model.variable.VarIntLS;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public abstract class ConstraintManyFunction extends Constraint {

    protected IFunction[] functions;
    protected HashMap<VarIntLS, HashSet<IFunction>> mapVarToFunctions;
    private final int level;

    public ConstraintManyFunction(IFunction... functions) {
        this.functions = functions;
        localSearchManager = functions[0].getLocalSearchManager();
        mapVarToFunctions = new HashMap<>();
        int maxLevel = 0;
        for (IFunction function : functions) {
            for (VarIntLS variable : function.getVariables()) {
                mapVarToFunctions.computeIfAbsent(variable, k -> new HashSet<>(Math.min(16, functions.length))).add(function);
            }
            if (function.getLevel() > maxLevel) {
                maxLevel = function.getLevel();
            }
        }
        variables = mapVarToFunctions.keySet().toArray(new VarIntLS[0]);
        level = maxLevel + 1;
        localSearchManager.post(this);
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Invariant[] getDependencyInvariants() {
        return functions;
    }
}
