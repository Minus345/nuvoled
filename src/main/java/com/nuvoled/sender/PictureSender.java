package com.nuvoled.sender;

import com.nuvoled.Main;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.RescaleOp;
import java.util.Arrays;

//import static com.nuvoled.sender.PictureCompress.compress;

/*
https://libjpeg-turbo.org/
private unsafe void jpegdecode(byte* imagedata, byte** @out, uint* outS, uint imageheight, uint imagewidth, uint quality)
{
	System.Runtime.CompilerServices.Unsafe.SkipInit(out jpeg_compress_struct cinfo);
	System.Runtime.CompilerServices.Unsafe.SkipInit(out jpeg_error_mgr jerr);
	*(int*)(&cinfo) = (int)<Module>.jpeg_std_error(&jerr);
	<Module>.jpeg_CreateCompress(&cinfo, 62, 360u);
	System.Runtime.CompilerServices.Unsafe.As<jpeg_compress_struct, uint>(ref System.Runtime.CompilerServices.Unsafe.AddByteOffset(ref cinfo, 28)) = imagewidth;
	System.Runtime.CompilerServices.Unsafe.As<jpeg_compress_struct, uint>(ref System.Runtime.CompilerServices.Unsafe.AddByteOffset(ref cinfo, 32)) = imageheight;
	System.Runtime.CompilerServices.Unsafe.As<jpeg_compress_struct, int>(ref System.Runtime.CompilerServices.Unsafe.AddByteOffset(ref cinfo, 36)) = 3;
	System.Runtime.CompilerServices.Unsafe.As<jpeg_compress_struct, int>(ref System.Runtime.CompilerServices.Unsafe.AddByteOffset(ref cinfo, 40)) = 2;
	uint outsize = 600000u;
	<Module>.jpeg_mem_dest(&cinfo, @out, &outsize);
	<Module>.jpeg_set_defaults(&cinfo);
	<Module>.jpeg_set_quality(&cinfo, (int)quality, 1);
	<Module>.jpeg_start_compress(&cinfo, 1);
	int row_stride = System.Runtime.CompilerServices.Unsafe.As<jpeg_compress_struct, int>(ref System.Runtime.CompilerServices.Unsafe.AddByteOffset(ref cinfo, 28)) * 3;
	if ((uint)System.Runtime.CompilerServices.Unsafe.As<jpeg_compress_struct, int>(ref System.Runtime.CompilerServices.Unsafe.AddByteOffset(ref cinfo, 208)) < (uint)System.Runtime.CompilerServices.Unsafe.As<jpeg_compress_struct, int>(ref System.Runtime.CompilerServices.Unsafe.AddByteOffset(ref cinfo, 32)))
	{
		do
		{
			byte* row_pointer = System.Runtime.CompilerServices.Unsafe.As<jpeg_compress_struct, int>(ref System.Runtime.CompilerServices.Unsafe.AddByteOffset(ref cinfo, 208)) * row_stride + imagedata;
			<Module>.jpeg_write_scanlines(&cinfo, &row_pointer, 1u);
		}
		while ((uint)System.Runtime.CompilerServices.Unsafe.As<jpeg_compress_struct, int>(ref System.Runtime.CompilerServices.Unsafe.AddByteOffset(ref cinfo, 208)) < (uint)System.Runtime.CompilerServices.Unsafe.As<jpeg_compress_struct, int>(ref System.Runtime.CompilerServices.Unsafe.AddByteOffset(ref cinfo, 32)));
	}
	<Module>.jpeg_finish_compress(&cinfo);
	<Module>.jpeg_destroy_compress(&cinfo);
	*outS = outsize;
}
 */


public class PictureSender {

    public static byte[] rgb = new byte[Main.getPanelSizeX() * Main.getPanelSizeY() * 3];// 128*128*3
    public static byte[] rgbOld = new byte[Main.getPanelSizeX() * Main.getPanelSizeY() * 3];

    private static boolean only_changed_pictures = false;
    private static int color_mode = 10;

    private static final boolean use_filter = false;
    //private static final boolean test_jpg = true;
    private static final boolean debug = false;
    private static final boolean DEBUG_RGB = false;

    private static boolean image_identical = false;

    public static void setScreenMode(boolean screenMode_b, int colormode) {
        only_changed_pictures = screenMode_b;
        color_mode = colormode;
    }

    public static void send(BufferedImage image) {

        /*
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);
        if (debug) {
            System.out.println(os.size());
        }

        RescaleOp rescaleOp = new RescaleOp(1.2f, 15, null);
rescaleOp.filter(image, image);  // Source and destination are the same.

        */

        //applyColorspace(image)
        send_rgb(applyFilter(image));

        if (DEBUG_RGB) {
            System.out.println(":");
            System.out.println("Image Buffer RGBdata:");
            printRgbFromPicture(image);
            System.out.println(":");
        }

    }

    private static void send_rgb(BufferedImage image) {
        //checkPicture(image); // checks if the picture ist big enough
        getRgbFromPicture(image, color_mode); //gets rgb data from pictures

        if (only_changed_pictures) {
            if (Arrays.equals(rgb, rgbOld)) {
                if (image_identical) {
                    if (debug) {
                        System.out.print(".");
                    }
                    return;
                } else {
                    if (debug) {
                        System.out.println("-");
                    }
                    image_identical = true;
                }
            } else {
                image_identical = false;
            }
        }

        int pixel = 0;
        int MaxPackets = ((Main.getPanelSizeX() * Main.getPanelSizeY() * 3) / 1440) + 1;

        for (int counter = 0; counter <= MaxPackets; counter++) { //35 = (128 * 128 * 3)/1440
            byte[] message = new byte[1450];
            message[0] = 36;
            message[1] = 36;
            message[2] = 20;
            message[3] = Main.getCourantFrame();
            message[4] = (byte) (color_mode); //RGB -> 10 JPG -> 20
            message[5] = (byte) (counter >> 8);
            message[6] = (byte) (counter & 255);
            message[7] = (byte) (MaxPackets >> 8);
            message[8] = (byte) (MaxPackets & 255);
            message[9] = 45;

            for (int i = 1; i < 1440; i = i + 3) {
                if (pixel >= rgb.length) {
                    //setzt die letzten bytes des Psackest auf 0
                    message[9 + i] = 0;
                    pixel++;
                    message[9 + 1 + i] = 0;
                    pixel++;
                    message[9 + 2 + i] = 0;
                } else {
                    //https://en.wikipedia.org/wiki/YCbCr
                    message[9 + i] = rgb[pixel];
                    pixel++;
                    message[9 + 1 + i] = rgb[pixel];
                    pixel++;
                    message[9 + 2 + i] = rgb[pixel];
                }
                pixel++;
            }
            SendSync.send_data(message);
        }
        try {
            Thread.sleep(Main.getSleep());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        SendSync.send_end_frame();

        System.arraycopy(rgb, 0, rgbOld, 0, rgb.length);
    }

    public static BufferedImage applyColorspace(BufferedImage image) {

        //if (use_filter) {
        //   ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);

        //   ColorConvertOp op = new ColorConvertOp(cs, null);
        //   BufferedImage bufferedImage = op.filter(image, null);
        //   send_rgb(bufferedImage);

        return image;
    }

    public static BufferedImage applyFilter(BufferedImage image) {

        RescaleOp rescaleOp = new RescaleOp(Main.getScaleFactor(), Main.getOffset(), null);
        rescaleOp.filter(image, image);  // Source and destination are the same.
        return image;
    }

    private static void send_jpg(byte[] image) {
        //checkPicture(image); // checks if the picture ist big enough
        int pixel = 0;
        int MaxPackets = (image.length / 1440) + 1;

        System.out.println("image " + image.length + " frames " + MaxPackets);

        for (int counter = 0; counter <= MaxPackets; counter++) { //35 = (128 * 128 * 3)/1440
            byte[] message = new byte[1450];
            message[0] = 36;
            message[1] = 36;
            message[2] = 20;
            message[3] = Main.getCourantFrame();
            message[4] = (byte) (color_mode); //RGB -> 10 JPG -> 20
            message[5] = (byte) (counter >> 8);
            message[6] = (byte) (counter & 255);
            message[7] = (byte) (MaxPackets >> 8);
            message[8] = (byte) (MaxPackets & 255);
            message[9] = 45;

            for (int i = 1; i < 1440; i++) {
                if (pixel >= image.length) {
                    message[9 + i] = 0;
                } else {
                    //https://en.wikipedia.org/wiki/YCbCr
                    message[9 + i] = image[pixel];
                }
                pixel++;
            }
            SendSync.send_data(message);
        }
        SendSync.send_end_frame();
        System.arraycopy(rgb, 0, rgbOld, 0, rgb.length);
    }

    private static void printRgbFromPicture(BufferedImage image) {
        int rgbCounterNumber = 0;
        for (int y = 1; y <= 1; y++) {
            for (int x = 1; x <= Main.getPanelSizeX(); x++) {
                int pixel = image.getRGB(x, y);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
                if (debug) {
                    System.out.print((byte) blue);
                    System.out.print(" ");
                    System.out.print((byte) green);
                    System.out.print(" ");
                    System.out.print((byte) red);
                    System.out.print(" ");
                }
                rgb[rgbCounterNumber] = (byte) blue;
                rgbCounterNumber++;
                rgb[rgbCounterNumber] = (byte) green;
                rgbCounterNumber++;
                rgb[rgbCounterNumber] = (byte) red;
                rgbCounterNumber++;
            }
        }

    }

    private static void getRgbFromPicture(BufferedImage image, int colormode) {

        if (colormode == 10) {
            getLedRgbData(image);
            return;
        }

        if (colormode == 20) {
            getLedJpgData(image);
        }
    }

    private static void getLedRgbData(BufferedImage image) {

        int rgbCounterNumber = 0;

        if (Main.rotationDegree() == 180) {
            //System.out.println( "x: " + Main.getPanelSizeX() + " Y: " +  Main.getPanelSizeY());
            for (int y = Main.getPanelSizeY() - 1; y >= 0; y--) {
                for (int x = Main.getPanelSizeX() - 1; x >= 0; x--) {
                    int pixel = image.getRGB(x, y);
                    int red = (pixel >> 16) & 0xff;
                    int green = (pixel >> 8) & 0xff;
                    int blue = (pixel) & 0xff;
                    rgb[rgbCounterNumber] = (byte) blue;
                    rgbCounterNumber++;
                    rgb[rgbCounterNumber] = (byte) green;
                    rgbCounterNumber++;
                    rgb[rgbCounterNumber] = (byte) red;
                    rgbCounterNumber++;
                }
            }

        } else if (Main.rotationDegree() == 90) {
            //System.out.println( "x: " + Main.getPanelSizeX() + " Y: " +  Main.getPanelSizeY());
            for (int x = Main.getPanelSizeX() - 1; x >= 0; x--) {
                for (int y = 0; y < Main.getPanelSizeY(); y++) {
                    int pixel = image.getRGB(x, y);
                    int red = (pixel >> 16) & 0xff;
                    int green = (pixel >> 8) & 0xff;
                    int blue = (pixel) & 0xff;
                    rgb[rgbCounterNumber] = (byte) blue;
                    rgbCounterNumber++;
                    rgb[rgbCounterNumber] = (byte) green;
                    rgbCounterNumber++;
                    rgb[rgbCounterNumber] = (byte) red;
                    rgbCounterNumber++;
                }
            }
        } else if (Main.rotationDegree() == 270) {
            //System.out.println( "x: " + Main.getPanelSizeX() + " Y: " +  Main.getPanelSizeY());
            for (int x = 0; x < Main.getPanelSizeX(); x++) {
                for (int y = Main.getPanelSizeY() - 1; y >= 0; y--) {
                    int pixel = image.getRGB(x, y);
                    int red = (pixel >> 16) & 0xff;
                    int green = (pixel >> 8) & 0xff;
                    int blue = (pixel) & 0xff;
                    rgb[rgbCounterNumber] = (byte) blue;
                    rgbCounterNumber++;
                    rgb[rgbCounterNumber] = (byte) green;
                    rgbCounterNumber++;
                    rgb[rgbCounterNumber] = (byte) red;
                    rgbCounterNumber++;
                }
            }
        } else {
            for (int y = 0; y < Main.getPanelSizeY(); y++) {
                for (int x = 0; x < Main.getPanelSizeX(); x++) {
                    int pixel = image.getRGB(x, y);
                    int red = (pixel >> 16) & 0xff;
                    int green = (pixel >> 8) & 0xff;
                    int blue = (pixel) & 0xff;
                    rgb[rgbCounterNumber] = (byte) blue;
                    rgbCounterNumber++;
                    rgb[rgbCounterNumber] = (byte) green;
                    rgbCounterNumber++;
                    rgb[rgbCounterNumber] = (byte) red;
                    rgbCounterNumber++;
                }
            }
        }
    }

    private static void getLedJpgData(BufferedImage image) {

        int rgbCounternumber = 0;
        int panelSize = 128;
        //System.out.println( "x: " + Main.getPanelSizeX() + " Y: " +  Main.getPanelSizeY());
        int rowsX = Main.getPanelSizeX() / panelSize;
        int rowsY = Main.getPanelSizeY() / panelSize;
        for (int y = 0; y < rowsY; y++) {
            for (int x = 0; x < rowsX; x++) {
                rgbCounternumber = getPixelPerPanel(image, rgbCounternumber, x, y, panelSize);
            }
        }
    }

    private static int getPixelPerPanel(BufferedImage image, int rgbCounternumber, int rowX, int colY, int panelSize) {
        int startX = rowX * panelSize;
        int startY = colY * panelSize;
        for (int y = 0; y < panelSize; y++) {
            for (int x = 0; x < panelSize; x++) {
                int pixel = image.getRGB(startX + x, startY + y);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                //int blue = (pixel) & 0xff;
                rgb[rgbCounternumber] = (byte) ((pixel) & 0xff); //blue
                rgbCounternumber++;
                rgb[rgbCounternumber] = (byte) green;
                rgbCounternumber++;
                rgb[rgbCounternumber] = (byte) red;
                rgbCounternumber++;
            }
        }
        return rgbCounternumber;
    }


    private static void checkPicture(BufferedImage image) {
        if (image.getHeight() < Main.getPanelSizeY() || image.getWidth() < Main.getPanelSizeX()) {
            System.out.println("Falsches Format");
            System.out.println("Bitte Format von mindestens " + Main.getPanelSizeX() + " * " + Main.getPanelSizeY() + " Pixeln verwenden");
            System.exit(0);
        }
    }


}
