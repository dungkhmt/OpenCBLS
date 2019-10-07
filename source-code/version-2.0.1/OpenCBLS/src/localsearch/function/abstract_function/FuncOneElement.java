package localsearch.function.abstract_function;

import localsearch.model.IFunction;
import localsearch.model.Invariant;
import localsearch.model.variable.VarIntLS;

import java.util.Collections;
import java.util.HashSet;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public abstract class FuncOneElement extends FuncDependFunc {

    protected IFunction function;
    protected HashSet<VarIntLS> variableSet;
    private final int level;

    public FuncOneElement(IFunction function) {
        this.function = function;
        localSearchManager = function.getLocalSearchManager();
        variables = function.getVariables();
        variableSet = new HashSet<>(function.getVariables().length);
        Collections.addAll(variableSet, function.getVariables());
        level = function.getLevel() + 1;
        localSearchManager.post(this);
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Invariant[] getDependencyInvariants() {
        return new IFunction[]{function};
    }
}
