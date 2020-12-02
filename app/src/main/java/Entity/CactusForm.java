package Entity;

import java.io.Serializable;

public class CactusForm implements Serializable,Cloneable {
    private int index;
    private String title;
    private int price;

    public CactusForm(int index, String title, int price) {
        this.index = index;
        this.title = title;
        this.price = price;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
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
