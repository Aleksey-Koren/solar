package io.solar.service;

import io.solar.entity.objects.StarShip;
import io.solar.entity.User;
import io.solar.mapper.StarShipMapper;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.beans.Service;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Service
public class StarShipService {

    public Optional<StarShip> getActiveShip(User user, Transaction transaction) {
        Query query = transaction.query("select" +
                " objects.id user_ship_id," +
                " objects.planet user_ship_planet," +
                " objects.population user_ship_population," +
                " objects.fraction user_ship_fraction," +
                " objects.title user_ship_title," +
                " objects.x user_ship_x," +
                " objects.y user_ship_y," +
                " objects.aphelion user_ship_aphelion," +
                " objects.orbital_period user_ship_orbital_period," +
                " objects.angle user_ship_angle," +
                " objects.hull_id hull_id," +
                " objects.user_id user_ship_user_id," +
                " objects.active user_ship_active," +
                " objects.durability user_ship_durability," +
                " objects.attached_to_ship user_ship_attached_to_ship," +
                " objects.attached_to_socket user_ship_attached_to_socket," +
                " objects.status user_ship_status," +
                " object_type_description.sub_type hull_type," +
                " object_type_description.title hull_title," +
                " object_type_description.power_degradation hull_power_degradation," +
                " object_type_description.energy_consumption hull_energy_consumption," +
                " object_type_description.durability hull_durability," +
                " object_type_description.description hull_description" +
                " from objects " +
                "inner join object_type_description on objects.hull_id = object_type_description.id " +
                "where active = 1 and user_id = :userId");
        query.setLong("userId", user.getId());
        List<StarShip> result = query.executeQuery(new StarShipMapper());
        if (result.size() == 1) {
            return Optional.of(result.get(0));
        }
        return Optional.empty();
    }
}
