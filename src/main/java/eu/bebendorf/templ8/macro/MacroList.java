package eu.bebendorf.templ8.macro;

import eu.bebendorf.templ8.Templ8Engine;
import eu.bebendorf.templ8.eval.BasicScope;
import eu.bebendorf.templ8.macro.control.BreakControlException;
import eu.bebendorf.templ8.macro.control.ContinueControlException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class MacroList {

    List<Macro> nodes;

    public String render(Templ8Engine engine, BasicScope scope) throws Exception {
        StringBuilder sb = new StringBuilder();
        for(Macro n : nodes) {
            try {
                sb.append(n.render(engine, scope));
            } catch (ContinueControlException ex) {
                throw new ContinueControlException(sb + ex.getCurrent());
            } catch (BreakControlException ex) {
                throw new BreakControlException(sb + ex.getCurrent());
            }
        }
        return sb.toString();
    }

    public String toString() {
        return nodes.stream().map(Object::toString).collect(Collectors.joining());
    }

}
