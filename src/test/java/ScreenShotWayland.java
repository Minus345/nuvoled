import org.junit.Ignore;
import org.junit.Test;

public class ScreenShotWayland {
    @Ignore
    public void testScreenShot() throws Exception {
        System.out.println(System.getProperty("java.version"));
        java.awt.Robot robot = new java.awt.Robot();
        java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
        java.awt.Rectangle rect = new java.awt.Rectangle(tk.getScreenSize());
        java.awt.image.BufferedImage im = robot.createScreenCapture(rect);
        javax.imageio.ImageIO.write(im, "png", new java.io.File("/tmp/screen.png"));
    }
}
