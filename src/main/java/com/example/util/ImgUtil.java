package com.example.util;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImgUtil {

    public static String resizeImage(byte[] imageBytes, int targetWidth, int targetHeight) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage originalImage = ImageIO.read(inputStream);

        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png", outputStream);
        byte[] resizedImageBytes = outputStream.toByteArray();

        return Base64.getEncoder().encodeToString(resizedImageBytes);
    }
}