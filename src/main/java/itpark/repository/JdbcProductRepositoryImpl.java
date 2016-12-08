package itpark.repository;

import itpark.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository("productRepository")
public class JdbcProductRepositoryImpl implements ProductRepository{

    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Product> productRowMapper = (resultSet, i) -> {
        Product product = new Product();
        product.setId(resultSet.getInt("id"));
        product.setName(resultSet.getString("name"));
        product.setCount(resultSet.getInt("count"));
        product.setPrice(resultSet.getInt("price"));
        return product;
    };

    @Autowired
    public void setJdbcTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Product> findAll() {
        return jdbcTemplate.query("SELECT * FROM products", productRowMapper);
    }

    @Override
    public Product get(int id) {
        List<Product> products = jdbcTemplate.query("SELECT * FROM products WHERE id=?", productRowMapper, id);
        return DataAccessUtils.singleResult(products);
    }

    @Override
    public List<Product> getByName(String productName) {
        return jdbcTemplate.query("SELECT * FROM products WHERE name LIKE ?", productRowMapper, "%"+productName+"%");
    }

    @Override
    public void add(Product product) {
        jdbcTemplate.update("INSERT INTO products(name, count, price) VALUES (?, ?, ?)",
                product.getName(), product.getCount(), product.getPrice());
    }

    @Override
    public void update(Product product) {
        jdbcTemplate.update("UPDATE products SET count = ? WHERE id = ?",
                product.getCount(), product.getId());
    }
}
