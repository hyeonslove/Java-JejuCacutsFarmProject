package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jejufarmreceiptproject.R;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

import Entity.CactusForm;

public class CactusListViewAdapter extends BaseAdapter implements Serializable {
    private ArrayList<CactusForm> list;
    public int layout = R.layout.control_cactuslistview;

    public CactusListViewAdapter(){
        list = new ArrayList<>();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        CactusForm listItem = list.get(i);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(layout, viewGroup, false);

        }
        DecimalFormat df = new DecimalFormat("#,###");
        if(layout == R.layout.control_cactus_indexlistview){
            TextView index = (TextView) view.findViewById(R.id.indexText);
            index.setText(listItem.getIndex() + "");
        }
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
        return list.get(i).getTitle() + "    " + list.get(i).getPrice() + "    " + list.get(i).getIndex();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void clear(){
        list.clear();
    }

    public void append(int index, CactusForm item){
        list.add(index, item);
    }

    public void append(int index, String title, int price){
        list.add(new CactusForm(index, title, price));
    }

    public ArrayList<CactusForm> getList(){
        return this.list;
    }
}
