package ConfigFile;

import com.nuvoled.settings.Settings;
import com.nuvoled.settings.SettingsException;
import com.nuvoled.settings.Yaml;
import org.junit.Test;

import java.io.FileNotFoundException;

public class TestConfigFileReader {
    @Test()
    public void allGood() throws FileNotFoundException {
        new Settings(new Yaml().readYamlFormFile("src/test/java/ConfigFile/config.yaml"));
    }

    @Test(expected = SettingsException.class)
    public void wrongKey() throws FileNotFoundException {
        new Settings(new Yaml().readYamlFormFile("src/test/java/ConfigFile/configWrongKey.yaml"));
    }

    @Test(expected = SettingsException.class)
    public void wrongString() throws FileNotFoundException {
        new Settings(new Yaml().readYamlFormFile("src/test/java/ConfigFile/configWrongPanel.yaml"));
    }

    @Test(expected = SettingsException.class)
    public void negativeInt() throws FileNotFoundException {
        new Settings(new Yaml().readYamlFormFile("src/test/java/ConfigFile/configNegativeInt.yaml"));
    }

    @Test(expected = SettingsException.class)
    public void wrongDouble() throws FileNotFoundException {
        new Settings(new Yaml().readYamlFormFile("src/test/java/ConfigFile/configWrongDouble.yaml"));
    }

    @Test(expected = SettingsException.class)
    public void noBoolean() throws FileNotFoundException {
        new Settings(new Yaml().readYamlFormFile("src/test/java/ConfigFile/configNoBoolean.yaml"));
    }
}
