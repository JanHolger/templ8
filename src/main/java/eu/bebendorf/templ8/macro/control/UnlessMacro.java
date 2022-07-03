package eu.bebendorf.templ8.macro.control;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.templ8.Templ8Engine;
import eu.bebendorf.templ8.eval.BasicScope;
import eu.bebendorf.templ8.eval.ExpressionEvaluator;
import eu.bebendorf.templ8.macro.Macro;
import eu.bebendorf.templ8.macro.MacroList;
import eu.bebendorf.templ8.util.EvalUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UnlessMacro implements Macro {

    Expression condition;
    MacroList body;

    public String render(Templ8Engine engine, BasicScope scope) throws Exception {
        if(!EvalUtil.isTrue(ExpressionEvaluator.evaluate(scope, condition, false)))
            return body.render(engine, new BasicScope(scope));
        return "";
    }

    public String toString() {
        return "@unless(" + condition.toString() + ")" + body.toString()  + "@endunless";
    }

}
