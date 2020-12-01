package Entity;

import java.io.Serializable;

public class CactusForm implements Serializable {
    private int index;
    private String title;
    private int price;

    public CactusForm(int index, String title, int price) {
        this.index = index;
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

    public int getIndex(){
        return this.index;
    }

    public void setIndex(int index){
        this.index = index;
    }
}
