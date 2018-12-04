package android.trafficlight2;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.view.View;
import android.widget.Toast;


import java.util.Calendar;

import static android.trafficlight2.R.drawable.green;
import static android.trafficlight2.R.drawable.red;
import static android.trafficlight2.R.drawable.yello;
import static android.util.Log.d;
import static android.util.Log.e;

public class TrafficSignalCountDown extends MainActivity{
    protected String deviceID;
    protected String phaseID,planID;
    protected int green_sec, yellow_sec, allred_sec,red_sec,cycle;
    protected int segment,offset,direction,phaseCount;
    protected String phaseOrder;

    private int timeLength;

    private Calendar calendar = Calendar.getInstance();
    private int[] countStatus = {0,0,0,0,0,0};

    private int startHour,startMin;

    public TrafficSignalCountDown() {

    }

    public void setDeviceID(String mDeviceID) {
        this.deviceID = mDeviceID;
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

    public void setDirection(int mDirection) {
        this.direction = mDirection;
    }

    public void setPhaseCount(int mPhaseCount) {
        this.phaseCount = mPhaseCount;
    }

    public void setPhaseOrder(String mPhaseOrder) {
        this.phaseOrder = mPhaseOrder;
    }

    /*
    public void setTimeLength(int mTimeLength) {
        this.timeLength = mTimeLength;
    } */


    public void calTimeLength() {

        int hour, min,sec;

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        sec = calendar.get(Calendar.SECOND);

        timeLength = ((((hour * 3600) + (min * 60) + sec) - ((startHour * 3600) + (startMin * 60))) - offset)%cycle;
        timeLength = timeLength % cycle;
    }


    //正常:第一分相
    public void calTime_First () {

        if (timeLength < green_sec) {

            timer_green = green_sec - timeLength;

            countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 3;

            count(countStatus[countStatusIndex]);

        } else if (green_sec <= timeLength && timeLength < (yellow_sec + green_sec)) {

            timer__yellow = (yellow_sec + green_sec) - timeLength;

            countStatus[4] = timer__yellow; //使用[4]表第一次運行要用不一樣的秒數
            countStatusIndex = 4;

            count(countStatus[countStatusIndex]);

        } else if ((green_sec + yellow_sec) <= timeLength) {

            timer_red = cycle - timeLength;

            countStatus[5] = timer_red; //使用[5]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;

            count(countStatus[countStatusIndex]);
        }

        //SetText


    }

    //正常:第二分相
    public void calTime_middle_2 (int g_phaseNUM) {

        if (timeLength < (s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM])) {

            timer_red = (s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + s_allred_sec[g_phaseNUM]) - timeLength;
            d("特殊紅燈秒數 ", "" + (s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]));
            d("倒數 ", "" + timer_red);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);


        } else if ((s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + s_allred_sec[g_phaseNUM]) <= timeLength && timeLength < (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + s_allred_sec[g_phaseNUM])) {

            timer_green = (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + s_allred_sec[g_phaseNUM]) - timeLength;

            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 3;
            count(countStatus[countStatusIndex]);


        } else if ((s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM]+ s_allred_sec[g_phaseNUM]) <= timeLength && timeLength < (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + yellow_sec + s_allred_sec[g_phaseNUM])) {

            timer__yellow = (s_green_sec[g_phaseNUM] + green_sec + s_yellow_sec[g_phaseNUM] + yellow_sec + s_allred_sec[g_phaseNUM]) - timeLength;
            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 4;
            count(countStatus[countStatusIndex]);

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
            count(countStatus[countStatusIndex]);
        }



    }

    //特殊:phase 1 早開的分相
    public void calTime_earlyOPEN_first (int g_phaseNUM) {

        if (timeLength < (green_sec + s_green_sec[g_phaseNUM])) {

            timer_green = green_sec + s_green_sec[g_phaseNUM] - timeLength;

            countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 3;

            Log.e("phase 1 早開的分相", "");
            Log.d("綠燈秒數 ", "green_sec" + green_sec + "s_green_sec" + s_green_sec[g_phaseNUM]);
            d("倒數 ", "" + timer_green);

            d("countStatusIndex", "" + countStatusIndex);
            d("countStatus", "" + countStatus[countStatusIndex]);
            count(countStatus[countStatusIndex]);


        } else if ((green_sec + s_green_sec[g_phaseNUM]) <= timeLength && timeLength < (green_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM])) {

            timer__yellow = (green_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]) - timeLength;

            countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 4;

            Log.e("phase 1 早開的分相", "");
            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);
            d("countStatusIndex", "" + countStatusIndex);
            d("countStatus", "" + countStatus[countStatusIndex]);

            count(countStatus[countStatusIndex]);


        } else if ((green_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]) <= timeLength) {

            timer_red = cycle - timeLength;

            Log.e("phase 1 早開的分相", "");
            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);

            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);
        }
    }

    //特殊:phase 1 遲閉
    public void calTime_lateCLOSE_first (int g_phaseNUM) { //g_phaseNUM 要參照的

        if (timeLength < (green_sec + yellow_sec + s_green_sec[g_phaseNUM])) {

            timer_green = (green_sec + yellow_sec + s_green_sec[g_phaseNUM]) - timeLength;

            countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 3;

            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);

            d("countStatusIndex", "" + countStatusIndex);
            d("countStatus", "" + countStatus[countStatusIndex]);
            count(countStatus[countStatusIndex]);


        } else if ((green_sec + yellow_sec + s_green_sec[g_phaseNUM]) <= timeLength && timeLength < (green_sec + yellow_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM])) {

            timer__yellow = (green_sec + yellow_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]) - timeLength;

            countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 4;

            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);
            d("countStatusIndex", "" + countStatusIndex);
            d("countStatus", "" + countStatus[countStatusIndex]);

            count(countStatus[countStatusIndex]);


        } else if ((green_sec + yellow_sec + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]) <= timeLength) {

            timer_red = cycle - timeLength;
            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);

            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);
        }
    }

    //特殊:中間的phase  遲閉
    public void calTime_lateCLOSE_middle (int g_phaseNUM) {

        if (timeLength < s_green_sec[0] ) {

            timer_red = s_green_sec[0] - timeLength;
            d("倒數 ", "" + timer_red);


            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);


        } else if (s_green_sec[0] <= timeLength && timeLength < (s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec)) {

            timer_green = (s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec) - timeLength;
            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 3;
            count(countStatus[countStatusIndex]);


        } else if ((s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec) <= timeLength && timeLength < (s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec + yellow_sec)) {

            timer__yellow = (s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec + yellow_sec) - timeLength;
            //  Log.d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);
            d("s_green_sec ", "" + s_green_sec[g_phaseNUM]);
            d("s_yellow_sec ", "" + s_yellow_sec[g_phaseNUM]);
            d("s_allred_sec ", "" + s_allred_sec[g_phaseNUM]);

            countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 4;
            count(countStatus[countStatusIndex]);

        } else if ((s_green_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM] + green_sec + yellow_sec) <= timeLength) {

            timer_red = cycle - timeLength;
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
    public void calTime_earlyOPEN_middle (int g_phaseNUM) {

        if (timeLength < red_sec ) {

            timer_red = red_sec - timeLength;
            d("倒數 ", "" + timer_red);


            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);


        } else if (red_sec <= timeLength && timeLength < (red_sec+ s_green_sec[g_phaseNUM] + green_sec + yellow_sec)) {

            timer_green = (red_sec+ s_green_sec[g_phaseNUM] + green_sec + yellow_sec) - timeLength;
            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);

            countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 3;
            count(countStatus[countStatusIndex]);


        } else if ((red_sec+ s_green_sec[g_phaseNUM] + green_sec + yellow_sec) <= timeLength) {

            timer__yellow = cycle - timeLength;
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
    public void calTime_middle_3 (int g_phaseNUM) {

        if (timeLength < (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM])) {

            timer_red = (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM]) - timeLength;
            d("特殊紅燈秒數 ", "" + (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM]));
            d("倒數 ", "" + timer_red);

            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);

        } else if ((s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM]) <= timeLength && timeLength < (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec)) {

            timer_green = (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec) - timeLength;
            d("特殊綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);

            countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 3;
            count(countStatus[countStatusIndex]);


        } else if ((s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec) <= timeLength && timeLength < (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec + yellow_sec)) {

            timer__yellow = (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec + yellow_sec) - timeLength;
            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);

            countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 4;
            count(countStatus[countStatusIndex]);

        } else if ((s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec + yellow_sec ) <= timeLength) {

            timeLength = timeLength - (s_green_sec[0] + s_yellow_sec[0] + s_allred_sec[0] + s_green_sec[g_phaseNUM] + s_yellow_sec[g_phaseNUM]+s_allred_sec[g_phaseNUM] + green_sec + yellow_sec);
            timer_red = red_sec - timeLength;
            //     timer_red = cycle - timeLength;
            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);

            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);
        }


    }

    //正常:最後一個分相
    public void calTime_Last() {
        if (timeLength < (red_sec - allred_sec)) {

            timer_red = red_sec - allred_sec - timeLength;
            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);

            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);

        } else if ((red_sec - allred_sec) <= timeLength && timeLength < ((red_sec - allred_sec) + green_sec)) {

            timer_green = ((red_sec - allred_sec) + green_sec) - timeLength;
            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);

            countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 3;
            count(countStatus[countStatusIndex]);


        } else if ( ((red_sec - allred_sec) + green_sec) <= timeLength && timeLength < ((red_sec - allred_sec) + green_sec + yellow_sec)) {

            timer__yellow = ((red_sec - allred_sec) + green_sec + yellow_sec) - timeLength;
            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);

            countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 4;
            count(countStatus[countStatusIndex]);

        } else if ( ((red_sec - allred_sec) + green_sec + yellow_sec) <= timeLength ) {
            timeLength = timeLength - ((red_sec - allred_sec) + green_sec + yellow_sec);
            timer_red = red_sec - timeLength;

            countStatus[5] = timer_red;
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);
        }

        /*SetText*/


    }

    //特殊:最後一個分相遲閉
    public void calTime_Last_lateCLOSE() {
        if (timeLength < s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] ) {

            timer_red = s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] - timeLength;
            d("紅燈秒數 ", "" + red_sec);
            d("倒數 ", "" + timer_red);

            countStatus[5] = timer_red; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 5;
            count(countStatus[countStatusIndex]);

        } else if (s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] <= timeLength && timeLength < (s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] + s_green_sec[2] + s_yellow_sec[2] + green_sec)) {

            timer_green = (s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] + s_green_sec[2] + s_yellow_sec[2] + green_sec) - timeLength;
            d("綠燈秒數 ", "" + green_sec);
            d("倒數 ", "" + timer_green);

            countStatus[3] = timer_green; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 3;
            count(countStatus[countStatusIndex]);


        } else if ((s_green_sec[0] + s_green_sec[1] + s_yellow_sec[1] + s_green_sec[2] + s_yellow_sec[2] + green_sec)  <= timeLength) {

            timer__yellow = cycle - timeLength;
            d("黃燈秒數 ", "" + yellow_sec);
            d("倒數 ", "" + timer__yellow);

            countStatus[4] = timer__yellow; //使用[3]表第一次運行要用不一樣的秒數
            countStatusIndex = 4;
            count(countStatus[countStatusIndex]);

        }
    }


    public void start() {
        if (cycle != 0 && !phaseOrder.equals("B0") && !phaseOrder.equals("b0")) { //先確認CYCLE不是0 或 閃光時相(b0)

            d("startHour ", "" + startHour); //依照startHour和startMin開始計算
            d("startMin ", "" + startMin); //依照startHour和startMin開始計算

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
            count(111111);
            countdown_txt.setText("閃光");
            threadFlag = 0;
        }

    }

    public void phaseCount_2() {

        switch (Integer.parseInt(phaseID)) {
            case 1:
                calTimeLength();
                calTime_First();
                break;

            case 2:
                calTimeLength();
                calTime_Last();
                break;
        }
    }

    public void phaseCount_3() {
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

    public void phaseCount_4(String gPhaseOrder) {

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
    
    public int gettimeLength() {
        return timeLength;
    }


}
