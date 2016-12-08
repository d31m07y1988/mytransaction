package itpark.repository;

import itpark.model.Customer;

import java.util.List;

/**
 * Created by user on 06.12.16.
 */
public interface CustomerRepository {
    List<Customer> findAll();
    Customer get(int id);
    void add(Customer customer);
    void update(Customer customer);
}
