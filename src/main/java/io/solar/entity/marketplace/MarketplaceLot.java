package io.solar.entity.marketplace;

import io.solar.entity.User;
import io.solar.entity.objects.BasicObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "marketplace_lots")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarketplaceLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id")
    private BasicObject object;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "lot", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    private List<MarketplaceBet> currentBet;

    private Instant startDate;
    private Instant finishDate;
    private Long startPrice;
    private Long instantPrice;
    private Boolean isBuyerHasTaken;
    private Boolean isSellerHasTaken;
}
