package eu.bebendorf.templ8.eval.operation;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.templ8.eval.ExpressionEvaluator;
import eu.bebendorf.templ8.eval.Scope;
import lombok.Getter;

@Getter
public class NumberPair {

    Number a;
    Number b;

    private NumberPair(Object a, Object b) {
        if(a == null || b == null)
            throw new NumberFormatException("numbers may not be null");
        if(a instanceof Character)
            a = (int) (Character) a;
        if(b instanceof Character)
            b = (int) (Character) b;
        if(!(a instanceof Number) || !(b instanceof Number))
            throw new NumberFormatException("numbers have to be numbers");
        if(a instanceof Byte)
            a = (int) (Byte) a;
        if(a instanceof Short)
            a = (int) (Short) a;
        if(b instanceof Byte)
            b = (int) (Byte) b;
        if(b instanceof Short)
            b = (int) (Short) b;
        if(!isValidNumber(a) || !isValidNumber(b))
            throw new NumberFormatException("numbers have to be primitive");
        if(a instanceof Double || b instanceof Double) {
            this.a = ((Number) a).doubleValue();
            this.b = ((Number) b).doubleValue();
        } else if(a instanceof Float || b instanceof Float) {
            this.a = ((Number) a).floatValue();
            this.b = ((Number) b).floatValue();
        } else if(a instanceof Long || b instanceof Long) {
            this.a = ((Number) a).longValue();
            this.b = ((Number) b).longValue();
        } else {
            this.a = ((Number) a).intValue();
            this.b = ((Number) b).intValue();
        }
    }

    private static boolean isValidNumber(Object n) {
        if(n instanceof Integer || n instanceof Long || n instanceof Double || n instanceof Float)
            return true;
        return false;
    }

    public static NumberPair make(Scope scope, Expression exprA, Expression exprB) throws Exception {
        Object a = ExpressionEvaluator.evaluate(scope, exprA, false);
        Object b = ExpressionEvaluator.evaluate(scope, exprB, false);
        return make(a, b);
    }

    public static NumberPair make(Object a, Object b) throws Exception {
        try {
            return new NumberPair(a, b);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

}
