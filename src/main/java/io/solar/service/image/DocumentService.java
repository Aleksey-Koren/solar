package io.solar.service.image;

import io.solar.service.exception.ServiceException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class DocumentService {

    public void upload(String imagePath, byte[] imageData) {
        File file = new File(imagePath);

        try {
            Files.createDirectories(file.getParentFile().toPath());
        } catch (IOException e) {
            throw new ServiceException("Cannot save image to path: " + imagePath);
        }

    }

}