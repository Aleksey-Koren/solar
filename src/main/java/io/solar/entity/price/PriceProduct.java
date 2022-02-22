package io.solar.entity.price;

import io.solar.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "price_products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceProduct {

    @Id
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "price_id")
    private Price price;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer amount;
}