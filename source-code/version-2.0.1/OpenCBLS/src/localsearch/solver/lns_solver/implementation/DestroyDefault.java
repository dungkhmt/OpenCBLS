package localsearch.solver.lns_solver.implementation;

import localsearch.model.variable.VarIntLS;
import localsearch.solver.lns_solver.IDestroy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class DestroyDefault implements IDestroy {

    protected final VarIntLS[] variables;
    protected final boolean[] destroyed;
    protected final int size;

    protected int numDestroyed;
    protected int id;

    protected Random rd;
    protected ArrayList<Integer> shuffleList;
    protected boolean sequence;

    public DestroyDefault(VarIntLS[] variables, int size, boolean sequence, Random rd) {
        if (size > variables.length) {
            throw new RuntimeException("Size must not be larger than numVariables.");
        }
        this.variables = variables;
        this.size = size;
        this.sequence = sequence;
        destroyed = new boolean[variables.length];
        numDestroyed = 0;
        id = 0;
        this.rd = rd;
        shuffleList = new ArrayList<>();
        for (int i = 0; i < variables.length; ++i) {
            shuffleList.add(i);
        }
    }

    @Override
    public VarIntLS[] destroy() {
        VarIntLS[] destroyVariables = new VarIntLS[size];
        if (this.rd != null) {
            if (sequence) {
                destroyVariables[0] = variables[id];
                addDestroy(id);
                Collections.shuffle(shuffleList, rd);
                int j = 0;
                for (int i = 1; i < size; ++i) {
                    if (shuffleList.get(j) == id) {
                        ++j;
                    }
                    destroyVariables[i] = variables[shuffleList.get(j)];
                    addDestroy(shuffleList.get(j));
                    ++j;
                }
                ++id;
                if (id >= variables.length) {
                    id = 0;
                }
            } else {
                Collections.shuffle(shuffleList, rd);
                for (int i = 0; i < size; ++i) {
                    destroyVariables[i] = variables[shuffleList.get(i)];
                    addDestroy(shuffleList.get(i));
                }
            }
        } else {
            for (int i = 0; i < size; ++i) {
                int j = (i + id) % variables.length;
                addDestroy(j);
                destroyVariables[i] = variables[j];
            }
            id += size;
            if (id >= variables.length) {
                id %= variables.length;
            }
        }
        return destroyVariables;
    }

    protected void addDestroy(int id) {
        if (!destroyed[id]) {
            ++numDestroyed;
            destroyed[id] = true;
        }
    }

    @Override
    public boolean isAllDestroy() {
        return numDestroyed == variables.length;
    }
}
