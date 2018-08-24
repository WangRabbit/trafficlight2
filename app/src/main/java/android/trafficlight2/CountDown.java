package android.trafficlight2;

import android.os.CountDownTimer;
import android.widget.TextView;
import java.util.Calendar;
import static android.util.Log.d;

/**
 * Created by user on 2018/5/9.
 */

public class CountDown{

    private int compensateStatus = 0;
    private Calendar c = Calendar.getInstance();

    public int getWeekday() {

        int weekday;

        weekday = c.get(Calendar.DAY_OF_WEEK);
        if (weekday != 1) {
            weekday = weekday - 1;
        } else {
            weekday = 7;
        }
        d("weekday ", "" + weekday);

        return weekday;
    }

    public int calSegment() {
        int hour;
        int min;

        int segment = 1;

        hour = (c.get(Calendar.HOUR_OF_DAY));
        min = c.get(Calendar.MINUTE);

        switch (hour) {  //分時間段，未來要改寫
            case 0:
                segment = 1;
                break;
            case 1:
                segment = 2;
                break;
            case 2:
                segment = 2;
                break;
            case 3:
                segment = 2;
                break;
            case 4:
                segment = 2;
                break;
            case 5:
                segment = 2;
                break;
            case 6:
                segment = 3;
                break;
            case 7:
                segment = 4;
                break;
            case 8:
                if (min < 15) {
                    segment = 4;
                } else if (15 <= min && min < 45) {
                    segment = 5;
                } else {
                    segment = 6;
                }
                break;
            case 9:
                segment = 6;
                break;
            case 10:
                segment = 6;
                break;
            case 11:
                if (min < 45) {
                    segment = 6;
                } else {
                    segment = 7;
                }
                break;
            case 12:
                if (min < 30) {
                    segment = 7;
                } else {
                    segment = 8;
                }
                break;
            case 13:
                if (min < 15) {
                    segment = 8;
                } else {
                    segment = 9;
                }
                break;
            case 14:
                segment = 9;
                break;
            case 15:
                segment = 9;
                break;
            case 16:
                segment = 10;
                break;
            case 17:
                segment = 11;
                break;
            case 18:
                if (min < 45) {
                    segment = 11;
                } else {
                    segment = 12;
                }
                break;
            case 19:
                segment = 12;
                break;
            case 20:
                segment = 12;
                break;
            case 21:
                segment = 13;
                break;
            case 22:
                if (min < 15) {
                    segment = 13;
                } else {
                    segment = 14;
                }
                break;
            case 23:
                segment = 15;
                break;
        }

        return segment;

    }

    public int getCompensateStatus() {
        return compensateStatus;
    }

    private void checkCompensate(int g_min,int boundTime) {
        if (boundTime <=  g_min && g_min <= boundTime + 4) {
            compensateStatus = 1;
        } else {
            compensateStatus = 0;
        }
    }


}
