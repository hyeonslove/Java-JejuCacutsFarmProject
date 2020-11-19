package Entity;

import java.io.Serializable;

public class CactusForm implements Serializable {
    private String title;
    private int price;

    public CactusForm(String title, int price) {
        this.title = title;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setTitle(int price) {
        this.price = price;
    }


}
