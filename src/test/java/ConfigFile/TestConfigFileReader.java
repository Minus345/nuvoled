package ConfigFile;

import com.nuvoled.yaml.YamlReader;
import org.junit.Test;

public class TestConfigFileReader {
    @Test()
    public void allGood() {
        new YamlReader("src/test/java/ConfigFile/config.yaml");
    }

    @Test(expected = NullPointerException.class)
    public void wrongKey() {
        new YamlReader("src/test/java/ConfigFile/configWrongKey.yaml");
    }

    @Test(expected = RuntimeException.class)
    public void wrongString() {
        new YamlReader("src/test/java/ConfigFile/configWrongPanel.yaml");
    }

    @Test(expected = RuntimeException.class)
    public void negativeInt() {
        new YamlReader("src/test/java/ConfigFile/configNegativeInt.yaml");
    }

    @Test(expected = RuntimeException.class)
    public void wrongDouble() {
        new YamlReader("src/test/java/ConfigFile/configWrongDouble.yaml");
    }

    @Test(expected = RuntimeException.class)
    public void noBoolean() {
        new YamlReader("src/test/java/ConfigFile/configNoBoolean.yaml");
    }
}
