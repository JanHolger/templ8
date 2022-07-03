package eu.bebendorf.templ8.eval.operation;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.purejavaparser.ast.expression.LogicalOr;

public class LogicalOrOperation implements BooleanOperation {

    public boolean eval(boolean a, boolean b) {
        return a || b;
    }

    public Expression first(Expression expression) {
        return ((LogicalOr) expression).getLeft();
    }

    public Expression second(Expression expression) {
        return ((LogicalOr) expression).getRight();
    }

}
