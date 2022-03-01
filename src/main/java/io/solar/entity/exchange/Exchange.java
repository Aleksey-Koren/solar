package io.solar.entity.exchange;

import io.solar.entity.User;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "exchanges")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "first_user_id")
    @ToString.Exclude
    private User firstUser;

    @OneToOne
    @JoinColumn(name = "second_user_id")
    @ToString.Exclude
    private User secondUser;

    private Boolean firstAccepted;
    private Boolean secondAccepted;
    private Instant startTime;

    @OneToMany(mappedBy = "exchange")
    @ToString.Exclude
    private List<ExchangeOffer> exchangeOffers;

    public void addOffer(ExchangeOffer exchangeOffer) {
        exchangeOffers.add(exchangeOffer);
        exchangeOffer.setExchange(this);
    }
}
