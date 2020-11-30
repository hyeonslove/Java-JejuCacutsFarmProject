package com.example.jejufarmreceiptproject;

import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.ini4j.Ini;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import Adapter.CactusListViewAdapter;

public class EditActivity extends AppCompatActivity {
    private int max_product;
    private int max_key = 0;

    private Button editButton;
    private Button deleteButton;
    /////////////////////////////////////////////////
    //          CactusListView Text 구현           //
    /////////////////////////////////////////////////
    private EditText titleText;
    private EditText priceText;
    private EditText indexText;
    private ListView cactusListView;
    private CactusListViewAdapter cactusListViewAdapter;

    private Ini ini;

    private void Init_CacutsListView() {
        cactusListViewAdapter = new CactusListViewAdapter();
        titleText = findViewById(R.id.EditTitleText);
        priceText = findViewById(R.id.EditPriceText);
        indexText = findViewById(R.id.EditIndexText);

        cactusListView = findViewById(R.id.cactusListView);
        cactusListView.setAdapter(cactusListViewAdapter);

        cactusListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String vo = (String)adapterView.getAdapter().getItem(i);  //리스트뷰의 포지션 내용을 가져옴.
                titleText.setText(vo.split("    ")[0]);
                priceText.setText(vo.split("    ")[1]);
                indexText.setText(vo.split("    ")[2].split("Cactus")[1]);
                if(editButton.getText().equals("추가")){
                    editButton.setText("수정");
                }
            }
        });
        max_product = Integer.parseInt(ini.get("ProgramSettings", "MAX_PRODUCT"));
        for (int i = 0; i < max_product; i++) {
            String obj = ini.get("CactusList","Cactus" + i);
            if(obj != null) {
                if(i > max_key)
                    max_key = i;
                cactusListViewAdapter.append("Cactus" + i, obj.split(" ")[0], Integer.parseInt(obj.split(" ")[1]));
                System.out.println(obj);
            }
        }
        cactusListViewAdapter.notifyDataSetChanged();
        indexText.setText((max_key + 1) + "");
    }

    private void init() {
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        iniSetting();
        Init_CacutsListView();
    }

    private void iniSetting() {
        AssetManager aMgr = getResources().getAssets();
        try {
            ini = new Ini(aMgr.open("setting.ini"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 가로모드 고정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 없앰(전체화면)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_edit);

        init();
    }


    //region Button Event

    public void editButton_onClick(View view) {
        if (editButton.getText().equals("추가")) {

        } else if (editButton.getText().equals("수정")) {


            editButton.setText("추가");
            indexText.setText((max_key + 1) + "");
        }
    }

    public void deleteButton_onClick(View view) {

    }

    public void checkEditButton_onClick(View view) {
        try {
            ini.store();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            finish();
        }
    }

    public void cancelEditButton_onClick(View view) {
        finish();
    }

    //endregion
}