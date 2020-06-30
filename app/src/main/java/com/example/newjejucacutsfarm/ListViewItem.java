package com.example.newjejucacutsfarm;

import android.content.Context;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.io.Serializable;
import java.util.ArrayList;


class CactusForm implements Serializable {
    private static final long serialVersionUID = -7946104941769296534L;
    private String title, price;
    CactusForm(){}
    public CactusForm(String title, String price) {
        this.title = title;
        this.price = price;
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
}

class ListViewAdapter extends BaseAdapter {
    private ArrayList<CactusForm> arrayList;

    private int nId;

    public ListViewAdapter() {
        arrayList = new ArrayList<>();
        nId = 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        CactusForm listItem = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.cactuslist_layout, viewGroup, false);
        }
        TextView title = (TextView) view.findViewById(R.id.titleText);
        TextView price = (TextView) view.findViewById(R.id.priceText);
        title.setText(String.valueOf(listItem.getTitle()));
        price.setText(listItem.getPrice());

        return view;
    }

    public void clear() {
        arrayList.clear();
    }

    public void remove(int cnt) {
        arrayList.remove(cnt);
    }

    public void addItem(String name, String price) {
        CactusForm item = new CactusForm();
        item.setTitle(name);
        item.setPrice(price);

        arrayList.add(item);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i).getTitle() + " " + arrayList.get(i).getPrice();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}

class ResCactusForm implements Serializable {
    private static final long serialVersionUID = -7946104941769296534L;
    private String title, cnt, price, sum;
    ResCactusForm(){}
    public ResCactusForm(Parcel in){}
    public ResCactusForm(String title, String cnt, String price, String sum) {
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
}

class ResListViewAdapter extends BaseAdapter {
    private ArrayList<ResCactusForm> arrayList;
    private int nId;
    public ResListViewAdapter() {
        arrayList = new ArrayList<>();
        nId = 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        ResCactusForm listItem = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.rescacutslist_layout, viewGroup, false);
        }
        TextView title = (TextView) view.findViewById(R.id.titleText);
        TextView cnt = (TextView) view.findViewById(R.id.cntText);
        TextView price = (TextView) view.findViewById(R.id.priceText);
        TextView sum = (TextView) view.findViewById(R.id.sumText);

        title.setText(String.valueOf(listItem.getTitle()));
        cnt.setText(String.valueOf(listItem.getCnt()));
        price.setText(listItem.getPrice());
        sum.setText(listItem.getSum());
        final String titleText = title.getText().toString();
        final String cntText = cnt.getText().toString().split(" ")[0].toString();
        final String priceText = price.getText().toString().split(" ")[0].replace(",","").toString();
        final String sumText = sum.getText().toString().split(" ")[0].replace(",","").toString();
        Button button = (Button) view.findViewById(R.id.delButton);
        button.setOnClickListener(new View.OnClickListener() { // 이거 final로하면 값 설정됨
            @Override
            public void onClick(View v) {
                ((MainActivity) MainActivity.mContext).delResList(titleText,cntText,priceText,sumText);
            }
        });
        return view;
    }

    public void addItem(String name, String cnt, String price, String sum) {
        ResCactusForm item = new ResCactusForm();
        item.setTitle(name);
        item.setPrice(price);
        item.setCnt(cnt);
        item.setSum(sum);

        arrayList.add(item);
    }

    public void clear() {
        arrayList.clear();
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i).getTitle();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}

class PrintListViewAdapter extends BaseAdapter {
    private ArrayList<ResCactusForm> arrayList;
    private int nId;
    public PrintListViewAdapter() {
        arrayList = new ArrayList<>();
        nId = 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        ResCactusForm listItem = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.print_layout, viewGroup, false);
        }
        TextView title = (TextView) view.findViewById(R.id.titleText);
        TextView cnt = (TextView) view.findViewById(R.id.cntText);
        TextView price = (TextView) view.findViewById(R.id.priceText);
        TextView sum = (TextView) view.findViewById(R.id.sumText);

        title.setText(String.valueOf(listItem.getTitle()));
        cnt.setText(String.valueOf(listItem.getCnt()));
        price.setText(listItem.getPrice());
        sum.setText(listItem.getSum());
        return view;
    }

    public void addItem(String name, String cnt, String price, String sum) {
        ResCactusForm item = new ResCactusForm();
        item.setTitle(name);
        item.setPrice(price);
        item.setCnt(cnt);
        item.setSum(sum);

        arrayList.add(item);
    }

    public void clear() {
        arrayList.clear();
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i).getTitle();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}