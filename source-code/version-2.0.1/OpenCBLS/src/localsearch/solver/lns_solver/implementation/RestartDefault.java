package localsearch.solver.lns_solver.implementation;

import localsearch.solver.lns_solver.IObjective;
import localsearch.solver.lns_solver.IRestart;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public abstract class RestartDefault implements IRestart {

    protected final IObjective objective;
    private final int maxIterNotBetter;

    protected int iter;

    public RestartDefault(IObjective objective, int maxIterNotBetter) {
        this.objective = objective;
        objective.update(this);
        this.maxIterNotBetter = maxIterNotBetter;
        iter = 0;
    }

    @Override
    public boolean isRestart() {
        int better = objective.isBetter(this);
        if (better >= 0) {
            ++iter;
            if (iter >= maxIterNotBetter) {
                iter = 0;
                return true;
            }
        } else {
            iter = 0;
            objective.update(this);
        }
        return false;
    }


}
