package io.solar.entity.marketplace;

import io.solar.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "lots_bets")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LotBet {

    @Id
    private Long id;

    @MapsId
    @JoinColumn(name = "lot_id")
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private MarketplaceLot lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Long amount;
    private Instant betTime;
}
