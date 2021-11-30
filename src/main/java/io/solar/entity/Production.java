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
    private Long product;
    private Float power;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "station")
    @EqualsAndHashCode.Exclude
    private Station station;
}
