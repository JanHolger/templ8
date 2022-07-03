package eu.bebendorf.templ8.eval;

import eu.bebendorf.purejavaparser.ast.expression.Equals;
import eu.bebendorf.purejavaparser.ast.expression.NotEqual;

import java.util.Objects;

public class ComparisonEvaluator {

    public static Object evaluate(Scope scope, Equals comparison) throws Exception {
        Object first = ExpressionEvaluator.evaluate(scope, comparison.getFirst(), false);
        Object second = ExpressionEvaluator.evaluate(scope, comparison.getSecond(), false);
        return Objects.equals(first, second);
    }

    public static Object evaluate(Scope scope, NotEqual comparison) {
        Object first = comparison.getFirst();
        Object second = comparison.getSecond();
        return !Objects.equals(first, second);
    }

}
