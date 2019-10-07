package localsearch.solver.lns_solver.implementation;

import localsearch.model.variable.VarIntLS;
import localsearch.solver.lns_solver.IDestroy;
import localsearch.solver.lns_solver.IObjective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class DestroyShuffle implements IDestroy {

    private final VarIntLS[] variables;
    private final int destroySize;
    private final IObjective objective;
    private final Random rd;
    private final ArrayList<Integer> idList;

    private int id;

    public DestroyShuffle(VarIntLS[] variables, int destroySize, IObjective objective, Random rd) {
        ArrayList<VarIntLS> varList = new ArrayList<>();
        for (VarIntLS var : variables) {
            if (var.getDomainSize() > 1) {
                varList.add(var);
            }
        }
        this.variables = varList.toArray(new VarIntLS[0]);
        this.destroySize = Math.min(destroySize, this.variables.length);
        this.objective = objective;
        this.rd = rd;
        idList = new ArrayList<>();
        for (int i = 0; i < this.variables.length; ++i) {
            idList.add(i);
        }
        reset();
    }

    @Override
    public VarIntLS[] destroy() {
        VarIntLS[] variables = new VarIntLS[destroySize];
        for (int i = id, j = 0; j < destroySize; ++i, ++j) {
            variables[j] = this.variables[idList.get(i % idList.size())];
        }
        id += destroySize;
        if (id >= this.variables.length) {
            reset();
        }
        return variables;
    }

    private void reset() {
        id = 0;
        Collections.shuffle(idList, rd);
    }

    @Override
    public boolean isAllDestroy() {
        return false;
    }
}
