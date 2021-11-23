package io.solar.entity;

import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String image;
    private Float bulk;
    private Float mass;
    private Float price;
}
