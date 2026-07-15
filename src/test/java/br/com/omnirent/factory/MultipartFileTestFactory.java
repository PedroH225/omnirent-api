package br.com.omnirent.factory;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.mock.web.MockMultipartFile;

public final class MultipartFileTestFactory {


    private MultipartFileTestFactory() {}
	
    public static MockMultipartFile image(
            String filename, String format) throws IOException {

        BufferedImage image = new BufferedImage(
                10, 10, BufferedImage.TYPE_INT_RGB);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        ImageIO.write(image, format, output);

        return new MockMultipartFile(
                "file",
                filename,
                "image/" + format,
                output.toByteArray());
    }
    
    public static MockMultipartFile png() throws IOException {
        return image("image.png", "png");
    }

    public static MockMultipartFile jpeg() throws IOException {
        return image("image.jpeg", "jpeg");
    }

    public static MockMultipartFile empty() {
        return new MockMultipartFile(
                "file", 
                "empty.png", 
                "image/png", 
                new byte[0]);
    }

    public static MockMultipartFile invalidImage() {
        return new MockMultipartFile(
                "file", 
                "document.txt", 
                "text/plain", 
                "not an image".getBytes());
    }

    public static MockMultipartFile corruptedImage() {
        return new MockMultipartFile(
                "file", 
                "corrupted.png", 
                "image/png", 
                new byte[]{1, 2, 3, 4, 5});
    }
}
