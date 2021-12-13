package io.solar.service.image;

import com.groupdocs.metadata.Metadata;
import io.solar.dto.ImageDto;
import io.solar.service.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class ImageUploadService {

    @Value("${app.images.path}")
    private String imagesPath;

    @Value("${app.images.width}")
    private Integer imageWidth;

    @Value("${app.images.height}")
    private Integer imageHeight;

    private final DocumentService documentService;

    @Autowired
    public ImageUploadService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void uploadAvatar(ImageDto imageDto) {
        byte[] decodedImageData = Base64.getDecoder().decode(imageDto.getImageData());

        String imagePath = buildImagePath(imageDto.getUserId());

        byte[] croppedImage = cutImage(decodedImageData);

        byte[] imageWithoutMetadata = removeImageMetadata(croppedImage);

        documentService.upload(imagePath, imageWithoutMetadata);
    }

    private String buildImagePath(Long userId) {

        String userIdRage = getUserIdRange(userId);

        //todo: remove hardcode image extension
        return String.format("%s/%s/%d/%s.%s", imagesPath, userIdRage, userId, "avatar", "png");
    }

    private String getUserIdRange(Long userId) {
        StringBuilder range = new StringBuilder();
        double startMidResult = userId / 1000.00;

        if (startMidResult >= 1.001) {
            long start = (long) startMidResult;

            start = (userId % 1000 == 0)
                    ? start - 999
                    : start * 1000 + 1;

            range.append(start).append("_").append(start + 999);
        } else {
            range.append("1_1000");
        }

        return range.toString();
    }

    private byte[] cutImage(byte[] imageData) {
        ByteArrayInputStream in = new ByteArrayInputStream(imageData);
        try {
            BufferedImage img = ImageIO.read(in);

            if (img.getWidth() != imageWidth || img.getHeight() != imageHeight) {
                BufferedImage croppedImage = img.getSubimage(0, 0, imageWidth, imageHeight);

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                ImageIO.write(croppedImage, "png", buffer); //todo: remove hardcode image format

                imageData = buffer.toByteArray();
            }

        } catch (IOException e) {
            throw new ServiceException("Cannot cut image");
        }

        return imageData;
    }

    private byte[] removeImageMetadata(byte[] imageData) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
        byte[] imageDataWithoutMetadata;

        try (Metadata metadata = new Metadata(inputStream);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            metadata.sanitize();
            metadata.save(buffer);
            imageDataWithoutMetadata = buffer.toByteArray();

        } catch (IOException e) {
            throw new ServiceException("Cannot remove metadata from image");
        }

        return imageDataWithoutMetadata;
    }
}
