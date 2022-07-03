package eu.bebendorf.templ8.macro.layout;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.templ8.Templ8Engine;
import eu.bebendorf.templ8.Templ8EngineException;
import eu.bebendorf.templ8.Templ8Template;
import eu.bebendorf.templ8.eval.BasicScope;
import eu.bebendorf.templ8.eval.ExpressionEvaluator;
import eu.bebendorf.templ8.macro.Macro;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class IncludeMacro implements Macro {

    Expression nameExpression;

    public String render(Templ8Engine engine, BasicScope scope) throws Exception {
        Object o = ExpressionEvaluator.evaluate(scope, nameExpression, false);
        if(!(o instanceof String))
            throw new Templ8EngineException("Include name " + o + " not a string");
        Templ8Template template = engine.getTemplate((String) o);
        if(template == null)
            throw new Templ8EngineException("Templ8Template " + o + " not found");
        return template.getContent().render(engine, new BasicScope(scope));
    }

    public String toString() {
        return "@include(" + nameExpression.toString() + ")";
    }

}
