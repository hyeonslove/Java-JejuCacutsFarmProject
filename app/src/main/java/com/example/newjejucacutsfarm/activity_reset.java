package com.example.newjejucacutsfarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class activity_reset extends AppCompatActivity {
    ArrayList<CactusForm> list = new ArrayList<>();
    static int listn=-1;
    EditText indexText;
    EditText nameText;
    EditText cntText;
    ListView cactuslv;
    Button saveButton;
    ListViewAdapter CactusListViewAdapter;

    public void resetListView(int n){
        CactusListViewAdapter.clear();
        for (int i = 0; i < list.size(); i++) {
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            String pricestr = myFormatter.format(Integer.parseInt(list.get(i).getPrice()));
            CactusListViewAdapter.addItem(list.get(i).getTitle(), pricestr + " 원");
        }
        CactusListViewAdapter.notifyDataSetChanged();
        if( n > 0)
            cactuslv.setSelection(n - 1);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 가로모드 고정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 없앰(전체화면)
        setContentView(R.layout.activity_reset);
        nameText = findViewById(R.id.nameText);
        cntText = findViewById(R.id.cntText);
        saveButton = findViewById(R.id.saveButton);
        indexText = findViewById(R.id.indexText);
        InputStream in = null;
        BufferedInputStream bin=null;
        ObjectInputStream oin = null;
        try{
            in = new FileInputStream("mnt/sdcard/cactus.txt");
            bin = new BufferedInputStream(in);
            oin = new ObjectInputStream(bin);
            list = (ArrayList<CactusForm>)oin.readObject();
            in.close();
            bin.close();
            oin.close();        }catch(Exception e){
            e.printStackTrace();
        }
        //cactuslv 리스트뷰
        cactuslv = (ListView) findViewById(R.id.cactuslv);
        CactusListViewAdapter = new ListViewAdapter();
        cactuslv.setAdapter(CactusListViewAdapter);
        cactuslv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object vo = (Object) adapterView.getAdapter().getItem(i);  //리스트뷰의 포지션 내용을 가져옴.
                listn=i;
                nameText.setText(vo.toString().split(" ")[0]);
                cntText.setText(vo.toString().split(" ")[1].replace(",",""));
            }
        });
        resetListView(0);
    }

    public void addButton_onClick(View view){
        String namestr = nameText.getText().toString();
        String cntstr= cntText.getText().toString();
        if(namestr.compareTo("") == 0){
            ((MainActivity)MainActivity.mContext).toastSend("이름을 제대로 입력해주세요.",2,Toast.LENGTH_SHORT,Gravity.TOP,0,40);
            return;
        }
        if(namestr.contains(" ") == true){
            ((MainActivity)MainActivity.mContext).toastSend("제품이름에 띄어쓰기는 입력하실 수 없습니다.",2,Toast.LENGTH_SHORT,Gravity.TOP,0,40);
            return;
        }
        if(cntstr.contains("단가") == true || cntstr.contains("단") == true || cntstr.compareTo("") == 0){
            ((MainActivity)MainActivity.mContext).toastSend("단가는 숫자만 입력가능합니다.",2,Toast.LENGTH_SHORT,Gravity.TOP,0,40);
            return;
        }
        for(int i=0;i<list.size();i++){
            if(list.get(i).getTitle().compareTo(namestr) == 0){
                ((MainActivity) MainActivity.mContext).toastSend("이미 존재하는 물품입니다",2f, Toast.LENGTH_SHORT, Gravity.TOP,0,40);
                return;
            }
        }
        if(indexText.getText().toString().compareTo("") == 0) {
            list.add(new CactusForm(namestr, cntstr));
        }else{
            int index = Integer.parseInt(indexText.getText().toString());
            list.add(index,new CactusForm(namestr, cntstr));
        }
        nameText.setText("");
        cntText.setText("");
        indexText.setText("");
        listn=-1;
        resetListView(CactusListViewAdapter.getCount());
    }

    public void saveButton_onClick(View view){
        if(listn < 0){
            ((MainActivity)MainActivity.mContext).toastSend("수정 할 품목을 선택해주세요.",2,Toast.LENGTH_SHORT,Gravity.TOP,0,40);
            return;
        }
        String namestr = nameText.getText().toString();
        String cntstr= cntText.getText().toString();
        if(namestr.contains(" ") == true){
            ((MainActivity)MainActivity.mContext).toastSend("제품이름에 띄어쓰기는 입력하실 수 없습니다.",2,Toast.LENGTH_SHORT,Gravity.TOP,0,40);
            return;
        }
        if(cntstr.contains("단가") == true || cntstr.contains("단") == true || cntstr.compareTo("") == 0){
            ((MainActivity)MainActivity.mContext).toastSend("단가는 숫자만 입력가능합니다.",2,Toast.LENGTH_SHORT,Gravity.TOP,0,40);
            return;
        }
        for(int i=0;i<list.size();i++){
            if(list.get(i).getTitle().compareTo(namestr) == 0 && i != listn){
                ((MainActivity) MainActivity.mContext).toastSend("이미 존재하는 물품입니다",2f, Toast.LENGTH_SHORT, Gravity.TOP,0,40);
                return;
            }
        }
        //cactuslv.clearChoices();
        list.remove(listn);
        list.add(listn,new CactusForm(namestr,cntstr));
        nameText.setText("");
        cntText.setText("");
        resetListView(0);
        indexText.setText("");
        listn=-1;
    }

    public void delButton_onClick(View view){
        if(listn < 0){
            ((MainActivity)MainActivity.mContext).toastSend("삭제 할 품목을 선택해주세요.",2,Toast.LENGTH_SHORT,Gravity.TOP,0,40);
            return;
        }else {
            list.remove(listn);
            nameText.setText("");
            cntText.setText("");
            resetListView(0);
            listn=-1;
            indexText.setText("");
        }
    }

    public void finishButton_onClick(View view){
        OutputStream out = null;
        BufferedOutputStream bout = null;
        ObjectOutputStream oout = null;

        try{
            out = new FileOutputStream("mnt/sdcard/cactus.txt");
            bout = new BufferedOutputStream(out);
            oout = new ObjectOutputStream(bout);
            oout.writeObject(list);
            oout.close();
            bout.close();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        ((MainActivity)MainActivity.mContext).callCactusData();
        ((MainActivity)MainActivity.mContext).callCactusList();
        finish();
    }

}
