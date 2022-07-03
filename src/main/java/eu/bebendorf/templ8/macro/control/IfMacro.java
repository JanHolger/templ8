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

import java.util.List;

@AllArgsConstructor
@Getter
public class IfMacro implements Macro {

    List<Branch> branches;
    MacroList elseBody;

    public String render(Templ8Engine engine, BasicScope scope) throws Exception {
        for(Branch branch : branches) {
            if(EvalUtil.isTrue(ExpressionEvaluator.evaluate(scope, branch.getCondition(), false))) {
                return branch.getBody().render(engine, new BasicScope(scope));
            }
        }
        if(elseBody != null)
            return elseBody.render(engine, new BasicScope(scope));
        return "";
    }

    @AllArgsConstructor
    @Getter
    public static class Branch {

        Expression condition;
        MacroList body;

    }

    public String toString() {
        StringBuilder sb = new StringBuilder("@if(")
                .append(branches.get(0).getCondition().toString())
                .append(")")
                .append(branches.get(0).getBody().toString());
        for(int i=1; i<branches.size(); i++) {
            sb
                    .append("@elseif(")
                    .append(branches.get(i).getCondition().toString())
                    .append(")")
                    .append(branches.get(i).getBody().toString());
        }
        if(elseBody != null)
            sb.append("@else").append(elseBody.toString());
        sb.append("@endif");
        return sb.toString();
    }

}
