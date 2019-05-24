package localsearch.solver.lns_solver;

import localsearch.model.variable.VarIntLS;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public interface IDestroy {

    public VarIntLS[] destroy();

    public boolean isAllDestroy();
}
