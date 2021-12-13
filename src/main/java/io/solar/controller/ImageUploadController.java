package io.solar.controller;

import io.solar.dto.ImageDto;
import io.solar.service.image.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile/image")
public class ImageUploadController {

    private final ImageUploadService imageUploadService;

    @Transactional
    @PreAuthorize("hasAnyAuthoriy('EDIT_USER', 'PLAY_THE_GAME')")
    @PostMapping
    public void uploadAvatar(@RequestBody ImageDto imageDto) {
        imageUploadService.uploadAvatar(imageDto);
    }
}