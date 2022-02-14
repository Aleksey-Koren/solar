package io.solar.service.scheduler;

import io.solar.entity.Goods;
import io.solar.entity.Product;
import io.solar.entity.Production;
import io.solar.entity.objects.Station;
import io.solar.repository.StationRepository;
import io.solar.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoodsGeneration {

    private final StationRepository stationRepository;
    private final GoodsService goodsService;

    public void generateOnStations() {
        List<Station> stations = stationRepository.findAll();
        for (Station station : stations) {
            generateGoods(station);
        }
    }

    private void generateGoods(Station station) {
        for (Production production : station.getProduction()) {
            Product product = production.getProduct();
            Long amount = (long) (production.getPower() * retrieveRandomModifier() + 10);
            long price = (long) (product.getPrice() * retrievePriceModifier(0.3, 1.7));
            Optional<Goods> goodOpt = goodsService.findByOwnerAndProduct(station, product);
            Goods goods;
            if (goodOpt.isPresent()) {
                goods = goodOpt.get();
                goods.setAmount(amount);
                goods.setPrice(price);
            } else {
                goods = Goods.builder()
                        .owner(station)
                        .product(product)
                        .amount(amount)
                        .price(price)
                        .build();
            }
            goodsService.save(goods);
        }
    }

    private Float retrieveRandomModifier() {
        return (float) (Math.random() + Math.random() + Math.random() + Math.random() + Math.random() + Math.random()) / 6;
    }

    private Float retrievePriceModifier(double from, double to) {
        return (retrieveRandomInRange(from, to) + retrieveRandomInRange(from, to) + retrieveRandomInRange(from, to) +
                retrieveRandomInRange(from, to) + retrieveRandomInRange(from, to) + retrieveRandomInRange(from, to)) / 6;
    }

    private Float retrieveRandomInRange(double from, double to) {
        double f = Math.random() / Math.nextDown(1.0);
        double x = from * (1.0 - f) + to * f;
        return (float) x;
    }
}