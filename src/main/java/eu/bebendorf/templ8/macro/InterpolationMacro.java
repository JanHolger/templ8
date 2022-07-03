package eu.bebendorf.templ8.macro;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.templ8.Templ8Engine;
import eu.bebendorf.templ8.eval.BasicScope;
import eu.bebendorf.templ8.eval.ExpressionEvaluator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InterpolationMacro implements Macro {

    Expression expression;
    boolean raw;

    public String render(Templ8Engine engine, BasicScope scope) throws Exception {
        return String.valueOf(ExpressionEvaluator.evaluate(scope, expression, false));
    }

    public String toString() {
        if(raw)
            return "{!! " + expression.toString() + " !!}";
        else
            return "{{ " + expression.toString() + " }}";
    }

}
