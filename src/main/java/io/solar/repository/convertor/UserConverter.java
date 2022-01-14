package io.solar.repository.convertor;

import io.solar.entity.User;
import io.solar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;

@Component
@RequiredArgsConstructor
public class UserConverter implements AttributeConverter<User, Long> {

    private final UserRepository userRepository;

    @Override
    public Long convertToDatabaseColumn(User attribute) {
        return attribute.getId();
    }

    @Override
    public User convertToEntityAttribute(Long dbData) {
        return userRepository.getById(dbData);
    }
}
