package io.solar.service.data_generation;

import io.solar.entity.Goods;
import io.solar.entity.Product;
import io.solar.entity.Production;
import io.solar.entity.objects.Station;
import io.solar.repository.ProductionRepository;
import io.solar.repository.StationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoodsGeneration {

    private final StationRepository stationRepository;
    private final ProductionRepository productionRepository;

    public GoodsGeneration(StationRepository stationRepository, ProductionRepository productionRepository) {
        this.stationRepository = stationRepository;
        this.productionRepository = productionRepository;
    }

    public void generateOnStations() {
        List<Station> stations = stationRepository.findAll();
        for(Station station : stations) {
            generateGoods(station);
        }
    }

    private void generateGoods(Station station) {
        List<Goods> goods = new ArrayList();
        for(Production production : station.getProduction()) {
            Product product = production.getProduct();
            Long amount = (long) (production.getPower() * retrieveRandomModifier() + 10);
            goods.add(new Goods(station, product, amount));
        }
        station.setGoods(goods);
        stationRepository.save(station);
    }

    private Float retrieveRandomModifier() {
        return (float) (Math.random() + Math.random() + Math.random() + Math.random() + Math.random() + Math.random()) / 6;
    }
}
