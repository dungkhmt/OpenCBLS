package localsearch.function.abstract_function;

import localsearch.model.IFunction;
import localsearch.model.Invariant;
import localsearch.model.variable.VarIntLS;

import java.util.Collections;
import java.util.HashSet;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public abstract class FuncTwoElement extends FuncDependFunc {

    protected IFunction f1;
    protected IFunction f2;
    protected HashSet<VarIntLS> variableSet;
    private final int level;

    public FuncTwoElement(IFunction f1, IFunction f2) {
        this.f1 = f1;
        this.f2 = f2;
        localSearchManager = f1.getLocalSearchManager();
        variableSet = new HashSet<>(f1.getVariables().length + f2.getVariables().length);
        Collections.addAll(variableSet, f1.getVariables());
        Collections.addAll(variableSet, f2.getVariables());
        variables = variableSet.toArray(new VarIntLS[0]);
        if (f1.getLevel() > f2.getLevel()) {
            level = f1.getLevel() + 1;
        } else {
            level = f2.getLevel() + 1;
        }
        localSearchManager.post(this);
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Invariant[] getDependencyInvariants() {
        return new IFunction[]{f1, f2};
    }
}
