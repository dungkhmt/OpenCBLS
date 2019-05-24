package localsearch.utils;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class CountingUtils {

    public static <T> T getFirst(Map<T, Integer> mapOrigin, Map<T, Integer> mapChanging, Comparator<T> comparator) {
        Iterator<T> iterOrigin = mapOrigin.keySet().iterator();
        Iterator<T> iterChanging = mapChanging.keySet().iterator();
        while (true) {
            if (iterOrigin.hasNext()) {
                if (iterChanging.hasNext()) {
                    T keyOrigin = iterOrigin.next();
                    T keyChanging = iterChanging.next();
                    int compareResult = comparator.compare(keyOrigin, keyChanging);
                    if (compareResult < 0) {
                        return keyOrigin;
                    } else if (compareResult > 0) {
                        return keyChanging;
                    }
                    Integer valueOrigin = mapOrigin.get(keyOrigin);
                    Integer valueChanging = mapChanging.get(keyChanging);
                    if (valueOrigin + valueChanging > 0) {
                        return keyOrigin;
                    }
                } else {
                    return iterOrigin.next();
                }
            } else {
                throw new RuntimeException("mapOrigin or mapChanging is invalid.");
            }
        }
    }

    public static <T> int getSize(Map<T, Integer> mapOrigin, Map<T, Integer> mapChanging) {
        int size = mapOrigin.size();
        for (Map.Entry<T, Integer> e : mapChanging.entrySet()) {
            Integer count = mapOrigin.get(e.getKey());
            if (count == null) {
                ++size;
            } else if (count + e.getValue() == 0) {
                --size;
            }
        }
        return size;
    }

    public static <T> Integer remove(T oldValue, Map<T, Integer> map) {
        Integer count = map.get(oldValue);
        if (count == null) {
            map.put(oldValue, -1);
            return -1;
        } else if (count == 1) {
            map.remove(oldValue);
            return 0;
        } else {
            map.put(oldValue, count - 1);
            return count - 1;
        }
    }

    public static <T> Integer add(T newValue, Map<T, Integer> map) {
        Integer count = map.get(newValue);
        if (count == null) {
            map.put(newValue, 1);
            return 1;
        } else if (count == -1) {
            map.remove(newValue);
            return 0;
        } else {
            map.put(newValue, count + 1);
            return count + 1;
        }
    }

    public static <T> void update(T oldValue, T newValue, Map<T, Integer> map) {
        remove(oldValue, map);
        add(newValue, map);
    }
}
