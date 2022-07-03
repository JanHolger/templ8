# templ8
An easy to use Java 8 template language based on Laravel Blade's template syntax

## Example
MyApp.java
```java
public class MyApp {
    
    public static final String NAME = "Todos";

    public static void main(String[] args) {
        MyApp app = new MyApp();
        try {
            app.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void run() throws Exception {
        Templ8Engine engine = new Templ8Engine(new File("templates"));
        BasicScope scope = new BasicScope();
        scope.addImport("MyApp", MyApp.class);
        scope.set("todos", new String[] { "Clean Room", "Prepare Food" });
        System.out.println(engine.render("test", scope));
    }
    
}
```

templates/test.templ8
```html
<!DOCTYPE html>
<html>
<head>
    <title>{{ MyApp.NAME }}</title>
</head>
<body>
    <h1>{{ MyApp.NAME }}</h1>
    <ul>
        @foreach(todo : todos)
        <li>{{ todo }}</li>
        @endforeach
    </ul>
</body>
</html>
```

## TODO
- @for
- @switch
- Components
- Custom Macros