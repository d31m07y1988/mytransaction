package itpark.repository;

import itpark.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository("customerRepository")
public class JdbcCustomerRepositoryImpl implements CustomerRepository {
    private JdbcTemplate jdbcTemplate;
    private static final RowMapper<Customer> customerRowMapper = (resultSet, i) -> {
        Customer customer = new Customer();
        customer.setId(resultSet.getInt("id"));
        customer.setName(resultSet.getString("name"));
        customer.setBalance(resultSet.getInt("balance"));
        return customer;
    };

    @Autowired
    public void setJdbcTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }



    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.query("SELECT * FROM customers", customerRowMapper);
    }

    @Override
    public Customer get(int id) {
        List<Customer> customers = jdbcTemplate.query("SELECT * FROM customers WHERE id=?", customerRowMapper, id);

        return DataAccessUtils.singleResult(customers);
    }

    @Override
    public Customer getByName(String exactName) {
        List<Customer> customers = jdbcTemplate.query("SELECT * FROM customers WHERE name=?", customerRowMapper, exactName);
        return DataAccessUtils.singleResult(customers);
    }

    @Override
    public void add(Customer customer) {
        jdbcTemplate.update("INSERT INTO customers(name, balance) VALUES (?, ?)",
                customer.getName(), customer.getBalance());
    }

    @Override
    public void update(Customer customer) {
        jdbcTemplate.update("UPDATE customers SET balance = ? WHERE id = ?",
                customer.getBalance(), customer.getId());
    }
}
