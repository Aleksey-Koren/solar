package io.solar.entity;

import io.solar.entity.objects.BasicObject;
import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "goods")
public class Goods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner")
    private BasicObject owner;
    @ManyToOne
    @JoinColumn(name = "product")
    private Product product;
    private Long amount;

    public Goods(BasicObject owner, Product product, Long amount) {
        this.owner = owner;
        this.product = product;
        this.amount = amount;
    }
}