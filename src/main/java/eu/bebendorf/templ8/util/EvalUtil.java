package eu.bebendorf.templ8.util;

public class EvalUtil {

    public static boolean isTrue(Object v) {
        if(v == null)
            return false;
        if(v instanceof Boolean)
            return (Boolean) v;
        return true;
    }

}
