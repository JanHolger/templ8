package eu.bebendorf.templ8.eval.operation;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.templ8.eval.Scope;

public interface NumericOperation {

    default Object eval(Scope scope, Expression expression) throws Exception {
        return eval(pair(scope, expression));
    }

    default Object eval(NumberPair pair) {
        Number a = pair.getA();
        Number b = pair.getB();
        if(a instanceof Double)
            return eval(a.doubleValue(), b.doubleValue());
        if(a instanceof Float)
            return eval(a.floatValue(), b.floatValue());
        if(a instanceof Long)
            return eval(a.longValue(), b.longValue());
        return eval(a.intValue(), b.intValue());
    }

    Object eval(int a, int b);
    Object eval(long a, long b);
    Object eval(double a, double b);
    Object eval(float a, float b);
    NumberPair pair(Scope scope, Expression expression) throws Exception;

}
