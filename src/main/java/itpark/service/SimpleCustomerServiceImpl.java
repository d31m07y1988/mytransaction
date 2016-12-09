package itpark.service;

import itpark.model.Customer;
import itpark.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("customerService")
public class SimpleCustomerServiceImpl implements CustomerService {
    private CustomerRepository customerRepository;

    @Autowired
    public SimpleCustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer get(int id) {
        return customerRepository.get(id);
    }

    @Override
    public Customer getByName(String exactName) {
        return customerRepository.getByName(exactName);
    }

    @Override
    @Transactional
    public void add(Customer customer) {
        customerRepository.add(customer);
    }

    @Override
    @Transactional
    public void withdraw(Customer customer, int amount)  {
        if(customer==null) {
            throw new IllegalTransactionStateException("пользователь для списания не найден");
        }
        if (amount < 0) {
            throw new IllegalTransactionStateException("списание отрицательной значения не возможно");
        }
        if (customer.getBalance() < amount) {
            throw new IllegalTransactionStateException("остаток на счету меньше суммы списания");
        }
        customer.setBalance(customer.getBalance() - amount);
        customerRepository.update(customer);

    }

    @Override
    @Transactional
    public void deposit(Customer customer, int amount) {
        if(customer==null) {
            throw new IllegalTransactionStateException("пользователь для списания не найден");
        }
        if (amount < 0) {
            throw new IllegalTransactionStateException("зачисление отрицательной значения не возможно");
        }
        customer.setBalance(customer.getBalance() + amount);
        //throw new RuntimeException("Something bad happened");
        customerRepository.update(customer);
    }

    @Override
    @Transactional
    public void transfer(Customer sender, Customer receiver, int amount) {
        withdraw(sender, amount);
        deposit(receiver, amount);
    }
}
