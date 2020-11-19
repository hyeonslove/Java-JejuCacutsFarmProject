package BluetoothService;

import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothService extends Service {
    public static Intent mainActivityIntent;
    /////////////////////////////////////////////////
    //               Bluetooth 구현                //
    /////////////////////////////////////////////////
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓
    private static OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림

    class RecvThread extends Thread {
        @Override
        public void run() {
            try {
                while (bluetoothSocket.isConnected()) {
                    // 버퍼 내 문자 저장 위치
                    int readBufferPosition = 0;
                    // 수신 된 문자열을 저장하기 위한 버퍼
                    byte[] readBuffer = new byte[1024];
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
                                        mainActivityIntent.putExtra("toast_message", "connected");
                                        sendBroadcast(mainActivityIntent);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class PingThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    sendData("");
                    Thread.sleep(1000);
                }
            }catch (IOException e){
                mainActivityIntent.putExtra("toast_message", "disconnected");
                sendBroadcast(mainActivityIntent);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //region Bluetooth Func
    public static void sendData(String text) throws Exception {
        if (outputStream == null)
            return;

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
            new RecvThread().start();
            new PingThread().start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //endregion

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void Init_Bluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        if (bluetoothAdapter == null) { // 디바이스가 블루투스를 지원하지 않을 때
            return;
        } else { // 디바이스가 블루투스를 지원 할 때
            if (bluetoothAdapter.isEnabled()) { // 블루투스가 활성화 상태 (기기에 블루투스가 켜져있음)
                //selectBluetoothDevice(); // 블루투스 디바이스 선택 함수 호출
                devices = bluetoothAdapter.getBondedDevices();
                connectDevice("DESKTOP-5DDDEKH");
            } else { // 블루투스가 비 활성화 상태 (기기에 블루투스가 꺼져있음)

                return;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Init_Bluetooth();

        mainActivityIntent = new Intent("bluetoothService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (bluetoothSocket != null)
                bluetoothSocket.close();
            if (outputStream != null)
                outputStream.close();
            if (inputStream != null)
                inputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
