package localsearch.solver.model;

import localsearch.model.variable.VarIntLS;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class Move {
    private final VarIntLS variable;
    private final int value;

    public Move(VarIntLS variable, int value) {
        this.variable = variable;
        this.value = value;
    }

    public VarIntLS getVariable() {
        return variable;
    }

    public int getValue() {
        return value;
    }
}