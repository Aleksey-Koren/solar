package io.solar.service.image;

import io.solar.entity.User;
import io.solar.repository.UserRepository;
import io.solar.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final UserRepository userRepository;

    public void upload(String imagePath, byte[] imageData, Long userId) {
        updateDatabase(imagePath, userId);

        File file = new File(imagePath);

        try {
            Files.createDirectories(file.getParentFile().toPath());
            Files.write(file.toPath(), imageData);
        } catch (IOException e) {
            throw new ServiceException("Cannot save image to path: " + imagePath);
        }

    }

    private void updateDatabase(String imagePath, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no user with such id in database"));
        if(user.getAvatar() != null) {
            deleteAvatarFile(user.getAvatar());
        }
        user.setAvatar(imagePath);
        userRepository.save(user);
    }

    public void deleteAvatarByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no user with such id in database"));
        if(user.getAvatar() != null) {
            deleteAvatarFile(user.getAvatar());
            user.setAvatar(null);
            userRepository.save(user);
        }else{
            log.warn("Attempt to delete an avatar from a user without an avatar user id = {}", userId);
        }
    }

    private void deleteAvatarFile(String avatarPath) {
        File previous = new File(avatarPath);

        try {
            Files.delete(previous.toPath());
        } catch (NoSuchFileException e) {
            log.warn("Tried do delete avatar, but hadn't found file with path from database: {}", avatarPath);
        } catch (IOException e) {
            throw new ServiceException(String.format("Troubles with deleting avatar, path = %s", avatarPath), e);
        }
    }
}