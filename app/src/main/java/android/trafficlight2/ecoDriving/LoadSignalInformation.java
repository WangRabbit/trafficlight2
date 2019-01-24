package android.trafficlight2.ecoDriving;

import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import java.util.List;

import static android.trafficlight2.R.drawable.no_light;
import static android.util.Log.d;
import static android.util.Log.e;




public class LoadSignalInformation extends AsyncTask<String, Integer, String[]> {

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

    public LoadSignalInformationResult<String[]> loadSignalInformationResult = null;


    protected String[] doInBackground(String... args) { //傳入參數

        String[] planResult = new String[16];
        int i;
        int red_sec,cycle,green_sec,yellow_sec;


        List<NameValuePair> params = new ArrayList<NameValuePair>(); //利用http傳送資料進去
        params.add(new BasicNameValuePair("ICID", args[0])); //ICID
        params.add(new BasicNameValuePair("direction", args[1])); //現在是傳方向代號
        params.add(new BasicNameValuePair("segment", args[2]));
        params.add(new BasicNameValuePair("weekday", args[3]));

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
                    planResult[0] = c.getString(TAG_DEVICEID);
                    d("deviceID", "" + c.getString(TAG_DEVICEID));
                    planResult[1] = c.getString(TAG_ICID);
                    d("ICID", "" + c.getString(TAG_ICID));
                    planResult[2] = c.getString(TAG_ICNAME);
                    d("icName", "" + c.getString(TAG_ICNAME));
                    planResult[3] = c.getString(TAG_PHASEID);
                    d("PHASEID", "" + c.getString(TAG_PHASEID));
                    planResult[4] = String.valueOf(c.getInt(TAG_greentime));
                    d("GREEN", "" + c.getString(TAG_greentime));
                    planResult[5] = String.valueOf(c.getInt(TAG_YELLOW));
                    d("yellow", "" + c.getString(TAG_YELLOW));
                    planResult[6] = String.valueOf(c.getInt(TAG_ALLRED));
                    d("ALLred", "" + c.getString(TAG_ALLRED));
                    planResult[7] = String.valueOf(c.getInt(TAG_CYCLE));
                    d("cycle", "" + c.getString(TAG_CYCLE));
                    // segment = c.getInt(TAG_SEGMENT);
                    // d("segment", "" + c.getString(TAG_SEGMENT));
                    planResult[8] = c.getString(TAG_PLANID);
                    d("planID", "" + c.getString(TAG_PLANID));
                    planResult[9] = String.valueOf(c.getInt(TAG_OFFSET));
                    d("offset", "" + c.getString(TAG_OFFSET));
                    planResult[10] = c.getString(TAG_DIRECTION);
                    d("direction", "" + c.getString(TAG_DIRECTION));
                    planResult[11] = String.valueOf(c.getInt(TAG_PHASECOUNT));
                    d("phaseCount", "" + c.getString(TAG_PHASECOUNT));
                    planResult[12] = String.valueOf(c.getString(TAG_PHASEORDER));
                    d("phaseOrder", "" + c.getString(TAG_PHASEORDER));
                }

                cycle = Integer.valueOf(planResult[7]);
                green_sec = Integer.valueOf(planResult[4]);
                yellow_sec = Integer.valueOf(planResult[5]);
                red_sec = cycle - green_sec - yellow_sec; //換算紅燈時間

                planResult[13] = String.valueOf(red_sec); //紅燈

                planResult[14] = "OK";


            } else  {
                planResult[14] = "Fail";
                planResult[15] = String.valueOf(success);
                //threadFlag = 0;
            }
        } catch (JSONException e) {
            planResult[14] = "Exception";

        }

        return planResult;

    }


    protected void onPostExecute(String[] result) {
        //執行後 完成背景任務
        this.loadSignalInformationResult.signalResult(result);
        System.out.print("@@@@@@@@@@@@@@@@@");

    }


}


