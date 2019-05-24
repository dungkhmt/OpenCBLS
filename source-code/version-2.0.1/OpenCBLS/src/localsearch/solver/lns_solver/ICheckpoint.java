package localsearch.solver.lns_solver;

import localsearch.model.LocalSearchManager;
import localsearch.model.variable.VarIntLS;

import java.io.File;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public interface ICheckpoint {

    public String solutionJson();

    public VarIntLS[] getVariables();

    public LocalSearchManager getLocalSearchManager();

    public void writeSolutionToFile(File file);

    public void refresh(IObjective objective);
}
