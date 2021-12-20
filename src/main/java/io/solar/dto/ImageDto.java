package io.solar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDto {
    private Long userId;
    private String imageData;
    private String mimeType;
    private String filename;
}
