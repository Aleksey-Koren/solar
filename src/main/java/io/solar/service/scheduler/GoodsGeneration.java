package io.solar.service.scheduler;

import io.solar.entity.Goods;
import io.solar.entity.Product;
import io.solar.entity.Production;
import io.solar.entity.objects.Station;
import io.solar.repository.ProductionRepository;
import io.solar.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoodsGeneration {

    private final StationRepository stationRepository;

    public void generateOnStations() {
        List<Station> stations = stationRepository.findAll();
        for (Station station : stations) {
            generateGoods(station);
        }
    }

    private void generateGoods(Station station) {
        List<Goods> goods = new ArrayList<>();
        for (Production production : station.getProduction()) {
            Product product = production.getProduct();
            Long amount = (long) (production.getPower() * retrieveRandomModifier() + 10);
            long price = (long) (product.getPrice() * retrievePriceModifier(0.3, 1.7));
            goods.add(new Goods(station, product, amount, price));
        }
        station.setGoods(goods);
        stationRepository.save(station);
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