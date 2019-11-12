package io.solar.entity.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ManyToMany {
    private Long id;
    private Long left;
    private Long right;
}
