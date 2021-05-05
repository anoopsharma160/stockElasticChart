package util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropUtils {
    public static void main(String[] args) throws IOException {
        FileReader reader=new FileReader("src/main/resources/application.properties");

        Properties p=new Properties();
        p.load(reader);

        System.out.println(p.getProperty("elastic.hostname"));
        System.out.println(p.getProperty("password"));

        System.out.println(PropUtils.getPropValue("elastic.hostname"));
    }

    public static String getPropValue(String propKey) throws IOException {
        FileReader reader = null;
        Properties p = new Properties();
        try {
             reader = new FileReader("src/main/resources/application.properties");

            p.load(reader);
        }
        finally {
            String value = p.getProperty(propKey);
            reader.close();
            return value;

        }
    }
}
