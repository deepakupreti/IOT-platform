package com.example.ritesh.tanklevel;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    String SERVER_IP = "192.168.125.1";
    String REGISTRY_SERVER_PORT = "5000";
    String LOGIC_SERVER_PORT = "9658";
    String FILTER_SERVER_PORT = "19620";
    Button b3, setip, boil;
    ImageView b4;
    TextView sdata, toil, twater;
    EditText ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sdata = (TextView) findViewById(R.id.fieldalert);
        b3=(Button)findViewById(R.id.buttonwater);
        b3.setOnClickListener(this);
        boil=(Button)findViewById(R.id.buttonoil);
        boil.setOnClickListener(this);
        b4=(ImageView)findViewById(R.id.buttonalert);
        b4.setOnClickListener(this);
        setip = (Button) findViewById(R.id.setIP);
        ip = (EditText) findViewById(R.id.newip);
        setip.setOnClickListener(this);

        toil = (TextView) findViewById(R.id.oil);
        twater = (TextView) findViewById(R.id.water);
    }


    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.setIP)
        {
            SERVER_IP = ip.getText().toString();
            Toast.makeText(getApplicationContext(), "New IP: " + SERVER_IP, Toast.LENGTH_SHORT).show();
        }

        if(v.getId() == R.id.buttonoil)
        {
            Thread background = new Thread(new Runnable() {

                HttpClient Client = new DefaultHttpClient();
                private String URL = "http://"+SERVER_IP+":"+LOGIC_SERVER_PORT+"/command/?id="+"6";
                // After call for background.start this run method call
                public void run() {
                    try {

                        String SetServerString = "";
                        HttpGet httpget = new HttpGet(URL);
                        Log.v("TAG", "httpget: " + URL);
                        HttpResponse responseGet = Client.execute(httpget);
                        Log.v("TAG", "Client.exe");
                        //t.setText("connected");

                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        SetServerString = Client.execute(httpget, responseHandler);
                        threadMsg(SetServerString);

                    } catch (Throwable t) {
                        // just end the background thread
                        Log.i("Animation", "Thread  exception " + t);
                    }
                }

                private void threadMsg(String msg) {

                    if (!msg.equals(null) && !msg.equals("")) {
                        Message msgObj = handler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("message", msg);
                        msgObj.setData(b);
                        handler.sendMessage(msgObj);
                    }
                }

                // Define the Handler that receives messages from the thread and update the progress
                private final Handler handler = new Handler() {

                    public void handleMessage(Message msg) {

                        String aResponse = msg.getData().getString("message");
                        if ((null != aResponse)) {

                            toil.setText(aResponse);
                            // ALERT MESSAGE
                            Toast.makeText(
                                    getBaseContext(),
                                    "Server Response: " + aResponse,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                            // ALERT MESSAGE
                            Toast.makeText(
                                    getBaseContext(),
                                    "Not Got Response From Server.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                };

            });
            // Start Thread
            background.start();  //After call start method thread called run Method
        }

        if(v.getId()==R.id.buttonwater){

            // Fetch from URL

            Thread background = new Thread(new Runnable() {

                HttpClient Client = new DefaultHttpClient();
                private String URL = "http://"+SERVER_IP+":"+LOGIC_SERVER_PORT+"/command/?id="+"5";
                // After call for background.start this run method call
                public void run() {
                    try {

                        String SetServerString = "";
                        HttpGet httpget = new HttpGet(URL);
                        Log.v("TAG", "httpget: " + URL);
                        HttpResponse responseGet = Client.execute(httpget);
                        Log.v("TAG", "Client.exe");
                        //t.setText("connected");

                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        SetServerString = Client.execute(httpget, responseHandler);
                        threadMsg(SetServerString);

                    } catch (Throwable t) {
                        // just end the background thread
                        Log.i("Animation", "Thread  exception " + t);
                    }
                }

                private void threadMsg(String msg) {

                    if (!msg.equals(null) && !msg.equals("")) {
                        Message msgObj = handler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("message", msg);
                        msgObj.setData(b);
                        handler.sendMessage(msgObj);
                    }
                }

                // Define the Handler that receives messages from the thread and update the progress
                private final Handler handler = new Handler() {

                    public void handleMessage(Message msg) {

                        String aResponse = msg.getData().getString("message");
                        if ((null != aResponse)) {

                            twater.setText(aResponse);
                            // ALERT MESSAGE
                            Toast.makeText(
                                    getBaseContext(),
                                    "Server Response: " + aResponse,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                            // ALERT MESSAGE
                            Toast.makeText(
                                    getBaseContext(),
                                    "Not Got Response From Server.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                };

            });
            // Start Thread
            background.start();  //After call start method thread called run Method
        }

        if(v.getId()==R.id.buttonalert){


            final String URL;

            URL="http://"+SERVER_IP+":"+FILTER_SERVER_PORT+"/callback/?id1="+"5"+"&val1="+"35"+
                    "&op1="+"ge"+"&join="+"or"+"&id2="+"6"+"&val2="+"50"+"&op2="+"lt";

            Log.v("URL", URL);
            Thread background = new Thread(new Runnable() {

                HttpClient Client = new DefaultHttpClient();

                // After call for background.start this run method call
                public void run() {
                    try {

                        String SetServerString = "";
                        HttpGet httpget = new HttpGet(URL);
                        Log.v("TAG", "httpget");
                        HttpResponse responseGet = Client.execute(httpget);
                        Log.v("TAG", "Client.exe");
                        //t.setText("connected");

                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        SetServerString = Client.execute(httpget, responseHandler);
                        threadMsg(SetServerString);

                    } catch (Throwable t) {
                        // just end the background thread
                        Log.i("Animation", "Thread  exception " + t);
                    }
                }

                private void threadMsg(String msg) {

                    if (!msg.equals(null) && !msg.equals("")) {
                        Message msgObj = handler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("message", msg);
                        msgObj.setData(b);
                        handler.sendMessage(msgObj);
                    }
                }

                // Define the Handler that receives messages from the thread and update the progress
                private final Handler handler = new Handler() {

                    public void handleMessage(Message msg) {

                        String aResponse = msg.getData().getString("message");
                        if ((null != aResponse)) {
                            sdata.setText(aResponse.toString());
                            // ALERT MESSAGE
                            Toast.makeText(
                                    getBaseContext(),
                                    ""+aResponse+"Please Check the Water and Oil Levels !!!",
                                    Toast.LENGTH_SHORT).show();
                            try {
                                MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this, R.raw.emergency);
                                mPlayer.start();
                                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                // Vibrate for 500 milliseconds
                                v.vibrate(500);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {

                            // ALERT MESSAGE
                            Toast.makeText(
                                    getBaseContext(),
                                    "Not Got Response From Server.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                };

            });
            // Start Thread
            background.start();  //After call start method thread called run Method
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
