package io.solar.mapper;

import io.solar.entity.Product;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class ProductMapper implements DbMapper<Product> {

    @Override
    public Product map(SafeResultSet resultSet) {
        Product out = new Product();

        out.setId(resultSet.fetchLong("id"));
        out.setTitle(resultSet.getString("title"));
        out.setImage(resultSet.getString("image"));
        out.setBulk(resultSet.fetchFloat("bulk"));
        out.setMass(resultSet.fetchFloat("mass"));
        out.setPrice(resultSet.fetchFloat("price"));

        return out;
    }
}