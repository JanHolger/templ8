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
public class YieldMacro implements Macro {

    Expression nameExpression;
    Expression fallbackBody;

    public String render(Templ8Engine engine, BasicScope scope) throws Exception {
        Object o = ExpressionEvaluator.evaluate(scope, nameExpression, false);
        if(!(o instanceof String))
            throw new Templ8EngineException("Yield name " + o + " not a string");
        Section section = scope.section((String) o);
        if(section == null) {
            if(fallbackBody != null)
                return String.valueOf(ExpressionEvaluator.evaluate(scope, fallbackBody, false));
            throw new Templ8EngineException("Yield section " + o + " not found");
        }
        return section.getBody().render(engine, new BasicScope(scope));
    }

    public String toString() {
        return "@yield(" + nameExpression.toString() + (fallbackBody != null ? (", " + fallbackBody) : "") + ")";
    }

}
