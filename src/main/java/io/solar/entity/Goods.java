package io.solar.entity;

import io.solar.entity.objects.BasicObject;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@IdClass(Goods.Key.class)
@Table(name = "goods")
@EqualsAndHashCode
public class Goods {

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner")
    @Getter
    @Setter
    private BasicObject owner;

    @Id
    @ManyToOne
    @JoinColumn(name = "product")
    @Getter
    @Setter
    private Product product;
    @Getter
    @Setter
    private Long amount;

    public Goods() {

    }

    public Goods(BasicObject owner, Product product, Long amount) {
        this.owner = owner;
        this.product = product;
        this.amount = amount;
    }

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