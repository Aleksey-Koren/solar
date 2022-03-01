package io.solar.entity.exchange;

import io.solar.entity.Product;
import io.solar.entity.User;
import io.solar.entity.objects.BasicObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;

@Data
@Entity
@Table(name = "exchange_offers")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_id")
    private Exchange exchange;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @OneToOne
    @JoinColumn(name = "object_id")
    private BasicObject inventoryObject;

    private Long moneyAmount;

    @OneToOne()
    @JoinColumn(name = "product_id")
    private Product product;

    private Long productAmount;

    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    private OfferType offerType;
}