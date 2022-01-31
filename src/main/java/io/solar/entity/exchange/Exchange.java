package io.solar.entity.exchange;

import groovy.transform.builder.Builder;
import io.solar.entity.User;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "exchanges")
@Data
@Builder
public class Exchange {

    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "first_user_id")
    private User firstUser;

    @OneToOne
    @JoinColumn(name ="second_user_id")
    private User secondUser;

    private Boolean firstAccepted;
    private Boolean secondAccepted;
    private Instant startTime;

    @OneToMany(mappedBy = "exchange")
    private List<ExchangeOffer> exchangeOffers;

    public void addOffer(ExchangeOffer exchangeOffer) {
        exchangeOffers.add(exchangeOffer);
        exchangeOffer.setExchange(this);
    }
}
