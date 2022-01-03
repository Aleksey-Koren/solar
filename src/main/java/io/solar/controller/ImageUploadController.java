package io.solar.controller;

import io.solar.dto.ImageDto;
import io.solar.service.image.DocumentService;
import io.solar.service.image.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile/image")
public class ImageUploadController {

    private final ImageUploadService imageUploadService;
    private final DocumentService documentService;

    @Transactional
    @PreAuthorize("hasAnyAuthority('EDIT_USER', 'PLAY_THE_GAME')")
    @PostMapping
    public void uploadAvatar(@RequestBody ImageDto imageDto) {

        imageUploadService.uploadAvatar(imageDto);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('EDIT_USER', 'PLAY_THE_GAME')")
    @Transactional
    public void deleteAvatar(@PathVariable("id") Long id) {

        documentService.deleteAvatarByUserId(id);
    }
}