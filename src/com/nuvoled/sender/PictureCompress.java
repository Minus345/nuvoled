/*
package com.nuvoled.sender;

import org.libjpegturbo.turbojpeg.TJ;
import org.libjpegturbo.turbojpeg.TJCompressor;
import org.libjpegturbo.turbojpeg.TJScalingFactor;
import org.libjpegturbo.turbojpeg.TJTransform;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

public class PictureCompress {

    TJScalingFactor scalingFactor = new TJScalingFactor(1, 1);

    TJTransform xform = new TJTransform();
    boolean display = false;
    static int flags = 0;
    static final boolean debug = true;
    int width, height;
    String inFormat = "jpg", outFormat = "jpg";
    BufferedImage img = null;
    static byte[] imgBuf = null;

    static final String[] SUBSAMP_NAME = {
            "4:4:4", "4:2:2", "4:2:0", "Grayscale", "4:4:0", "4:1:1"
    };

    public static byte[] compress(BufferedImage img) {
        int width, height, itype;
        int outQual = 100; //1-100 - 95 default
        int outSubsamp = TJ.CS_RGB;

        byte[] jpegBuf;

        String filename = "c:/data/myfile.jpg";
        String filename2 = "c:/data/myfile2.bmp";

        //String filename = "/Users/MFU/tmp/myfile.jpg";
        //String filename2 = "/Users/MFU/tmp/myfile2.bmp";

        try {
            if (img == null)
                throw new Exception("Input image type not supported.");
            width = img.getWidth();
            height = img.getHeight();
            itype = img.getType();

            System.out.println("Input Image:  " + width + " x " + height +
                    " pixels " + itype);

            System.out.println("Out " + SUBSAMP_NAME[outSubsamp] +
                    " subsampling, quality = " + outQual);

            TJCompressor tjc = new TJCompressor();
            tjc.setSubsamp(outSubsamp);
            tjc.setJPEGQuality(outQual);
            tjc.setSourceImage(img, 0, 0, width,height);
            //tjc.setSourceImage(img, 0, 0, width, 0, height, TJ.PF_BGR);
            jpegBuf = tjc.compress(flags);
            int jpegSize = tjc.getCompressedSize();
            tjc.close();

            System.out.println("neu " + jpegBuf.length );

            if (debug) {
                File outFile = new File(filename);
                FileOutputStream fos = new FileOutputStream(outFile);
                fos.write(jpegBuf, 0, jpegSize);
                fos.close();

                File outputfile = new File(filename2);
                ImageIO.write(img, "bmp", outputfile);

                System.out.println(filename + " " + jpegSize + " byte");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return jpegBuf;
    }
}
*/