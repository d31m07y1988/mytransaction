package itpark.service;

import itpark.model.Customer;

import java.util.List;

public interface CustomerService {
    List<Customer> findAll();
    Customer get(int id);
    Customer getByName(String exactName);
    void add(Customer customer);
    void withdraw(Customer customer, int amount);
    void deposit(Customer customer, int amount);
    void transfer(Customer sender, Customer receiver, int amount);
}
