package io.solar.entity;

import io.solar.entity.objects.BasicObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
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
    @JoinColumn(name = "product")
    private Product product;

    private Long amount;

    @ManyToOne
    @JoinColumn(name = "owner")
    private BasicObject owner;

    private Long buyPrice;
    private Long sellPrice;

    @Column(name = "available_for_sale")
    private Boolean isAvailableForSale;

    @Column(name = "available_for_buy")
    private Boolean isAvailableForBuy;
}