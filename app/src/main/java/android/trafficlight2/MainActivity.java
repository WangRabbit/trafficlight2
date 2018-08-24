
package android.trafficlight2;

import java.io.IOException;
import java.io.InputStream;

import java.util.Calendar;
import java.util.ArrayList;

import android.support.v4.app.ActivityCompat;
import static android.Manifest.permission.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.R.attr.countDown;
import static android.R.attr.id;
import static android.R.attr.offset;
import static android.R.attr.tag;
import static android.R.attr.y;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.media.CamcorderProfile.get;
import static android.os.Build.ID;
import static android.os.Build.VERSION_CODES.M;
import static android.os.Build.VERSION_CODES.N;
import static android.trafficlight2.R.drawable.green;
import static android.trafficlight2.R.drawable.no_light;
import static android.trafficlight2.R.drawable.red;
import static android.trafficlight2.R.drawable.yello;
import static android.trafficlight2.R.id.HOUR;
import static android.trafficlight2.R.id.PhasrOrder;
import static android.trafficlight2.R.id.TIMELENGTH;
import static android.trafficlight2.R.id.countDown_sec;
import static android.trafficlight2.R.id.intersection_input;
import static android.trafficlight2.R.id.lightColor;
import static android.trafficlight2.R.id.location;
import static android.trafficlight2.R.id.time;
import static android.util.Log.d;
import static android.util.Log.e;


public class MainActivity extends AppCompatActivity implements LocationListener {


    static boolean firstRun = true;
    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    // url to get all products list
    private static String url_getLightData = "http://140.116.97.98/traffic_light/get_newLightData.php";
    private static String url_S_getLightData = "http://140.116.97.98/traffic_light/get_S_newLightData.php"; //192.168.0.100

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    private static final String TAG_PRODUCTS = "product";
    private static final String TAG_DEVICEID = "deviceID";
    private static final String TAG_ICID = "icID";
    private static final String TAG_ICNAME = "icNAME";
    private static final String TAG_PHASEID = "phaseID";
    private static final String TAG_YELLOW = "yellow";
    private static final String TAG_ALLRED = "allred";
    private static final String TAG_greentime = "greentime";
    private static final String TAG_CYCLE = "Cycle";
    private static final String TAG_SEGMENT = "segment";
    private static final String TAG_PLANID = "planID";
    private static final String TAG_OFFSET = "offset";
    private static final String TAG_DIRECTION = "Direction";
    private static final String TAG_PHASECOUNT = "PHASECOUNT"; //分相數量
    private static final String TAG_PHASEORDER = "PHASEORDER"; //燈態編號

    // products JSONArray
    JSONArray products = null;
    JSONArray getPhase = null;
    Button btnShowCountdown;
    Button btnReset;
    private LocationManager locationManager;
    private String provider;

    //定時器
    private boolean startflag = false;
    private int light_case = 0;

    private ImageView lightColor_img;
    private MediaPlayer mp_20;
    private MediaPlayer mp_10;
    private MediaPlayer mp_red;


    //gps座標
    String lat;
    String lng;
    //從資料庫取得，轉換後的gps座標

    //手機gps取得的座標位置
    double gps_lat1,gps_lng1,gps_lat,gps_lng;

    //for getDirection()
    double angle;
    int direction;


    String ICID, gICID = "1", phaseID,planID,Direction,phaseOrder; //for 資料庫
    String inputDirection;

    int segment, weekday,offset,phaseCount;

    CountDownTimer cc;

    String deviceID, icID, icName;
    int green_sec;
    int[] s_green_sec = {0,0,0,0,0,0}; //最多6個分相
    int[] s_yellow_sec = {0,0,0,0,0,0};
    int[] s_allred_sec = {0,0,0,0,0,0};
    int yellow_sec;
    int allred_sec;
    int cycle;
    int red_sec;

    int timer_red = 0, timer__yellow = 0, timer_green = 0;
    int countFlag = 0;
    int threaFlag = 0;

   // public int timeLength;

    static int[] countstatus = {0,0,0,0,0,0};
    static int countstatusIndex = 0;

    TextView latituteField;
    TextView longitudeField;

    TextView ICIDin;
    TextView times;
    TextView TextAngle;
    TextView TextSpeed;

    EditText intersection_input;
    EditText direction_input;

    /* TextView 時制資訊*/
    TextView G1,Y1,AR1,R1;
    TextView G2,Y2,AR2,R2;
    TextView G3,Y3,AR3,R3;
    TextView G4,Y4,AR4,R4;
    TextView CYCLE,OFFSET,ORDER,TIMELENGTH,PLANID,HOUR,MIN,SEC;


    int hour, min, sec;
    int i;

    int startHour, startMin;

    public int timeLength;

    //紀錄時間分段
    int[][] timeArray = {{0, 1, 6, 7, 8, 8, 11, 12, 13, 16, 17, 18, 21, 22, 23},
                         {0, 0, 0, 0, 15, 45, 45, 30, 15, 00, 00, 45, 00, 15, 00}};

    /*for 藍芽*/ /*
    private final static int REQUEST_CONNECT_DEVICE = 1;    //宏定义查询设备句柄

    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号

    private InputStream is;    //输入流，用来接收蓝牙数据
    private TextView dis;       //接收数据显示句柄
    private String smsg = "";    //显示用数据缓存
    private String fmsg = "";    //保存用数据缓存


    BluetoothDevice _device = null;     //蓝牙设备
    BluetoothSocket _socket = null;      //蓝牙通信socket
    boolean _discoveryFinished = false;
    boolean bRun = true;
    boolean bThread = false;

    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();    //获取本地蓝牙适配器，即蓝牙设备
*/
    /*for 藍芽終止*/


    public void ActiveComponent() {
        intersection_input = (EditText) findViewById(R.id.intersection_input);
        direction_input = (EditText) findViewById(R.id.phaseid_input);

        latituteField = (TextView) findViewById(R.id.gps_x);
        longitudeField = (TextView) findViewById(R.id.gps_y);

        ICIDin = (TextView) findViewById(R.id.ICIDin);
        times = (TextView) findViewById(R.id.times);
        TextAngle = (TextView) findViewById(R.id.angle);
        TextSpeed = (TextView) findViewById(R.id.Speed);

        lightColor_img = (ImageView) findViewById(lightColor);
        mp_20 = MediaPlayer.create(this, R.raw.count_20);
        mp_10 = MediaPlayer.create(this, R.raw.count_10);
        mp_red = MediaPlayer.create(this, R.raw.intersection_red);

        G1 = (TextView) findViewById(R.id.G1);
        G2 = (TextView) findViewById(R.id.G2);
        G3 = (TextView) findViewById(R.id.G3);
        G4 = (TextView) findViewById(R.id.G4);

        Y1 = (TextView) findViewById(R.id.Y1);
        Y2 = (TextView) findViewById(R.id.Y2);
        Y3 = (TextView) findViewById(R.id.Y3);
        Y4 = (TextView) findViewById(R.id.Y4);

        AR1 = (TextView) findViewById(R.id.AR1);
        AR2 = (TextView) findViewById(R.id.AR2);
        AR3 = (TextView) findViewById(R.id.AR3);
        AR4 = (TextView) findViewById(R.id.AR4);

        R1 = (TextView) findViewById(R.id.R1);
        R2 = (TextView) findViewById(R.id.R2);
        R3 = (TextView) findViewById(R.id.R3);
        R4 = (TextView) findViewById(R.id.R4);

        OFFSET = (TextView) findViewById(R.id.Offset);
        ORDER = (TextView) findViewById(R.id.PhasrOrder);
        CYCLE = (TextView) findViewById(R.id.Cycle);
        TIMELENGTH = (TextView) findViewById(R.id.TIMELENGTH);
        PLANID = (TextView) findViewById(R.id.PLANID);

        HOUR = (TextView) findViewById(R.id.HOUR);
        MIN = (TextView) findViewById(R.id.MIN);
        SEC = (TextView) findViewById(R.id.SEC);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView interSection_txt = (TextView) findViewById(R.id.interSection_Name);

        ActiveComponent();

        //aa = new count();
        /*for 藍芽*/
        /*
        dis = (TextView) findViewById(R.id.in);      //得到数据显示句柄


        //如果打开本地蓝牙设备不成功，提示信息，结束程序
        if (_bluetooth == null) {
            Toast.makeText(this, "無法打開藍芽，請檢查手機是否有藍芽功能", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 设置设备可以被搜索
        new Thread() {
            public void run() {
                if (_bluetooth.isEnabled() == false) {
                    _bluetooth.enable();
                }
            }
        }.start();
        */
        /*for 藍芽*/


        btnShowCountdown = (Button) findViewById(location);
        //btnReset = (Button) findViewById(R.id.reset);

        btnShowCountdown.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                ICID = intersection_input.getText().toString();
                inputDirection = direction_input.getText().toString(); //現在輸入的是方向性


                if (!ICID.equals(gICID) && !ICID.equals("xxx")) { //條件一: ICID不一樣 或 方向不一樣(20180811拿掉，會有頻繁送資訊問題) 且不可為xxx
                    if (inputDirection.equals("1") || inputDirection.equals("2") || inputDirection.equals("3")  ||inputDirection.equals("4") || inputDirection.equals("0") || inputDirection.equals("5") || inputDirection.equals("6")  || inputDirection.equals("7")) {
                        /*
                        if (ICID.charAt(0) != 'I') { // (藍芽用)檢查是否有傳送封包丟失
                                ICID = new StringBuffer(ICID).insert(0, "I").toString();
                                intersection_input.setText(ICID);
                            } */

                            gICID = ICID; //檢測重複用

                            if (countFlag == 1) { //若有人在倒數，要先把原本的停掉
                                cc.cancel();
                                System.out.println("CountFlag = " + countFlag);
                                countFlag = 0;
                            }

                            CountDown c1 = new CountDown(); //new countdown物件
                            weekday = c1.getWeekday(); //調用CountDown物件中的weekday方法
                            segment = c1.calSegment(); //調用CountDown物件中的calSegment方法，計算分段後回傳

                            Log.e("threadFlag ", "" + threaFlag);
                            if (threaFlag == 0) { //有人在查詢時，不可以再次重複要求查詢
                                Log.e("EXECUTING, threadFlag ", "" + threaFlag);
                                threaFlag = 1; //1表示查詢中 0表示沒有在查詢
                                new LoadAllProducts().execute();
                            }
                        }
                    } else if (ICID.equals("xxx")) {
                    count(999999); //呼叫count函數指定輸入999999 顯示無路口
                }
            }
        });


        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);


        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {

            Toast.makeText(this, "服務器" + provider + "已啟動",
                    Toast.LENGTH_LONG).show();
            onLocationChanged(location);
        } else {
          Toast.makeText(this, "正在搜尋gps",
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 1000, 5, this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    Position pp = new Position();
    String t1_ICID = "1",t2_ICID = "1",t3_ICID = "1",directionResult = "1";
    int ICIDcount=0;
    int gpsSpeed;

    @Override
    public void onLocationChanged(Location location) {
          gps_lat = gps_lat1;
          gps_lng = gps_lng1;
          gps_lat1 = (double) (location.getLatitude());
          gps_lng1 = (double) (location.getLongitude());
          latituteField.setText(String.valueOf(gps_lat1));
          longitudeField.setText(String.valueOf(gps_lng1));

          pp.Setting(gps_lat1,gps_lng1,gps_lat,gps_lng); //操作pp物件

          t1_ICID = pp.calSquareRange();

          gpsSpeed = (int) (location.getSpeed()*3.6) ;

          TextSpeed.setText(gpsSpeed +"km/hr");

          ICIDin.setText(t1_ICID);
          times.setText(String.valueOf(ICIDcount));

              if (t1_ICID.equals(t2_ICID) && t1_ICID.equals(t3_ICID)) { //多次送同樣的才認定是最後正確的ICID
                  if (!t1_ICID.equals("xxx")) {
                      directionResult = String.valueOf(pp.getDirection());
                      TextAngle.setText(String.valueOf(pp.getAngle()));
                      direction_input.setText(directionResult);
                      intersection_input.setText(t1_ICID);

                      btnShowCountdown.callOnClick();

                      ICIDcount = 0;
                  } else {
                      intersection_input.setText("xxx");
                      btnShowCountdown.callOnClick();
                  }
              } else {
                  t3_ICID = t2_ICID; //三次驗證確認OK才顯示
                  t2_ICID = t1_ICID;
                  ICIDcount++;
              }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }


    //以下是藍牙控制
    /*
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:     //连接结果，由DeviceListActivity设置返回
                // 响应返回结果
                if (resultCode == Activity.RESULT_OK) {   //连接成功，由DeviceListActivity设置返回
                    // MAC地址，由DeviceListActivity设置返回
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // 得到蓝牙设备句柄
                    _device = _bluetooth.getRemoteDevice(address);

                    // 用服务号得到socket
                    try {
                        _socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                    } catch (IOException e) {
                        Toast.makeText(this, "連接失敗！", Toast.LENGTH_SHORT).show();
                    }
                    //连接socket
                    Button btn = (Button) findViewById(R.id.Button03);
                    try {
                        _socket.connect();
                        Toast.makeText(this, "連接" + _device.getName() + "成功！", Toast.LENGTH_SHORT).show();
                        btn.setText("斷開");
                    } catch (IOException e) {
                        try {
                            Toast.makeText(this, "連接失敗！", Toast.LENGTH_SHORT).show();
                            _socket.close();
                            _socket = null;
                        } catch (IOException ee) {
                            Toast.makeText(this, "連接失敗！", Toast.LENGTH_SHORT).show();
                        }

                        return;
                    }

                    //打开接收线程
                    try {
                        is = _socket.getInputStream();//得到蓝牙数据输入流
                    } catch (IOException e) {
                        Toast.makeText(this, "接收數據失敗！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (bThread == false) {
                        ReadThread.start();

                        bThread = true;
                    } else {
                        bRun = true;
                    }
                }
                break;
            default:
                break;
        }
    }


    //接收数据线程
    Thread ReadThread = new Thread() {

        public void run() {

            int num = 0;
            Integer Num = 0;

            byte[] buffer = new byte[1024];
            byte[] buffer_new = new byte[1024];

            int i = 0;
            int n = 0;
            bRun = true;
            //接收线程
            while (true) {
                try {
                    while (is.available() == 0) {
                        while (bRun == false) {
                        }
                    }
                    while (true) {

                        num = is.read(buffer);         //读入数据
                        n = 0;

                        String s0 = new String(buffer, 0, num);
                        fmsg += s0;    //保存收到数据
                        for (i = 0; i < num; i++) {
                            if ((buffer[i] == 0x0d) && (buffer[i + 1] == 0x0a)) {
                                buffer_new[n] = 0x0a;
                                i++;
                            } else {
                                buffer_new[n] = buffer[i];
                            }
                            n++;
                        }
                        String s = new String(buffer_new, 0, n);
                        smsg += s;


                        //写入接收缓存
                        if (is.available() == 0) {
                            smsg = fmsg;
                            break;
                        }  //短时间没有数据才跳出进行显


                    }
                    //发送显示消息，进行显示刷新
                    handler.sendMessage(handler.obtainMessage());
                } catch (IOException e) {
                }
            }
        }
    };



    //消息处理队列
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            dis.setText(smsg);//显示数据
            //切割字串
            if (smsg.contains(",")) {
                String[] smsg_parts = new String[4];
                smsg_parts = smsg.split(",");
                // Log.d("SPLITS LENGTH", "" + smsg_parts.length);
                // Log.d("SPLITS", "" + smsg_parts);
                if (smsg_parts.length > 1) {

                    final EditText intersection_in = (EditText) findViewById(R.id.intersection_input);
                    final EditText direction_in = (EditText) findViewById(R.id.phaseid_input);
                    intersection_in.setText(smsg_parts[0]);
                    //phaseID_in.setText(smsg_parts[1]);
                    angle = Double.parseDouble(smsg_parts[1]);
                    //   ICID = smsg_parts[0];
                    //  phaseID = smsg_parts[1];
                    direction = getDirection(angle);
                    direction_in.setText(direction);
                    btnShowCountdown.callOnClick(); //呼叫按下按鈕
                }
            } else {
                Toast.makeText(MainActivity.this, "傳了一個無法分解的東西", Toast.LENGTH_SHORT);
            }

            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fmsg = "";
                }
            }, 100);
        }
    };

    //关闭程序掉用处理部分
    public void onDestroy() {
        super.onDestroy();
        if (_socket != null)  //关闭连接socket
            try {
                _socket.close();
            } catch (IOException e) {
            }
        _bluetooth.disable();  //关闭蓝牙服务
    }

    //连接按键响应函数
    public void onConnectButtonClicked(View v) {
        if (_bluetooth.isEnabled() == false) {  //如果蓝牙服务不可用则提示
            Toast.makeText(this, " 打開藍芽中...", Toast.LENGTH_LONG).show();
            _bluetooth.enable();
            return;
        }


        //如未连接设备则打开DeviceListActivity进行设备搜索
        Button btn = (Button) findViewById(R.id.Button03);
        if (_socket == null) {
            Intent serverIntent = new Intent(this, DeviceListActivity.class); //跳转程序设置
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //设置返回宏定义
        } else {
            //关闭连接socket
            try {

                is.close();
                //_bluetooth.disable();
                //_device = null;
                _socket.close();
                _socket = null;
                bRun = false;
                btn.setText("連接");
            } catch (IOException e) {
            }
        }
        return;
    }
    */

    public void CleanSetText() {
        G1.setText(null);
        G2.setText(null);
        G3.setText(null);
        G4.setText(null);
        Y1.setText(null);
        Y2.setText(null);
        Y3.setText(null);
        Y4.setText(null);
        AR1.setText(null);
        AR2.setText(null);
        AR3.setText(null);
        AR4.setText(null);
        R1.setText(null);
        R2.setText(null);
        R3.setText(null);
        R4.setText(null);
    }

    public void count(int number) {

        final TextView countdown_txt = (TextView) findViewById(R.id.countDown_sec);
        final ImageView lightColor_img = (ImageView) findViewById(lightColor);

        if (number == 999999) { //查詢失敗或錯誤，顯示無路口
            TextView interSection_txt = (TextView) findViewById(R.id.interSection_Name);
            if (cc!=null) { //
                cc.cancel();
            }
            interSection_txt.setText("");
            lightColor_img.setImageResource(no_light);
            countdown_txt.setText("無路口");

        } else {

            cc = new CountDownTimer(number * 1000, 1000) {  //countstatus[countstatusIndex]

                @Override
                public void onTick(long millisUntilFinished) {
                    countFlag = 1;
                    threaFlag = 0;
                    if (countstatusIndex == 0) {
                        lightColor_img.setImageResource(green);
                    } else if (countstatusIndex == 1) {
                        lightColor_img.setImageResource(yello);
                    } else if (countstatusIndex == 2) {
                        lightColor_img.setImageResource(red);
                    }


                    countdown_txt.setText("" + millisUntilFinished / 1000);
                    if (millisUntilFinished / 1000 == 20) {
                        mp_20.start();
                    } else if (millisUntilFinished / 1000 == 10) {
                        mp_10.start();
                    }
                }

                @Override
                public void onFinish() {

                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@");
                    d("FINISH:countstatusIndex", "" + countstatusIndex);
                    d("FINISH:countstatus", "" + countstatus[countstatusIndex]);

                    if (countstatusIndex < 2) {
                        countstatusIndex++;
                        cc.cancel();
                        count(countstatus[countstatusIndex]);
                    } else if (countstatusIndex == 3) {
                        countstatusIndex = 1;
                        cc.cancel();
                        count(countstatus[countstatusIndex]);
                    } else if (countstatusIndex == 4) {
                        countstatusIndex = 2;
                        cc.cancel();
                        count(countstatus[countstatusIndex]);
                    } else if (countstatusIndex == 5) {
                        countstatusIndex = 0;
                        cc.cancel();
                        count(countstatus[countstatusIndex]);
                    } else if (countstatusIndex == 2) {
                        countstatusIndex = 0;
                        cc.cancel();
                        count(countstatus[countstatusIndex]);
                    }

                }
            };
            cc.start();
        }
    }

    //正常:第一分相
    public void calTime_First (int g_timeLength) {

        if (g_timeLength < green_sec) {

            lightColor_img.setImageResource(green);
            timer_green = green_sec - g_timeLength;

            countstatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 3;

            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);

            d("countstatusIndex", "" + countstatusIndex);
            d("countstatus", "" + countstatus[countstatusIndex]);
            count(countstatus[countstatusIndex]);

        } else if (green_sec <= g_timeLength && g_timeLength < (yellow_sec + green_sec)) {

            lightColor_img.setImageResource(yello);
            timer__yellow = (yellow_sec + green_sec) - g_timeLength;

            countstatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 4;

            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);
            d("countstatusIndex", "" + countstatusIndex);
            d("countstatus", "" + countstatus[countstatusIndex]);

            count(countstatus[countstatusIndex]);


        } else if ((green_sec + yellow_sec) <= g_timeLength) {

            lightColor_img.setImageResource(red);
            timer_red = cycle - g_timeLength;

            countstatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 5;
            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);

            d("countstatusIndex", "" + countstatusIndex);
            d("countstatus", "" + countstatus[countstatusIndex]);
            count(countstatus[countstatusIndex]);
        }

        /*SetText*/
        CleanSetText();
        G1.setText(String.valueOf(green_sec));
        Y1.setText(" "+String.valueOf(yellow_sec));
        AR1.setText(" "+String.valueOf(allred_sec));
        R1.setText(" "+String.valueOf(red_sec));
        TIMELENGTH.setText(" "+String.valueOf(g_timeLength));

    }
    //正常:第二分相
    public void calTime_middle_2 (int g_timeLength, int g_phaseNUM) {

        if (g_timeLength < (s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM])) {

            lightColor_img.setImageResource(red);
            timer_red = (s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + s_allred_sec[g_phaseNUM]) - g_timeLength;
            d("特殊紅燈秒數 ", "" + (s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]));
            d("倒數 ", "" + timer_red);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countstatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 5;
            count(countstatus[countstatusIndex]);


        } else if ((s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + s_allred_sec[g_phaseNUM]) <= g_timeLength && g_timeLength < (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + s_allred_sec[g_phaseNUM])) {

            lightColor_img.setImageResource(green);
            timer_green = (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + s_allred_sec[g_phaseNUM]) - g_timeLength;

            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countstatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 3;
            count(countstatus[countstatusIndex]);


        } else if ((s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM]+ s_allred_sec[g_phaseNUM]) <= g_timeLength && g_timeLength < (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + yellow_sec + s_allred_sec[g_phaseNUM])) {

            lightColor_img.setImageResource(yello);
            timer__yellow = (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + yellow_sec + s_allred_sec[g_phaseNUM]) - g_timeLength;
            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countstatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 4;
            count(countstatus[countstatusIndex]);

        } else if ((s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + yellow_sec + s_allred_sec[g_phaseNUM]) <= g_timeLength) {

            lightColor_img.setImageResource(red);
           // timer_red = cycle - g_timeLength;

            g_timeLength = g_timeLength - (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + yellow_sec + s_allred_sec[g_phaseNUM]);
            timer_red = red_sec - g_timeLength;

            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countstatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 5;
            count(countstatus[countstatusIndex]);
        }

        /*SetText*/
        CleanSetText();
        G2.setText(String.valueOf(green_sec));
        Y2.setText(" "+String.valueOf(yellow_sec));
        AR2.setText(" "+String.valueOf(allred_sec));
        R2.setText(" "+String.valueOf(red_sec));
        TIMELENGTH.setText(" "+String.valueOf(g_timeLength));

    }

    //特殊:phase 1 早開的分相
    public void calTime_earlyOPEN_first (int g_timeLength, int g_phaseNUM) {

        if (g_timeLength < (green_sec + s_green_sec[g_phaseNUM])) {

            lightColor_img.setImageResource(green);

            timer_green = green_sec + s_green_sec[g_phaseNUM] - g_timeLength;

            countstatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 3;

            Log.e("phase 1 早開的分相", "");
            Log.d("綠燈秒數 ", "green_sec" + green_sec + "s_green_sec" + s_green_sec[g_phaseNUM]);
            d("倒數 ", "" + timer_green);

            d("countstatusIndex", "" + countstatusIndex);
            d("countstatus", "" + countstatus[countstatusIndex]);
            count(countstatus[countstatusIndex]);


        } else if ((green_sec + s_green_sec[g_phaseNUM]) <= g_timeLength && g_timeLength < (green_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM])) {

            lightColor_img.setImageResource(yello);
            timer__yellow = (green_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]) - g_timeLength;

            countstatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 4;

            Log.e("phase 1 早開的分相", "");
            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);
            d("countstatusIndex", "" + countstatusIndex);
            d("countstatus", "" + countstatus[countstatusIndex]);

            count(countstatus[countstatusIndex]);


        } else if ((green_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]) <= g_timeLength) {

            lightColor_img.setImageResource(red);
            timer_red = cycle - g_timeLength;

            Log.e("phase 1 早開的分相", "");
            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);

            countstatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 5;
            count(countstatus[countstatusIndex]);
        }
    }

    //特殊:phase 1 遲閉
    public void calTime_lateCLOSE_first (int g_timeLength, int g_phaseNUM) { //g_phaseNUM 要參照的

        if (g_timeLength < (green_sec + yellow_sec + s_green_sec[g_phaseNUM])) {

            timer_green = (green_sec + yellow_sec + s_green_sec[g_phaseNUM]) - g_timeLength;

            countstatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 3;

            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);

            d("countstatusIndex", "" + countstatusIndex);
            d("countstatus", "" + countstatus[countstatusIndex]);
            count(countstatus[countstatusIndex]);


        } else if ((green_sec + yellow_sec + s_green_sec[g_phaseNUM]) <= g_timeLength && g_timeLength < (green_sec + yellow_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM])) {

            lightColor_img.setImageResource(yello);
            timer__yellow = (green_sec + yellow_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]) - g_timeLength;

            countstatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 4;

            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);
            d("countstatusIndex", "" + countstatusIndex);
            d("countstatus", "" + countstatus[countstatusIndex]);

            count(countstatus[countstatusIndex]);


        } else if ((green_sec + yellow_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]) <= g_timeLength) {

            lightColor_img.setImageResource(red);
            timer_red = cycle - g_timeLength;
            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);

            countstatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 5;
            count(countstatus[countstatusIndex]);
        }
    }

    //特殊:中間的phase  遲閉
    public void calTime_lateCLOSE_middle (int g_timeLength, int g_phaseNUM) {

        if (g_timeLength < s_green_sec[0] ) {

            lightColor_img.setImageResource(red);
            timer_red = s_green_sec[0] - g_timeLength;
            d("倒數 ", "" + timer_red);


            countstatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 5;
            count(countstatus[countstatusIndex]);


        } else if (s_green_sec[0] <= g_timeLength && g_timeLength < (s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec)) {

            lightColor_img.setImageResource(green);
            timer_green = (s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec) - g_timeLength;
            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countstatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 3;
            count(countstatus[countstatusIndex]);


        } else if ((s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec) <= g_timeLength && g_timeLength < (s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec + yellow_sec)) {

            lightColor_img.setImageResource(yello);
            timer__yellow = (s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec + yellow_sec) - g_timeLength;
          //  Log.d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countstatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 4;
            count(countstatus[countstatusIndex]);

        } else if ((s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec + yellow_sec) <= g_timeLength) {

            lightColor_img.setImageResource(red);
            timer_red = cycle - g_timeLength;
            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countstatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 5;
            count(countstatus[countstatusIndex]);
        }
    }

    //特殊:中間的phase  早開
    public void calTime_earlyOPEN_middle (int g_timeLength, int g_phaseNUM) {

        if (g_timeLength < red_sec ) {

            lightColor_img.setImageResource(red);
            timer_red = red_sec - g_timeLength;
            d("倒數 ", "" + timer_red);


            countstatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 5;
            count(countstatus[countstatusIndex]);


        } else if (red_sec <= g_timeLength && g_timeLength < (red_sec+ s_green_sec[g_phaseNUM] + green_sec + yellow_sec)) {

            lightColor_img.setImageResource(green);
            timer_green = (red_sec+ s_green_sec[g_phaseNUM] + green_sec + yellow_sec) - g_timeLength;
            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);

            countstatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 3;
            count(countstatus[countstatusIndex]);


        } else if ((red_sec+ s_green_sec[g_phaseNUM] + green_sec + yellow_sec) <= g_timeLength) {

            lightColor_img.setImageResource(yello);
            timer__yellow = cycle - g_timeLength;
            //  Log.d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countstatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 4;
            count(countstatus[countstatusIndex]);
        }
    }

    //正常:第三個分相
    public void calTime_middle_3 (int g_timeLength, int g_phaseNUM) {

        if (g_timeLength < (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM])) {

            lightColor_img.setImageResource(red);
            timer_red = (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM]) - g_timeLength;
            d("特殊紅燈秒數 ", "" + (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM]));
            d("倒數 ", "" + timer_red);

            countstatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 5;
            count(countstatus[countstatusIndex]);

        } else if ((s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM]) <= g_timeLength && g_timeLength < (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec)) {

            lightColor_img.setImageResource(green);
            timer_green = (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec) - g_timeLength;
            d("特殊綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);

            countstatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 3;
            count(countstatus[countstatusIndex]);


        } else if ((s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec) <= g_timeLength && g_timeLength < (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec + yellow_sec)) {

            lightColor_img.setImageResource(yello);
            timer__yellow = (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec + yellow_sec) - g_timeLength;
            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);

            countstatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 4;
            count(countstatus[countstatusIndex]);

        } else if ((s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec + yellow_sec ) <= g_timeLength) {

            lightColor_img.setImageResource(red);
            g_timeLength = g_timeLength - (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec + yellow_sec);
            timer_red = red_sec - g_timeLength;
       //     timer_red = cycle - g_timeLength;
            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);

            countstatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 5;
            count(countstatus[countstatusIndex]);
        }

        /*SetText*/
        CleanSetText();
        G3.setText(String.valueOf(green_sec));
        Y3.setText(" "+String.valueOf(yellow_sec));
        AR3.setText(" "+String.valueOf(allred_sec));
        R3.setText(" "+String.valueOf(red_sec));
        TIMELENGTH.setText(" "+String.valueOf(g_timeLength));
    }

    //正常:最後一個分相
    public void calTime_Last(int g_timeLength) {
        if (g_timeLength < (red_sec - allred_sec)) {

            lightColor_img.setImageResource(red);
            timer_red = red_sec - allred_sec - g_timeLength;
            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);

            countstatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 5;
            count(countstatus[countstatusIndex]);

        } else if ((red_sec - allred_sec) <= g_timeLength && g_timeLength < ((red_sec - allred_sec) + green_sec)) {

            lightColor_img.setImageResource(green);
            timer_green = ((red_sec - allred_sec) + green_sec) - g_timeLength;
            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);

            countstatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 3;
            count(countstatus[countstatusIndex]);


        } else if ( ((red_sec - allred_sec) + green_sec) <= g_timeLength && g_timeLength < ((red_sec - allred_sec) + green_sec + yellow_sec)) {

            lightColor_img.setImageResource(yello);
            timer__yellow = ((red_sec - allred_sec) + green_sec + yellow_sec) - g_timeLength;
            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);

            countstatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 4;
            count(countstatus[countstatusIndex]);

        } else if ( ((red_sec - allred_sec) + green_sec + yellow_sec) <= g_timeLength ) {
            g_timeLength = g_timeLength - ((red_sec - allred_sec) + green_sec + yellow_sec);
            timer_red = red_sec - g_timeLength;

            lightColor_img.setImageResource(red);
            countstatus[5] = timer_red;
            countstatusIndex = 5;
            count(countstatus[countstatusIndex]);
        }

        /*SetText*/
        CleanSetText();
        G4.setText(String.valueOf(green_sec));
        Y4.setText(" "+String.valueOf(yellow_sec));
        AR4.setText(" "+String.valueOf(allred_sec));
        R4.setText(" "+String.valueOf(red_sec));
        TIMELENGTH.setText(" "+String.valueOf(g_timeLength));

    }

    //特殊:最後一個分相遲閉
    public void calTime_Last_lateCLOSE(int g_timeLength) {
        if (g_timeLength < s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] ) {

            lightColor_img.setImageResource(red);
            timer_red = s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] - g_timeLength;
            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);

            countstatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 5;
            count(countstatus[countstatusIndex]);

        } else if (s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] <= g_timeLength && g_timeLength < (s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] + s_green_sec[2] + s_yellow_sec[2] + green_sec)) {

            lightColor_img.setImageResource(green);
            timer_green = (s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] + s_green_sec[2] + s_yellow_sec[2] + green_sec) - g_timeLength;
            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);

            countstatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 3;
            count(countstatus[countstatusIndex]);


        } else if ((s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] + s_green_sec[2] + s_yellow_sec[2] + green_sec)  <= g_timeLength) {

            lightColor_img.setImageResource(yello);
            timer__yellow = cycle - g_timeLength;
            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);

            countstatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countstatusIndex = 4;
            count(countstatus[countstatusIndex]);

        }
    }

    public void get_phase_theard() { //當時相數量 >= 3 需要其他分相資訊

        Thread get_p1_thread = new Thread() {
            public void run() {
                List<NameValuePair> s_params = new ArrayList<NameValuePair>();
                s_params.add(new BasicNameValuePair("ICID", ICID));
                s_params.add(new BasicNameValuePair("planID", planID));
              //  s_params.add(new BasicNameValuePair("phaseID", g_phase));

                JSONObject json = jParser.makeHttpRequest(url_S_getLightData, "GET", s_params);

                try {
                    // Checking for SUCCESS TAG
                    final int success = json.getInt(TAG_SUCCESS);
                 //   d("SUCCESS xxxxxx", "" + success);

                    if (success == 1) {

                        getPhase = json.getJSONArray(TAG_PRODUCTS);
                        Log.e("getPhaseLength", "" + getPhase.length());

                        for (i=0;i < 6 ; i++) { //先把陣列裡的都洗掉
                            s_green_sec[i] = 0;
                            s_allred_sec[i] = 0;
                            s_yellow_sec[i] = 0;
                        }

                        for (i = 0; i < getPhase.length(); i++) {
                            JSONObject s = getPhase.getJSONObject(i);
                            // Storing each json item in variable
                            d("phaseLength",""+getPhase.length());
                            s_green_sec[i] = s.getInt(TAG_greentime);
                            d("s_GREEN ", "num "+ i + "time " + s.getInt(TAG_greentime));
                            s_yellow_sec[i] = s.getInt(TAG_YELLOW);
                            d("s_yellow ", "num "+ i+"time " + s.getInt(TAG_YELLOW));
                            s_allred_sec[i] = s.getInt(TAG_ALLRED);
                            d("s_ALLred ", "num "+ i+"time " + s.getInt(TAG_ALLRED));
                        }


                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "錯誤碼" + success, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (JSONException e) {

                }
            }
        };

        get_p1_thread.start();

        try {
            get_p1_thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getTimeLength() {

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);
        sec = c.get(Calendar.SECOND);

        timeLength = (((hour * 3600) + (min * 60) + sec) - ((startHour * 3600) + (startMin * 60))) - offset;
        timeLength = timeLength % cycle;

        /*SetText*/
        HOUR.setText(String.valueOf(hour));
        MIN.setText(" "+ String.valueOf(min));
        SEC.setText(" "+ String.valueOf(sec));
        TIMELENGTH.setText(" "+String.valueOf(timeLength));

    }


    class LoadAllProducts extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

                runOnUiThread(new Runnable() {
                                  public void run() {
                                      TextView count_txt = (TextView) findViewById(R.id.countDown_sec);
                                      TextView interSection = (TextView) findViewById(R.id.interSection_Name);
                                      lightColor_img.setImageResource(no_light);
                                      interSection.setText("取得中");
                                      count_txt.setText("取得中");
                                  }
                                  });

                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>(); //利用http傳送資料進去
                params.add(new BasicNameValuePair("ICID", ICID));
                params.add(new BasicNameValuePair("direction", inputDirection)); //現在是傳方向代號
                params.add(new BasicNameValuePair("segment", String.valueOf(segment)));
                params.add(new BasicNameValuePair("weekday", String.valueOf(weekday)));

                JSONObject json = jParser.makeHttpRequest(url_getLightData, "GET", params);

                try {
                    // Checking for SUCCESS TAG
                    final int success = json.getInt(TAG_SUCCESS);
                    d("SUCCESS", "" + success);
                    if (success == 1) {

                        products = json.getJSONArray(TAG_PRODUCTS);
                        d("length", "" + products.length());

                        // looping through All Products
                        for (i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);
                            // Storing each json item in variable
                            deviceID = c.getString(TAG_DEVICEID);
                            d("deviceID", "" + c.getString(TAG_DEVICEID));
                            icID = c.getString(TAG_ICID);
                            d("ICID", "" + c.getString(TAG_ICID));
                            icName = c.getString(TAG_ICNAME);
                            d("icName", "" + c.getString(TAG_ICNAME));
                            phaseID = c.getString(TAG_PHASEID);
                            d("PHASEID", "" + c.getString(TAG_PHASEID));
                            green_sec = c.getInt(TAG_greentime);
                            d("GREEN", "" + c.getString(TAG_greentime));
                            yellow_sec = c.getInt(TAG_YELLOW);
                            d("yellow", "" + c.getString(TAG_YELLOW));
                            allred_sec = c.getInt(TAG_ALLRED);
                            d("ALLred", "" + c.getString(TAG_ALLRED));
                            cycle = c.getInt(TAG_CYCLE);
                            d("cycle", "" + c.getString(TAG_CYCLE));
                            segment = c.getInt(TAG_SEGMENT);
                            d("segment", "" + c.getString(TAG_SEGMENT));
                            planID = c.getString(TAG_PLANID);
                            d("planID", "" + c.getString(TAG_PLANID));
                            offset = c.getInt(TAG_OFFSET);
                            d("offset", "" + c.getString(TAG_OFFSET));
                            Direction = c.getString(TAG_DIRECTION);
                            d("direction", "" + c.getString(TAG_DIRECTION));
                            phaseCount = c.getInt(TAG_PHASECOUNT);
                            d("phaseCount", "" + c.getString(TAG_PHASECOUNT));
                            phaseOrder = c.getString(TAG_PHASEORDER);
                            d("phaseOrder", "" + c.getString(TAG_PHASEORDER));
                        }
                        red_sec = cycle - green_sec - yellow_sec; //換算紅燈時間


                        /*SetText*/
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ORDER.setText(" "+String.valueOf(phaseOrder));
                                OFFSET.setText(String.valueOf(offset));
                                CYCLE.setText(" "+String.valueOf(cycle));
                                PLANID.setText(String.valueOf(planID));
                            }
                        });



                    } else  {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "代號"+ICID+",方向"+inputDirection+"錯誤碼:"+success,Toast.LENGTH_LONG).show();
                            }
                        });
                        threaFlag = 0;
                    }
                } catch (JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "發生例外事件 ICID = "+ ICID +"方向 = "+ inputDirection , Toast.LENGTH_LONG).show();
                        }
                    });
                    //throw new RuntimeException(e);
                }

                        runOnUiThread(new Runnable() {
                            public void run() {


                                TextView interSection_txt = (TextView) findViewById(R.id.interSection_Name);
                              //  interSection_txt.setSelected(true);
                                final TextView countdown_txt = (TextView) findViewById(R.id.countDown_sec);

                                startHour = timeArray[0][segment - 1];
                                startMin = timeArray[1][segment - 1];

                                interSection_txt.setText(icName);

                                /*
                                Calendar c = Calendar.getInstance();
                                hour = c.get(Calendar.HOUR_OF_DAY);
                                min = c.get(Calendar.MINUTE);
                                sec = c.get(Calendar.SECOND);
                                d("小時 ", "" + hour);
                                d("分 ", "" + min);
                                d("秒 ", "" + sec); */

                                //getTimeLength();

                                countstatus[0] = green_sec;
                                countstatus[1] = yellow_sec;
                                countstatus[2] = red_sec;
                                Log.d("countstatus[0]",""+countstatus[0]);
                                Log.d("countstatus[1]",""+countstatus[1]);
                                Log.d("countstatus[2]",""+countstatus[2]);



                                if (cycle != 0 && !phaseOrder.equals("B0") && !phaseOrder.equals("b0")) { //先確認CYCLE不是0 或 閃光時相(b0)

                                    /*
                                    timeLength = (((hour * 3600) + (min * 60) + sec) - ((startHour * 3600) + (startMin * 60))) - offset;
                                    timeLength =timeLength % cycle; */

                                    d("startHour ", "" + startHour); //依照startHour和startMin開始計算
                                    d("startMin ", "" + startMin); //依照startHour和startMin開始計算

                                    switch(phaseCount) {
                                        case 2: //二分相情況
                                            switch (Integer.parseInt(phaseID)) {
                                                case 1:
                                                    getTimeLength();
                                                    calTime_First(timeLength);
                                                    break;

                                                case 2:
                                                    getTimeLength();
                                                    calTime_Last(timeLength);
                                                    break;

                                            }
                                            break;

                                        case 3: //三分相情況
                                            Log.e("我是三時相", "");
                                            switch (Integer.parseInt(phaseID)) {
                                                case 1:
                                                    getTimeLength();
                                                    calTime_First(timeLength);
                                                    break;

                                                case 2:
                                                    get_phase_theard();
                                                    getTimeLength();
                                                    calTime_middle_2(timeLength,0);
                                                    break;

                                                case 3:
                                                    getTimeLength();
                                                    calTime_Last(timeLength);
                                                    break;

                                            }
                                            break;

                                        case 4:

                                            if (phaseOrder.equals("1A")) { //1A 早開遲閉二時相

                                                Log.e("1A 早開遲閉二時相", ""); //四時相
                                                Toast.makeText(MainActivity.this, "1A 早開遲閉二時相",Toast.LENGTH_LONG).show();

                                                switch (Integer.parseInt(phaseID)) {
                                                    case 1:
                                                        get_phase_theard();
                                                        getTimeLength();
                                                        red_sec = cycle - s_green_sec[1] - s_yellow_sec[1] - green_sec; //重新計算紅燈秒數

                                                        countstatus[0] = green_sec + s_green_sec[1];
                                                        countstatus[1] = s_yellow_sec[1];
                                                        countstatus[2] = red_sec;
                                                        e("1A紅燈秒數:",""+red_sec);
                                                        calTime_earlyOPEN_first(timeLength, 1); //分相1早開 (參考第二分相秒數）
                                                        break;
                                                    case 2:
                                                        get_phase_theard();
                                                        getTimeLength();
                                                        calTime_middle_2(timeLength, 0);
                                                        break;
                                                    case 3:
                                                        get_phase_theard();
                                                        getTimeLength();
                                                        red_sec = cycle - s_green_sec[1] - s_yellow_sec[1] - green_sec - yellow_sec; //重新計算紅燈秒數

                                                        countstatus[0] = green_sec + s_green_sec[1] + s_yellow_sec[1];
                                                        countstatus[1] = yellow_sec;
                                                        countstatus[2] = red_sec;
                                                        e("1A紅燈秒數:",""+red_sec);
                                                        calTime_lateCLOSE_middle(timeLength, 1); //分相3遲閉 (參考第二分相秒數)
                                                        break;
                                                    case 4:
                                                        getTimeLength();
                                                        calTime_Last(timeLength);
                                                        break;
                                                }

                                            } else {
                                                Toast.makeText(MainActivity.this, "我是正常四時相",Toast.LENGTH_LONG).show();

                                                switch (Integer.parseInt(phaseID)) {
                                                    case 1:
                                                        getTimeLength();
                                                        calTime_First(timeLength);
                                                        break;
                                                    case 2:
                                                        get_phase_theard();
                                                        getTimeLength();
                                                        calTime_middle_2(timeLength, 0);
                                                        break;

                                                    case 3:
                                                        get_phase_theard();
                                                        getTimeLength();
                                                        calTime_middle_3(timeLength, 1);
                                                        break;

                                                    case 4:

                                                        if (phaseOrder.equals("D3")) { //for I0215 I0551
                                                            get_phase_theard();
                                                            getTimeLength();
                                                            countstatus[0] = green_sec + s_green_sec[2] + s_yellow_sec[2];
                                                            countstatus[1] = yellow_sec;
                                                            countstatus[2] = cycle - green_sec - yellow_sec - s_green_sec[2] - s_yellow_sec[2] ;

                                                            calTime_Last_lateCLOSE(timeLength);

                                                        } else {
                                                            getTimeLength();
                                                            calTime_Last(timeLength);
                                                        }
                                                       break;
                                                }
                                            }
                                            break;
                                    }

                                } else {
                                    lightColor_img.setImageResource(no_light);
                                    countdown_txt.setText("閃光");
                                    threaFlag = 0;
                                }
                            }
                        });

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        /*
        protected void onPostExecute(String file_url) {

            runOnUiThread(new Runnable() {
                public void run() {

                }
            });

        } */

    }
}
