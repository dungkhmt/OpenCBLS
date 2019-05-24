package localsearch.solver.lns_solver.implementation;

import localsearch.solver.lns_solver.IRestart;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class RestartNone implements IRestart {

    @Override
    public boolean isRestart() {
        return false;
    }

    @Override
    public void restart() {
    }
}
