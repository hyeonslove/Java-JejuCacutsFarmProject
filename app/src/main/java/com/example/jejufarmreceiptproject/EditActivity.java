package com.example.jejufarmreceiptproject;

import android.content.Intent;
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
import Entity.CactusForm;

public class EditActivity extends AppCompatActivity {
    //region define
    public static Intent editActivityIntent;
    private int max_product;
    private int selected_index = -1;

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

    //endregion

    //region Init
    private void Init_CacutsListView() {
        cactusListViewAdapter = new CactusListViewAdapter();
        titleText = findViewById(R.id.EditTitleText);
        priceText = findViewById(R.id.EditPriceText);
        indexText = findViewById(R.id.EditIndexText);

        cactusListView = findViewById(R.id.cactusListView);
        cactusListViewAdapter.layout = R.layout.control_cactus_indexlistview;
        cactusListView.setAdapter(cactusListViewAdapter);

        cactusListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String vo = (String) adapterView.getAdapter().getItem(i);  //리스트뷰의 포지션 내용을 가져옴.
                titleText.setText(vo.split("    ")[0]);
                priceText.setText(vo.split("    ")[1]);
                indexText.setText(vo.split("    ")[2]);
                selected_index = Integer.parseInt(vo.split("    ")[2]);
                if (editButton.getText().equals("추가")) {
                    editButton.setText("수정");
                }
            }
        });
        max_product = Integer.parseInt(ini.get("ProgramSettings", "MAX_PRODUCT"));
        for (int i = 0; i < max_product; i++) {
            String obj = ini.get("CactusList", "Cactus" + i);
            if (obj != null) {
                cactusListViewAdapter.append(i, obj.split(" ")[0], Integer.parseInt(obj.split(" ")[1]));
                System.out.println(obj);
            }
        }
        cactusListViewAdapter.notifyDataSetChanged();
        indexText.setText(cactusListViewAdapter.getCount() + "");
    }

    private void init() {
        editActivityIntent = new Intent("editActivity");
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
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 가로모드 고정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 없앰(전체화면)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_edit);

        init();
    }

    @Override
    public void onBackPressed() {

    }

    //region Button Event

    public void editButton_onClick(View view) {
        if (editButton.getText().equals("추가")) {
            if (Integer.parseInt(indexText.getText().toString()) > cactusListViewAdapter.getCount()) {
                indexText.setText(cactusListViewAdapter.getCount() + "");
            } else if (Integer.parseInt(indexText.getText().toString()) == cactusListViewAdapter.getCount()) {
                cactusListViewAdapter.append(Integer.parseInt(indexText.getText().toString()), titleText.getText().toString(), Integer.parseInt(priceText.getText().toString()));
                titleText.setText("");
                priceText.setText("");
                indexText.setText(cactusListViewAdapter.getCount() + "");
            } else {
                int index = Integer.parseInt(indexText.getText().toString());
                cactusListViewAdapter.append(index, new CactusForm(index, titleText.getText().toString(), Integer.parseInt(priceText.getText().toString())));
                for (int i = index + 1; i < cactusListViewAdapter.getCount(); i++) {
                    cactusListViewAdapter.getList().get(i).setIndex(i);
                }
            }
        } else if (editButton.getText().equals("수정")) {
            if (selected_index != Integer.parseInt(indexText.getText().toString())) {
                // Index가 달라짐 -> 위치변경 필요
                int price;
                String title;
                int pres_index = Integer.parseInt(indexText.getText().toString());
                if (pres_index >= cactusListViewAdapter.getCount()) {
                    pres_index = cactusListViewAdapter.getCount() - 1;
                }
                title = cactusListViewAdapter.getList().get(pres_index).getTitle();
                price = cactusListViewAdapter.getList().get(pres_index).getPrice();

                cactusListViewAdapter.getList().get(pres_index).setTitle(titleText.getText().toString());
                cactusListViewAdapter.getList().get(pres_index).setPrice(Integer.parseInt(priceText.getText().toString()));

                cactusListViewAdapter.getList().get(selected_index).setTitle(title);
                cactusListViewAdapter.getList().get(selected_index).setPrice(price);

            } else {
                cactusListViewAdapter.getList().get(selected_index).setTitle(titleText.getText().toString());
                cactusListViewAdapter.getList().get(selected_index).setPrice(Integer.parseInt(priceText.getText().toString()));
            }
            titleText.setText("");
            priceText.setText("");
            editButton.setText("추가");
            indexText.setText(cactusListViewAdapter.getCount() + "");
        }
        cactusListViewAdapter.notifyDataSetChanged();

    }

    public void deleteButton_onClick(View view) {

    }

    public void checkEditButton_onClick(View view) {
        try {
            editActivityIntent.putExtra("cactus_list", cactusListViewAdapter);
            sendBroadcast(editActivityIntent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            finish();
        }
    }

    public void cancelEditButton_onClick(View view) {
        finish();
    }

    //endregion
}