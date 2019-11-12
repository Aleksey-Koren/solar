package io.solar.utils.context;

import java.util.Optional;

public interface AuthInterface<T> {
    Optional<T> verify(String token);
}
