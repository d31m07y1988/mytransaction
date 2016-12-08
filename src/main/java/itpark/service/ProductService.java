package itpark.service;

import itpark.model.Customer;
import itpark.model.Product;

import java.util.List;

/**
 * Created by Ramil on 08.12.2016.
 */
public interface ProductService {

    List<Product> findAll();
    Product get(int id);
    List<Product> getByName(String productName);
    void add(Product product);
    void buy(Product product, Customer customer, int soldCount);
}
