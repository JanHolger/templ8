package eu.bebendorf.templ8.eval;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class ReflectionFunction implements Function {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPE_MAP = new HashMap<Class<?>, Class<?>>() {{
        put(int.class, Integer.class);
        put(long.class, Long.class);
        put(double.class, Double.class);
        put(float.class, Float.class);
        put(byte.class, Byte.class);
        put(short.class, Short.class);
        put(char.class, Character.class);
        put(boolean.class, Boolean.class);
    }};

    Object instance;
    List<Method> methods;

    public Object execute(Object[] args) {
        Method m = findMethod(args);
        if(m == null)
            return null;
        try {
            return m.invoke(instance, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Method findMethod(Object[] args) {
        Method method = null;
        outer:
        for(Method m : methods) {
            Class<?>[] types = m.getParameterTypes();
            if(types.length != args.length)
                continue;
            for(int i=0; i<types.length; i++) {
                if(!assignable(types[i], args[i] == null ? null : args[i].getClass()))
                    continue outer;
            }
            if(method != null)
                return null;
            method = m;
        }
        return method;
    }

    private static boolean assignable(Class<?> target, Class<?> value) {
        if(value == null)
            return !target.isPrimitive();
        target = PRIMITIVE_TYPE_MAP.getOrDefault(target, target);
        return target.isAssignableFrom(value);
    }

}
