package io.solar.service.engine;

import io.solar.entity.Goods;
import io.solar.entity.Product;
import io.solar.entity.Production;
import io.solar.entity.objects.Station;
import io.solar.multithreading.StationMonitor;
import io.solar.service.GoodsService;
import io.solar.service.engine.interfaces.GoodsGenerationEngine;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import io.solar.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoodsGenerationEngineImpl implements GoodsGenerationEngine {

    private final GoodsService goodsService;
    private final StationMonitor stationMonitor;
    private final SpaceTechEngine spaceTechEngine;

    @Override
    public void generateGoods(Station station) {
//        synchronized (stationMonitor.getMonitor(station.getId())) {
            for (Production production : station.getProduction()) {
                Product product = production.getProduct();
                Long amount = (long) (production.getPower() * retrieveRandomInRange(0.5, 1.0) + 10);
                Optional<Goods> goodsOptional = goodsService.findByOwnerAndProduct(station, product);
                Goods goods;
                if (goodsOptional.isEmpty()) {
                    goods = Goods.builder()
                            .owner(station)
                            .product(product)
                            .amount(0L)
                            .price(0L)
                            .build();
                    station.getGoods().add(goods);
                }else{
                    goods = goodsOptional.get();
                }

                boolean stillEnoughVolume = addAmount(product, amount, goods, station);
                if(!stillEnoughVolume) {
                    break;
                }
            }
            goodsService.saveAll(station.getGoods());
        }
//    }

    @Override
    //todo refactor this method to more natural volume proportions calculation algorithm
    public void recalculatePrice(Station station) {
        float maxVolumeValue = (float) station.getGoods().stream()
                .mapToDouble(s -> s.getProduct().getVolume())
                .max()
                .orElseThrow(() -> new ServiceException("Something wrong with good's volume values at  station id = " + station.getId()));

        float maxVolumeProportion = spaceTechEngine.calculateTotalVolume(station) / station.getGoods().size();

        for (Goods goods : station.getGoods()) {
            float volumeRatio = maxVolumeValue/goods.getProduct().getVolume();
            float maxVolumeCorrectedWithVolumeRatio = maxVolumeProportion / volumeRatio;
            long newPrice = recalculatePrice(goods, maxVolumeCorrectedWithVolumeRatio);
            goods.setPrice(newPrice);
        }
    }

    private long recalculatePrice(Goods goods, float maxVolume) {
        float currentVolume = goods.getProduct().getVolume() * goods.getAmount();
        return (long) (goods.getProduct().getPrice() / (currentVolume / maxVolume));
    }

    private boolean addAmount(Product product, Long amount, Goods goods, Station station) {
            float freeVolume = spaceTechEngine.calculateFreeAvailableVolume(station);
            float producedGoodsVolume = product.getVolume() * amount;
            if (freeVolume >= producedGoodsVolume) {
                goods.setAmount(goods.getAmount() + amount);
                return true;
            } else {
                goods.setAmount(goods.getAmount() + (int) (freeVolume/product.getVolume()));
                return false;
            }
    }

    private static Float retrieveRandomInRange(double from, double to) {
        double f = Math.random() / Math.nextDown(1.0);
        double x = from * (1.0 - f) + to * f;
        return (float) x;
    }
}