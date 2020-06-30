package com.example.newjejucacutsfarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class SubActivity extends AppCompatActivity {
    public static StockClass datalist;
    Gson gson = new Gson();
    String json = new String();
    final static String filename = "mnt/sdcard/stock/stocklist.ini";
    ArrayList<ResCactusForm> _printList = new ArrayList<>();
    ListView printLv;
    PrintListViewAdapter ResCactusListViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 가로모드 고정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 없앰(전체화면)
        setContentView(R.layout.activity_sub);
        _printList = (ArrayList<ResCactusForm>) getIntent().getSerializableExtra("key");
        ResCactusListViewAdapter = new PrintListViewAdapter();
        printLv = (ListView)findViewById(R.id.printLv);
        printLv.setAdapter(ResCactusListViewAdapter);
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        System.out.println(_printList.size() + " 개임");
        for (int i = 0; i < _printList.size(); i++) {
            String price1 = myFormatter.format(Integer.parseInt(_printList.get(i).getPrice()));
            String sum1 = myFormatter.format(Integer.parseInt(_printList.get(i).getSum()));
            ResCactusListViewAdapter.addItem(_printList.get(i).getTitle(), _printList.get(i).getCnt() + " 개", price1 + " 원", sum1 + " 원");
        } ResCactusListViewAdapter.notifyDataSetChanged();
        cal();
    }


    public void cal(){
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        int box=0,sum=0;
        for(int i=0;i<_printList.size();i++){
            box+=Integer.parseInt(_printList.get(i).getCnt());
            sum+=Integer.parseInt(_printList.get(i).getSum());
        }
        String sum1 = myFormatter.format(sum);
        setTitle("농협 - 최창수 (217-05252-005911)     [" + box + "]박스 [" + sum1 + "]원");
    }
    public void printButton_onClick(View view){
        String sendText="";

        for(int i=0;i<_printList.size();i++){
            sendText= sendText + _printList.get(i).getTitle() + " " + _printList.get(i).getCnt() + " " + _printList.get(i).getPrice() + " " + _printList.get(i).getSum() + "!";
        }
        try {
            ((MainActivity) MainActivity.mContext).sendData(sendText);
            ((MainActivity) MainActivity.mContext).toastSend("인쇄요청을 성공하였습니다.",3, Toast.LENGTH_LONG, Gravity.CENTER,0,0);
        }catch(Exception e){
            ((MainActivity) MainActivity.mContext).toastSend("블루투스 연결이 끊겼습니다. 다시 시도해주세요",2, Toast.LENGTH_LONG, Gravity.TOP,0,40);
            ((MainActivity) MainActivity.mContext).setTitle("제주농원 영수중 for Android 페어링 실패");
            ((MainActivity) MainActivity.mContext).bluetoothconnection = false;
        }
        finish();
    }
    public void backButton_onClick(View view){
        finish();
    }
}
