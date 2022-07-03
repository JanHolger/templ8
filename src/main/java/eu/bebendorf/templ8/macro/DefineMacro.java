package eu.bebendorf.templ8.macro;

import eu.bebendorf.purejavaparser.ast.Variable;
import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.templ8.Templ8Engine;
import eu.bebendorf.templ8.eval.BasicScope;
import eu.bebendorf.templ8.eval.ExpressionEvaluator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DefineMacro implements Macro {

    Variable variable;
    Expression value;

    public String render(Templ8Engine engine, BasicScope scope) throws Exception {
        scope.define(variable.getName(), ExpressionEvaluator.evaluate(scope, value, false));
        return "";
    }

    public String toString() {
        return "@define(" + variable.getName() + (value != null ? (" = " + value) : "") + ")";
    }

}
