package io.solar.mapper;

import io.solar.entity.Product;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class ProductMapper implements DbMapper<Product> {

    @Override
    public Product map(SafeResultSet resultSet) {
        Product out = new Product();

        out.setId(resultSet.getLong("id"));
        out.setTitle(resultSet.getString("title"));
        out.setImage(resultSet.getString("image"));
        out.setBulk(resultSet.getFloat("bulk"));
        out.setMass(resultSet.getFloat("mass"));
        out.setPrice(resultSet.getFloat("price"));

        return out;
    }
}