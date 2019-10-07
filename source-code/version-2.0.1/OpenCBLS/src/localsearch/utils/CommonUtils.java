package localsearch.utils;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class CommonUtils {

    @SuppressWarnings("unchecked")
    public static <T> List<T[]> mapToList(Map<T, T> map) {
        ArrayList<T[]> list = new ArrayList<>();
        map.forEach((k, v) -> {
            T[] ts = (T[]) Array.newInstance(k.getClass(), 2);
            ts[0] = k;
            ts[1] = k;
            list.add(ts);
        });
        return list;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> sortingMapByValue(Map<K, V> map, Comparator<V> comparator) {
        ArrayList<Object[]> solutionsList = new ArrayList<>();
        map.forEach((k, v) -> solutionsList.add(new Object[]{k, v}));
        solutionsList.sort((o1, o2) -> comparator.compare((V) o1[1], (V) o2[1]));
        LinkedHashMap<K, V> mapResult = new LinkedHashMap<>();
        for (Object[] o : solutionsList) {
            mapResult.put((K) o[0], (V) o[1]);
        }
        return mapResult;
    }

}
