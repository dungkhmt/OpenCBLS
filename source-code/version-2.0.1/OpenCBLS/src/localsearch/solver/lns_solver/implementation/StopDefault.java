package localsearch.solver.lns_solver.implementation;

import localsearch.solver.lns_solver.IObjective;
import localsearch.solver.lns_solver.IStop;
import localsearch.utils.NumberUtils;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class StopDefault implements IStop {

    private int iter;
    private int iterNotBetter;
    private int maxIterNotBetter;
    private int maxIter;
    private double maxTime; // second
    private IObjective objective;

    private long startTime = -1L;

    private StopDefault(Builder builder) {
        maxIterNotBetter = builder.maxIterNotBetter;
        maxIter = builder.maxIter;
        maxTime = builder.maxTime;
        objective = builder.objective;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public boolean isStop() {
        ++iter;
        if (iter >= maxIter) {
            return true;
        }
        if (startTime == -1L) {
            startTime = System.nanoTime();
        } else {
            long now = System.nanoTime();
            if (NumberUtils.compare((double) (now - startTime) / 1e9, maxTime) > 0) {
                return true;
            }
        }
        if (objective != null) {
            if (objective.isAcceptSolution()) {
                return true;
            }
            if (objective.isBetter(this) < 0) {
                iterNotBetter = 0;
                objective.update(this);
            } else {
                ++iterNotBetter;
                return iterNotBetter >= maxIterNotBetter;
            }
        }
        return false;
    }

    @Override
    public int getIter() {
        return iter;
    }

    public static final class Builder {
        private int maxIterNotBetter = Integer.MAX_VALUE;
        private int maxIter = Integer.MAX_VALUE;
        private double maxTime = Double.MAX_VALUE;
        private IObjective objective;

        public Builder() {
        }

        public Builder maxIterNotBetter(int val) {
            maxIterNotBetter = val;
            return this;
        }

        public Builder maxIter(int val) {
            maxIter = val;
            return this;
        }

        public Builder maxTime(double val) {
            maxTime = val;
            return this;
        }

        public Builder objective(IObjective val) {
            objective = val;
            return this;
        }

        public StopDefault build() {
            return new StopDefault(this);
        }
    }
}
