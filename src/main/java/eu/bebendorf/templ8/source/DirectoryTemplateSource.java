package eu.bebendorf.templ8.source;

import lombok.AllArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@AllArgsConstructor
public class DirectoryTemplateSource implements TemplateSource {

    File baseDirectory;

    public TemplateFile getFile(String name) {
        File file = new File(baseDirectory, name.replace('.', File.pathSeparatorChar) + ".templ8");
        String source = readFile(file);
        if(source == null)
            return null;
        return new TemplateFile(name, file.getAbsolutePath(), source);
    }

    private static String readFile(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int r;
            byte[] buffer = new byte[1024];
            while ((r = fis.read(buffer)) != -1)
                baos.write(buffer, 0, r);
            fis.close();
            return new String(baos.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

}
