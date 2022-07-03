package eu.bebendorf.templ8.eval;

import eu.bebendorf.templ8.macro.layout.Section;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class BasicScope implements Scope {

    final BasicScope parent;
    final Map<String, Object> variables = new HashMap<>();
    Map<String, Section> sections;
    Map<String, Function> functions;
    Map<String, Class<?>> imports;
    @Getter @Setter
    String extendTemplate;

    public BasicScope() {
        this(null);
    }

    public BasicScope(BasicScope parent) {
        this.parent = parent;
        if(parent == null) {
            sections = new HashMap<>();
            functions = new HashMap<>();
            imports = new HashMap<>();
        }
    }

    public boolean has(String key) {
        return variables.containsKey(key) || (parent != null && parent.has(key));
    }

    public Object get(String key) {
        if(variables.containsKey(key))
            return variables.get(key);
        if(parent != null)
            return parent.get(key);
        return null;
    }

    public void set(String key, Object value) {
        if(parent != null && parent.has(key)) {
            parent.set(key, value);
            return;
        }
        variables.put(key, value);
    }

    public BasicScope define(String key, Object value) {
        variables.put(key, value);
        return this;
    }

    public Object remove(String key) {
        if(variables.containsKey(key))
            return variables.remove(key);
        if(parent != null)
            return parent.remove(key);
        return null;
    }

    public BasicScope section(String name, Section section) {
        if(parent != null) {
            parent.root().section(name, section);
            return this;
        }
        sections.put(name, section);
        return this;
    }

    public Section section(String name) {
        if(parent != null)
            return parent.root().section(name);
        return sections.get(name);
    }

    public void function(String name, Function function) {
        if(parent != null) {
            parent.root().function(name, function);
            return;
        }
        functions.put(name, function);
    }

    public Function function(String name) {
        if(parent != null)
            return parent.root().function(name);
        return functions.get(name);
    }

    public void addImport(String name, Class<?> clazz) {
        if(parent != null) {
            parent.root().addImport(name, clazz);
            return;
        }
        imports.put(name, clazz);
    }

    public Class<?> getImport(String name) {
        if(parent != null)
            return parent.root().getImport(name);
        return imports.get(name);
    }

    public BasicScope root() {
        if(parent != null)
            return parent.root();
        return this;
    }

}
