package com.example.newjejucacutsfarm;


import java.io.Serializable;
public class CactusData implements Serializable {
    private static final long serialVersionUID = -514235523541L;
    private String title, cnt, price, sum;
    CactusData(){}
    public CactusData(CactusData data) {
        this.title = data.title;
        this.cnt = data.cnt;
        this.price = data.price;
        this.sum = data.sum;

    }
    public CactusData(String title, String cnt, String price, String sum) {
        this.title = title;
        this.cnt = cnt;
        this.price = price;
        this.sum = sum;
    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return title + " " + cnt + " " + price + " " + sum;
    }
}