package io.solar.entity.objects;

import io.solar.entity.Goods;
import io.solar.entity.interfaces.SpaceTech;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity
@Table(name = "star_ships")
@PrimaryKeyJoinColumn(name = "id")
@ToString
public class StarShip extends BasicObject implements SpaceTech {

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Goods> goods;
}
