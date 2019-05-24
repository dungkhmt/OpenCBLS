package localsearch.solver.lns_solver;

import localsearch.model.variable.VarIntLS;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public interface IObjective {


    /**
     * Check if this assignment makes the goal better?
     *
     * @param o         instance that call this method
     * @param variables set of variables to assign values
     * @param values    set of values assigned to variables
     * @return -1 if better, 0 if not better and not worse, 1 if worse
     */
    public int isBetter(Object o, VarIntLS[] variables, int[] values);

    public default int isBetter(Object o, VarIntLS variable, int value) {
        return isBetter(o, new VarIntLS[]{variable}, new int[]{value});
    }

    public default int isBetter(Object o, VarIntLS x, VarIntLS y) {
        return isBetter(o, new VarIntLS[]{x, y}, new int[]{y.getValue(), x.getValue()});
    }

    public int[] isBetter(Object[] os, VarIntLS[] variables, int[] values);

    public default int[] isBetter(Object[] os, VarIntLS variable, int value) {
        return isBetter(os, new VarIntLS[]{variable}, new int[]{value});
    }

    public default int[] isBetter(Object[] os, VarIntLS x, VarIntLS y) {
        return isBetter(os, new VarIntLS[]{x, y}, new int[]{y.getValue(), x.getValue()});
    }

    /**
     * Check if the saved state with parameter o1 is better than the saved state with the o2 parameter.
     *
     * @param o1 first parameter
     * @param o2 second parameter
     * @return -1 if better, 0 if not better and not worse, 1 if worse
     */
    public int isBetter(Object o1, Object o2);

    /**
     * Check if the solution has been found
     *
     * @return true if the solution has been found and wants to stop searching,
     * false if the solution has not been found, or want to continue searching for another solution
     */
    public boolean isAcceptSolution();


    /**
     * Check if the current state is better than the state at the previous check of the parameter object
     *
     * @param o instance that call this method
     * @return -1 if better, 0 if not better and not worse, 1 if worse
     */
    public int isBetter(Object o);

    /**
     * Update for instance parameter
     *
     * @param o instance that call this method
     */
    public void update(Object o);

    public void update(Object o, boolean worstValue);

    /**
     * Create a string representing the current value of this instance
     *
     * @return constraint string and (/ or) the value string of the objective function
     */
    public String currentValue();

    public String value(Object o);

    public void remove(Object o);
}
