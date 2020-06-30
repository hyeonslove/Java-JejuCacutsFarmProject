package com.example.newjejucacutsfarm;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    public static Context mContext;

    String json;
    Gson gson;
    ArrayList<CactusForm> CactusList;
    ArrayList<String> CntList;
    ArrayList<ResCactusForm> ResCactusList = new ArrayList<>();
    boolean bluetoothconnection = false;
    /***************************************************************************************************************/
    /*                                                GUI 객체 선언                                                */
    ListView CactListView; // ListView1
    ListViewAdapter CactusListViewAdapter; // Lv1 Adapter

    ListView CntListView;
    ArrayAdapter<String> CntListVIewAdapter;

    ListView ResCactusListLv;
    ResListViewAdapter ResCactusListViewAdapter = new ResListViewAdapter();

    TextView titleText;
    TextView cntText;
    TextView sumText;
    /***************************************************************************************************************/
    /***************************************************************************************************************/
    /*                                             블루투스 변수 선언                                              */

    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓
    private OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림
    private byte[] readBuffer; // 수신 된 문자열을 저장하기 위한 버퍼
    private int readBufferPosition; // 버퍼 내 문자 저장 위치

    /***************************************************************************************************************/
    /*                                               알림 함수 정의                                                */
    public void toastSend(String text, float textsize, int showtime, int postition, int offsetX, int offsetY) {
        SpannableStringBuilder biggerText = new SpannableStringBuilder(text);
        biggerText.setSpan(new RelativeSizeSpan(textsize), 0, text.length(), 0);
        Toast toast = Toast.makeText(getApplicationContext(), biggerText, showtime);
        toast.setGravity(postition, offsetX, offsetY);
        toast.show();
    }

    /***************************************************************************************************************/
    /*                                             블루투스 함수 정의                                              */
    void sendData(String text) throws Exception {
        text += "\r\n";
        outputStream.write(text.getBytes());
    }

    public void connectDevice(String deviceName) {
        // 페어링 된 디바이스들을 모두 탐색
        for (BluetoothDevice tempDevice : devices) {
            // 사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료
            if (deviceName.equals(tempDevice.getName())) {
                bluetoothDevice = tempDevice;
                break;
            }
        }
        // UUID 생성
        UUID uuid = java.util.UUID.fromString("00000003-0000-1000-8000-00805f9b34fb");
        // Rfcomm 채널을 통해 블루투스 디바이스와 통신하는 소켓 생성
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            // 데이터 송,수신 스트림을 얻어옵니다.
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            setTitle("제주농원 for Android 페어링중");
            receiveData();
        } catch (IOException e) {
            bluetoothconnection = false;
            toastSend("페어링에 실패하였습니다.", 2, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
            setTitle("제주농원 for Android 페어링 실패");
            e.printStackTrace();
        }
    }

    public void selectBluetoothDevice() {
        int pariedDeviceCount;
        // 이미 페어링 되어있는 블루투스 기기를 찾습니다.
        devices = bluetoothAdapter.getBondedDevices();
        // 페어링 된 디바이스의 크기를 저장
        pariedDeviceCount = devices.size();
        // 페어링 되어있는 장치가 없는 경우
        if (pariedDeviceCount == 0) {
            // 페어링을 하기위한 함수 호출
        }
        // 페어링 되어있는 장치가 있는 경우
        else {
            // 디바이스를 선택하기 위한 다이얼로그 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("페어링 되어있는 블루투스 디바이스 목록");
            // 페어링 된 각각의 디바이스의 이름과 주소를 저장
            List<String> list = new ArrayList<>();
            // 모든 디바이스의 이름을 리스트에 추가
            for (BluetoothDevice bluetoothDevice : devices) {
                list.add(bluetoothDevice.getName());
            }
            // List를 CharSequence 배열로 변경
            final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);
            list.toArray(new CharSequence[list.size()]);
            // 해당 아이템을 눌렀을 때 호출 되는 이벤트 리스너
            try {
                builder.setItems(charSequences, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 해당 디바이스와 연결하는 함수 호출
                        connectDevice(charSequences[which].toString());
                    }
                });
            }catch(Exception e){ }
            // 뒤로가기 버튼 누를 때 창이 안닫히도록 설정
            builder.setCancelable(false);
            // 다이얼로그 생성
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void receiveData() {
        final Handler handler = new Handler();
        // 데이터를 수신하기 위한 버퍼를 생성
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        try {
            // 1초마다 받아옴
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 데이터를 수신하기 위한 쓰레드 생성
        try {
            // 데이터를 수신했는지 확인합니다.
            int byteAvailable = inputStream.available();
            // 데이터가 수신 된 경우
            if (byteAvailable > 0) {
                // 입력 스트림에서 바이트 단위로 읽어 옵니다.
                byte[] bytes = new byte[byteAvailable];
                inputStream.read(bytes);
                // 입력 스트림 바이트를 한 바이트씩 읽어 옵니다.
                for (int i = 0; i < byteAvailable; i++) {
                    byte tempByte = bytes[i];
                    // 개행문자를 기준으로 받음(한줄)
                    if (tempByte == '\n') {
                        // readBuffer 배열을 encodedBytes로 복사
                        byte[] encodedBytes = new byte[readBufferPosition];
                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                        // 인코딩 된 바이트 배열을 문자열로 변환
                        final String text = new String(encodedBytes, StandardCharsets.US_ASCII);
                        readBufferPosition = 0;
                        if (text.compareTo("1053-4030") == 0) { // 페어링 인식번호
                            //Toast.makeText(getApplicationContext(), "연결 성공!", Toast.LENGTH_SHORT).show()
                            toastSend("페어링 성공!", 2, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
                            setTitle("제주농원 영수증 for Android 페어링");
                            bluetoothconnection = true;
                        }
                    } else {
                        readBuffer[readBufferPosition++] = tempByte;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***************************************************************************************************************/
    /*                                        서비스 시작 변수 및 함수 정의                                        */
    private MyService mService;
    private boolean isBind;

    /***************************************************************************************************************/
    /*                                               Permission 설정                                               */
    private void setPermission(){
        int permissionInfo = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissionInfo != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        String str=null;
        if(requestCode== 100){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                toastSend("SD Card 쓰기권한 승인",1.25f,Toast.LENGTH_SHORT,Gravity.TOP,0,40);
            else
                toastSend("SD Card 쓰기권한 rjqn",1.25f,Toast.LENGTH_SHORT,Gravity.TOP,0,40);
        }
    }
    /***************************************************************************************************************/
    public void callCactusList(){
        CactusListViewAdapter.clear();
        for (int i = 0; i < CactusList.size(); i++) {
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            String pricestr = myFormatter.format(Integer.parseInt(CactusList.get(i).getPrice()));
            CactusListViewAdapter.addItem(CactusList.get(i).getTitle(), pricestr + " 원");
        }CactusListViewAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 가로모드 고정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 없앰(전체화면)
        setContentView(R.layout.activity_main);
        setPermission();
        init();
        mContext=this;
        setTitle("제주농원 for Android 연결중");
        /***************************************************************************************************************/
        /*                                                 서비스 시작                                                 */
        startService(new Intent(MainActivity.this, MyService.class));
        /***************************************************************************************************************/
        /*                                                블루투스 시작                                                */
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        if (bluetoothAdapter == null) { // 디바이스가 블루투스를 지원하지 않을 때
            toastSend("블루투스를 지원하지 않는 기기입니다", 2, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
            setTitle("제주농원 for Android 블루투스 미지원");
        } else { // 디바이스가 블루투스를 지원 할 때
            if (bluetoothAdapter.isEnabled()) { // 블루투스가 활성화 상태 (기기에 블루투스가 켜져있음)
                selectBluetoothDevice(); // 블루투스 디바이스 선택 함수 호출
            } else { // 블루투스가 비 활성화 상태 (기기에 블루투스가 꺼져있음)
                // 블루투스를 활성화 하기 위한 다이얼로그 출력
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // 선택한 값이 onActivityResult 함수에서 콜백된다.
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            }
        }
        /***************************************************************************************************************/
        /*                                                CactusLv 구현                                                */
        CactusListViewAdapter = new ListViewAdapter();
        CactListView.setAdapter(CactusListViewAdapter);
        titleText = findViewById(R.id.titleText);
        CactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int check_position = CactListView.getCheckedItemPosition();   //리스트뷰의 포지션을 가져옴.
                Object vo = adapterView.getAdapter().getItem(i);  //리스트뷰의 포지션 내용을 가져옴.
                titleText.setText(vo.toString());
            }
        });
        callCactusList(); // Cactus 목록을 갱신함
        /***************************************************************************************************************/
        /*                                                 CntLv 구현                                                  */
        CntListVIewAdapter = new ArrayAdapter<String>(this, R.layout.cntlist_layout, CntList);
        CntListView.setAdapter(CntListVIewAdapter);
        CntListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        CntListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int check_position = CntListView.getCheckedItemPosition();   //리스트뷰의 포지션을 가져옴.
                Object vo = adapterView.getAdapter().getItem(i);  //리스트뷰의 포지션 내용을 가져옴.
                final String inputText = cntText.getText().toString();
                if(cntText.getText().length() > 2) {
                    if(vo.toString().compareTo("0") != 0)
                        cntText.setText(vo.toString());
                }else{
                    if(((cntText.getText().length() == 0  || cntText.getText().length() == 3) && vo.toString().compareTo("0") != 0) || cntText.getText().length() > 0)
                        cntText.setText(inputText + vo.toString());
                }
            }
        });
        /***************************************************************************************************************/
    }





    public void callCactusData(){
        InputStream in = null;
        BufferedInputStream bin=null;
        ObjectInputStream oin = null;
        try{
            in = new FileInputStream("mnt/sdcard/cactus.txt");
            bin = new BufferedInputStream(in);
            oin = new ObjectInputStream(bin);
            CactusList = (ArrayList<CactusForm>)oin.readObject();
            in.close();
            bin.close();
            oin.close();        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void init() {
        CactListView = findViewById(R.id.cactuslv);
        CntListView = findViewById(R.id.cntlv);
        ResCactusListLv = findViewById(R.id.rescacutslv);
        cntText = findViewById(R.id.cntText);
        sumText = findViewById(R.id.sumText);
        CactusList = new ArrayList<>();
        gson = new Gson();
        json = "";
        callCactusData();

        CntList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CntList.add(Integer.toString(i));
        }

    }

    public void delResList(String title, String cnt, String price, String sum) {
        ResCactusListViewAdapter.clear();
        ResCactusListViewAdapter.notifyDataSetChanged();
        for (int i = 0; i < ResCactusList.size(); i++) {
            if (title.compareTo(ResCactusList.get(i).getTitle()) == 0 && cnt.compareTo(ResCactusList.get(i).getCnt()) == 0 && price.compareTo(ResCactusList.get(i).getPrice()) == 0 && sum.compareTo(ResCactusList.get(i).getSum()) == 0) {
                ResCactusList.remove(i);
                DecimalFormat myFormatter = new DecimalFormat("###,###");
                for (int j = 0; j < ResCactusList.size(); j++) {
                    String price1 = myFormatter.format(Integer.parseInt(ResCactusList.get(j).getPrice()));
                    String sum1 = myFormatter.format(Integer.parseInt(ResCactusList.get(j).getSum()));
                    ResCactusListViewAdapter.addItem(ResCactusList.get(j).getTitle(), ResCactusList.get(j).getCnt() + " 개", price1 + " 원", sum1 + " 원");
                }
                ResCactusListViewAdapter.notifyDataSetChanged();
                break;
            }
        }
        cal();
    }

    public void addButton_onClick(View view) {
        boolean overlap = false;
        if(ResCactusList.size() > 20){
            toastSend("22개 이상으로 품목을 추가 할 수 없습니다.",1.5f,Toast.LENGTH_SHORT,Gravity.TOP,0,40);
            return;
        }
        if(titleText.getText().toString().compareTo("품목을 입력하세요") == 0){
            toastSend("품목을 제대로 입력해주세요",1.5f,Toast.LENGTH_SHORT,Gravity.TOP,0,40);
            return;
        }
        if(cntText.getText().toString().compareTo("") ==0){
            toastSend("수량을 제대로 입력해주세요",1.5f,Toast.LENGTH_SHORT,Gravity.TOP,0,40);
            return;
        }
        ResCactusListLv.setAdapter(ResCactusListViewAdapter);
        ResCactusListLv.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String title, sum, cnt, price;
        title = titleText.getText().toString().split(" ")[0];
        cnt = cntText.getText().toString();
        price = titleText.getText().toString().split(" ")[1].replace(",", "");

        ResCactusListViewAdapter.clear();
        ResCactusListViewAdapter.notifyDataSetChanged();

        for(int i=0;i<ResCactusList.size();i++){
            if(title.compareTo(ResCactusList.get(i).getTitle()) == 0){
                String cnts = Integer.toString(Integer.parseInt(ResCactusList.get(i).getCnt()));
                cnts = Integer.toString(Integer.parseInt(cnts) + Integer.parseInt(cnt));
                sum = Integer.toString(Integer.parseInt(price) * Integer.parseInt(cnts));
                ResCactusList.add(new ResCactusForm(title, cnts, price, sum));
                ResCactusList.remove(i);
                overlap=true;
                break;
            }
        }
        sum = Integer.toString(Integer.parseInt(price) * Integer.parseInt(cnt));
        if(overlap == false)
            ResCactusList.add(new ResCactusForm(title, cnt, price, sum));

        for (int i = 0; i < ResCactusList.size(); i++) {
            String price1 = myFormatter.format(Integer.parseInt(ResCactusList.get(i).getPrice()));
            String sum1 = myFormatter.format(Integer.parseInt(ResCactusList.get(i).getSum()));
            ResCactusListViewAdapter.addItem(ResCactusList.get(i).getTitle(), ResCactusList.get(i).getCnt() + " 개", price1 + " 원", sum1 + " 원");
        }
        ResCactusListViewAdapter.notifyDataSetChanged();
        ResCactusListLv.setSelection(ResCactusListViewAdapter.getCount() - 1);
        cal();
        titleText.setText("품목을 입력하세요");
        cntText.setText("");
    }

    public void cal(){
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        int box=0,sum=0;
        for(int i=0;i<ResCactusList.size();i++){
            box+=Integer.parseInt(ResCactusList.get(i).getCnt());
            sum+=Integer.parseInt(ResCactusList.get(i).getSum());
        }
        String sum1 = myFormatter.format(sum);
        sumText.setText(box + "박스 " + sum1 + "원");
    }

    public void clearButton_onClick(View view) {
        if (ResCactusList.size() < 1) {
            toastSend("초기화 할 목록이 없습니다.", 1.5f, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
        } else {
            ResCactusList.clear();
            ResCactusListViewAdapter.clear();
            ResCactusListViewAdapter.notifyDataSetChanged();
            cal();
        }
    }

    public void printButton_onClick(View view){
        if(ResCactusList.size() > 21){
            toastSend("22개 이상으로 품목을 추가 할 수 없습니다.",1.5f,Toast.LENGTH_SHORT,Gravity.TOP,0,40);
            return;
        }
        if(bluetoothconnection==false){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("확인");
            builder.setMessage("블루투스 연결이 안되어있습니다.\n그래도 계속 하시겠습니까?");
            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (ResCactusList.size() > 0) {
                        Intent intent = new Intent(getApplicationContext(), SubActivity.class);
                        intent.putExtra("key", ResCactusList);
                        startActivity(intent);
                    } else {
                        toastSend("인쇄할 제품이 없습니다.", 2, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
                    }
                }
            });
            builder.setNegativeButton("아니오",null);
            builder.create().show();
            //toastSend("블루투스 연결을 하고 시도해주세요.",2,Toast.LENGTH_LONG,Gravity.TOP,0,40);
        }else {
            if (ResCactusList.size() > 0) {
                Intent intent = new Intent(getApplicationContext(), SubActivity.class);
                intent.putExtra("key", ResCactusList);
                startActivity(intent);
            } else {
                toastSend("인쇄할 제품이 없습니다.", 2, Toast.LENGTH_SHORT, Gravity.TOP, 0, 40);
            }
        }
    }

    public void connectButton_onClick(View view){
        selectBluetoothDevice();
    }

    public void resetButton_onClick(View view){
        Intent intent = new Intent(getApplicationContext(), activity_reset.class);
        startActivity(intent);
    }
}
