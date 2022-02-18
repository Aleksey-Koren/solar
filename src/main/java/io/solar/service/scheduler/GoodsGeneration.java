package io.solar.service.scheduler;

import io.solar.entity.objects.Station;
import io.solar.repository.StationRepository;
import io.solar.service.engine.interfaces.GoodsGenerationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoodsGeneration {

    private final StationRepository stationRepository;
    private final GoodsGenerationEngine goodsGenerationEngine;

    public void generateOnStations() {
        List<Station> stations = stationRepository.findAll();
        for (Station station : stations) {
            goodsGenerationEngine.generateGoods(station);
        }
    }

//    private Float retrievePriceModifier(double from, double to) {
//        return (retrieveRandomInRange(from, to) + retrieveRandomInRange(from, to) + retrieveRandomInRange(from, to) +
//                retrieveRandomInRange(from, to) + retrieveRandomInRange(from, to) + retrieveRandomInRange(from, to)) / 6;
//    }
//
//    private static Float retrieveRandomInRange(double from, double to) {
//        double f = Math.random() / Math.nextDown(1.0);
//        double x = from * (1.0 - f) + to * f;
//        return (float) x;
//    }

}