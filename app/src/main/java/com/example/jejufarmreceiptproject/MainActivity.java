package com.example.jejufarmreceiptproject;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.ini4j.Ini;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

import Adapter.BasketListViewAdapter;
import Adapter.CactusListViewAdapter;
import BluetoothService.BluetoothService;
import Entity.BasketForm;
import Entity.CactusForm;

public class MainActivity extends AppCompatActivity {
    public static Context mContext;
    //region define
    /////////////////////////////////////////////////
    //          CounterListView Text 구현          //
    /////////////////////////////////////////////////
    private TextView counterText;
    private ListView counterListView;

    private ArrayList<String> counterList;
    private ArrayAdapter<String> CntListVIewAdapter;
    /////////////////////////////////////////////////
    //          CactusListView Text 구현           //
    /////////////////////////////////////////////////
    private TextView cactusText;
    private ListView cactusListView;
    private CactusListViewAdapter cactusListViewAdapter;
    /////////////////////////////////////////////////
    //             basketListView 구현             //
    /////////////////////////////////////////////////
    private ListView basketListView;
    private BasketListViewAdapter basketListViewAdapter;
    /////////////////////////////////////////////////
    //              Total Text  구현               //
    /////////////////////////////////////////////////
    private TextView totalCountText;
    private TextView totalSumText;

    /////////////////////////////////////////////////
    //             Intent Reciver 구현             //
    /////////////////////////////////////////////////
    private BroadcastReceiver bluetoothRecvier;
    private BroadcastReceiver editRecvier;

    /////////////////////////////////////////////////
    //                 Status 구현                 //
    /////////////////////////////////////////////////
    private boolean connected = false;
    /////////////////////////////////////////////////
    // 마지막으로 뒤로 가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로 가기 버튼을 누를 때 표시
    private Toast toast;

    private Ini ini;
    //endregion

    //region General Func
    public void BasketListViewChagned() {
        int count = 0, sum = 0;
        for (BasketForm item : basketListViewAdapter.getList()) {
            count += item.getCount();
            sum += item.getTotal();
        }
        DecimalFormat df = new DecimalFormat("#,###");
        totalCountText.setText(df.format(count));
        totalSumText.setText(df.format(sum));
    }

    public void toastSend(String text, float textsize, int showtime, int postition, int offsetX, int offsetY) {
        SpannableStringBuilder biggerText = new SpannableStringBuilder(text);
        biggerText.setSpan(new RelativeSizeSpan(textsize), 0, text.length(), 0);
        Toast toast = Toast.makeText(getApplicationContext(), biggerText, showtime);
        toast.setGravity(postition, offsetX, offsetY);
        toast.show();
    }
    //endregion

    //region Permission Func
    //////////////////////////////////////////////////////////////////////////////////////////////////.////////////////////////////////
    //                                                        Permission 설정                                                        //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////.//////////////////////////
    private void setPermission() {
        int permissionInfo = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionInfo != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                toastSend("SD Card 쓰기권한 승인", 1.25f, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
            else
                toastSend("SD Card 쓰기권한 거부", 1.25f, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //endregion

    //region Init
    // CounterText와 CounterListView를 한번에 처리해주는 Init
    private void iniSetting() {
        try {
            ini = new Ini(new FileInputStream("mnt/sdcard/Settings.ini"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Init_CounterListView() {
        counterList = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            counterList.add(Integer.toString(i));

        counterListView = findViewById(R.id.counterListView);
        counterText = findViewById(R.id.counterText);

        CntListVIewAdapter = new ArrayAdapter<>(this, R.layout.control_counterlistview, counterList);
        counterListView.setAdapter(CntListVIewAdapter);
        counterListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        counterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object vo = adapterView.getAdapter().getItem(i);  //리스트뷰의 포지션 내용을 가져옴.
                final String inputText = counterText.getText().toString();
                if (counterText.getText().length() > 2) {
                    if (vo.toString().compareTo("0") != 0)
                        counterText.setText(vo.toString());
                } else {
                    if (((counterText.getText().length() == 0 || counterText.getText().length() == 3) && vo.toString().compareTo("0") != 0) || counterText.getText().length() > 0)
                        counterText.setText(inputText + vo.toString());
                }
            }
        });
    }

    // CactusText와 CactusListView를 한번에 처리해주는 Init
    private void Init_CactusListVIew() {
        cactusListViewAdapter = new CactusListViewAdapter();
        cactusText = findViewById(R.id.cactusText);
        cactusListView = findViewById(R.id.cactusListView);
        cactusListView.setAdapter(cactusListViewAdapter);

        cactusListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object vo = adapterView.getAdapter().getItem(i);  //리스트뷰의 포지션 내용을 가져옴.
                cactusText.setText(vo.toString().split("    ")[0] + "    " + vo.toString().split("    ")[1]);
            }
        });

        int max_product = Integer.parseInt(ini.get("ProgramSettings", "MAX_PRODUCT"));
        for (int i = 0; i < max_product; i++) {
            String obj = ini.get("CactusList", "Cactus" + i);
            if (obj != null) {
                cactusListViewAdapter.append(i, obj.split("\\|\\|")[0], Integer.parseInt(obj.split("\\|\\|")[1]));
                System.out.println(obj);
            }
        }
        cactusListViewAdapter.notifyDataSetChanged();
    }

    // BasketListView를 한번에 처리해주는 Init
    private void Init_BasketListView() {
        basketListViewAdapter = new BasketListViewAdapter();
        basketListView = findViewById(R.id.basketListView);
        basketListView.setAdapter(basketListViewAdapter);
    }

    private void Init_TextView() {
        totalCountText = findViewById(R.id.totalCountText);
        totalSumText = findViewById(R.id.totalSumText);
    }

    private void InitAll() {
        iniSetting();
        Init_CounterListView();
        Init_CactusListVIew();
        Init_BasketListView();
        Init_TextView();
    }
    //endregion

    @Override
    protected void onResume() {
        super.onResume();

        if (bluetoothRecvier == null) {
            bluetoothRecvier = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String text = intent.getStringExtra("status_message");
                    if (text == null)
                        return;

                    if (text.equals("connected")) {
                        setTitle("제주농원 for android 연결 완료");
                        connected = true;
                    } else if (text.equals("disconnected")) {
                        setTitle("제주농원 for android 연결 끊김");
                        connected = false;
                    } else if (text.equals("failconnected")) {
                        setTitle("제주농원 for android 연결 실패");
                        connected = false;
                    }
                }
            };
        }

        if (editRecvier == null) {
            editRecvier = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    CactusListViewAdapter list = (CactusListViewAdapter) intent.getSerializableExtra("cactus_list");
                    cactusListViewAdapter.clear();
                    for (CactusForm item : list.getList()) {
                        cactusListViewAdapter.append(item.getIndex(), item.getTitle(), item.getPrice());
                    }
                    cactusListViewAdapter.notifyDataSetChanged();
                }
            };
        }

        registerReceiver(editRecvier, new IntentFilter("editActivity"));
        registerReceiver(bluetoothRecvier, new IntentFilter("bluetoothService"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 가로모드 고정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 없앰(전체화면)
        setContentView(R.layout.activity_main);
        setPermission();
        mContext = this;
        setTitle("제주농원 for android 연결대기");
        InitAll();

        startService(new Intent(MainActivity.this, BluetoothService.class));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(MainActivity.this, BluetoothService.class));
        if (bluetoothRecvier != null) {
            unregisterReceiver(bluetoothRecvier);
        }

        if (editRecvier != null) {
            unregisterReceiver(editRecvier);
        }
        moveTaskToBack(true);						// 태스크를 백그라운드로 이동
        finishAndRemoveTask();						// 액티비티 종료 + 태스크 리스트에서 지우기
        android.os.Process.killProcess(android.os.Process.myPid());
//        ActivityCompat.finishAffinity(this);
//        System.exit(0);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지나지 않았으면 종료
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            stopService(new Intent(MainActivity.this, BluetoothService.class));
            finish();
            toast.cancel();
            toast = Toast.makeText(this, "이용해 주셔서 감사합니다.", Toast.LENGTH_LONG);
            toast.show();

        }
    }

    //region Button Events
    public void addButton_onClick(View view) {
        if (basketListViewAdapter.getCount() > 20) {
            return;
        }
        if (cactusText.getText().toString().compareTo("품목을 입력하세요") == 0) {
            return;
        }
        if (counterText.getText().toString().compareTo("") == 0) {
            return;
        }

        String title;
        int price, count;
        title = cactusText.getText().toString().split("    ")[0];
        price = Integer.parseInt(cactusText.getText().toString().split("    ")[1]);
        count = Integer.parseInt(counterText.getText().toString());
        basketListViewAdapter.append(title, count, price, (price * count));
        basketListView.setSelection(basketListViewAdapter.getCount() - 1);
        cactusText.setText("품목을 입력하세요");
        counterText.setText("");
        BasketListViewChagned();
    }

    public void resetButton_onClick(View view) {
        basketListViewAdapter.clear();
        BasketListViewChagned();
    }

    public void printButton_onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), PrintActivity.class);
        try {
            if (basketListViewAdapter.getList().size() == 0) {
                toastSend("인쇄 할 제품이 없습니다.", 2f, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
                return;
            }
            if (connected) {
                intent.putExtra("bluetooth_connected", true);
            } else {
                //connectButton_onClick(null);
                toastSend("블루투스 연결이 안 되어있습니다.", 2f, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
                intent.putExtra("bluetooth_connected", false);
            }
            intent.putExtra("list", basketListViewAdapter);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectButton_onClick(View view) {
        try {
            stopService(new Intent(MainActivity.this, BluetoothService.class));
            setTitle("다시 연결 하는중");
            Thread.sleep(300);
            startService(new Intent(MainActivity.this, BluetoothService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cactusEditButton_onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), EditActivity.class);
        startActivity(intent);
    }
    //endregion

}