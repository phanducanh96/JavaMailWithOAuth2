package javaMailWithOAuth2.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Crunchify.com
 *
 */

public class ReadPropertyValues {
    String result = "";
    InputStream inputStream;

    public Properties getProperties() throws IOException {
        Properties prop = new Properties();

        try {
            String propFileName = "javaMailWithOAuth2/resources/config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
        return prop;
    }
}