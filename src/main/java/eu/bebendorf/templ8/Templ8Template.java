package eu.bebendorf.templ8;

import eu.bebendorf.templ8.macro.MacroList;
import eu.bebendorf.templ8.source.TemplateFile;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Templ8Template {

    TemplateFile file;
    MacroList content;

}
