
package android.trafficlight2;


import java.io.InputStream;

import java.util.Calendar;
import java.util.ArrayList;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
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
import android.support.v7.app.AlertDialog;
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


import static android.Manifest.permission.*;
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

import static android.trafficlight2.R.id.countDown_sec;
import static android.trafficlight2.R.id.intersection_input;
import static android.trafficlight2.R.id.lightColor;
import static android.trafficlight2.R.id.location;
import static android.trafficlight2.R.id.time;
import static android.util.Log.d;
import static android.util.Log.e;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int REQUEST_EXTERNAL_ACCESS_FINE_LOCATION = 0;
    private static final int REQUEST_EXTERNAL_ACCESS_COARSE_LOCATION = 0;
    private static final int REQUEST_EXTERNAL_INTERNET = 0;
    private static final int REQUEST_EXTERNAL_INTERNET_STATE = 0;
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
    private static final String TAG_PLANID = "planID";
    private static final String TAG_OFFSET = "offset";
    private static final String TAG_DIRECTION = "Direction";
    private static final String TAG_PHASECOUNT = "PHASECOUNT"; //分相數量
    private static final String TAG_PHASEORDER = "PHASEORDER"; //燈態編號

    // products JSONArray
    JSONArray products = null;
    JSONArray getPhase = null;
    Button btnShowCountdown;
    private LocationManager locationManager;
    private String provider;


    private ImageView lightColor_img;
    private MediaPlayer mp_20;
    private MediaPlayer mp_10;
    private MediaPlayer mp_red;


    //從資料庫取得，轉換後的gps座標

    //手機gps取得的座標位置
    double gps_lat1,gps_lng1,gps_lat,gps_lng;

    //for getDirection()
    double angle;

    String ICID, gICID = "1", phaseID,planID,Direction,phaseOrder; //for 資料庫
    String inputDirection;

    int segment, weekday,offset,phaseCount;


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
    int threadFlag = 0;

   // public int timeLength;

    static int[] countStatus = {0,0,0,0,0,0};
    static int countStatusIndex = 0;

    TextView latituteField;
    TextView longitudeField;

    TextView ICIDin;
    TextView times;
    TextView TextAngle;
    TextView TextSpeed;
    TextView countdown_txt;

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

    Position pp;



    public void ActiveComponent() {
        pp = new Position();

        intersection_input = (EditText) findViewById(R.id.intersection_input);
        direction_input = (EditText) findViewById(R.id.phaseid_input);

        latituteField = (TextView) findViewById(R.id.gps_x);
        longitudeField = (TextView) findViewById(R.id.gps_y);

        ICIDin = (TextView) findViewById(R.id.ICIDin);
        times = (TextView) findViewById(R.id.times);
        TextAngle = (TextView) findViewById(R.id.angle);
        TextSpeed = (TextView) findViewById(R.id.Speed);
        countdown_txt = (TextView) findViewById(R.id.countDown_sec);

        lightColor_img = (ImageView) findViewById(lightColor);
        mp_20 = MediaPlayer.create(this, R.raw.count_20);
        mp_10 = MediaPlayer.create(this, R.raw.count_10);
        mp_red = MediaPlayer.create(this, R.raw.intersection_red);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_EXTERNAL_INTERNET:

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActiveComponent(); //宣告所有元件

        int permission_fineLocation = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int permission_coarseLocation  = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission_Network  = ActivityCompat.checkSelfPermission(this,Manifest.permission.INTERNET);
        int permission_NetworkState  = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_NETWORK_STATE);

        if (permission_Network != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[] {INTERNET},REQUEST_EXTERNAL_INTERNET);
        } else {

        }

        if (permission_NetworkState != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {ACCESS_NETWORK_STATE},REQUEST_EXTERNAL_INTERNET_STATE);
        } else {

        }

        btnShowCountdown = (Button) findViewById(location);

        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo == null || !mNetworkInfo.isConnected()) {
            new AlertDialog.Builder(this).setMessage("請檢查網路是否已連線！連線後請重新啟動本APP方能使用").setPositiveButton("OK",null).show();
            btnShowCountdown.setEnabled(false);
        } else {
            pp.startGetICIDList(); //下載ICIDlist
        }


        btnShowCountdown.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                ICID = intersection_input.getText().toString();
                inputDirection = direction_input.getText().toString(); //現在輸入的是方向性


                if (!ICID.equals(gICID) && !ICID.equals("xxx")) { //條件一: ICID不一樣 或 方向不一樣(20180811拿掉，會有頻繁送資訊問題) 且不可為xxx
                    if (inputDirection.equals("1") || inputDirection.equals("2") || inputDirection.equals("3")  ||inputDirection.equals("4") || inputDirection.equals("0") || inputDirection.equals("5") || inputDirection.equals("6")  || inputDirection.equals("7")) {

                            gICID = ICID; //檢測重複用

                            if (countFlag == 1) { //若有人在倒數，要先把原本的停掉
                                cc.cancel();
                                System.out.println("CountFlag = " + countFlag);
                                countFlag = 0;
                            }

                            CountDown c1 = new CountDown(); //new countdown物件
                            weekday = c1.getWeekday(); //調用CountDown物件中的weekday方法
                            segment = c1.calSegment(); //調用CountDown物件中的calSegment方法，計算分段後回傳

                            Log.e("threaddFlag ", "" + threadFlag);
                            if (threadFlag == 0) { //有人在查詢時，不可以再次重複要求查詢
                                Log.e("EXECUTING, threaddFlag ", "" + threadFlag);
                                threadFlag = 1; //1表示查詢中 0表示沒有在查詢
                                new LoadAllProducts().execute();
                            }
                        }
                    } else if (ICID.equals("xxx")) {
                    count(999999); //呼叫count函數指定輸入999999 顯示無路口
                }
            }
        });


        if (permission_coarseLocation != PackageManager.PERMISSION_GRANTED || permission_fineLocation != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[] {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION},REQUEST_EXTERNAL_ACCESS_COARSE_LOCATION);
        } else {

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




    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 1000, 5, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cc != null) {
            cc.cancel();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (cc!=null) {
            cc.cancel();
        }
    }


    String t1_ICID = "1",t2_ICID = "1",t3_ICID = "1",directionResult = "1";
    int ICID_xxx_Count=0;
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

          gpsSpeed = (int) (location.getSpeed()*3.6) ; //目前速度功能

          TextSpeed.setText(gpsSpeed +"km/hr");

          ICIDin.setText(t1_ICID);

              if (t1_ICID.equals(t2_ICID) && t1_ICID.equals(t3_ICID)) { //多次送同樣的才認定是最後正確的ICID
                  if (!t1_ICID.equals("xxx")) {
                      directionResult = String.valueOf(pp.getDirection());
                      TextAngle.setText(String.valueOf(pp.getAngle()));
                      direction_input.setText(directionResult);
                      intersection_input.setText(t1_ICID);


                      btnShowCountdown.callOnClick();

                  } else {
                      if (ICID_xxx_Count == 5) {
                          intersection_input.setText("xxx");
                          btnShowCountdown.callOnClick();
                          ICID_xxx_Count = 0;
                      } else {
                          ICID_xxx_Count++;
                      }
                  }
              } else {
                  if (t1_ICID.equals("xxx")) {
                      ICID_xxx_Count++;
                  }
                  t3_ICID = t2_ICID; //三次驗證確認OK才顯示
                  t2_ICID = t1_ICID;
              }

        times.setText(String.valueOf(ICID_xxx_Count)); //顯示計數次數

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


    /*
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
    } */

    CountDownTimer cc;
    public void count(int number) {


        if (number == 999999) { //查詢失敗或錯誤，顯示無路口
            TextView interSection_txt = (TextView) findViewById(R.id.interSection_Name);
            if (cc!=null) { //確認物件cc存在才做取消，否則會有null exception 錯誤
                cc.cancel();
            }
            interSection_txt.setText("");
            lightColor_img.setImageResource(no_light);
            countdown_txt.setText("無路口");

        } else if (number == 111111) {

            lightColor_img.setImageResource(no_light);
            countdown_txt.setText("閃光");

        } else {

                cc = new CountDownTimer(number * 1000 , 1000) {  //countStatus[countStatusIndex]

                    @Override
                    public void onTick(long millisUntilFinished) {
                        countFlag = 1;
                        threadFlag = 0;
                        if (countStatusIndex == 0 || countStatusIndex == 3) {
                            lightColor_img.setImageResource(green);
                        } else if (countStatusIndex == 1 || countStatusIndex == 4) {
                            lightColor_img.setImageResource(yello);
                        } else if (countStatusIndex == 2 || countStatusIndex == 5) {
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
                        if (countStatusIndex < 2) {
                            countStatusIndex++;
                        } else if (countStatusIndex == 3) {
                            countStatusIndex = 1;
                        } else if (countStatusIndex == 4) {
                            countStatusIndex = 2;
                        } else if (countStatusIndex == 5) {
                            countStatusIndex = 0;
                        } else if (countStatusIndex == 2) {
                            countStatusIndex = 0;
                        }
                        cc.cancel();
                        count(countStatus[countStatusIndex]);
                    }
                }.start();
            }
        }


    //正常:第一分相
    public void calTime_First (int g_timeLength) {

        if (g_timeLength < green_sec) {

            lightColor_img.setImageResource(green);
            timer_green = green_sec - g_timeLength;

            countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 3;

            count(countStatus[countStatusIndex]);

        } else if (green_sec <= g_timeLength && g_timeLength < (yellow_sec + green_sec)) {

            lightColor_img.setImageResource(yello);
            timer__yellow = (yellow_sec + green_sec) - g_timeLength;

            countStatus[4] = timer__yellow; //使用[4]表第一次運行要用不一樣的秒數
            countStatusIndex = 4;

            count(countStatus[countStatusIndex]);


        } else if ((green_sec + yellow_sec) <= g_timeLength) {

            lightColor_img.setImageResource(red);
            timer_red = cycle - g_timeLength;

            countStatus[5] = timer_red; //使用[5]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;

            count(countStatus[countStatusIndex]);
        }

        /*SetText
        CleanSetText();
        G1.setText(String.valueOf(green_sec));
        Y1.setText(" "+String.valueOf(yellow_sec));
        AR1.setText(" "+String.valueOf(allred_sec));
        R1.setText(" "+String.valueOf(red_sec));
        TIMELENGTH.setText(" "+String.valueOf(g_timeLength));*/

    }
    //正常:第二分相
    public void calTime_middle_2 (int g_timeLength, int g_phaseNUM) {

        if (g_timeLength < (s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM])) {

            //lightColor_img.setImageResource(red);
            timer_red = (s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + s_allred_sec[g_phaseNUM]) - g_timeLength;
            d("特殊紅燈秒數 ", "" + (s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]));
            d("倒數 ", "" + timer_red);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);


        } else if ((s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + s_allred_sec[g_phaseNUM]) <= g_timeLength && g_timeLength < (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + s_allred_sec[g_phaseNUM])) {

           // lightColor_img.setImageResource(green);
            timer_green = (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + s_allred_sec[g_phaseNUM]) - g_timeLength;

            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 3;
            count(countStatus[countStatusIndex]);


        } else if ((s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM]+ s_allred_sec[g_phaseNUM]) <= g_timeLength && g_timeLength < (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + yellow_sec + s_allred_sec[g_phaseNUM])) {

           // lightColor_img.setImageResource(yello);
            timer__yellow = (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + yellow_sec + s_allred_sec[g_phaseNUM]) - g_timeLength;
            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 4;
            count(countStatus[countStatusIndex]);

        } else if ((s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + yellow_sec + s_allred_sec[g_phaseNUM]) <= g_timeLength) {

           // lightColor_img.setImageResource(red);
           // timer_red = cycle - g_timeLength;

            g_timeLength = g_timeLength - (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + yellow_sec + s_allred_sec[g_phaseNUM]);
            timer_red = red_sec - g_timeLength;

            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);
        }

        /*SetText
        CleanSetText();
        G2.setText(String.valueOf(green_sec));
        Y2.setText(" "+String.valueOf(yellow_sec));
        AR2.setText(" "+String.valueOf(allred_sec));
        R2.setText(" "+String.valueOf(red_sec));
        TIMELENGTH.setText(" "+String.valueOf(g_timeLength));*/

    }

    //特殊:phase 1 早開的分相
    public void calTime_earlyOPEN_first (int g_timeLength, int g_phaseNUM) {

        if (g_timeLength < (green_sec + s_green_sec[g_phaseNUM])) {

          //  lightColor_img.setImageResource(green);

            timer_green = green_sec + s_green_sec[g_phaseNUM] - g_timeLength;

            countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 3;

            Log.e("phase 1 早開的分相", "");
            Log.d("綠燈秒數 ", "green_sec" + green_sec + "s_green_sec" + s_green_sec[g_phaseNUM]);
            d("倒數 ", "" + timer_green);

            d("countStatusIndex", "" + countStatusIndex);
            d("countStatus", "" + countStatus[countStatusIndex]);
            count(countStatus[countStatusIndex]);


        } else if ((green_sec + s_green_sec[g_phaseNUM]) <= g_timeLength && g_timeLength < (green_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM])) {

          //  lightColor_img.setImageResource(yello);
            timer__yellow = (green_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]) - g_timeLength;

            countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 4;

            Log.e("phase 1 早開的分相", "");
            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);
            d("countStatusIndex", "" + countStatusIndex);
            d("countStatus", "" + countStatus[countStatusIndex]);

            count(countStatus[countStatusIndex]);


        } else if ((green_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]) <= g_timeLength) {

          //  lightColor_img.setImageResource(red);
            timer_red = cycle - g_timeLength;

            Log.e("phase 1 早開的分相", "");
            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);

            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);
        }
    }

    //特殊:phase 1 遲閉
    public void calTime_lateCLOSE_first (int g_timeLength, int g_phaseNUM) { //g_phaseNUM 要參照的

        if (g_timeLength < (green_sec + yellow_sec + s_green_sec[g_phaseNUM])) {

            timer_green = (green_sec + yellow_sec + s_green_sec[g_phaseNUM]) - g_timeLength;

            countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 3;

            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);

            d("countStatusIndex", "" + countStatusIndex);
            d("countStatus", "" + countStatus[countStatusIndex]);
            count(countStatus[countStatusIndex]);


        } else if ((green_sec + yellow_sec + s_green_sec[g_phaseNUM]) <= g_timeLength && g_timeLength < (green_sec + yellow_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM])) {

            lightColor_img.setImageResource(yello);
            timer__yellow = (green_sec + yellow_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]) - g_timeLength;

            countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 4;

            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);
            d("countStatusIndex", "" + countStatusIndex);
            d("countStatus", "" + countStatus[countStatusIndex]);

            count(countStatus[countStatusIndex]);


        } else if ((green_sec + yellow_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]) <= g_timeLength) {

            lightColor_img.setImageResource(red);
            timer_red = cycle - g_timeLength;
            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);

            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);
        }
    }

    //特殊:中間的phase  遲閉
    public void calTime_lateCLOSE_middle (int g_timeLength, int g_phaseNUM) {

        if (g_timeLength < s_green_sec[0] ) {

        //    lightColor_img.setImageResource(red);
            timer_red = s_green_sec[0] - g_timeLength;
            d("倒數 ", "" + timer_red);


            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);


        } else if (s_green_sec[0] <= g_timeLength && g_timeLength < (s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec)) {

       //     lightColor_img.setImageResource(green);
            timer_green = (s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec) - g_timeLength;
            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 3;
            count(countStatus[countStatusIndex]);


        } else if ((s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec) <= g_timeLength && g_timeLength < (s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec + yellow_sec)) {

         //   lightColor_img.setImageResource(yello);
            timer__yellow = (s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec + yellow_sec) - g_timeLength;
          //  Log.d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 4;
            count(countStatus[countStatusIndex]);

        } else if ((s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec + yellow_sec) <= g_timeLength) {

       //     lightColor_img.setImageResource(red);
            timer_red = cycle - g_timeLength;
            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);
        }
    }

    //特殊:中間的phase  早開
    public void calTime_earlyOPEN_middle (int g_timeLength, int g_phaseNUM) {

        if (g_timeLength < red_sec ) {

            lightColor_img.setImageResource(red);
            timer_red = red_sec - g_timeLength;
            d("倒數 ", "" + timer_red);


            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);


        } else if (red_sec <= g_timeLength && g_timeLength < (red_sec+ s_green_sec[g_phaseNUM] + green_sec + yellow_sec)) {

            lightColor_img.setImageResource(green);
            timer_green = (red_sec+ s_green_sec[g_phaseNUM] + green_sec + yellow_sec) - g_timeLength;
            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);

            countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 3;
            count(countStatus[countStatusIndex]);


        } else if ((red_sec+ s_green_sec[g_phaseNUM] + green_sec + yellow_sec) <= g_timeLength) {

            lightColor_img.setImageResource(yello);
            timer__yellow = cycle - g_timeLength;
            //  Log.d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 4;
            count(countStatus[countStatusIndex]);
        }
    }

    //正常:第三個分相
    public void calTime_middle_3 (int g_timeLength, int g_phaseNUM) {

        if (g_timeLength < (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM])) {

     //       lightColor_img.setImageResource(red);
            timer_red = (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM]) - g_timeLength;
            d("特殊紅燈秒數 ", "" + (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM]));
            d("倒數 ", "" + timer_red);

            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);

        } else if ((s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM]) <= g_timeLength && g_timeLength < (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec)) {

       //     lightColor_img.setImageResource(green);
            timer_green = (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec) - g_timeLength;
            d("特殊綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);

            countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 3;
            count(countStatus[countStatusIndex]);


        } else if ((s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec) <= g_timeLength && g_timeLength < (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec + yellow_sec)) {

       //     lightColor_img.setImageResource(yello);
            timer__yellow = (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec + yellow_sec) - g_timeLength;
            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);

            countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 4;
            count(countStatus[countStatusIndex]);

        } else if ((s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec + yellow_sec ) <= g_timeLength) {

       //     lightColor_img.setImageResource(red);
            g_timeLength = g_timeLength - (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec + yellow_sec);
            timer_red = red_sec - g_timeLength;
       //     timer_red = cycle - g_timeLength;
            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);

            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);
        }

        /*SetText*/

    }

    //正常:最後一個分相
    public void calTime_Last(int g_timeLength) {
        if (g_timeLength < (red_sec - allred_sec)) {

    //        lightColor_img.setImageResource(red);
            timer_red = red_sec - allred_sec - g_timeLength;
            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);

            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);

        } else if ((red_sec - allred_sec) <= g_timeLength && g_timeLength < ((red_sec - allred_sec) + green_sec)) {

      //      lightColor_img.setImageResource(green);
            timer_green = ((red_sec - allred_sec) + green_sec) - g_timeLength;
            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);

            countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 3;
            count(countStatus[countStatusIndex]);


        } else if ( ((red_sec - allred_sec) + green_sec) <= g_timeLength && g_timeLength < ((red_sec - allred_sec) + green_sec + yellow_sec)) {

      //      lightColor_img.setImageResource(yello);
            timer__yellow = ((red_sec - allred_sec) + green_sec + yellow_sec) - g_timeLength;
            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);

            countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 4;
            count(countStatus[countStatusIndex]);

        } else if ( ((red_sec - allred_sec) + green_sec + yellow_sec) <= g_timeLength ) {
            g_timeLength = g_timeLength - ((red_sec - allred_sec) + green_sec + yellow_sec);
            timer_red = red_sec - g_timeLength;

    //        lightColor_img.setImageResource(red);
            countStatus[5] = timer_red;
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);
        }

        /*SetText
        CleanSetText();
        G4.setText(String.valueOf(green_sec));
        Y4.setText(" "+String.valueOf(yellow_sec));
        AR4.setText(" "+String.valueOf(allred_sec));
        R4.setText(" "+String.valueOf(red_sec));
        TIMELENGTH.setText(" "+String.valueOf(g_timeLength));*/

    }

    //特殊:最後一個分相遲閉
    public void calTime_Last_lateCLOSE(int g_timeLength) {
        if (g_timeLength < s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] ) {

      //      lightColor_img.setImageResource(red);
            timer_red = s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] - g_timeLength;
            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);

            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);

        } else if (s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] <= g_timeLength && g_timeLength < (s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] + s_green_sec[2] + s_yellow_sec[2] + green_sec)) {

      //      lightColor_img.setImageResource(green);
            timer_green = (s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] + s_green_sec[2] + s_yellow_sec[2] + green_sec) - g_timeLength;
            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);

            countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 3;
            count(countStatus[countStatusIndex]);


        } else if ((s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] + s_green_sec[2] + s_yellow_sec[2] + green_sec)  <= g_timeLength) {

       //     lightColor_img.setImageResource(yello);
            timer__yellow = cycle - g_timeLength;
            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);

            countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 4;
            count(countStatus[countStatusIndex]);

        }
    }

    public void get_phase_theard() { //當時相數量 >= 3 需要其他分相資訊

        Thread get_p1_threadd = new Thread() {
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

        get_p1_threadd.start();

        try {
            get_p1_threadd.join();
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

        /*SetText
        HOUR.setText(String.valueOf(hour));
        MIN.setText(" "+ String.valueOf(min));
        SEC.setText(" "+ String.valueOf(sec));
        TIMELENGTH.setText(" "+String.valueOf(timeLength));*/

    }


    class LoadAllProducts extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

                runOnUiThread(new Runnable() {
                                  public void run() {
                                      //TextView count_txt = (TextView) findViewById(R.id.countDown_sec);
                                      TextView interSection = (TextView) findViewById(R.id.interSection_Name);
                                      lightColor_img.setImageResource(no_light);
                                      interSection.setText("取得中");
                                      countdown_txt.setText("取得中");
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
                           // segment = c.getInt(TAG_SEGMENT);
                           // d("segment", "" + c.getString(TAG_SEGMENT));
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



                    } else  {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "代號"+ICID+",方向"+inputDirection+"錯誤碼:"+success,Toast.LENGTH_LONG).show();
                            }
                        });
                        threadFlag = 0;
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

                                startHour = timeArray[0][segment - 1];
                                startMin = timeArray[1][segment - 1];

                                interSection_txt.setText(icName);

                                countStatus[0] = green_sec;
                                countStatus[1] = yellow_sec;
                                countStatus[2] = red_sec;
                                Log.d("countStatus[0]",""+ countStatus[0]);
                                Log.d("countStatus[1]",""+ countStatus[1]);
                                Log.d("countStatus[2]",""+ countStatus[2]);


                                if (cycle != 0 && !phaseOrder.equals("B0") && !phaseOrder.equals("b0")) { //先確認CYCLE不是0 或 閃光時相(b0)

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

                                                        countStatus[0] = green_sec + s_green_sec[1];
                                                        countStatus[1] = s_yellow_sec[1];
                                                        countStatus[2] = red_sec;
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

                                                        countStatus[0] = green_sec + s_green_sec[1] + s_yellow_sec[1];
                                                        countStatus[1] = yellow_sec;
                                                        countStatus[2] = red_sec;
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
                                                            countStatus[0] = green_sec + s_green_sec[2] + s_yellow_sec[2];
                                                            countStatus[1] = yellow_sec;
                                                            countStatus[2] = cycle - green_sec - yellow_sec - s_green_sec[2] - s_yellow_sec[2] ;

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
                                    threadFlag = 0;
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
