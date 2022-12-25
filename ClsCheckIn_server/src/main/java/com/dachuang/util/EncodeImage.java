package com.dachuang.util;

import net.iharder.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class EncodeImage {
    public static String cvtImg2Str(String filepath) {
        byte [] bytes;
        String str = new String("");
        try {
            ByteArrayOutputStream bytes_stream = new ByteArrayOutputStream();
            InputStream inputStream = new BufferedInputStream(new FileInputStream(filepath));
            BufferedImage img = ImageIO.read(inputStream);
            ImageIO.write(img, "jpg", bytes_stream);
            bytes = bytes_stream.toByteArray();
            str = Base64.encodeBytes(bytes);
            bytes_stream.close();
        } catch (FileNotFoundException fnfe) {
            System.out.println("Image Path not found" + fnfe);
        } catch (IOException ioe) {
            System.out.println("Exception while converting the Image " + ioe);
        }
        return str;
    }
}
