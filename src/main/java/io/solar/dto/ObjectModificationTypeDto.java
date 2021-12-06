package io.solar.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class ObjectModificationTypeDto {
    private Long id;
    private String title;
    private String data;
    private String description;
}
