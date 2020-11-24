package com.example.jejufarmreceiptproject;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import Adapter.BasketListViewAdapter;
import Entity.BasketForm;
import BluetoothService.BluetoothService;

public class PrintActivity extends AppCompatActivity {
    //region Init
    /////////////////////////////////////////////////
    //             basketListView 구현             //
    /////////////////////////////////////////////////
    private Button checkButton;
    private ListView basketListView;
    private BasketListViewAdapter basketListViewAdapter;
    //endregion

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 가로모드 고정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 없앰(전체화면)
        setContentView(R.layout.activity_print);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yy년MM월dd일 h시mm분ss초");
        String nowString = now.format(dateTimeFormatter);   // 결과 : 2016년 4월 2일 오전 1시 4분
        setTitle("제주농원 217-05252-005911  [" + nowString + "]");

        checkButton = findViewById(R.id.checkButton);
        checkButton.setEnabled((boolean) getIntent().getBooleanExtra("bluetooth_connected", false));

        basketListViewAdapter = (BasketListViewAdapter) getIntent().getSerializableExtra("list");
        basketListViewAdapter.layout = R.layout.control_printlistview; // 이렇게 public으로 get;set; 안쓰고 해도 되는지 모르겠음.
        basketListView = findViewById(R.id.basketListView);
        basketListView.setAdapter(basketListViewAdapter);
        basketListViewAdapter.appendResult();

    }

    @Override
    public void onBackPressed() {

    }
    //region General Func
    public void toastSend(String text, float textsize, int showtime, int postition, int offsetX, int offsetY) {
        SpannableStringBuilder biggerText = new SpannableStringBuilder(text);
        biggerText.setSpan(new RelativeSizeSpan(textsize), 0, text.length(), 0);
        Toast toast = Toast.makeText(getApplicationContext(), biggerText, showtime);
        toast.setGravity(postition, offsetX, offsetY);
        toast.show();
    }
    //endregion

    //region Object Events
    public void checkButton_onClick(View view) {
        try {
            String send_msg = "";
            for (BasketForm item : basketListViewAdapter.GetInstance()) {
                send_msg += (item.getTitle() + " " + item.getCount() + " " + item.getPrice() + "\\");
            }
            BluetoothService.sendData(send_msg + "\r\n");
            toastSend("인쇄 요청에 성공하였습니다.", 2f, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
        } catch (Exception e) {
            toastSend("인쇄 요청을 실패하였습니다.", 2f, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
        } finally {
            finish();
        }
    }

    public void cancelButton_onClick(View view) {
        finish();
    }
    //endregion
}