package eu.bebendorf.templ8.macro.control;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.templ8.Templ8Engine;
import eu.bebendorf.templ8.eval.BasicScope;
import eu.bebendorf.templ8.eval.ExpressionEvaluator;
import eu.bebendorf.templ8.macro.Macro;
import eu.bebendorf.templ8.macro.MacroList;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Array;

@AllArgsConstructor
@Getter
public class EmptyMacro implements Macro {

    Expression condition;
    MacroList body;

    public String render(Templ8Engine engine, BasicScope scope) throws Exception {
        Object iter = ExpressionEvaluator.evaluate(scope, condition, false);
        if(iter == null)
            return body.render(engine, new BasicScope(scope));
        if(iter.getClass().isArray() && Array.getLength(iter) == 0)
            return body.render(engine, new BasicScope(scope));
        if(iter instanceof Iterable && !((Iterable<Object>) iter).iterator().hasNext())
            return body.render(engine, new BasicScope(scope));
        return "";
    }

    public String toString() {
        return "@empty(" + condition.toString() + ")" + body.toString()  + "@endempty";
    }

}
