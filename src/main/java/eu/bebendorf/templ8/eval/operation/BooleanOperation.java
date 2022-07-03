package eu.bebendorf.templ8.eval.operation;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.templ8.eval.ExpressionEvaluator;
import eu.bebendorf.templ8.eval.Scope;

public interface BooleanOperation {

    default boolean eval(Scope scope, Expression expression) throws Exception {
        Object a = ExpressionEvaluator.evaluate(scope, first(expression), false);
        Object b = ExpressionEvaluator.evaluate(scope, second(expression), false);
        if(!(a instanceof Boolean))
            a = a != null;
        if(!(b instanceof Boolean))
            b = b != null;
        return eval((Boolean) a, (Boolean) b);
    }

    boolean eval(boolean a, boolean b);
    Expression first(Expression expression);
    Expression second(Expression expression);

}
