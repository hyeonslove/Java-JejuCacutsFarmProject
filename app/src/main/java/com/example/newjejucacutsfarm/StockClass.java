package com.example.newjejucacutsfarm;

import java.io.Serializable;
import java.util.ArrayList;

public class StockClass implements Serializable {
    private static final long serialVersionUID = -514235523541L;
    private ArrayList<DateClass> list = new ArrayList<>();

    public StockClass(){ }
    public ArrayList<DateClass> getList(){
        return list;
    }
    public int addDate(DateClass date) { // 날짜를 등록
        for(int i=0;i<list.size();i++) {
            if(list.get(i).compareN(date) == 0) {
                return -1;
            }else if(list.get(i).compareN(date) == 1) {
                list.add(i,date);
                System.out.println(date.toString());
                return i;
            }
        }
        list.add(date);
        return list.size() - 1;
    }
    public DateClass get(int idx) {
        return list.get(idx);
    }
}