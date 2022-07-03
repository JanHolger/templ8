package eu.bebendorf.templ8.macro.control;

import eu.bebendorf.templ8.Templ8Engine;
import eu.bebendorf.templ8.eval.BasicScope;
import eu.bebendorf.templ8.macro.Macro;

public class ContinueMacro implements Macro {

    public String render(Templ8Engine engine, BasicScope scope) throws Exception {
        throw new ContinueControlException("");
    }

    public String toString() {
        return "@continue";
    }

}
