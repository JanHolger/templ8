package eu.bebendorf.templ8.macro.control;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ContinueControlException extends RuntimeException {

    String current;

}
