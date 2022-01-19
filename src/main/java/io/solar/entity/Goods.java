package io.solar.entity;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.shop.StationShop;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@IdClass(Goods.Key.class)
@Table(name = "goods")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Goods {

    @Id
    @ManyToOne
    @JoinColumn(name = "owner")
    private BasicObject owner;

    @Id
    @ManyToOne
    @JoinColumn(name = "product")
    private Product product;

    private Long amount;
    private Float price;

    @EqualsAndHashCode
    @Getter
    @Setter
    public static class Key implements Serializable {

        private BasicObject owner;
        private Product product;

        public Key() {

        }

        public Key(BasicObject owner, Product product) {
            this.owner = owner;
            this.product = product;
        }
    }
}