package eu.bebendorf.templ8.macro.layout;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.templ8.Templ8Engine;
import eu.bebendorf.templ8.Templ8EngineException;
import eu.bebendorf.templ8.eval.BasicScope;
import eu.bebendorf.templ8.eval.ExpressionEvaluator;
import eu.bebendorf.templ8.macro.Macro;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExtendsMacro implements Macro {

    Expression nameExpression;

    public String render(Templ8Engine engine, BasicScope scope) throws Exception {
        if(scope.getExtendTemplate() != null)
            throw new Templ8EngineException("A template can only extend a single template");
        Object o = ExpressionEvaluator.evaluate(scope, nameExpression, false);
        if(!(o instanceof String))
            throw new Templ8EngineException("Template name " + o + " not a string");
        scope.setExtendTemplate((String) o);
        return "";
    }

    public String toString() {
        return "@extends(" + nameExpression.toString() + ")";
    }

}
