package eu.bebendorf.templ8.macro;

import eu.bebendorf.purejavaparser.ast.expression.Expression;
import eu.bebendorf.templ8.Templ8Engine;
import eu.bebendorf.templ8.Templ8EngineException;
import eu.bebendorf.templ8.eval.BasicScope;
import eu.bebendorf.templ8.eval.ExpressionEvaluator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ImportMacro implements Macro {

    Expression nameExpression;
    Expression classExpression;

    public String render(Templ8Engine engine, BasicScope scope) throws Exception {
        Object o = ExpressionEvaluator.evaluate(scope, classExpression, false);
        if(!(o instanceof String))
            throw new Templ8EngineException("Class name " + o + " not a string");
        String className = (String) o;
        Class<?> clazz = Class.forName(className);
        String name;
        if(nameExpression != null) {
            o = ExpressionEvaluator.evaluate(scope, nameExpression, false);
            if(!(o instanceof String))
                throw new Templ8EngineException("Import name " + o + " not a string");
            name = (String) o;
        } else {
            String[] spl = className.split("\\.");
            name = spl[spl.length - 1];
        }
        scope.addImport(name, clazz);
        return "";
    }

}
