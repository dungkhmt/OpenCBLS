package localsearch.solver.model;

import localsearch.model.variable.VarIntLS;

import java.util.Arrays;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class Assign {

    private final VarIntLS[] variables;
    private final int[] values;

    public Assign(VarIntLS[] variables, int[] values) {
        this.variables = new VarIntLS[variables.length];
        System.arraycopy(variables, 0, this.variables, 0, variables.length);
        this.values = new int[values.length];
        System.arraycopy(values, 0, this.values, 0, values.length);
    }

    public VarIntLS[] getVariables() {
        return variables;
    }

    public int[] getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assign)) return false;
        Assign assign = (Assign) o;
        return Arrays.equals(variables, assign.variables) &&
                Arrays.equals(values, assign.values);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(variables);
        result = 31 * result + Arrays.hashCode(values);
        return result;
    }
}
