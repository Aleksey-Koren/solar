package io.solar.entity.price;

import io.solar.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "price_products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "price_id")
    @ToString.Exclude
    private Price price;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer productAmount;
}