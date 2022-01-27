package io.solar.entity;

import io.solar.entity.objects.BasicObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "goods")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner")
    private BasicObject owner;

    @ManyToOne
    @JoinColumn(name = "product")
    private Product product;

    private Long amount;
    private Long price;

//    @EqualsAndHashCode
//    @Getter
//    @Setter
//    public static class Key implements Serializable {
//
//        private BasicObject owner;
//        private Product product;
//
//        public Key() {
//
//        }
//
//        public Key(BasicObject owner, Product product) {
//            this.owner = owner;
//            this.product = product;
//        }
//    }
}