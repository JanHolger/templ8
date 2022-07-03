package eu.bebendorf.templ8.eval;

public interface Scope {

    boolean has(String key);
    Object get(String key);
    void set(String key, Object value);
    Object remove(String key);
    void function(String name, Function function);
    Function function(String name);
    void addImport(String name, Class<?> clazz);
    Class<?> getImport(String name);

}
