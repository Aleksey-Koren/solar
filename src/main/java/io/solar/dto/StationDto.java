package io.solar.dto;

import lombok.Data;

@Data
public class StationDto {

    private Long id;
    private Long planetId;
    private Long population;
    private String fraction;
    private String title;
    private Float x;
    private Float y;
    private Float aphelion;
    private Float angle;
    private Float orbitalPeriod;
    private Long objectTypeDescriptionId;
}