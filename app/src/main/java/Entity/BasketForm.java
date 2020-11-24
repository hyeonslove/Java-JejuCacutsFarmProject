package Entity;

import java.io.Serializable;

public class BasketForm implements Serializable {
    private String title;
    private int count;
    private int price;
    private int total;

    public BasketForm(){
        title = "";
        count = 0;
        price = 0;
        total = 0;
    }

    public BasketForm(String title, int count, int price, int total) {
        this.title = title;
        this.count = count;
        this.price = price;
        this.total = total;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setTitle(int price) {
        this.price = price;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
