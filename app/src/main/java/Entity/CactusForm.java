package Entity;

import java.io.Serializable;

public class CactusForm implements Serializable {
    private String key;
    private String title;
    private int price;

    public CactusForm(String key, String title, int price) {
        this.key = key;
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

    public void setPrice(int price) {
        this.price = price;
    }

    public String getKey(){
        return this.key;
    }

    public void setKey(String key){
        this.key = key;
    }
}
