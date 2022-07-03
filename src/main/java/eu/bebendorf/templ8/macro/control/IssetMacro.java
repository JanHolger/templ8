package eu.bebendorf.templ8.macro.control;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.templ8.Templ8Engine;
import eu.bebendorf.templ8.eval.BasicScope;
import eu.bebendorf.templ8.eval.ExpressionEvaluator;
import eu.bebendorf.templ8.macro.Macro;
import eu.bebendorf.templ8.macro.MacroList;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class IssetMacro implements Macro {

    Expression condition;
    MacroList body;

    public String render(Templ8Engine engine, BasicScope scope) throws Exception {
        if(ExpressionEvaluator.evaluate(scope, condition, false) != null)
            return body.render(engine, new BasicScope(scope));
        return "";
    }

    public String toString() {
        return "@isset(" + condition.toString() + ")" + body.toString()  + "@endisset";
    }

}
