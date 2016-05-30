package net.net16.aplasergrid.alpakapro;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    String result = "";
    String ab;
    TextView textViewSafe;
    TextView textViewBroken;
    TimerTask task;
    Alarm alarm;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewBroken = (TextView) findViewById(R.id.textViewBroken);
        textViewSafe = (TextView) findViewById(R.id.textViewSafe);

        alarm = new Alarm(getApplicationContext());

        Timer timer = new Timer();
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                // do something on UI
            }
        };
         task = new TimerTask() {
            @SuppressWarnings("deprecation")
            @Override
            public void run () {
                new RetreiveData().execute();
                mHandler.obtainMessage(1).sendToTarget();
                Log.d("timer", "run: ");
            }

        };
        timer.schedule(task, 0, 2000);
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if(result.contentEquals("broken")){

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        textViewSafe.setVisibility(View.INVISIBLE);
                        textViewBroken.setVisibility(View.VISIBLE);

                        alarm.playSound();

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(R.drawable.alpaka)
                                        .setContentTitle("Broken")
                                        .setContentText("Laser has been broken!");

                        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                        stackBuilder.addParentStack(MainActivity.class);

// Adds the Intent that starts the Activity to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);

                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
                        mNotificationManager.notify(666, mBuilder.build());



                        Log.d("checkstate", "Broken");
                    }
                });


            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewSafe.setVisibility(View.VISIBLE);
                        textViewBroken.setVisibility(View.INVISIBLE);
                         Log.d("checkstate", "Not Broken");
                    }
                });

            }
        }
    };




    @SuppressWarnings("deprecation")
    public void reset(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://aplasergrid.net16.net/reset.php";
                    HttpClient client = new DefaultHttpClient();
                    HttpGet resetData = new HttpGet(url);
                    HttpResponse response;
                    response = client.execute(resetData);
                    HttpEntity entity = response.getEntity();

                    if(entity!=null){
                        InputStream inputStream = entity.getContent();
                        result = inputStream.toString();
                        inputStream.close();

                    }
                } catch(IOException e) {
                    e.printStackTrace();
                    Context context = getApplicationContext();
                    CharSequence text = "Something went wrong!!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        }).start();

        textViewSafe.setVisibility(View.VISIBLE);
        textViewBroken.setVisibility(View.INVISIBLE);
       alarm.stopSound();
    }

    private class RetreiveData extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... arg0) {
             /**TODO Auto-generated method stub**/
           /* jobj = jsonParser.makeHttpRequest("http://aplasergrid.net16.net/reset.php");

            try {
                ab = jobj.getString("key");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/

            String url = "http://aplasergrid.net16.net/check.php";
            HttpClient client = new DefaultHttpClient();
            HttpGet resetData = new HttpGet(url);
            HttpResponse response;
            try {

                response = client.execute(resetData);
                HttpEntity entity = response.getEntity();
                if(entity!=null){
                    String temp = EntityUtils.toString(entity);
                    ab = temp.substring(1,7);
                    StringBuilder sb = new StringBuilder();
                    sb.append(ab);


                    result = sb.toString();

                    Log.d("Laser State", result);

                }


                //textViewSafe.setVisibility(View.VISIBLE);
                //textViewBroken.setVisibility(View.INVISIBLE);
            } catch(IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      
                        Context context = getApplicationContext();
                        CharSequence text = "Something went wrong!!";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                });

            }


            ab = "1";
            return ab;
        }
        protected void onPostExecute(String ab){


        }
    }

}