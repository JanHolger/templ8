package eu.bebendorf.templ8.macro.layout;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.templ8.Templ8Engine;
import eu.bebendorf.templ8.Templ8EngineException;
import eu.bebendorf.templ8.eval.BasicScope;
import eu.bebendorf.templ8.eval.ExpressionEvaluator;
import eu.bebendorf.templ8.macro.Macro;
import eu.bebendorf.templ8.macro.MacroList;
import eu.bebendorf.templ8.macro.TextMacro;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public class SectionMacro implements Macro {

    Expression nameExpression;
    Expression fallbackBody;
    MacroList body;

    public String render(Templ8Engine engine, BasicScope scope) throws Exception {
        Object o = ExpressionEvaluator.evaluate(scope, nameExpression, false);
        if(!(o instanceof String))
            throw new Templ8EngineException("Section name " + o + " not a string");
        String name = (String) o;
        MacroList body;
        if(fallbackBody != null) {
            o = ExpressionEvaluator.evaluate(scope, fallbackBody, false);
            body = new MacroList(Arrays.asList(new TextMacro(String.valueOf(o), false, false)));
        } else {
            body = this.body;
        }
        scope.section(name, new Section(body));
        return "";
    }

    public String toString() {
        if(fallbackBody != null) {
            return "@section(" + nameExpression.toString() + ", " + fallbackBody.toString() + ")";
        } else {
            return "@section(" + nameExpression.toString() + ")" + body.toString() + "@endsection";
        }
    }

}
