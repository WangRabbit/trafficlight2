package android.trafficlight2.ecoDriving;

import android.content.Context;
import android.content.Intent;
import android.trafficlight2.JSONParser;
import android.trafficlight2.MainActivity;
import android.trafficlight2.R;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.support.v4.content.ContextCompat.startActivity;
import static android.trafficlight2.R.drawable.no_light;
import static android.util.Log.d;
import static android.util.Log.e;

public class RSU extends MainActivity implements LoadSignalInformationResult<String[]>{

    //RSU基本資訊
    public boolean RSU_status;
    public boolean getInformationStatus;


    //RSU號誌資料
    private String deviceID,ICID,icName;
    private String phaseID,planID;
    private int green_sec, yellow_sec, allred_sec,red_sec,cycle;
    private int segment,offset,phaseCount;
    private String phaseOrder;

    //計算倒數秒數用(暫時不移至子類別trafficSignalCountDown)
    private int countStatusIndex;
    private int[] countStatus = {0,0,0,0,0,0};



    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    // url to get all products list

    private TrafficSignalCountDown tscd = new TrafficSignalCountDown();

    private static String url_S_getLightData = "http://140.116.97.98/traffic_light/get_S_newLightData.php"; //192.168.0.100

    private Calendar calendar = Calendar.getInstance();


    private int startHour,startMin;

    //紀錄時間分段
    int[][] timeArray = {{0, 1, 6, 7, 8, 8, 11, 12, 13, 16, 17, 18, 21, 22, 23},
            {0, 0, 0, 0, 15, 45, 45, 30, 15, 00, 00, 45, 00, 15, 00}};

    private Context context;

    public RSU(Context context) {

        this.context = context;
    }

    private void showToast(String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public int getCountStatusIndex() {
        return this.countStatusIndex; //返回給予UI顯示秒數
    }

    public int[] getCountStatus() {
        return this.countStatus; //返回給予UI顯示秒數
    }

    public void setDeviceID(String mDeviceID) {
        this.deviceID = mDeviceID;
    }

    public void setICID(String mICID) {
        this.ICID = mICID;
    }

    public void setPhaseID(String mPhaseID) {
        this.phaseID = mPhaseID;
    }

    public void setGreen_sec(int mGreen_sec) {
        this.green_sec = mGreen_sec;
        countStatus[0] = mGreen_sec;
    }

    public void setYellow_sec(int mYellow_sec) {
        this.yellow_sec = mYellow_sec;
        countStatus[1] = mYellow_sec;
    }

    public void setAllred_sec(int mAllred_sec) {
        this.allred_sec = mAllred_sec;
    }

    public void setRed_sec(int mRed_sec) {
        this.red_sec = mRed_sec;
        countStatus[2] = mRed_sec;
    }

    public void setCycle(int mCycle) {
        this.cycle = mCycle;
    }

    public void setSegment(int mSegment) {
        this.segment = mSegment;
    }

    public void setPlanID(String mPlanID) {
        this.planID = mPlanID;
    }

    public void setOffset(int mOffset) {
        this.offset = mOffset;
    }


    public void setPhaseCount(int mPhaseCount) {
        this.phaseCount = mPhaseCount;
    }

    public void setPhaseOrder(String mPhaseOrder) {
        this.phaseOrder = mPhaseOrder;
    }

    public void start(String ICID,String inputDirection,int segment,int weekday) {
        this.segment = segment;

        LoadSignalInformation ld = new LoadSignalInformation();
        ld.loadSignalInformationResult = this;
        ld.execute(ICID,inputDirection,String.valueOf(segment),String.valueOf(weekday));



    }

    @Override
    public void signalResult(String[] planResult ) {

        this.getInformationStatus = true;
        Log.d("9999",""+this.getInformationStatus);

        if (planResult[14].equals("OK")) {

            System.out.println("OK");
            Toast.makeText(context, "OK",Toast.LENGTH_LONG).show();

            this.deviceID = planResult[0];
            this.ICID = planResult[1];
            this.icName = planResult[2];
            this.phaseID = planResult[3];
            this.green_sec = Integer.valueOf(planResult[4]);
            this.yellow_sec = Integer.valueOf(planResult[5]);
            this.allred_sec = Integer.valueOf(planResult[6]);
            this.red_sec = Integer.valueOf(planResult[13]);
            this.cycle = Integer.valueOf(planResult[7]);
            this.planID = planResult[8];
            this.offset = Integer.valueOf(planResult[9]);
          //  this.direction = planResult[10];
            this.phaseCount = Integer.valueOf(planResult[11]);
            this.phaseOrder = planResult[12];

            tscd.start();

        } else if (planResult[14].equals("Fail")) {

            System.out.println("Fail");
            Toast.makeText(context, "代號"+ICID+",方向"+planResult[16]+"錯誤碼:"+ planResult[15],Toast.LENGTH_LONG).show();

            RSU_status = false;

        } else {
            System.out.println("Exception");
            Toast.makeText(context, "發生例外事件 ICID = "+ ICID +"方向 = "+ planResult[16] , Toast.LENGTH_LONG).show();

            RSU_status = false;
        }

        startHour = timeArray[0][segment - 1];
        startMin = timeArray[1][segment - 1];

        countStatus[0] = this.green_sec;
        countStatus[1] = this.yellow_sec;
        countStatus[2] = this.red_sec;
        Log.d("countStatus[0]",""+ countStatus[0]);
        Log.d("countStatus[1]",""+ countStatus[1]);
        Log.d("countStatus[2]",""+ countStatus[2]);


    }




    public class TrafficSignalCountDown {

        public RSUstatus status;

        //計算倒數秒數用
        private int timeLength;
        private int timer_green;
        private int timer__yellow;
        private int timer_red;
        int[] s_green_sec = {0,0,0,0,0,0}; //最多6個分相
        int[] s_yellow_sec = {0,0,0,0,0,0};
        int[] s_allred_sec = {0,0,0,0,0,0};

        JSONArray getPhase = null;
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_PRODUCTS = "product";
        private static final String TAG_YELLOW = "yellow";
        private static final String TAG_ALLRED = "allred";
        private static final String TAG_greentime = "greentime";


        public void calTimeLength() {

            int hour, min,sec;

            hour = calendar.get(Calendar.HOUR_OF_DAY);
            min = calendar.get(Calendar.MINUTE);
            sec = calendar.get(Calendar.SECOND);

            timeLength = ((((hour * 3600) + (min * 60) + sec) - ((startHour * 3600) + (startMin * 60))) - offset)%cycle;
            timeLength = timeLength % cycle;
        }

        //正常:第一分相
        private void calTime_First () {

            if (timeLength < green_sec) {

                timer_green = green_sec - timeLength;

                countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 3;

                //// count(countStatus[countStatusIndex]);



            } else if (green_sec <= timeLength && timeLength < (yellow_sec + green_sec)) {

                timer__yellow = (yellow_sec + green_sec) - timeLength;

                countStatus[4] = timer__yellow; //使用[4]表第一次運行要用不一樣的秒數
                countStatusIndex = 4;

               // // count(countStatus[countStatusIndex]);

            } else if ((green_sec + yellow_sec) <= timeLength) {

                timer_red = cycle - timeLength;

                countStatus[5] = timer_red; //使用[5]表第一次運行要用不一樣的秒數
                countStatusIndex = 5;

               // // count(countStatus[countStatusIndex]);

            }


        }

        //正常:第二分相
        private void calTime_middle_2 (int g_phaseNUM) {

            if (timeLength < (s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM])) {

                timer_red = (s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + s_allred_sec[g_phaseNUM]) - timeLength;
                d("特殊紅燈秒數 ", "" + (s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]));
                d("倒數 ", "" + timer_red);
                d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
                d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
                d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

                countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 5;
                //// count(countStatus[countStatusIndex]);


            } else if ((s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + s_allred_sec[g_phaseNUM]) <= timeLength && timeLength < (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + s_allred_sec[g_phaseNUM])) {

                timer_green = (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + s_allred_sec[g_phaseNUM]) - timeLength;

                d("綠燈秒數 ", "" + green_sec);
                d("倒數 ", "" + timer_green);
                d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
                d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
                d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

                countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 3;
                //// count(countStatus[countStatusIndex]);


            } else if ((s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM]+ s_allred_sec[g_phaseNUM]) <= timeLength && timeLength < (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + yellow_sec + s_allred_sec[g_phaseNUM])) {

                timer__yellow = (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + yellow_sec + s_allred_sec[g_phaseNUM]) - timeLength;
                d("黃燈秒數 ", "" + yellow_sec);
                d("倒數 ", "" + timer__yellow);
                d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
                d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
                d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

                countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 4;
               // // count(countStatus[countStatusIndex]);

            } else if ((s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + yellow_sec + s_allred_sec[g_phaseNUM]) <= timeLength) {

                // timer_red = cycle - timeLength;

                timeLength = timeLength - (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + yellow_sec + s_allred_sec[g_phaseNUM]);
                timer_red = red_sec - timeLength;

                d("紅燈秒數 ", "" + red_sec);
                d("倒數 ", "" + timer_red);
                d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
                d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
                d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

                countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 5;
                //// count(countStatus[countStatusIndex]);
            }



        }

        //特殊:phase 1 早開的分相
        private void calTime_earlyOPEN_first (int g_phaseNUM) {

            if (timeLength < (green_sec + s_green_sec[g_phaseNUM])) {

                timer_green = green_sec + s_green_sec[g_phaseNUM] - timeLength;

                countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 3;

                Log.e("phase 1 早開的分相", "");
                Log.d("綠燈秒數 ", "green_sec" + green_sec + "s_green_sec" + s_green_sec[g_phaseNUM]);
                d("倒數 ", "" + timer_green);

                d("countStatusIndex", "" + countStatusIndex);
                d("countStatus", "" + countStatus[countStatusIndex]);
                //// count(countStatus[countStatusIndex]);


            } else if ((green_sec + s_green_sec[g_phaseNUM]) <= timeLength && timeLength < (green_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM])) {

                timer__yellow = (green_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]) - timeLength;

                countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 4;

                Log.e("phase 1 早開的分相", "");
                d("黃燈秒數 ", "" + yellow_sec);
                d("倒數 ", "" + timer__yellow);
                d("countStatusIndex", "" + countStatusIndex);
                d("countStatus", "" + countStatus[countStatusIndex]);

                //// count(countStatus[countStatusIndex]);


            } else if ((green_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]) <= timeLength) {

                timer_red = cycle - timeLength;

                Log.e("phase 1 早開的分相", "");
                d("紅燈秒數 ", "" + red_sec);
                d("倒數 ", "" + timer_red);

                countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 5;
                //// count(countStatus[countStatusIndex]);
            }
        }

        //特殊:phase 1 遲閉
        private void calTime_lateCLOSE_first (int g_phaseNUM) { //g_phaseNUM 要參照的

            if (timeLength < (green_sec + yellow_sec + s_green_sec[g_phaseNUM])) {

                timer_green = (green_sec + yellow_sec + s_green_sec[g_phaseNUM]) - timeLength;

                countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 3;

                d("綠燈秒數 ", "" + green_sec);
                d("倒數 ", "" + timer_green);

                d("countStatusIndex", "" + countStatusIndex);
                d("countStatus", "" + countStatus[countStatusIndex]);
                //// count(countStatus[countStatusIndex]);


            } else if ((green_sec + yellow_sec + s_green_sec[g_phaseNUM]) <= timeLength && timeLength < (green_sec + yellow_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM])) {

                timer__yellow = (green_sec + yellow_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]) - timeLength;

                countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 4;

                d("黃燈秒數 ", "" + yellow_sec);
                d("倒數 ", "" + timer__yellow);
                d("countStatusIndex", "" + countStatusIndex);
                d("countStatus", "" + countStatus[countStatusIndex]);

                //// count(countStatus[countStatusIndex]);


            } else if ((green_sec + yellow_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]) <= timeLength) {

                timer_red = cycle - timeLength;
                d("紅燈秒數 ", "" + red_sec);
                d("倒數 ", "" + timer_red);

                countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 5;
                //// count(countStatus[countStatusIndex]);
            }
        }

        //特殊:中間的phase  遲閉
        private void calTime_lateCLOSE_middle (int g_phaseNUM) {

            if (timeLength < s_green_sec[0] ) {

                timer_red = s_green_sec[0] - timeLength;
                d("倒數 ", "" + timer_red);


                countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 5;
                //// count(countStatus[countStatusIndex]);


            } else if (s_green_sec[0] <= timeLength && timeLength < (s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec)) {

                timer_green = (s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec) - timeLength;
                d("綠燈秒數 ", "" + green_sec);
                d("倒數 ", "" + timer_green);
                d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
                d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
                d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

                countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 3;
                //// count(countStatus[countStatusIndex]);


            } else if ((s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec) <= timeLength && timeLength < (s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec + yellow_sec)) {

                timer__yellow = (s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec + yellow_sec) - timeLength;
                //  Log.d("黃燈秒數 ", "" + yellow_sec);
                d("倒數 ", "" + timer__yellow);
                d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
                d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
                d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

                countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 4;
                //// count(countStatus[countStatusIndex]);

            } else if ((s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec + yellow_sec) <= timeLength) {

                timer_red = cycle - timeLength;
                d("紅燈秒數 ", "" + red_sec);
                d("倒數 ", "" + timer_red);
                d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
                d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
                d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

                countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 5;
                //// count(countStatus[countStatusIndex]);
            }
        }

        //特殊:中間的phase  早開
        private void calTime_earlyOPEN_middle (int g_phaseNUM) {

            if (timeLength < red_sec ) {

                timer_red = red_sec - timeLength;
                d("倒數 ", "" + timer_red);


                countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 5;
                //// count(countStatus[countStatusIndex]);


            } else if (red_sec <= timeLength && timeLength < (red_sec+ s_green_sec[g_phaseNUM] + green_sec + yellow_sec)) {

                timer_green = (red_sec+ s_green_sec[g_phaseNUM] + green_sec + yellow_sec) - timeLength;
                d("綠燈秒數 ", "" + green_sec);
                d("倒數 ", "" + timer_green);

                countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 3;
                //// count(countStatus[countStatusIndex]);


            } else if ((red_sec+ s_green_sec[g_phaseNUM] + green_sec + yellow_sec) <= timeLength) {

                timer__yellow = cycle - timeLength;
                //  Log.d("黃燈秒數 ", "" + yellow_sec);
                d("倒數 ", "" + timer__yellow);
                d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
                d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
                d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

                countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 4;
                //// count(countStatus[countStatusIndex]);
            }
        }

        //正常:第三個分相
        private void calTime_middle_3 (int g_phaseNUM) {

            if (timeLength < (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM])) {

                timer_red = (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM]) - timeLength;
                d("特殊紅燈秒數 ", "" + (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM]));
                d("倒數 ", "" + timer_red);

                countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 5;
                //// count(countStatus[countStatusIndex]);

            } else if ((s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM]) <= timeLength && timeLength < (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec)) {

                timer_green = (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec) - timeLength;
                d("特殊綠燈秒數 ", "" + green_sec);
                d("倒數 ", "" + timer_green);

                countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 3;
                // count(countStatus[countStatusIndex]);


            } else if ((s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec) <= timeLength && timeLength < (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec + yellow_sec)) {

                timer__yellow = (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec + yellow_sec) - timeLength;
                d("黃燈秒數 ", "" + yellow_sec);
                d("倒數 ", "" + timer__yellow);

                countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 4;
                //// count(countStatus[countStatusIndex]);

            } else if ((s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec + yellow_sec ) <= timeLength) {

                timeLength = timeLength - (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec + yellow_sec);
                timer_red = red_sec - timeLength;
                //     timer_red = cycle - timeLength;
                d("紅燈秒數 ", "" + red_sec);
                d("倒數 ", "" + timer_red);

                countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 5;
                //// count(countStatus[countStatusIndex]);
            }


        }

        //正常:最後一個分相
        private void calTime_Last() {
            if (timeLength < (red_sec - allred_sec)) {

                timer_red = red_sec - allred_sec - timeLength;
                d("紅燈秒數 ", "" + red_sec);
                d("倒數 ", "" + timer_red);

                countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 5;
               // // count(countStatus[countStatusIndex]);

            } else if ((red_sec - allred_sec) <= timeLength && timeLength < ((red_sec - allred_sec) + green_sec)) {

                timer_green = ((red_sec - allred_sec) + green_sec) - timeLength;
                d("綠燈秒數 ", "" + green_sec);
                d("倒數 ", "" + timer_green);

                countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 3;
               // // count(countStatus[countStatusIndex]);


            } else if ( ((red_sec - allred_sec) + green_sec) <= timeLength && timeLength < ((red_sec - allred_sec) + green_sec + yellow_sec)) {

                timer__yellow = ((red_sec - allred_sec) + green_sec + yellow_sec) - timeLength;
                d("黃燈秒數 ", "" + yellow_sec);
                d("倒數 ", "" + timer__yellow);

                countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 4;
                // count(countStatus[countStatusIndex]);

            } else if ( ((red_sec - allred_sec) + green_sec + yellow_sec) <= timeLength ) {
                timeLength = timeLength - ((red_sec - allred_sec) + green_sec + yellow_sec);
                timer_red = red_sec - timeLength;

                countStatus[5] = timer_red;
                countStatusIndex = 5;
              //  // count(countStatus[countStatusIndex]);
            }




        }

        //特殊:最後一個分相遲閉
        private void calTime_Last_lateCLOSE() {
            if (timeLength < s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] ) {

                timer_red = s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] - timeLength;
                d("紅燈秒數 ", "" + red_sec);
                d("倒數 ", "" + timer_red);

                countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 5;
                // count(countStatus[countStatusIndex]);

            } else if (s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] <= timeLength && timeLength < (s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] + s_green_sec[2] + s_yellow_sec[2] + green_sec)) {

                timer_green = (s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] + s_green_sec[2] + s_yellow_sec[2] + green_sec) - timeLength;
                d("綠燈秒數 ", "" + green_sec);
                d("倒數 ", "" + timer_green);

                countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 3;
                // count(countStatus[countStatusIndex]);


            } else if ((s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] + s_green_sec[2] + s_yellow_sec[2] + green_sec)  <= timeLength) {

                timer__yellow = cycle - timeLength;
                d("黃燈秒數 ", "" + yellow_sec);
                d("倒數 ", "" + timer__yellow);

                countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
                countStatusIndex = 4;
                // count(countStatus[countStatusIndex]);

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

                    int i ;

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
                            System.out.print("12121212");

                        }
                    } catch (JSONException e) {
                        System.out.print("exception");

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


        public void start() {


            Log.e("tsdc start","tsdc start");
            Log.e("cycle",String.valueOf(cycle));
            Log.e("phaseOrder ",String.valueOf(phaseOrder));

            if (cycle != 0 && !phaseOrder.equals("B0") && !phaseOrder.equals("b0")) { //先確認CYCLE不是0 或 閃光時相(b0)

                RSU_status = true; //表示號誌可用
                Log.e("RSU status ",String.valueOf(RSU_status));

                switch(phaseCount) {
                    case 2: //二分相情況
                        phaseCount_2();
                        break;

                    case 3: //三分相情況
                        Log.e("我是三時相", "");
                        phaseCount_3();
                        break;

                    case 4:
                        phaseCount_4(phaseOrder);
                        break;
                }

            } else {
                RSU_status = false; //表示不可用

            }

        }

        private void phaseCount_2() {

            switch (Integer.parseInt(phaseID)) {
                case 1:
                    calTimeLength();
                    calTime_First();

                    break;

                case 2:
                    calTimeLength();
                    calTime_Last();

                   this.status.showSignal();


                    break;
            }
        }

        private void phaseCount_3() {
            Log.e("我是三時相", "");
            switch (Integer.parseInt(phaseID)) {
                case 1:
                    calTimeLength();
                    calTime_First();
                    break;

                case 2:
                    get_phase_theard();
                    calTimeLength();
                    calTime_middle_2(0);
                    break;

                case 3:
                    calTimeLength();
                    calTime_Last();
                    break;

            }
        }

        private void phaseCount_4(String gPhaseOrder) {

            if (gPhaseOrder.equals("1A")) { //1A 早開遲閉二時相

                Log.e("1A 早開遲閉二時相", ""); //四時相
                //   Toast.makeText(MainActivity.this, "1A 早開遲閉二時相",Toast.LENGTH_LONG).show();

                switch (Integer.parseInt(phaseID)) {
                    case 1:
                        get_phase_theard();
                        calTimeLength();
                        red_sec = cycle - s_green_sec[1] - s_yellow_sec[1] - green_sec; //重新計算紅燈秒數

                        countStatus[0] = green_sec + s_green_sec[1];
                        countStatus[1] = s_yellow_sec[1];
                        countStatus[2] = red_sec;
                        e("1A紅燈秒數:",""+red_sec);
                        calTime_earlyOPEN_first(1); //分相1早開 (參考第二分相秒數）
                        break;
                    case 2:
                        get_phase_theard();
                        calTimeLength();
                        calTime_middle_2(0);
                        break;
                    case 3:
                        get_phase_theard();
                        calTimeLength();
                        red_sec = cycle - s_green_sec[1] - s_yellow_sec[1] - green_sec - yellow_sec; //重新計算紅燈秒數

                        countStatus[0] = green_sec + s_green_sec[1] + s_yellow_sec[1];
                        countStatus[1] = yellow_sec;
                        countStatus[2] = red_sec;
                        e("1A紅燈秒數:",""+red_sec);
                        calTime_lateCLOSE_middle(1); //分相3遲閉 (參考第二分相秒數)
                        break;
                    case 4:
                        calTimeLength();
                        calTime_Last();
                        break;
                }

            } else {
                //    Toast.makeText(MainActivity.this, "我是正常四時相",Toast.LENGTH_LONG).show();

                switch (Integer.parseInt(phaseID)) {
                    case 1:
                        calTimeLength();
                        calTime_First();
                        break;
                    case 2:
                        get_phase_theard();
                        calTimeLength();
                        calTime_middle_2(0);
                        break;

                    case 3:
                        get_phase_theard();
                        calTimeLength();
                        calTime_middle_3(1);
                        break;

                    case 4:

                        if (gPhaseOrder.equals("D3")) { //for I0215 I0551
                            get_phase_theard();
                            calTimeLength();
                            countStatus[0] = green_sec + s_green_sec[2] + s_yellow_sec[2];
                            countStatus[1] = yellow_sec;
                            countStatus[2] = cycle - green_sec - yellow_sec - s_green_sec[2] - s_yellow_sec[2] ;

                            calTime_Last_lateCLOSE();

                        } else {
                            calTimeLength();
                            calTime_Last();
                        }
                        break;
                }
            }
        }




    }

}
