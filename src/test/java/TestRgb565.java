import com.nuvoled.util.Rgb565;
import org.junit.Assert;
import org.junit.Test;

public class TestRgb565 {
    /*
    https://barth-dev.de/online/rgb565-color-picker/
     */


    @Test
    public void sigleRgbToRgb565() {
        byte[] input = {100, 100, 100};
        byte[] output = Rgb565.convertBGR888ToBGR565(input);
        byte[] actual = {0x2c, 0x63}; // little-endian
        Assert.assertArrayEquals(actual, output);
    }

    @Test
    public void red() {
        byte[] input = {0, 0,(byte) 0xff};
        byte[] output = Rgb565.convertBGR888ToBGR565(input);
        byte[] actual = {0x0, (byte) 0xf8};
        Assert.assertArrayEquals(actual, output);
    }

    @Test
    public void green() {
        byte[] input = {0, (byte) 0xff, 0};
        byte[] output = Rgb565.convertBGR888ToBGR565(input);
        byte[] actual = {(byte) 0xe0, 0x07};
        Assert.assertArrayEquals(actual, output);
    }

    @Test
    public void blue() {
        byte[] input = {(byte) 0xff, 0, 0};
        byte[] output = Rgb565.convertBGR888ToBGR565(input);
        byte[] actual = {0x1f, 0};
        Assert.assertArrayEquals(actual, output);
    }


    @Test
    public void multipleRgbToRgb565() {
        int length = 100;
        byte[] input = new byte[3 * length];
        byte[] output = Rgb565.convertBGR888ToBGR565(input);
        Assert.assertEquals(2 * length, output.length);
    }
}
