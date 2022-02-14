package io.solar.entity.objects;

import io.solar.entity.Goods;
import io.solar.entity.User;
import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.inventory.socket.SpaceTechSocket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity
@Table(name = "star_ships")
@PrimaryKeyJoinColumn(name = "id")
@EqualsAndHashCode(callSuper = true)
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StarShip extends BasicObject implements SpaceTech {

    @OneToMany(mappedBy = "owner", orphanRemoval = true, cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Goods> goods;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "spaceTech")
    private List<SpaceTechSocket> sockets;
}