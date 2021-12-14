package io.solar.service.image;

import io.solar.dto.ImageDto;
import io.solar.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    @Value("${app.images.path}")
    private String imagesPath;

    @Value("${app.images.width}")
    private Integer imageWidth;

    @Value("${app.images.height}")
    private Integer imageHeight;

    private final DocumentService documentService;

    public void uploadAvatar(ImageDto imageDto) {
        byte[] decodedImageData = Base64.getDecoder().decode(imageDto.getImageData());

        String imagePath = buildImagePath(imageDto.getUserId(), imageDto.getMimeType());

        byte[] croppedImage = cutImage(decodedImageData, imageDto.getMimeType());

        documentService.upload(imagePath, croppedImage, imageDto.getUserId());
    }

    private String buildImagePath(Long userId, String mimeType) {
        String userIdRage = getUserIdRange(userId);
        String imageExtension = MimeType.valueOf(mimeType).getSubtype();

        return String.format("%s/%s/%d/avatar.%s", imagesPath, userIdRage, userId, imageExtension);
    }

    private String getUserIdRange(Long userId) {

        long count = (userId % 1000 != 0)
                ? (userId / 1000) * 1000 + 1
                : (userId / 1000 - 1) * 1000 + 1;

        return count + "_" + (count + 999);
    }

    private byte[] cutImage(byte[] imageData, String mimeType) {
        ByteArrayInputStream in = new ByteArrayInputStream(imageData);
        try {
            BufferedImage img = ImageIO.read(in);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            if (img.getWidth() > imageWidth || img.getHeight() > imageHeight) {
                BufferedImage croppedImage = img.getSubimage(
                        0, 0,
                        Math.min(img.getWidth(), imageWidth),
                        Math.min(img.getHeight(), imageHeight)
                );
                ImageIO.write(croppedImage, MimeType.valueOf(mimeType).getSubtype(), buffer);
            } else {
                ImageIO.write(img, MimeType.valueOf(mimeType).getSubtype(), buffer);
            }
            imageData = buffer.toByteArray();

        } catch (IOException e) {
            throw new ServiceException("Cannot cut image");
        }
        return imageData;
    }
}