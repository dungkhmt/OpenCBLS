package localsearch.solver.lns_solver.implementation;

import localsearch.model.variable.VarIntLS;
import localsearch.solver.lns_solver.IObjective;
import localsearch.utils.NumberUtils;

import java.util.HashMap;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class MultiObjective implements IObjective {

    private final ObjectiveParameter[] parameters;

    private final HashMap<Object, Double>[] mapBestValues;

    /**
     * Put the invariants in the order of priority.
     */
    @SuppressWarnings("unchecked")
    public MultiObjective(ObjectiveParameter... parameters) {
        this.parameters = parameters;
        mapBestValues = new HashMap[parameters.length];
        for (int i = 0; i < mapBestValues.length; ++i) {
            mapBestValues[i] = new HashMap<>();
        }
    }

    @Override
    public int isBetter(Object o, VarIntLS[] variables, int[] values) {
        double[] currentValues = new double[parameters.length];
        double[] nextValues = new double[parameters.length];
        for (int i = 0; i < parameters.length; ++i) {
            currentValues[i] = parameters[i].getInvariantValue();
            nextValues[i] = currentValues[i] + parameters[i].getInvariant().getAssignDelta(variables, values);
        }
        int better;
        synchronized (this) {
            for (int i = 0; i < mapBestValues.length; ++i) {
                double currentValue = currentValues[i];
                Double value = mapBestValues[i].computeIfAbsent(o, k -> currentValue);
                better = NumberUtils.compare(nextValues[i], value);
                if (better != 0) {
                    if (parameters[i].isMaximize()) {
                        better = -better;
                    }
                    if (better < 0) {
                        for (int j = i; j < mapBestValues.length; ++j) {
                            mapBestValues[j].put(o, nextValues[j]);
                        }
                    }
                    return better;
                }
            }
        }
        return 0;
    }

    public int[] isBetter(Object[] os, VarIntLS[] variables, int[] values) {
        double[] currentValues = new double[parameters.length];
        double[] nextValues = new double[parameters.length];
        for (int i = 0; i < parameters.length; ++i) {
            currentValues[i] = parameters[i].getInvariantValue();
            nextValues[i] = currentValues[i] + parameters[i].getInvariant().getAssignDelta(variables, values);
        }
        int better;
        int[] results = new int[os.length];
        synchronized (this) {
            for (int i = 0; i < os.length; ++i) {
                for (int j = 0; j < mapBestValues.length; ++j) {
                    double currentValue = currentValues[j];
                    Double value = mapBestValues[j].computeIfAbsent(os[i], k -> currentValue);
                    better = NumberUtils.compare(nextValues[j], value);
                    if (better != 0) {
                        if (parameters[j].isMaximize()) {
                            better = -better;
                        }
                        if (better < 0) {
                            for (int k = j; k < mapBestValues.length; ++k) {
                                mapBestValues[k].put(os[i], nextValues[k]);
                            }
                        }
                        results[i] = better;
                    }
                }
                results[i] = 0;
            }
        }
        return results;
    }

    @Override
    public int isBetter(Object o1, Object o2) {
        for (int i = 0; i < mapBestValues.length; ++i) {
            ObjectiveParameter parameter = parameters[i];
            double value1 = mapBestValues[i].computeIfAbsent(o1, k -> parameter.getInvariantValue());
            double value2 = mapBestValues[i].computeIfAbsent(o2, k -> parameter.getInvariantValue());
            int better = NumberUtils.compare(value1, value2);
            if (better != 0) {
                return better;
            }
        }
        return 0;
    }

    @Override
    public boolean isAcceptSolution() {
        for (ObjectiveParameter parameter : parameters) {
            double currentValue = parameter.getInvariantValue();
            int compareResult = NumberUtils.compare(currentValue, parameter.getBestValue());
            if (parameter.isMaximize()) {
                if (compareResult < 0) {
                    return false;
                }
            } else {
                if (compareResult > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int isBetter(Object o) {
        for (int i = 0; i < mapBestValues.length; ++i) {
            double currentValue = parameters[i].getInvariantValue();
            Double violation = mapBestValues[i].computeIfAbsent(o, k -> currentValue);
            int better = NumberUtils.compare(currentValue, violation);
            if (better != 0) {
                if (parameters[i].isMaximize()) {
                    better = -better;
                }
                return better;
            }
        }
        return 0;
    }

    @Override
    public void update(Object o) {
        for (int i = 0; i < mapBestValues.length; ++i) {
            mapBestValues[i].put(o, parameters[i].getInvariantValue());
        }
    }

    @Override
    public void update(Object o, boolean worstValue) {
        if (!worstValue) {
            update(o);
        }
        for (int i = 0; i < mapBestValues.length; ++i) {
            mapBestValues[i].put(o, parameters[i].isMaximize() ? Double.MIN_VALUE : Double.MAX_VALUE);
        }
    }

    @Override
    public String currentValue() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parameters.length; ++i) {
            if (parameters[i].getName() == null) {
                sb.append("invariant[").append(i).append("] = ");
            } else {
                sb.append(parameters[i].getName()).append(" = ");
            }
            sb.append(parameters[i].getInvariantValue()).append(", ");
        }
        return sb.toString();
    }

    @Override
    public String value(Object o) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parameters.length; ++i) {
            ObjectiveParameter parameter = parameters[i];
            if (parameter.getName() == null) {
                sb.append("invariant[").append(i).append("] = ");
            } else {
                sb.append(parameter.getName()).append(" = ");
            }
            sb.append(mapBestValues[i].computeIfAbsent(o, k -> parameter.getInvariantValue())).append(", ");
        }
        return sb.toString();
    }

    @Override
    public void remove(Object o) {
        for (HashMap<Object, Double> map : mapBestValues) {
            map.remove(o);
        }
    }
}
