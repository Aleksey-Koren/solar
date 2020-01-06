package io.solar.entity.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManyToMany {
    private Long id;
    private Long left;
    private Long right;
    private Integer sort;

    public ManyToMany(Long id, Long left, Long right) {
        this(id, left, right, null);
    }
    public ManyToMany(Long id, Long left, Long right, Integer sort) {
        this.id = id;
        this.left = left;
        this.right = right;
        this.sort = sort;
    }
}
