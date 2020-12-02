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
import java.util.Properties;

import Adapter.CactusListViewAdapter;
import Entity.CactusForm;

public class EditActivity extends AppCompatActivity {
    //region define
    /////////////////////////////////////////////////
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
    /////////////////////////////////////////////////
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
                cactusListViewAdapter.append(i, obj.split("\\|\\|")[0], Integer.parseInt(obj.split("\\|\\|")[1]));
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
        try {
            ini = new Ini(new FileInputStream("mnt/sdcard/Settings.ini"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //endregion

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
            if (Integer.parseInt(indexText.getText().toString()) > cactusListViewAdapter.getCount()) {
                indexText.setText(cactusListViewAdapter.getCount() + "");
            } else if (Integer.parseInt(indexText.getText().toString()) == cactusListViewAdapter.getCount()) {
                cactusListViewAdapter.append(Integer.parseInt(indexText.getText().toString()), titleText.getText().toString(), Integer.parseInt(priceText.getText().toString()));

            } else {
                int index = Integer.parseInt(indexText.getText().toString());
                cactusListViewAdapter.append(index, new CactusForm(index, titleText.getText().toString(), Integer.parseInt(priceText.getText().toString())));
                for (int i = index + 1; i < cactusListViewAdapter.getCount(); i++) {
                    cactusListViewAdapter.getList().get(i).setIndex(i);
                }
            }
            titleText.setText("");
            priceText.setText("");
            indexText.setText(cactusListViewAdapter.getCount() + "");
        } else if (editButton.getText().equals("수정")) {
            if (selected_index != Integer.parseInt(indexText.getText().toString())) {
                // Index가 달라짐 -> 위치변경 필요
                // TODO: Swap이 아닌, 한칸씩 밀리는 방법으로 추후 변경예정.\
                int n = selected_index;
                int m = Integer.parseInt(indexText.getText().toString());
                if (m > cactusListViewAdapter.getCount()) {
                    m = cactusListViewAdapter.getCount() - 1;
                }
                CactusForm temp = (CactusForm) cactusListViewAdapter.getList().get(n).clone();
                temp.setIndex(m);
                temp.setTitle(titleText.getText().toString());
                temp.setPrice(Integer.parseInt(priceText.getText().toString()));
                if (n > m) {
                    for (int i = n; i > m; i--) {
                        cactusListViewAdapter.getList().get(i).setIndex(cactusListViewAdapter.getList().get(i - 1).getIndex() + 1);
                        cactusListViewAdapter.getList().get(i).setTitle(cactusListViewAdapter.getList().get(i - 1).getTitle());
                        cactusListViewAdapter.getList().get(i).setPrice(cactusListViewAdapter.getList().get(i - 1).getPrice());
                    }
                } else {
                    for (int i = n; i < m; i++) {
                        cactusListViewAdapter.getList().get(i).setIndex(cactusListViewAdapter.getList().get(i + 1).getIndex() - 1);
                        cactusListViewAdapter.getList().get(i).setTitle(cactusListViewAdapter.getList().get(i + 1).getTitle());
                        cactusListViewAdapter.getList().get(i).setPrice(cactusListViewAdapter.getList().get(i + 1).getPrice());
                    }
                }
                cactusListViewAdapter.getList().get(m).setIndex(temp.getIndex());
                cactusListViewAdapter.getList().get(m).setTitle(temp.getTitle());
                cactusListViewAdapter.getList().get(m).setPrice(temp.getPrice());
                /*region Swap Changed
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
                cactusListViewAdapter.getList().get(selected_index).setPrice(price);*/

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
        if (indexText.getText().toString().equals("")) {
            toastSend("입력이 잘못되었습니다.", 1.5f, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
            return;
        }

        if (cactusListViewAdapter.getCount() < 1) {
            toastSend("삭제 할 제품이 없습니다.", 1.5f, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
            return;
        }
        int pos = Integer.parseInt(indexText.getText().toString());
        int idx;
        for (idx = pos; idx < cactusListViewAdapter.getCount() - 1; idx++) {
            cactusListViewAdapter.getList().get(idx).setTitle(cactusListViewAdapter.getList().get(idx + 1).getTitle());
            cactusListViewAdapter.getList().get(idx).setPrice(cactusListViewAdapter.getList().get(idx + 1).getPrice());
        }
        cactusListViewAdapter.getList().remove(cactusListViewAdapter.getCount() - 1);
        cactusListViewAdapter.notifyDataSetChanged();
        indexText.setText(cactusListViewAdapter.getCount() + "");
        titleText.setText("");
        priceText.setText("");
        editButton.setText("추가");
    }

    public void checkEditButton_onClick(View view) {
        try {
            ini.remove("CactusList");
            for (CactusForm item : cactusListViewAdapter.getList())
                ini.put("CactusList", "Cactus" + item.getIndex(), item.getTitle() + "||" + item.getPrice());
            editActivityIntent.putExtra("cactus_list", cactusListViewAdapter);
            sendBroadcast(editActivityIntent);

            ini.store(new FileOutputStream("mnt/sdcard/Settings.ini"));
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