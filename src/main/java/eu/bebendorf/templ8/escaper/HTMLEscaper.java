package eu.bebendorf.templ8.escaper;

public class HTMLEscaper implements Escaper {

    public String escape(String value) {
        // TODO This might not be safe?
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

}
