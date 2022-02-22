package io.solar.entity.price;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Data
@Table(name = "prices")
@NoArgsConstructor
@AllArgsConstructor
public class Price {

    @Id
    private Long id;
    private Long moneyAmount;

    @OneToMany(mappedBy = "price")
    private List<PriceProduct> priceProducts;
}