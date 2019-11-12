package io.solar.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Page<T> {

    private final List<T> content;
    private final Long total;


    public static <T> Page<T> empty() {
        return new Page<>(new ArrayList<>(), 0L);
    }

    public Page(List<T> content, Long total) {
        this.content = content;
        this.total = total;
    }

    public Long getTotalElements() {
        return total;
    }
    public List<T> getContent() {
        return content;
    }
    <U> Page<U> map(Function<? super T,? extends U> converter) {
        return new Page<>(content.stream().map(converter).collect(Collectors.toList()), total);
    }

}
