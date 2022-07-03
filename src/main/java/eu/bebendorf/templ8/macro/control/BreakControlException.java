package eu.bebendorf.templ8.macro.control;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BreakControlException extends RuntimeException {

    String current;

}
