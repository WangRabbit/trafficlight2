package android.trafficlight2;

import android.util.Log;

import java.util.ArrayList;

import static android.R.attr.angle;
import static android.R.attr.mode;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static org.apache.http.HttpHeaders.IF;

/**
 * Created by user on 2018/7/21.
 */

public class Position {

    private double s1_lat,s1_lng,s2_lat,s2_lng,s3_lat,s3_lng,s4_lat,s4_lng;
    private double gps_lat_1,gps_lng_1,gps_lat, gps_lng;
    private double Angle;
    private int l,h;

    private ArrayList<Integer> ICIDinRange = new ArrayList();

    private String[][] ICID_list = {
            {"I0597","120.218579","23.000978"},
            {"I0598","120.220509","23.000856"},
            {"I0599","120.222341","23.000700"},
            {"I0193","120.218195","22.996277"},
            {"I0216","120.221915","22.996014"},
            {"I0370","120.224558","23.000558"},
            {"I0551","120.224394","22.998492"},
            {"I0215","120.224166","22.995854"},
            {"I0415","120.220720","23.003004"},
            {"I0456","120.223942","22.993546"},
            {"I0369","120.224490","22.991118"},
            {"I0639","120.221734","22.993739"},
            //         {"I0259","120.221677","22.992773"},
            {"I0144","120.221642","22.992011"},
            {"I0981","120.222467","22.991715"},
            {"I0044","120.220021","22.992184"},
            {"I0122","120.21788","22.992354"},
            {"I0906","120.218012","22.993817"},
            {"I1005","120.218084","22.995385"},
            {"I0979","120.217038","23.001070"},
            {"I0929","120.218325","22.998454"},
            {"I0079","120.216672","22.996486"},
            {"I0274","120.213615","22.996906"},
            {"I0873","120.214893","22.996789"},
            {"I0406","120.214482","22.991384"},
            {"I0862","120.214555","22.992662"},
            {"I1010","120.214615","22.993435"},
            {"I1320","120.214683","22.994342"},
            {"I0357","120.214661","23.001452"},
            {"I0330","120.215118","23.003969"},
            {"I0328","120.218784","23.003417"},
            {"I0329","120.222489","23.002829"},
            {"I0371","120.224681","23.002397"},
            {"I1153","120.217576","23.003505"},
    };


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

    private double rad(double d) {
        double PI = 3.1415926535898;
        return d * PI / 180.0;
    }

    private double calGPSdiatance(double fLati1, double fLong1, double fLati2, double fLong2) {

        double EARTH_RADIUS = 6378.137;
            double radLat1 = rad(fLati1);
            double radLat2 = rad(fLati2);
            double a = radLat1 - radLat2;
            double b = rad(fLong1) - rad(fLong2);
            double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
            s = s * EARTH_RADIUS;
            s = (s * 10000000) / 10000;
            return s;
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




        double function_b1,function_b2,function_b3,function_b4; //方程式中的b值

        System.out.println("ICID長度"+ICID_list.length);
        System.out.println("ModeNUM: "+modeNum);

        switch(modeNum) {

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

                function_b1 = s2_lat - ((s3_lat-s2_lat)/(s3_lng-s2_lng)*s2_lng);
                function_b2 = s4_lat - ((s4_lat-s1_lat)/(s4_lng-s1_lng)*s4_lng);
                function_b3 = s3_lat - ((s3_lat-s4_lat)/(s3_lng-s4_lng)*s3_lng);
                function_b4 = s2_lat - ((s2_lat-s1_lat)/(s2_lng-s1_lng)*s2_lng);


                ICIDinRange.clear();
                for (i=0;i<ICID_list.length;i++) {
                    calICID(modeNum,i,function_b1,function_b2,function_b3,function_b4);
                //    System.out.println("modeNum = " + modeNum + "mICID = " + mICID);
                }

                ResultRowNum = Check_ICID();
                if (ResultRowNum != 999999) {
                    mICID = ICID_list[ResultRowNum][0];
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

                function_b1 = s2_lat - ((s3_lat-s2_lat)/(s3_lng-s2_lng)*s2_lng);
                function_b2 = s4_lat - ((s4_lat-s1_lat)/(s4_lng-s1_lng)*s4_lng);
                function_b3 = s3_lat - ((s3_lat-s4_lat)/(s3_lng-s4_lng)*s3_lng);
                function_b4 = s2_lat - ((s2_lat-s1_lat)/(s2_lng-s1_lng)*s2_lng);

                ICIDinRange.clear();
                for (i=0;i<ICID_list.length;i++) {
                    calICID(modeNum,i,function_b1,function_b2,function_b3,function_b4);
                 //   System.out.println("modeNum = " + modeNum + "mICID = " + mICID);

                }

                ResultRowNum = Check_ICID(); //檢查是否有>1個ICID落於區域內
                if (ResultRowNum != 999999) {
                    mICID = ICID_list[ResultRowNum][0];
                } else {
                    mICID = "xxx";
                }

                break;

            case 3:
                gps_lat_n = gps_lat_1 - BB;
                gps_lng_n = gps_lng_1 - AA;

                Log.e("gps_lat_1",""+gps_lat_1);
                Log.e("gps_lng_1",""+gps_lng_1);

                Log.e("gps_lat_n",""+gps_lat_n);
                Log.e("gps_lng_n",""+gps_lng_n);

                s1_lat = gps_lat_1 - a;
                s1_lng = gps_lng_1 + b;
                Log.e("s1_lat",""+s1_lat);
                Log.e("s1_lng",""+s1_lng);

                s2_lat = gps_lat_1 + a;
                s2_lng = gps_lng_1 - b;
                Log.e("s2_lat",""+s2_lat);
                Log.e("s2_lng",""+s2_lng);

                s3_lat = gps_lat_n + a;
                s3_lng = gps_lng_n - b;
                Log.e("s3_lat",""+s3_lat);
                Log.e("s3_lng",""+s3_lng);

                s4_lat = gps_lat_n - a;
                s4_lng = gps_lng_n + b;
                Log.e("s4_lat",""+s4_lat);
                Log.e("s4_lng",""+s4_lng);

                function_b1 = s2_lat - ((s3_lat-s2_lat)/(s3_lng-s2_lng)*s2_lng);
                function_b2 = s4_lat - ((s4_lat-s1_lat)/(s4_lng-s1_lng)*s4_lng);
                function_b3 = s3_lat - ((s3_lat-s4_lat)/(s3_lng-s4_lng)*s3_lng);
                function_b4 = s2_lat - ((s2_lat-s1_lat)/(s2_lng-s1_lng)*s2_lng);

                ICIDinRange.clear();
                for (i=0;i<ICID_list.length;i++) {
                    calICID(modeNum, i, function_b1, function_b2, function_b3, function_b4);
                   // System.out.println("modeNum = " + modeNum + "mICID = " + mICID);
                }

                ResultRowNum = Check_ICID(); //檢查是否有>1個ICID落於區域內
                if (ResultRowNum != 999999) {
                    mICID = ICID_list[ResultRowNum][0];
                } else {
                    mICID = "xxx";
                }

                break;

            case 4:
                gps_lat_n = gps_lat_1 - BB;
                gps_lng_n = gps_lng_1 + AA;

                s1_lat = gps_lat_1 + a;
                s1_lng = gps_lng_1 + b;
                Log.e("s1_lat",""+s1_lat);
                Log.e("s1_lng",""+s1_lng);

                s2_lat = gps_lat_1 - a;
                s2_lng = gps_lng_1 - b;

                Log.e("s2_lat",""+s2_lat);
                Log.e("s2_lng",""+s2_lng);

                s3_lat = gps_lat_n - a;
                s3_lng = gps_lng_n - b;

                Log.e("s3_lat",""+s3_lat);
                Log.e("s3_lng",""+s3_lng);

                s4_lat = gps_lat_n + a;
                s4_lng = gps_lng_n + b;

                Log.e("s4_lat",""+s4_lat);
                Log.e("s4_lng",""+s4_lng);

                function_b1 = s2_lat - ((s3_lat-s2_lat)/(s3_lng-s2_lng)*s2_lng);
                function_b2 = s4_lat - ((s4_lat-s1_lat)/(s4_lng-s1_lng)*s4_lng);
                function_b3 = s3_lat - ((s3_lat-s4_lat)/(s3_lng-s4_lng)*s3_lng);
                function_b4 = s2_lat - ((s2_lat-s1_lat)/(s2_lng-s1_lng)*s2_lng);

/*
                function_b1 = s2_lng - ((s3_lng-s2_lng)/(s3_lat-s2_lat)*s2_lat);
                function_b2 = s4_lng - ((s4_lng-s1_lng)/(s4_lat-s1_lat)*s4_lat);
                function_b3 = s3_lng - ((s3_lng-s4_lng)/(s3_lat-s4_lat)*s3_lat);
                function_b4 = s2_lng - ((s2_lng-s1_lng)/(s2_lat-s1_lat)*s2_lat);
*/
                ICIDinRange.clear();
                for (i=0;i<ICID_list.length;i++) {
                    calICID(modeNum,i,function_b1,function_b2,function_b3,function_b4);
                   // System.out.println("modeNum = " + modeNum + "mICID = " + mICID);
                }

                ResultRowNum = Check_ICID(); //檢查是否有>1個ICID落於區域內
                if (ResultRowNum != 999999) {
                    mICID = ICID_list[ResultRowNum][0];
                } else {
                    mICID = "xxx";
                }
                break;
        }
        return mICID;
    }

    public void calICID(int gModeNum,int rowNum,double function_b1, double function_b2, double function_b3, double function_b4) {
        double result1,result2,result3,result4;
        double ICID_lng,ICID_lat;

        ICID_lat = Double.parseDouble(ICID_list[rowNum][2]);
        ICID_lng = Double.parseDouble(ICID_list[rowNum][1]);

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

    }

    private int Check_ICID() {
        int ICID_RowNum = 999999;
        if (ICIDinRange.size() > 1) { //檢查是否有>1個ICID落於區域內
            System.out.println("有>1個ICID");
            ICID_RowNum = Find_Correct_ICID_in_Many(); //有則呼叫運算，找出距離最短的那個
        } else if (ICIDinRange.size() == 1){
            System.out.println("ICID只有一個");
            ICID_RowNum = ICIDinRange.get(0); //若沒有則唯一的那個就是我們要的ICID
        } else if (ICIDinRange.size() == 0) { //都沒有找到則回傳999999
            System.out.println("找不到ICID");
            ICID_RowNum = 999999;
        }
        return ICID_RowNum;
    }

    private int Find_Correct_ICID_in_Many() { //若有>1個ICID落於區域內，找出最短的那個
        int i,j;
        int correctRowNum = 0;
        String rICID = "1";
        double lat,lng;
        double tmp_result;
        double result = 99999999;

        System.out.println("icidinrangeSize:"+ICIDinRange.size());

        for (j=0;j < ICIDinRange.size();j++) {
            Log.d("ICIDinrange: ","i = "+ j + " value = " + ICIDinRange.get(j));
        }

            for (i=0;i < ICIDinRange.size();i++) {

                    lat = Double.parseDouble(ICID_list[ICIDinRange.get(i)][2]);
                    lng = Double.parseDouble(ICID_list[ICIDinRange.get(i)][1]);
                    tmp_result = Math.sqrt(((lat - gps_lat_1) * (lat - gps_lat_1)) + ((lng - gps_lng_1) * (lng - gps_lng_1)));
                    if (tmp_result < result) {
                        result = tmp_result;
                        correctRowNum = ICIDinRange.get(i);
                    }
            }

        return correctRowNum;
    }

    /*以上是抓ICID的部分*/

    /*以下抓方位角 20180808尚未完成*/

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
