package itpark.service;

import itpark.model.Customer;
import itpark.model.Product;
import itpark.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Ramil on 08.12.2016.
 */
@Service("productService")
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product get(int id) {
        return productRepository.get(id);
    }

    @Override
    public List<Product> getByName(String productName) {
        return productRepository.getByName(productName);
    }

    @Override
    @Transactional
    public void add(Product product) {
        productRepository.add(product);
    }

    @Override
    @Transactional
    public void buy(Product product, Customer customer, int soldCount) {
        if (soldCount < 0) {
            throw new IllegalTransactionStateException("списание отрицательной значения не возможно");
        }
        if (product.getCount() < soldCount) {
            throw new IllegalTransactionStateException("остаток на счету меньше суммы списания");
        }
        product.setCount(product.getCount()-soldCount);
        productRepository.update(product);
        int amount = product.getPrice() * soldCount;
        customerService.transfer(customer,customerService.getByName("Shop"), amount);
    }

}
