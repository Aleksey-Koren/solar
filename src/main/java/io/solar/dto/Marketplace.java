package io.solar.dto;

import io.solar.entity.Inventory;
import io.solar.entity.Product;
import lombok.Getter;

import java.util.List;

@Getter
public class Marketplace {
    List<Inventory> ships;
    List<Inventory> inventory;
    List<Product> goods;

    public Marketplace(List<Inventory> ships, List<Inventory> inventory, List<Product> goods) {
        this.ships = ships;
        this.inventory = inventory;
        this.goods = goods;
    }
}
