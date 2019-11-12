package io.solar.utils.server;

import lombok.Getter;

@Getter
public class Pageable {
    private Integer page;
    private Integer pageSize;

    public Pageable(Integer page, Integer pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }
}
