package io.solar.entity;

import io.solar.entity.objects.BasicObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;


@Data
@Entity
@IdClass(Goods.Key.class)
@Table(name = "goods")
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    private Long price;

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