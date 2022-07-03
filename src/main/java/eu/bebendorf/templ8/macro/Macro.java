package eu.bebendorf.templ8.macro;

import eu.bebendorf.templ8.Templ8Engine;
import eu.bebendorf.templ8.eval.BasicScope;

public interface Macro {

    String render(Templ8Engine engine, BasicScope scope) throws Exception;

}
