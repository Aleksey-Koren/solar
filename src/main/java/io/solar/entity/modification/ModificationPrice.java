package io.solar.entity.modification;

import io.solar.entity.objects.Station;
import io.solar.entity.price.Price;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "modification_prices")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModificationPrice {

    @Id
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "modification_id")
    private Modification modification;

    @ManyToOne
    @JoinColumn(name = "price_id")
    private Price price;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;
}