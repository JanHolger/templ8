package eu.bebendorf.templ8.eval;

import eu.bebendorf.purejavaparser.ast.expression.Addition;
import eu.bebendorf.templ8.eval.operation.NumberPair;

public class AdditionEvaluator {

    public static Object evaluate(Scope scope, Addition addition) throws Exception {
        NumberPair numberPair = NumberPair.make(scope, addition.getAugend(), addition.getAddend());

        return null;
    }


}
