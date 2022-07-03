package eu.bebendorf.templ8.eval;

import eu.bebendorf.purejavaparser.ast.Variable;
import eu.bebendorf.purejavaparser.ast.expression.*;
import eu.bebendorf.templ8.eval.operation.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExpressionEvaluator {

    private static final Map<Class<?>, NumericOperation> NUMERIC_OPERATIONS = new HashMap<Class<?>, NumericOperation>() {{
        put(Addition.class, new AddOperation());
        put(Subtraction.class, new SubtractOperation());
        put(Multiplication.class, new MultiplyOperation());
        put(Division.class, new DivideOperation());
        put(Modulo.class, new ModuloOperation());
        put(LessThan.class, new LessThanOperation());
        put(GreaterThan.class, new GreaterThanOperation());
        put(LessThanOrEqual.class, new LessThanOrEqualOperation());
        put(GreaterThanOrEqual.class, new GreaterThanOrEqualOperation());
    }};

    private static final Map<Class<?>, BooleanOperation> BOOLEAN_OPERATIONS = new HashMap<Class<?>, BooleanOperation>() {{
        put(LogicalOr.class, new LogicalOrOperation());
        put(LogicalAnd.class, new LogicalAndOperation());
    }};

    public static Object evaluate(Scope scope, Expression expression, boolean expectMethod) throws Exception {
        if(expression instanceof ExpressionGroup)
            return evaluate(scope, ((ExpressionGroup) expression).getExpression(), expectMethod);
        if(expression instanceof NullLiteral)
            return null;
        if(expression instanceof BooleanLiteral)
            return ((BooleanLiteral) expression).isValue();
        if(expression instanceof DoubleLiteral)
            return ((DoubleLiteral) expression).getValue();
        if(expression instanceof FloatLiteral)
            return ((FloatLiteral) expression).getValue();
        if(expression instanceof IntegerLiteral)
            return ((IntegerLiteral) expression).getValue();
        if(expression instanceof LongLiteral)
            return ((LongLiteral) expression).getValue();
        if(expression instanceof StringLiteral)
            return ((StringLiteral) expression).getValue();
        if(expression instanceof CharLiteral)
            return ((CharLiteral) expression).getValue();
        if(expression instanceof ClassLiteral)
            return Class.forName(String.join(".", ((ClassLiteral) expression).getName()));
        if(expression instanceof Variable) {
            String name = ((Variable) expression).getName();
            if(expectMethod)
                return scope.function(name);
            if(scope.has(name))
                return scope.get(name);
            Class<?> clazz = scope.getImport(name);
            if(clazz != null)
                return new ClassAccess(clazz);
            try {
                clazz = Class.forName(name);
                return new ClassAccess(clazz);
            } catch (ClassNotFoundException ignored) {}
            Package p = Package.getPackage(name);
            if(p != null)
                return new PackageAccess(name);
            return null;
        }
        if(expression instanceof Equals)
            return ComparisonEvaluator.evaluate(scope, (Equals) expression);
        if(expression instanceof NotEqual)
            return ComparisonEvaluator.evaluate(scope, (NotEqual) expression);
        if(expression instanceof Cast) {
            Class<?> t = Class.forName(((Cast) expression).getType().toString());
            Object v = evaluate(scope, ((Cast) expression).getValue(), false);
            return t.cast(v);
        }
        if(expression instanceof Ternary) {
            Object c = evaluate(scope, ((Ternary) expression).getCondition(), false);
            if(!(c instanceof Boolean))
                c = c != null;
            if((Boolean) c) {
                return evaluate(scope, ((Ternary) expression).getValue(), false);
            } else {
                return evaluate(scope, ((Ternary) expression).getElseValue(), false);
            }
        }
        if(expression instanceof Not) {
            Object value = evaluate(scope, ((Not) expression).getInner(), false);
            if(!(value instanceof Boolean))
                value = value != null;
            return !((Boolean) value);
        }
        if(expression instanceof ArrayAccess) {
            ArrayAccess arrayAccess = (ArrayAccess) expression;
            Object array = evaluate(scope, arrayAccess.getArray(), false);
            if(array == null || !array.getClass().isArray())
                return null;
            Object indexObj = evaluate(scope, arrayAccess.getIndex(), false);
            if(!(indexObj instanceof Number))
                return null;
            int index = ((Number) indexObj).intValue();
            return Array.get(array, index);
        }
        if(expression instanceof PropertyAccess) {
            PropertyAccess propertyAccess = (PropertyAccess) expression;
            Object obj = evaluate(scope, propertyAccess.getExpression(), false);
            if(obj == null)
                return null;
            if(obj instanceof PackageAccess) {
                PackageAccess packageAccess = (PackageAccess) obj;
                if(expectMethod)
                    return null;
                try {
                    Class<?> clazz = Class.forName(packageAccess.getName() + "." + propertyAccess.getProperty());
                    return new ClassAccess(clazz);
                } catch (ClassNotFoundException ignored) {}
                Package p = Package.getPackage(packageAccess.getName() + "." + propertyAccess.getProperty());
                if(p != null)
                    return new PackageAccess(p.getName());
                return null;
            } else if(obj instanceof ClassAccess) {
                ClassAccess classAccess = (ClassAccess) obj;
                if(expectMethod) {
                    List<Method> methods = Stream.of(classAccess.getClazz().getDeclaredMethods()).filter(m -> Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers()) && m.getName().equals(propertyAccess.getProperty())).collect(Collectors.toList());
                    if(methods.size() > 0)
                        return new ReflectionFunction(null, methods);
                    return null;
                } else {
                    Field field = Stream.of(classAccess.getClazz().getDeclaredFields()).filter(f -> Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers()) && f.getName().equals(propertyAccess.getProperty())).findFirst().orElse(null);
                    if(field != null)
                        return field.get(null);
                    try {
                        Class<?> clazz = Class.forName(classAccess.getClazz().getName() + "." + propertyAccess.getProperty());
                        return new ClassAccess(clazz);
                    } catch (ClassNotFoundException ignored) {}
                    return null;
                }
            } else if(obj instanceof ReflectionFunction) {
                return null;
            } else {
                if(expectMethod) {
                    List<Method> methods = getMethodsRec(obj.getClass(), propertyAccess.getProperty());
                    if(methods.size() == 0)
                        return null;
                    return new ReflectionFunction(obj, methods);
                } else {
                    Field f = getFieldRec(obj.getClass(), propertyAccess.getProperty());
                    if(f == null)
                        return null;
                    return f.get(obj);
                }
            }
        }
        if(expression instanceof MethodCall) {
            MethodCall methodCall = (MethodCall) expression;
            Function function = (Function) evaluate(scope, methodCall.getFunction(), true);
            if(function == null)
                return null;
            Object[] args = new Object[methodCall.getArguments().getArguments().size()];
            for(int i=0; i<args.length; i++)
                args[i] = evaluate(scope, methodCall.getArguments().getArguments().get(i), false);
            return function.execute(args);
        }
        if(expression instanceof Addition) {
            NumberPair pair = NumberPair.make(scope, ((Addition) expression).getAugend(), ((Addition) expression).getAddend());
            if(pair != null)
                return NUMERIC_OPERATIONS.get(Addition.class).eval(pair);
            return String.valueOf(((Addition) expression).getAugend()) + ((Addition) expression).getAddend();
        }
        if(BOOLEAN_OPERATIONS.containsKey(expression.getClass()))
            return BOOLEAN_OPERATIONS.get(expression.getClass()).eval(scope, expression);
        if(NUMERIC_OPERATIONS.containsKey(expression.getClass()))
            return NUMERIC_OPERATIONS.get(expression.getClass()).eval(scope, expression);
        throw new IllegalArgumentException("Unknown Expression Type '" + expression.getClass().getSimpleName() + "'");
    }

    private static List<Method> getMethodsRec(Class<?> clazz, String name) {
        List<Method> methods = new ArrayList<>();
        Stream.of(clazz.getDeclaredMethods()).filter(m -> Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers()) && m.getName().equals(name)).forEach(methods::add);
        if(clazz.getSuperclass() != null)
            methods.addAll(getMethodsRec(clazz.getSuperclass(), name));
        return methods;
    }

    private static Field getFieldRec(Class<?> clazz, String name) {
        try {
            Field f = clazz.getDeclaredField(name);
            if(!Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers()))
                return f;
            return null;
        } catch (NoSuchFieldException ignored) {
            if(clazz.getSuperclass() != null)
                return getFieldRec(clazz.getSuperclass(), name);
        }
        return null;
    }

}
