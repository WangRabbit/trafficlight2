package android.trafficlight2.ecoDriving;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.drm.DrmStore;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.trafficlight2.JSONParser;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.angle;
import static android.R.attr.backgroundDimEnabled;
import static android.R.attr.mode;
import static android.R.attr.right;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static org.apache.http.HttpHeaders.IF;

/**
 * Created by user on 2018/7/21.
 */

public class Position extends AppCompatActivity{

    private double s1_lat,s1_lng,s2_lat,s2_lng,s3_lat,s3_lng,s4_lat,s4_lng;
    private double gps_lat_1,gps_lng_1,gps_lat, gps_lng;
    private double Angle;
    private int l,h;
    private Context context;

    private ArrayList<Integer> ICIDinRange = new ArrayList();
    private ArrayList<Integer> ICIDpreprocessResult = new ArrayList();

    // for test begin
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    private static String url_getICIDlist = "http://140.116.97.98/traffic_light/get_ICIDlist.php";
    private static String url_getICIDlist_lat = "http://140.116.97.98/traffic_light/get_ICIDlist_lat.php";


    private static final String TAG_SUCCESS = "success";
    private static final String TAG_LIST = "list";
    private static final String TAG_ICID = "ICID";
    private static final String TAG_WGSX = "WGSX";
    private static final String TAG_WGSY = "WGSY";

    JSONArray list = null;
    JSONArray list1 = null;

    private String[][] ICID_list; //下載後儲存
    private String[][] ICID_list_orderBylat;

    protected ProgressDialog barProgressDialog;

    public Position(Context mcontext) {
        this.context = mcontext;
    }

    private class getICIDList extends AsyncTask<String, Integer, String> {

        protected void onPreExecute () {

            super.onPreExecute();

            runOnUiThread(new Runnable() {
                public void run() {

                    barProgressDialog = new ProgressDialog(context);
                    barProgressDialog.setTitle("請稍後");
                    barProgressDialog.setMessage("路口資料下載中...");
                    barProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    barProgressDialog.setCancelable(false);
                    barProgressDialog.setProgress(0);
                    barProgressDialog.show();
                }
            });

        }

        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = jParser.makeHttpRequest(url_getICIDlist, "GET", params);
            JSONObject json1 = jParser.makeHttpRequest(url_getICIDlist_lat, "GET", params);


            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                int success1 = json1.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    list = json.getJSONArray(TAG_LIST);

                    int LEN = list.length();

                    ICID_list = new String[LEN][3];
                    Log.e("121",""+ICID_list.length);

                    // looping through All Products
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject c = list.getJSONObject(i);

                        // Storing each json item in variable
                        String icid = c.getString(TAG_ICID);
                        String wgsx = c.getString(TAG_WGSX);
                        String wgsy = c.getString(TAG_WGSY);

                        wgsx = wgsx.replaceAll("\"", "");
                        wgsy = wgsy.replaceAll("\"", "");

                        if (!icid.equals("") && !wgsx.equals("") && !wgsy.equals("")) {

                            ICID_list[i][0] = icid;
                            ICID_list[i][1] = wgsx;
                            ICID_list[i][2] = wgsy;

                        }
                    }

                    
                } else {
                    // no products found
                    // Launch Add New product Activity
                }

                if (success1 == 1) {
                    // products found
                    // Getting Array of Products

                    list1 = json1.getJSONArray(TAG_LIST);

                    int LEN_1 = list1.length();

                    ICID_list_orderBylat = new String[LEN_1][3];
                    Log.e("length","ICID_list_orderBylat : "+ICID_list_orderBylat.length);


                    for (int j = 0; j < list1.length(); j++) {
                        JSONObject c1 = list1.getJSONObject(j);

                        // Storing each json item in variable
                        String icid_1 = c1.getString(TAG_ICID);
                        String wgsx_1 = c1.getString(TAG_WGSX);
                        String wgsy_1 = c1.getString(TAG_WGSY);

                        wgsx_1 = wgsx_1.replaceAll("\"","");
                        wgsy_1 = wgsy_1.replaceAll("\"","");

                        if (!icid_1.equals("") && !wgsx_1.equals("") && !wgsy_1.equals("")) {

                            ICID_list_orderBylat[j][0] = icid_1;
                            ICID_list_orderBylat[j][1] = wgsx_1;
                            ICID_list_orderBylat[j][2] = wgsy_1;

                            Log.e("ICID_list_irderBylat",+j+"[0]"+ICID_list_orderBylat[j][0]);
                            Log.e("ICID_list_irderBylat",+j+"[1]"+ICID_list_orderBylat[j][1]);
                            Log.e("ICID_list_irderBylat",+j+"[2]"+ICID_list_orderBylat[j][2]);


                        }
                    }

                } else {
                    // no products found
                    // Launch Add New product Activity
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(String file_url) {
            barProgressDialog.dismiss();

        }

    }

    public void startGetICIDList(){
        new getICIDList().execute();
    }
    // for test end


    public void Setting(double gps_lat_1, double gps_lng_1, double gps_lat, double gps_lng) {
        this.gps_lat_1 = gps_lat_1;
        this.gps_lng_1 = gps_lng_1;
        this.gps_lat = gps_lat;
        this.gps_lng = gps_lng;
    }



    public double getGps_lat_1() {
        return gps_lat_1;
    }

    public double getGps_lng_1() {
        return gps_lng_1;
    }

    public double getGps_lat() {
        return gps_lat;
    }

    public double getGps_lng() {
        return gps_lng;
    }

    public double getAngle() {
        return Angle;
    }

    private static double rad(double d) {
        double PI = 3.1415926535898;
        return d * PI / 180.0;
    }

    private double calGPSdiatance(double fLati1, double fLong1, double fLati2, double fLong2) {

        double EARTH_RADIUS = 6378.137;
        double radLat1 = rad(fLati1);
        double radLat2 = rad(fLati2);
        double a = radLat1 - radLat2;
        double b = rad(fLong1) - rad(fLong2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = (s * 10000000) / 10000;
        return s;
    }

    int centerLabel,leftLabel,rightLabel,centerLabel_1,leftLabel_1,rightLabel_1;
    private int preProcessMode;

    private void ICID_PreProcess(double latChange,double lngChange,int g_mode) {

        leftLabel = 0;
        centerLabel = 0;
        rightLabel = ICID_list.length-1;

        leftLabel_1 = 0;
        centerLabel_1 = 0;
        rightLabel_1 = ICID_list_orderBylat.length-1;

        if (latChange > lngChange) {

            while (Math.abs(rightLabel - leftLabel) > 1) {

                centerLabel = (rightLabel + leftLabel) / 2;
                if (Double.parseDouble(ICID_list[centerLabel][1]) > gps_lng_1) {
                    rightLabel = centerLabel - 1;
                } else {
                    leftLabel = centerLabel + 1;
                }

                Log.e("leftLabel"," " + leftLabel);
                Log.e("rightLabel "," " + rightLabel);
                Log.e("centerLabel ","" + centerLabel);
                Log.e("======= "," ");
            }
            preProcessMode = 1;
            putResultIntoList(leftLabel,rightLabel,g_mode);


        } else {

            while (Math.abs(rightLabel_1 - leftLabel_1) > 1) {

                centerLabel_1 = (rightLabel_1 + leftLabel_1) / 2;
                if (Double.parseDouble(ICID_list_orderBylat[centerLabel_1][2]) > gps_lat_1) {
                    rightLabel_1 = centerLabel_1 - 1;
                } else {
                    leftLabel_1 = centerLabel_1 + 1;
                }

                Log.e("leftLabel_1"," " + leftLabel_1);
                Log.e("rightLabel_1 "," " + rightLabel_1);
                Log.e("centerLabel_1 ","" + centerLabel_1);
                Log.e("======= "," ");
            }

            preProcessMode = 2;
            putResultIntoList(leftLabel_1,rightLabel_1,g_mode);

        }


        Log.e("size ",""+ICIDpreprocessResult.size());
        for (int j = 0;j < ICIDpreprocessResult.size();j++) {
            Log.e("result ","label "+ j +" = "+ICIDpreprocessResult.get(j));
        }


    }

    private void putResultIntoList(int left,int right,int gg_mode) {

        int N = 7; //蒐集上下N個
        int j;

        ICIDpreprocessResult.clear(); //先清空

        if (preProcessMode == 1) {
            if (gg_mode == 3 || gg_mode ==  4) {

                ICIDpreprocessResult.add(right);
                for (j = 1 ; j <= (N-6) ; j++) {
                    if ( (right + j) <= ICID_list.length-1 ) {
                        ICIDpreprocessResult.add(right + j);
                    }
                }

                ICIDpreprocessResult.add(left);
                for (j = 1 ; j <= (N+6) ; j++) {
                    if ( (left - j) >= 0  ) {
                        ICIDpreprocessResult.add(left - j);
                    }
                }

            } else if (gg_mode == 2 || gg_mode == 1){

                ICIDpreprocessResult.add(right);
                for (j = 1 ; j <= (N+6) ; j++) {
                    if ( (right + j) <= ICID_list.length-1 ) {
                        ICIDpreprocessResult.add(right + j);
                    }
                }

                ICIDpreprocessResult.add(left);
                for (j = 1 ; j <= (N-6) ; j++) {
                    if ( (left - j) >= 0  ) {
                        ICIDpreprocessResult.add(left - j);
                    }
                }

            }

        } else if (preProcessMode == 2) {

            N = 10;

            ICIDpreprocessResult.add(right);
            for (j = 1 ; j <= N ; j++) {
                if ( (right + j) <= ICID_list.length-1 ) {
                    ICIDpreprocessResult.add(right + j);
                }
            }

            ICIDpreprocessResult.add(left);
            for (j = 1 ; j <= N ; j++) {
                if ( (left - j) >= 0  ) {
                    ICIDpreprocessResult.add(left - j);
                }
            }
        }


    }

    public String calSquareRange() {

        int modeNum = 0;
        int i;
        int ResultRowNum;

        String mICID = "0";
        double a,b,C,A,B,AA,BB;
        double gps_lat_n,gps_lng_n;
        l = 60; //伸縮比例
        h = 300;

        B = Math.abs(gps_lat_1 - gps_lat); // 緯度變化(y)
        A = Math.abs(gps_lng_1 - gps_lng); // 經度變化(x)
        C = calGPSdiatance(gps_lat,gps_lng,gps_lat_1,gps_lng_1);
        Log.e("C:",""+C);
        a = A*l/C;
        b = B*l/C;

        AA = A*h/C;
        BB = B*h/C;

        if ((gps_lng_1 - gps_lng) < 0 && (gps_lat_1 - gps_lat) >0) {
            modeNum = 1;
        } if ((gps_lng_1 - gps_lng) > 0 && (gps_lat_1 - gps_lat) >0) {
            modeNum = 2;
        } if ((gps_lng_1 - gps_lng) < 0 &&  (gps_lat_1 - gps_lat) <0) {
            modeNum = 3;
        } if ((gps_lng_1 - gps_lng) > 0 && (gps_lat_1 - gps_lat) <0) {
            modeNum = 4;
        }



        try { //確認ICIDlist已經下載完成，才能進行計算，否則nullPointerException

            ICID_PreProcess(B,A,modeNum);
            Log.e("preProcessMode",""+preProcessMode);

            double function_b1, function_b2, function_b3, function_b4; //方程式中的b值

            System.out.println("ICID長度" + ICID_list.length);
            System.out.println("ModeNUM: " + modeNum);

            switch (modeNum) {

                case 1:
                    gps_lat_n = gps_lat_1 + BB;
                    gps_lng_n = gps_lng_1 - AA;

                    s1_lat = gps_lat_1 - a;
                    s1_lng = gps_lng_1 - b;

                    s2_lat = gps_lat_1 + a;
                    s2_lng = gps_lng_1 + b;

                    s3_lat = gps_lat_n + a;
                    s3_lng = gps_lng_n + b;

                    s4_lat = gps_lat_n - a;
                    s4_lng = gps_lng_n - b;

                    function_b1 = s2_lat - ((s3_lat - s2_lat) / (s3_lng - s2_lng) * s2_lng);
                    function_b2 = s4_lat - ((s4_lat - s1_lat) / (s4_lng - s1_lng) * s4_lng);
                    function_b3 = s3_lat - ((s3_lat - s4_lat) / (s3_lng - s4_lng) * s3_lng);
                    function_b4 = s2_lat - ((s2_lat - s1_lat) / (s2_lng - s1_lng) * s2_lng);


                    ICIDinRange.clear();
                    for (i = 0; i < ICIDpreprocessResult.size() ; i++) {
                        int j = ICIDpreprocessResult.get(i); //把ICIDpreprocessResult內的ICIDlist編號依序取出
                        calICID(modeNum, j, function_b1, function_b2, function_b3, function_b4); //再傳入calICID，計算出誰是想要的ICID
                    }

                    ResultRowNum = Check_ICID();
                    Log.e("ModeNum","" + modeNum);
                    Log.e("preProcessMode in","preProcessMode in result " + preProcessMode);
                    Log.e("ResultRowNum","" + ResultRowNum);

                    if (ResultRowNum != 999999) {

                        if (preProcessMode == 1) {
                            mICID = ICID_list[ResultRowNum][0];
                            Log.e("reault Case 1","");
                        } else {
                            mICID = ICID_list_orderBylat[ResultRowNum][0];
                        }

                    } else {
                        mICID = "xxx";
                    }

                    break;

                case 2:
                    gps_lat_n = gps_lat_1 + BB;
                    gps_lng_n = gps_lng_1 + AA;

                    s1_lat = gps_lat_1 + a;
                    s1_lng = gps_lng_1 - b;

                    s2_lat = gps_lat_1 - a;
                    s2_lng = gps_lng_1 + b;

                    s3_lat = gps_lat_n - a;
                    s3_lng = gps_lng_n + b;

                    s4_lat = gps_lat_n + a;
                    s4_lng = gps_lng_n - b;

                    function_b1 = s2_lat - ((s3_lat - s2_lat) / (s3_lng - s2_lng) * s2_lng);
                    function_b2 = s4_lat - ((s4_lat - s1_lat) / (s4_lng - s1_lng) * s4_lng);
                    function_b3 = s3_lat - ((s3_lat - s4_lat) / (s3_lng - s4_lng) * s3_lng);
                    function_b4 = s2_lat - ((s2_lat - s1_lat) / (s2_lng - s1_lng) * s2_lng);

                    ICIDinRange.clear();
                    for (i = 0; i < ICIDpreprocessResult.size(); i++) {
                        int j = ICIDpreprocessResult.get(i);
                        calICID(modeNum, j,function_b1, function_b2, function_b3, function_b4);
                    }

                    ResultRowNum = Check_ICID(); //檢查是否有>1個ICID落於區域內
                    Log.e("ModeNum","" + modeNum);
                    Log.e("preProcessMode in","" + preProcessMode);
                    Log.e("ResultRowNum","" + ResultRowNum);

                    if (ResultRowNum != 999999) {

                        if (preProcessMode == 1) {
                            mICID = ICID_list[ResultRowNum][0];
                            Log.e("reault Case 1","");
                        } else {
                            mICID = ICID_list_orderBylat[ResultRowNum][0];
                        }

                    } else {
                        mICID = "xxx";
                    }

                    break;

                case 3:
                    gps_lat_n = gps_lat_1 - BB;
                    gps_lng_n = gps_lng_1 - AA;

                    Log.e("gps_lat_1", "" + gps_lat_1);
                    Log.e("gps_lng_1", "" + gps_lng_1);

                    Log.e("gps_lat_n", "" + gps_lat_n);
                    Log.e("gps_lng_n", "" + gps_lng_n);

                    s1_lat = gps_lat_1 - a;
                    s1_lng = gps_lng_1 + b;
                    Log.e("s1_lat", "" + s1_lat);
                    Log.e("s1_lng", "" + s1_lng);

                    s2_lat = gps_lat_1 + a;
                    s2_lng = gps_lng_1 - b;
                    Log.e("s2_lat", "" + s2_lat);
                    Log.e("s2_lng", "" + s2_lng);

                    s3_lat = gps_lat_n + a;
                    s3_lng = gps_lng_n - b;
                    Log.e("s3_lat", "" + s3_lat);
                    Log.e("s3_lng", "" + s3_lng);

                    s4_lat = gps_lat_n - a;
                    s4_lng = gps_lng_n + b;
                    Log.e("s4_lat", "" + s4_lat);
                    Log.e("s4_lng", "" + s4_lng);

                    function_b1 = s2_lat - ((s3_lat - s2_lat) / (s3_lng - s2_lng) * s2_lng);
                    function_b2 = s4_lat - ((s4_lat - s1_lat) / (s4_lng - s1_lng) * s4_lng);
                    function_b3 = s3_lat - ((s3_lat - s4_lat) / (s3_lng - s4_lng) * s3_lng);
                    function_b4 = s2_lat - ((s2_lat - s1_lat) / (s2_lng - s1_lng) * s2_lng);

                    ICIDinRange.clear();
                    for (i = 0; i < ICIDpreprocessResult.size(); i++) {
                        int j = ICIDpreprocessResult.get(i);
                        calICID(modeNum, j,function_b1, function_b2, function_b3, function_b4);
                    }

                    ResultRowNum = Check_ICID(); //檢查是否有>1個ICID落於區域內
                    Log.e("ModeNum","" + modeNum);
                    Log.e("preProcessMode in","preProcessMode in result " + preProcessMode);
                    Log.e("ResultRowNum","" + ResultRowNum);


                    if (ResultRowNum != 999999) {
                        if (preProcessMode == 1) {
                            mICID = ICID_list[ResultRowNum][0];
                            Log.e("reault Case 1","");
                        } else {
                            mICID = ICID_list_orderBylat[ResultRowNum][0];
                        }

                    } else {
                        mICID = "xxx";
                    }

                    break;

                case 4:
                    gps_lat_n = gps_lat_1 - BB;
                    gps_lng_n = gps_lng_1 + AA;

                    s1_lat = gps_lat_1 + a;
                    s1_lng = gps_lng_1 + b;
                    Log.e("s1_lat", "" + s1_lat);
                    Log.e("s1_lng", "" + s1_lng);

                    s2_lat = gps_lat_1 - a;
                    s2_lng = gps_lng_1 - b;

                    Log.e("s2_lat", "" + s2_lat);
                    Log.e("s2_lng", "" + s2_lng);

                    s3_lat = gps_lat_n - a;
                    s3_lng = gps_lng_n - b;

                    Log.e("s3_lat", "" + s3_lat);
                    Log.e("s3_lng", "" + s3_lng);

                    s4_lat = gps_lat_n + a;
                    s4_lng = gps_lng_n + b;

                    Log.e("s4_lat", "" + s4_lat);
                    Log.e("s4_lng", "" + s4_lng);

                    function_b1 = s2_lat - ((s3_lat - s2_lat) / (s3_lng - s2_lng) * s2_lng);
                    function_b2 = s4_lat - ((s4_lat - s1_lat) / (s4_lng - s1_lng) * s4_lng);
                    function_b3 = s3_lat - ((s3_lat - s4_lat) / (s3_lng - s4_lng) * s3_lng);
                    function_b4 = s2_lat - ((s2_lat - s1_lat) / (s2_lng - s1_lng) * s2_lng);

                    ICIDinRange.clear();
                    for (i = 0; i < ICIDpreprocessResult.size(); i++) {
                        int j = ICIDpreprocessResult.get(i);
                        calICID(modeNum, j,function_b1, function_b2, function_b3, function_b4);
                    }

                    ResultRowNum = Check_ICID(); //檢查是否有>1個ICID落於區域內
                    Log.e("ModeNum","" + modeNum);
                    Log.e("preProcessMode in","preProcessMode in result" + preProcessMode);
                    Log.e("ResultRowNum","" + ResultRowNum);

                    if (ResultRowNum != 999999) {

                        if (preProcessMode == 1) {
                            mICID = ICID_list[ResultRowNum][0];
                            Log.e("reault Case 1","");
                        } else {
                            mICID = ICID_list_orderBylat[ResultRowNum][0];
                        }

                    } else {
                        mICID = "xxx";
                    }
                    break;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return mICID;
    }

    public void calICID(int gModeNum,int rowNum, double function_b1, double function_b2, double function_b3, double function_b4) {
        double result1,result2,result3,result4;
        double ICID_lng,ICID_lat;


        try {
            switch(preProcessMode) {
                case 1:
                    ICID_lat = Double.parseDouble(ICID_list[rowNum][2]);
                    ICID_lng = Double.parseDouble(ICID_list[rowNum][1]);
                    break;
                case 2:
                    ICID_lat = Double.parseDouble(ICID_list_orderBylat[rowNum][2]);
                    ICID_lng = Double.parseDouble(ICID_list_orderBylat[rowNum][1]);
                    break;

                default:
                    ICID_lat = 0;
                    ICID_lng = 0;
            }


        result1 = ICID_lat - ((s3_lat-s2_lat)/(s3_lng-s2_lng)*ICID_lng) - function_b1;
        result2 = ICID_lat - ((s4_lat-s1_lat)/(s4_lng-s1_lng)*ICID_lng) - function_b2;
        result3 = ICID_lat - ((s3_lat-s4_lat)/(s3_lng-s4_lng)*ICID_lng) - function_b3;
        result4 = ICID_lat - ((s2_lat-s1_lat)/(s2_lng-s1_lng)*ICID_lng) - function_b4;

        switch (gModeNum) {
            case 1:
                if (result1 < 0 && result2 > 0 && result3 < 0 && result4 > 0) {
                    ICIDinRange.add(rowNum);
                    Log.e("case 1","rowNum" + rowNum);
                } else {

                }

                break;

            case 2:
                if (result1 > 0 && result2 < 0 && result3 < 0 && result4 > 0) {
                    ICIDinRange.add(rowNum);
                    Log.e("case 2","rowNum" + rowNum);
                } else {

                }

                break;
            case 3:
                Log.e("result 1",""+result1);
                Log.e("result 2",""+result2);
                Log.e("result 3",""+result3);
                Log.e("result 4",""+result4);

                if (result1 < 0 && result2 > 0 && result3 > 0 && result4 < 0) {
                    ICIDinRange.add(rowNum);
                    Log.e("case 3","rowNum" + rowNum);
                } else {

                }

                break;
            case 4:
                Log.e("result 1",""+result1);
                Log.e("result 2",""+result2);
                Log.e("result 3",""+result3);
                Log.e("result 4",""+result4);

                if (result1 > 0 && result2 < 0 && result3 > 0 && result4 < 0) {
                    ICIDinRange.add(rowNum);
                    Log.e("case 4","rowNum" + rowNum);
                } else {

                }
                break;
        }

        } catch (ArrayIndexOutOfBoundsException e_out) {
            e_out.printStackTrace();
        }

    }

    private int Check_ICID() {
        int ICID_RowNum = 999999;
        if (ICIDinRange.size() > 1) { //檢查是否有>1個ICID落於區域內
            System.out.println("有>1個ICID");
            ICID_RowNum = Find_Correct_ICID_in_Many(); //有則呼叫運算，找出距離最短的那個
        } else if (ICIDinRange.size() == 1){
            System.out.println("ICID只有一個");
            ICID_RowNum = ICIDinRange.get(0); //若沒有則唯一的那個就是我們要的ICID
            System.out.println("ICID_RowNum "+ICID_RowNum);

        } else if (ICIDinRange.size() == 0) { //都沒有找到則回傳999999
            System.out.println("找不到ICID");
            ICID_RowNum = 999999;
        }
        return ICID_RowNum;
    }

    private int Find_Correct_ICID_in_Many() { //若有>1個ICID落於區域內，找出最短的那個
        int i,j;
        int correctRowNum = 0;

        double lat,lng;
        double tmp_result;
        double result = 99999999;

        System.out.println("icidinrangeSize:"+ICIDinRange.size());

        for (j=0;j < ICIDinRange.size();j++) {
            Log.d("ICIDinrange: ","i = "+ j + " value = " + ICIDinRange.get(j));
        }

            for (i=0;i < ICIDinRange.size();i++) {

             if (preProcessMode == 1) {
                 lat = Double.parseDouble(ICID_list[ICIDinRange.get(i)][2]);
                 lng = Double.parseDouble(ICID_list[ICIDinRange.get(i)][1]);
             } else {
                 lat = Double.parseDouble(ICID_list_orderBylat[ICIDinRange.get(i)][2]);
                 lng = Double.parseDouble(ICID_list_orderBylat[ICIDinRange.get(i)][1]);
             }

                    tmp_result = Math.sqrt(((lat - gps_lat_1) * (lat - gps_lat_1)) + ((lng - gps_lng_1) * (lng - gps_lng_1)));
                    if (tmp_result < result) {
                        result = tmp_result;
                        correctRowNum = ICIDinRange.get(i);
                    }
            }

        return correctRowNum;
    }

    /*以上是抓ICID的部分*/


    private double computeAzimuth() {
        double result = 0.0;

        double lat1 = gps_lat;
        double lon1 = gps_lng;
        double lat2 = gps_lat_1;
        double lon2 = gps_lng_1;

        int ilat1 = (int) (0.50 + lat1 * 360000.0);
        int ilat2 = (int) (0.50 + lat2 * 360000.0);
        int ilon1 = (int) (0.50 + lon1 * 360000.0);
        int ilon2 = (int) (0.50 + lon2 * 360000.0);
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);
        if ((ilat1 == ilat2) && (ilon1 == ilon2)) {
            return result;
        } else if (ilon1 == ilon2) {
            if (ilat1 > ilat2)
                result = 180.0;
        } else {
            double c = Math
                    .acos(Math.sin(lat2) * Math.sin(lat1) + Math.cos(lat2)
                            * Math.cos(lat1) * Math.cos((lon2 - lon1)));
            double A = Math.asin(Math.cos(lat2) * Math.sin((lon2 - lon1))
                    / Math.sin(c));
            result = Math.toDegrees(A);
            if ((ilat2 > ilat1) && (ilon2 > ilon1)) {
            } else if ((ilat2 < ilat1) && (ilon2 < ilon1)) {
                result = 180.0 - result;
            } else if ((ilat2 < ilat1) && (ilon2 > ilon1)) {
                result = 180.0 - result;
            } else if ((ilat2 > ilat1) && (ilon2 < ilon1)) {
                result += 360.0;
            }
        }
        return result;
    }

    public int getDirection() {
        Angle = computeAzimuth();
        int gDirection = 0;
        if (0 < Angle && Angle <= 45) {
            gDirection = 0;
        } else if (45 < Angle && Angle <=90) {
            gDirection = 1;
        } else if (90 < Angle && Angle <=135) {
            gDirection = 2;
        } else if (135 < Angle && Angle <= 180) {
            gDirection = 3;
        } else if (180 < Angle && Angle <= 225) {
            gDirection = 4;
        } else if (225 < Angle && Angle <= 270) {
            gDirection = 5;
        } else if (270 < Angle && Angle <= 315) {
            gDirection = 6;
        } else if (315 < Angle && Angle <= 360) {
            gDirection = 7;
        }
        return gDirection;
    }

}
