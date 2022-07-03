package eu.bebendorf.templ8.macro;

import eu.bebendorf.templ8.Templ8Engine;
import eu.bebendorf.templ8.eval.BasicScope;
import lombok.Getter;

@Getter
public class TextMacro implements Macro {

    String text;

    public TextMacro(String text, boolean trimLeft, boolean trimRight) {
        if(trimLeft)
            text = trimFirstNewline(text);
        if(trimRight)
            text = trimLastNewline(text);
        this.text = text;
    }

    private static String trimFirstNewline(String text) {
        int index = text.indexOf('\n');
        if(index == -1)
            return text;
        for(int i=0; i<index; i++) {
            char c = text.charAt(i);
            if(c == '\t' || c == ' ')
                continue;
        }
        return text.substring(index+1);
    }

    private static String trimLastNewline(String text) {
        int index = text.lastIndexOf('\n');
        if(index == -1)
            return text;
        for(int i=index + 1; i<text.length(); i++) {
            char c = text.charAt(i);
            if(c == '\t' || c == ' ')
                continue;
        }
        return text.substring(0, index);
    }

    public String render(Templ8Engine engine, BasicScope scope) {
        return text;
    }

    public String toString() {
        return text;
    }

}
