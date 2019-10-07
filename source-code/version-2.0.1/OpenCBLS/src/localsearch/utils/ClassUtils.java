package localsearch.utils;

public class ClassUtils {

    public static String getClassName(Object o) {
        String s = o.getClass().getName();
        return s.substring(s.lastIndexOf(".") + 1);
    }
}
