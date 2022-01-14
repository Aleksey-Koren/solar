package io.solar.repository.convertor;

import io.solar.entity.messenger.Room;
import io.solar.repository.messenger.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;

@Component
@RequiredArgsConstructor
public class RoomConverter implements AttributeConverter<Room, Long> {

    private final RoomRepository roomRepository;

    @Override
    public Long convertToDatabaseColumn(Room attribute) {
        return attribute.getId();
    }

    @Override
    public Room convertToEntityAttribute(Long dbData) {
        return roomRepository.getById(dbData);
    }
}
