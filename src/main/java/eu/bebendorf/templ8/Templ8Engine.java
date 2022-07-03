package eu.bebendorf.templ8;

import eu.bebendorf.purejavaparser.exception.UnexpectedCharacterException;
import eu.bebendorf.purejavaparser.parser.UnexpectedTokenException;
import eu.bebendorf.templ8.escaper.Escaper;
import eu.bebendorf.templ8.escaper.HTMLEscaper;
import eu.bebendorf.templ8.eval.BasicScope;
import eu.bebendorf.templ8.macro.control.BreakControlException;
import eu.bebendorf.templ8.macro.control.ContinueControlException;
import eu.bebendorf.templ8.source.DirectoryTemplateSource;
import eu.bebendorf.templ8.source.TemplateFile;
import eu.bebendorf.templ8.source.TemplateSource;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Templ8Engine {

    @Getter @Setter
    Escaper escaper = new HTMLEscaper();
    final Templ8Parser parser = new Templ8Parser();
    final Map<String, Templ8Template> templates = new HashMap<>();
    final TemplateSource templateSource;

    public Templ8Engine(File baseDirectory) {
        this(new DirectoryTemplateSource(baseDirectory));
    }

    public Templ8Engine(TemplateSource templateSource) {
        this.templateSource = templateSource;
    }

    public Templ8Template getTemplate(String name) {
        Templ8Template t = templates.get(name);
        if(t != null)
            return t;
        TemplateFile file = templateSource.getFile(name);
        if(file == null)
            return null;
        try {
            t = parser.parse(file);
            templates.put(name, t);
            return t;
        } catch (UnexpectedCharacterException | UnexpectedTokenException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String render(String name, BasicScope scope) throws Exception {
        Templ8Template template = getTemplate(name);
        if(template == null)
            throw new Templ8EngineException("Templ8Template " + name + " not found");
        String rendered;
        try {
            rendered = template.getContent().render(this, scope);
        } catch (BreakControlException ex) {
            rendered = ex.getCurrent();
        } catch (ContinueControlException ex) {
            rendered = ex.getCurrent();
        }
        String extendTemplate = scope.getExtendTemplate();
        if(extendTemplate != null) {
            scope.setExtendTemplate(null);
            return render(extendTemplate, scope);
        } else {
            return rendered;
        }
    }

}
