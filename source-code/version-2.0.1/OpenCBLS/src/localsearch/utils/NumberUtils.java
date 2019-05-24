package localsearch.utils;

import java.util.Comparator;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class NumberUtils {

    public final static Comparator<Double> REAL_COMPARATOR = NumberUtils::compare;

    public final static Comparator<Double> REAL_COMPARATOR_REVERSE = (o1, o2) -> compare(o2, o1);

    public final static Comparator<Integer> INT_COMPARATOR = Integer::compare;

    public static int compare(double d1, double d2) {
        double epsilon = 1e-10;
        if (d1 < d2) {
            if (d1 + epsilon >= d2) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (d1 - epsilon <= d2) {
                return 0;
            } else {
                return 1;
            }
        }
    }

}
