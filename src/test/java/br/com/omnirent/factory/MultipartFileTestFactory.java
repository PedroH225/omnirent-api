package br.com.omnirent.factory;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.omnirent.item.context.ItemImagesRequestDto;
import br.com.omnirent.item.domain.ItemImageRequestDto;

public final class MultipartFileTestFactory {

    private static final ObjectMapper mapper = new ObjectMapper();
	
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
    
    public static MockMultipartFile image(
            String partName,
            String filename,
            String format) throws IOException {

        BufferedImage image = new BufferedImage(
                10, 10, BufferedImage.TYPE_INT_RGB);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        ImageIO.write(image, format, output);

        return new MockMultipartFile(
                partName,
                filename,
                "image/" + format,
                output.toByteArray());
    }
    
    public static MockMultipartFile png() throws IOException {
        return image("image.png", "png");
    }
    
    public static MockMultipartFile png(String name) throws IOException {
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
    
    public static MockMultipartFile createRequest(String tempId, int order) throws JsonProcessingException {
        ItemImagesRequestDto dto = new ItemImagesRequestDto(
                List.of(
                    new ItemImageRequestDto(
                        null,
                        tempId,
                        order
                    )
                )
        );

        return new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(dto)
        );
    }
}
