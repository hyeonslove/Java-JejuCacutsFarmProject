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
        indexText.setText(cactusListViewAdapter.getCount() + "");
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
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 가로모드 고정
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
        if (editButton.getText().equals("추가")) {
            sqlite.Insert(new CactusForm(Integer.parseInt(indexText.getText().toString()), titleText.getText().toString(), Integer.parseInt(priceText.getText().toString())));


            titleText.setText("");
            priceText.setText("");
            indexText.setText(cactusListViewAdapter.getCount() + "");
        } else if (editButton.getText().equals("수정")) {
            sqlite.Update(new CactusForm(Integer.parseInt(indexText.getText().toString()), titleText.getText().toString(), Integer.parseInt(priceText.getText().toString())));

            titleText.setText("");
            priceText.setText("");
            editButton.setText("추가");
            indexText.setEnabled(true);
            indexText.setText(cactusListViewAdapter.getCount() + "");
        }
        ReadCacutsListDB();
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
        indexText.setText(cactusListViewAdapter.getCount() + "");
        titleText.setText("");
        priceText.setText("");
        editButton.setText("추가");
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