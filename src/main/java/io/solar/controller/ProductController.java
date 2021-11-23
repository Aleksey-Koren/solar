package io.solar.controller;

import io.solar.entity.Product;
import io.solar.entity.User;
import io.solar.mapper.ProductMapper;
import io.solar.mapper.TotalMapper;
import io.solar.utils.Option;
import io.solar.utils.Page;
import io.solar.utils.QueryUtils;
import io.solar.utils.context.AuthData;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.Pageable;
import io.solar.utils.server.controller.PathVariable;
import io.solar.utils.server.controller.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/api/product")
public class ProductController {

    @PreAuthorize("hasAuthority('EDIT_PRODUCT')")
    @PostMapping
    public Product save(@RequestBody Product product, @AuthData User user, Transaction transaction) {
        Query save = null;
        if (product.getId() != null) {
            Query query = transaction.query("select * from products where id = :id");
            query.setLong("id", product.getId());
            List<Product> existing = null;

            if (existing.size() == 1) {
                save = transaction.query("UPDATE products set title=:title,image=:image,bulk=:bulk," +
                        "mass=:mass,price=:price where id=:id");
                save.setLong("id", product.getId());
            } else {
                log.error("can't find planet with id: " + product.getId());
            }
        } else {
            save = transaction.query("insert into products (title, image, bulk, mass, price)" +
                    " values (:title, :image, :bulk, :mass, :price)");
        }
        if (save != null) {
            save.setString("title", product.getTitle());
            save.setString("image", product.getImage());
            save.setFloat("bulk", product.getBulk());
            save.setFloat("mass", product.getMass());
            save.setFloat("price", product.getPrice());

            save.execute();
            if (product.getId() == null) {
                product.setId(save.getLastGeneratedKey(Long.class));
            }
        } else {
            throw new RuntimeException("Can't save or update product");
        }
        return product;
    }




//    @PreAuthorize("hasAuthority('EDIT_PRODUCT')")
//    @PostMapping
//    public Product save(@RequestBody Product product, @AuthData User user, Transaction transaction) {
//        if (!AuthController.userCan(user, "edit-product", transaction)) {
//            throw new RuntimeException("no privileges");
//        }
//        Query save = null;
//        if (product.getId() != null) {
//            Query query = transaction.query("select * from products where id = :id");
//            query.setLong("id", product.getId());
//            List<Product> existing = query.executeQuery(new ProductMapper());
//
//            if (existing.size() == 1) {
//                save = transaction.query("UPDATE products set title=:title,image=:image,bulk=:bulk," +
//                        "mass=:mass,price=:price where id=:id");
//                save.setLong("id", product.getId());
//            } else {
//                log.error("can't find planet with id: " + product.getId());
//            }
//        } else {
//            save = transaction.query("insert into products (title, image, bulk, mass, price)" +
//                    " values (:title, :image, :bulk, :mass, :price)");
//        }
//        if (save != null) {
//            save.setString("title", product.getTitle());
//            save.setString("image", product.getImage());
//            save.setFloat("bulk", product.getBulk());
//            save.setFloat("mass", product.getMass());
//            save.setFloat("price", product.getPrice());
//
//            save.execute();
//            if (product.getId() == null) {
//                product.setId(save.getLastGeneratedKey(Long.class));
//            }
//        } else {
//            throw new RuntimeException("Can't save or update product");
//        }
//        return product;
//    }

    @RequestMapping("{id}")
    public Product get(@PathVariable("id") Long id, Transaction transaction) {
        Query query = transaction.query("select * from products where id = :id");
        query.setLong("id", id);
        List<Product> existing = null;
        return existing.size() == 1 ? existing.get(0) : null;
    }


    @RequestMapping
    public Page<Product> getAll(Pageable pageable, Transaction transaction) {
        Query countQ = transaction.query("select count(1) from products");
        Long count = countQ.executeQuery(new TotalMapper()).get(0);
        if(count == 0) {
            return Page.empty();
        }

        Query query = transaction.query("select * from products limit :skip, :pageSize");
        QueryUtils.applyPagination(query, pageable);
        List<Product> existing = null;
        return new Page<>(existing, count);
    }

    @RequestMapping("utils/dropdown")
    public List<Option> dropdown(Transaction transaction) {
        return getAll(new Pageable(0, 9999999), transaction).getContent()
                .stream()
                .map(v -> new Option(v.getId(), v.getTitle()))
                .collect(Collectors.toList());
    }


    @RequestMapping(value = "{id}")
    public void delete(@PathVariable("id") Long id, @AuthData User user, Transaction transaction) {
        if (!AuthController.userCan(user, "edit-product", transaction)) {
            throw new RuntimeException("no privileges");
        }
        Query query = transaction.query("delete from products where id = :id");
        query.setLong("id", id);
        query.execute();
    }


}
