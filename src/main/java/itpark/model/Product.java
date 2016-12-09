package itpark.model;

/**
 * Created by Ramil on 08.12.2016.
 */
public class Product {
    private int id;
    private String name;
    private int count;
    private int price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "id: " + id + ", " + name + ", кол-во: " + count + ", цена:" + price;
    }
}
