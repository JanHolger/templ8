package eu.bebendorf.templ8.macro.control;

import eu.bebendorf.purejavaparser.ast.Variable;
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
public class ForEachMacro implements Macro {

    Variable variable;
    Expression iterable;
    MacroList body;

    public String render(Templ8Engine engine, BasicScope scope) throws Exception {
        Object iter = ExpressionEvaluator.evaluate(scope, iterable, false);
        ForEachLoopState loopState = new ForEachLoopState((scope.has("loop") && scope.get("loop") instanceof ForEachLoopState) ? ((ForEachLoopState) scope.get("loop")) : null, iter);
        StringBuilder sb = new StringBuilder();
        while (loopState.hasNext()) {
            BasicScope inner = new BasicScope(scope);
            inner.define("loop", loopState);
            inner.define(variable.getName(), loopState.next());
            try {
                sb.append(body.render(engine, inner));
            } catch (ContinueControlException ex) {
                sb.append(ex.getCurrent());
            } catch (BreakControlException ex) {
                sb.append(ex.getCurrent());
                break;
            }
        }
        return sb.toString();
    }

    public String toString() {
        return "@foreach(" + variable.toString() + " : " + iterable.toString() + ")" + body.toString() + "@endforeach";
    }

}
