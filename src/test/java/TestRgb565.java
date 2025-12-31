import com.nuvoled.util.Rgb565;
import org.junit.Assert;
import org.junit.Test;

public class TestRgb565 {
    @Test
    public void sigleRgbToRgb565() {
        byte[] input = {100, 100, 100};
        byte[] output = Rgb565.getLedRgb565Data(input);
        byte[] actual = {44, 99};
        Assert.assertArrayEquals(actual, output);
    }

    @Test
    public void red() {
        byte[] input = {(byte) 255, 0, 0};
        byte[] output = Rgb565.getLedRgb565Data(input);
        byte[] actual = {31, 0};
        Assert.assertArrayEquals(actual, output);
    }

    @Test
    public void green() {
        byte[] input = {0, (byte) 255, 0};
        byte[] output = Rgb565.getLedRgb565Data(input);
        byte[] actual = {-32, 7};
        Assert.assertArrayEquals(actual, output);
    }

    @Test
    public void blue() {
        byte[] input = {0, 0, (byte) 255};
        byte[] output = Rgb565.getLedRgb565Data(input);
        byte[] actual = {0, -8};
        Assert.assertArrayEquals(actual, output);
    }


    @Test
    public void multipleRgbToRgb565() {
        int length = 100;
        byte[] input = new byte[3 * length];
        byte[] output = Rgb565.getLedRgb565Data(input);
        Assert.assertEquals(2 * length, output.length);
    }
}
