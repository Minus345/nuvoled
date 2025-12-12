import com.nuvoled.util.rotation.Rotation;
import org.junit.Assert;
import org.junit.Test;

public class TestingRotaion {
    private final byte[] rgb = {0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7, 8, 8, 8};

    @Test
    public void rotation90() {
        byte[] rgbExpect90 = {6, 6, 6, 3, 3, 3, 0, 0, 0, 7, 7, 7, 4, 4, 4, 1, 1, 1, 8, 8, 8, 5, 5, 5, 2, 2, 2};
        byte[] rgbReturn90 = Rotation.rotateRgbData(rgb, 90, 3, 3);
        Assert.assertArrayEquals(rgbExpect90, rgbReturn90);
    }

    @Test
    public void rotation180() {
        byte[] rgbExpect180 = {8, 8, 8, 7, 7, 7, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0};
        byte[] rgbReturn180 = Rotation.rotateRgbData(rgb, 180, 3, 3);
        Assert.assertArrayEquals(rgbExpect180, rgbReturn180);
    }

    @Test
    public void rotation270() {
        byte[] rgbExpect270 = {2, 2, 2, 5, 5, 5, 8, 8, 8, 1, 1, 1, 4, 4, 4, 7, 7, 7, 0, 0, 0, 3, 3, 3, 6, 6, 6};
        byte[] rgbReturn270 = Rotation.rotateRgbData(rgb, 270, 3, 3);
        Assert.assertArrayEquals(rgbExpect270, rgbReturn270);
    }
}
