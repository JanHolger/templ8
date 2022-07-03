package eu.bebendorf.templ8.eval.operation;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.purejavaparser.ast.expression.GreaterThan;
import eu.bebendorf.templ8.eval.Scope;

public class GreaterThanOperation implements NumericOperation {

    public Object eval(int a, int b) {
        return a > b;
    }

    public Object eval(long a, long b) {
        return a > b;
    }

    public Object eval(double a, double b) {
        return a > b;
    }

    public Object eval(float a, float b) {
        return a > b;
    }

    public NumberPair pair(Scope scope, Expression expression) throws Exception {
        if(!(expression instanceof GreaterThan))
            return null;
        return NumberPair.make(((GreaterThan) expression).getFirst(), ((GreaterThan) expression).getSecond());
    }

}
