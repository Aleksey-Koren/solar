package io.solar.dto.modification;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApplyModificationDto {

    private Long modificationId;
    private Long itemId;
    private String message;
    private Boolean isModified;
}
