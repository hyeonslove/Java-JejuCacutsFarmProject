package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jejufarmreceiptproject.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import Entity.CactusForm;

public class CactusListViewAdapter extends BaseAdapter {
    private ArrayList<CactusForm> list;

    public CactusListViewAdapter(){
        list = new ArrayList<>();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        CactusForm listItem = list.get(i);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.control_cactuslistview, viewGroup, false);

        }
        DecimalFormat df = new DecimalFormat("#,###");

        TextView title = (TextView) view.findViewById(R.id.titleText);
        TextView price = (TextView) view.findViewById(R.id.priceText);

        title.setText(String.valueOf(listItem.getTitle()));
        price.setText(df.format(listItem.getPrice()));

        return view;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i).getTitle() + "    " + list.get(i).getPrice() + "    " + list.get(i).getKey();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void clear(){
        list.clear();
    }

    public void append(String key, String title, int price){
        list.add(new CactusForm(key, title, price));
    }
}
