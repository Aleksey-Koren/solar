package io.solar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InventoryModificationDto {
    private Long id;
    private String title;
    private String data;
    private String description;
}
