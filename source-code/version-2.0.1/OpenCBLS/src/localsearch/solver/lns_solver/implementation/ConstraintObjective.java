package localsearch.solver.lns_solver.implementation;

import localsearch.model.ConstraintSystem;
import localsearch.model.variable.VarIntLS;
import localsearch.solver.lns_solver.IObjective;
import localsearch.utils.NumberUtils;

import java.util.HashMap;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class ConstraintObjective implements IObjective {

    private final HashMap<Object, Double> mapBestViolations;
    private final ConstraintSystem cs;

    public ConstraintObjective(ConstraintSystem cs) {
        this.cs = cs;
        mapBestViolations = new HashMap<>();
    }

    @Override
    public int isBetter(Object o, VarIntLS[] variables, int[] values) {
        double delta = cs.getAssignDelta(variables, values);
        int better;
        synchronized (this) {
            Double violation = mapBestViolations.computeIfAbsent(o, k -> cs.getViolation());
            double nextViolation = cs.getViolation() + delta;
            better = NumberUtils.compare(nextViolation, violation);
            if (better < 0) {
                mapBestViolations.put(o, nextViolation);
            }
        }
        return better;
    }

    @Override
    public int[] isBetter(Object[] os, VarIntLS[] variables, int[] values) {
        double delta = cs.getAssignDelta(variables, values);
        int better;
        int[] results = new int[os.length];
        synchronized (this) {
            for (int i = 0; i < os.length; ++i) {
                Double violation = mapBestViolations.computeIfAbsent(os[i], k -> cs.getViolation());
                double nextViolation = cs.getViolation() + delta;
                better = NumberUtils.compare(nextViolation, violation);
                if (better < 0) {
                    mapBestViolations.put(os[i], nextViolation);
                }
                results[i] = better;
            }
        }
        return results;
    }

    @Override
    public int isBetter(Object o1, Object o2) {
        double c1 = mapBestViolations.computeIfAbsent(o1, k -> cs.getViolation());
        double c2 = mapBestViolations.computeIfAbsent(o2, k -> cs.getViolation());
        return NumberUtils.compare(c1, c2);
    }

    @Override
    public boolean isAcceptSolution() {
        return NumberUtils.compare(cs.getViolation(), 0) == 0;
    }

    @Override
    public int isBetter(Object o) {
        Double violation = mapBestViolations.computeIfAbsent(o, k -> cs.getViolation());
        return NumberUtils.compare(cs.getViolation(), violation);
    }

    @Override
    public void update(Object o) {
        mapBestViolations.put(o, cs.getViolation());
    }

    @Override
    public void update(Object o, boolean worstValue) {
        if (!worstValue) {
            update(o);
        }
        mapBestViolations.put(o, Double.MAX_VALUE);
    }

    @Override
    public String currentValue() {
        return "current violation=" + cs.getViolation();
    }

    @Override
    public String value(Object o) {
        return "violation=" + mapBestViolations.computeIfAbsent(o, k -> cs.getViolation());
    }

    @Override
    public void remove(Object o) {
        mapBestViolations.remove(o);
    }

}
