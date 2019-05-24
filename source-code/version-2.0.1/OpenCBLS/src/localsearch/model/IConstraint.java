package localsearch.model;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public interface IConstraint extends Invariant {

    public double getViolation();
}
