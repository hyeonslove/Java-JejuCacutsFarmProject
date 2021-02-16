package com.example.jejufarmreceiptproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.ini4j.Ini;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

import Adapter.BasketListViewAdapter;
import Adapter.CactusListViewAdapter;
import Entity.CactusForm;
import SQLite.SQLiteControl;
import SQLite.SQLiteHelper;

public class EditActivity extends AppCompatActivity {
    //region define
    /////////////////////////////////////////////////
    public static Intent editActivityIntent;
    private Button editButton;
    /////////////////////////////////////////////////
    //          CactusListView Text 구현           //
    /////////////////////////////////////////////////
    private EditText titleText;
    private EditText priceText;
    private EditText indexText;
    private ListView cactusListView;
    private CactusListViewAdapter cactusListViewAdapter;
    /////////////////////////////////////////////////
    private Ini ini;

    private SQLiteHelper helper;
    private SQLiteControl sqlite;
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
                CactusForm vo = (CactusForm) adapterView.getAdapter().getItem(i);  //리스트뷰의 포지션 내용을 가져옴.
                indexText.setText(vo.getIndex() + "");
                titleText.setText(vo.getTitle());
                priceText.setText(vo.getPrice() + "");
                if (editButton.getText().equals("추가")) {
                    indexText.setEnabled(false);
                    editButton.setText("수정");
                }
            }
        });

        ReadCacutsListDB();

        cactusListViewAdapter.notifyDataSetChanged();
        indexText.setText(sqlite.GetMaxUid() + "");
        cactusListView.requestFocusFromTouch();
    }

    private void init() {
        editActivityIntent = new Intent("editActivity");
        editButton = findViewById(R.id.editButton);
//        iniSetting();
        Init_CacutsListView();
    }

    private void iniSetting() {
        try {
            ini = new Ini(new FileInputStream("mnt/sdcard/Settings.ini"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //endregion

    private void ReadCacutsListDB() {
        cactusListViewAdapter.clear();
        ArrayList<CactusForm> temp = (ArrayList<CactusForm>) sqlite.GetData("SELECT * FROM CACTUSLIST ORDER BY cactus_uid;");
        for (CactusForm item : temp) {
            cactusListViewAdapter.append(item.getIndex(), item.getTitle(), item.getPrice());
        }
        cactusListViewAdapter.notifyDataSetChanged();
    }

    private void ReadCacutsListDB(int idx) {
        cactusListViewAdapter.clear();
        ArrayList<CactusForm> temp = (ArrayList<CactusForm>) sqlite.GetData("SELECT * FROM CACTUSLIST ORDER BY cactus_uid;");
        for (CactusForm item : temp) {
            cactusListViewAdapter.append(item.getIndex(), item.getTitle(), item.getPrice());
        }
        cactusListViewAdapter.notifyDataSetChanged();
        cactusListView.setSelection(idx);
    }

    public void toastSend(String text, float textsize, int showtime, int postition, int offsetX, int offsetY) {
        SpannableStringBuilder biggerText = new SpannableStringBuilder(text);
        biggerText.setSpan(new RelativeSizeSpan(textsize), 0, text.length(), 0);
        Toast toast = Toast.makeText(getApplicationContext(), biggerText, showtime);
        toast.setGravity(postition, offsetX, offsetY);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 가로모드 고정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 없앰(전체화면)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_edit);
        helper = new SQLiteHelper(EditActivity.this,
                "data.db",
                null,
                1);
        sqlite = new SQLiteControl(helper);
        init();

    }

    @Override
    public void onBackPressed() {

    }

    //region Button Event

    public void editButton_onClick(View view) {
        if (indexText.getText().toString().equals("") || titleText.getText().length() < 2 || titleText.getText().toString().equals("") || priceText.getText().toString().equals("")) {
            toastSend("입력이 잘못되었습니다.", 1.5f, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
            return;
        }
        int temp_idx = Integer.parseInt(indexText.getText().toString());

        if (editButton.getText().equals("추가")) {
            sqlite.Insert(new CactusForm(Integer.parseInt(indexText.getText().toString()), titleText.getText().toString(), Integer.parseInt(priceText.getText().toString())));
            titleText.setText("");
            priceText.setText("");
            indexText.setText(sqlite.GetMaxUid() + "");
        } else if (editButton.getText().equals("수정")) {
            sqlite.Update(new CactusForm(Integer.parseInt(indexText.getText().toString()), titleText.getText().toString(), Integer.parseInt(priceText.getText().toString())));

            titleText.setText("");
            priceText.setText("");
            editButton.setText("추가");
            indexText.setEnabled(true);
            indexText.setText(sqlite.GetMaxUid() + "");
        }
        ReadCacutsListDB(temp_idx);
    }

    public void deleteButton_onClick(View view) {
        if (indexText.getText().toString().equals("")) {
            toastSend("입력이 잘못되었습니다.", 1.5f, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
            return;
        }

        if (cactusListViewAdapter.getCount() < 1) {
            toastSend("삭제 할 제품이 없습니다.", 1.5f, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
            return;
        }
        sqlite.Delete(Integer.parseInt(indexText.getText().toString()));
        ReadCacutsListDB();
        indexText.setText(sqlite.GetMaxUid() + "");
        titleText.setText("");
        priceText.setText("");
        indexText.setEnabled(true);
        editButton.setText("추가");
    }

    public void developButton_onClick(View view){
        sqlite.ExecuteSQL("DELETE FROM CACTUSLIST");
        sqlite.ExecuteSQL("INSERT INTO CACTUSLIST(cactus_uid, cactus_name, cactus_price) VALUES (0, '설황(중)',40000),(1, '설황',44000),(2, '성성환',44000),(3, '설황2',30000),(4, '설황(대)',36000),(5, '성성환',35000),(6, '성성환1',40000),(7, '성성환2',42000),(8, '성성환3',45000),(9, '금황환',44000),(10, '금황환1',42000),(11, '성성환',44000),(12, '레오',40000),(13, '레오1',42000),(14, '오공',40000),(15, '오공1',44000),(16, '신천지',40000),(17, '용심목6',50000),(18, '용심목1',70000),(19, '용심목8',100000),(20, '설황(소)',35000),(21, '금호',40000),(22, '구름세',42000),(23, '구름세1',15000),(24, '구름세2',25000),(25, '소정1',44000),(26, '레오2',60000),(27, '화은옥',40000),(28, '레프티아',44000),(29, '눈꽃',40000),(30, '레오군',45000),(31, '설황5',24000),(32, '성성환5',44000),(33, '소정5',30000),(34, '용심목철화5',10000),(35, '백단',40000),(36, '소정4',35000),(37, '소정6',70000),(38, '성성환철화4',20000),(39, '백섬',20000),(40, '성성환철화',30000),(41, '눈꽃3',8000),(42, '용심목9',33000),(43, '눈꽃',8000),(44, '눈꽃',10000),(45, '백섬',30000),(46, '홍기린',33000);");
        ReadCacutsListDB();
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

    //endregion
}