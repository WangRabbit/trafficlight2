package android.trafficlight2;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MyCountDownTimer {
    private long millisInFuture;
    private long countDownInterval;
    private boolean status;
    public MyCountDownTimer(long pMillisInFuture, long pCountDownInterval) {
        this.millisInFuture = pMillisInFuture;
        this.countDownInterval = pCountDownInterval;
        status = false;
        Initialize();
    }

    public void Stop() {
        status = false;
    }

    public long getCurrentTime() {
        return millisInFuture;
    }

    public void Start() {
        status = true;
    }
    public void Initialize()
    {
        final Handler handler = new Handler();
        Log.v("status", "starting");
        final Runnable counter = new Runnable(){

            public void run(){
                long sec = millisInFuture/1000;
                if(status) {
                    if(millisInFuture <= 0) {
                        Log.v("status", "done");
                    } else {

                        Log.v("status", Long.toString(sec) + " seconds remain");
                        millisInFuture -= countDownInterval;
                        handler.postDelayed(this, countDownInterval);
                    }
                } else {
                    Log.v("status", Long.toString(sec) + " seconds remain and timer has stopped!");
                    handler.postDelayed(this, countDownInterval);
                }
            }
        };

        handler.postDelayed(counter, countDownInterval);
    }
}
