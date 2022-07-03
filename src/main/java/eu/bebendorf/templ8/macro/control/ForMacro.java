package eu.bebendorf.templ8.macro.control;

import eu.bebendorf.purejavaparser.ast.Variable;
import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.templ8.Templ8Engine;
import eu.bebendorf.templ8.eval.BasicScope;
import eu.bebendorf.templ8.macro.Macro;
import eu.bebendorf.templ8.macro.MacroList;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ForMacro implements Macro {

    Variable variable;
    Expression initialValue;
    Expression condition;
    Expression incrementor;

    MacroList body;

    public String render(Templ8Engine engine, BasicScope scope) throws Exception {
        StringBuilder sb = new StringBuilder();
        // TODO Implement
        return sb.toString();
    }

    public String toString() {
        return "@for(" + variable.toString() + " = " + initialValue.toString() + "; " + condition.toString() + "; " + incrementor.toString() + ")" + body.toString() + "@endfor";
    }

}
