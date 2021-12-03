package io.solar.entity;

import io.solar.entity.objects.Station;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;



@Data
@Entity
@Table(name = "productions")
public class Production {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product")
    private Product product;
    private Float power;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station")
    @EqualsAndHashCode.Exclude
    private Station station;
}
