package com.example.sameer.medicinal;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.os.Vibrator;
import android.content.Context;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    Thread thread;
    String SERVER_IP = "192.168.125.1";
    String REGISTRY_SERVER_PORT = "5000";
    String LOGIC_SERVER_PORT = "9658";
    String FILTER_SERVER_PORT = "19620";
    String ambulanceID = "a-1";

    public final int TIME = 3000;
    public static Context context;

    TextView heart, respire, glucose, pressure;
    Button alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //blink();
       context = this.getBaseContext();

        heart = (TextView) findViewById(R.id.heartRate);
        respire = (TextView) findViewById(R.id.respiratory);
        glucose = (TextView) findViewById(R.id.glucose);
        pressure = (TextView) findViewById(R.id.pressure);

        alert = (Button) findViewById(R.id.alertButton);

        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new MedicalData().execute("heart");
                //new MedicalData().execute("respire");
                //new MedicalData().execute("glucose");
                //new MedicalData().execute("pressure");
                startDiagonosis();
            }
        });

    }

    public void startDiagonosis()
    {
        Thread dt = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true)
                {
                    try{
                        new MedicalData().execute("heart");
                        Thread.sleep(TIME);
                        new MedicalData().execute("respire");
                        Thread.sleep(TIME);
                        new MedicalData().execute("glucose");
                        Thread.sleep(TIME);
                        new MedicalData().execute("pressure");
                        Thread.sleep(TIME);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        dt.start();
    }

    public void updateUI1(String msg)
    {
        try {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

            MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this, R.raw.emergency_small);
            mPlayer.start();

            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};

            // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
            v.vibrate(pattern, -1);
            //v.vibrate(500);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private class MedicalData extends AsyncTask<String, String, String>
    {
        String type = null;
        @Override
        protected String doInBackground(String... params) {

            String responseGet = null;
            try {

                type = params[0];
                if(type.equalsIgnoreCase("j"))
                    return "finish";

                HttpClient Client = new DefaultHttpClient();
                String URL = "http://"+SERVER_IP+":"+LOGIC_SERVER_PORT+"/getmedicaldata/?part="+type+"&aid="+ambulanceID+"&type=medical";
                HttpGet httpget = new HttpGet(URL);
                Log.v("TAG", "httpget: " + URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                responseGet = Client.execute(httpget, responseHandler);
                Log.v("TAG", "Client.exe");
                Log.v("TAG",""+responseGet.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseGet;

        }

        public void updateUI(String msg)
        {
            try {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this, R.raw.emergency_small);
                mPlayer.start();

                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};

                // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
                v.vibrate(pattern, -1);
                //v.vibrate(500);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (s == null)
                    return;

                //Thread.sleep(5000);
                if (s.charAt(0) == 'a') {
                    String arr[] = s.split(";");
                    if (type.equalsIgnoreCase("heart")) {
                        heart.setText(arr[2]);
                        Toast.makeText(getApplicationContext(), arr[1], Toast.LENGTH_SHORT).show();
                        MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this, R.raw.emergency_small);
                        mPlayer.start();
                        //updateUI(arr[1]);
                        //new MedicalData().execute("heart");
                    } else if (type.equalsIgnoreCase("respire")) {
                        respire.setText(arr[2]);
                        Toast.makeText(getApplicationContext(), arr[1], Toast.LENGTH_SHORT).show();
                        MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this, R.raw.emergency_small);
                        mPlayer.start();
                        //updateUI(arr[1]);
                       // new MedicalData().execute("respire");
                    } else if (type.equalsIgnoreCase("glucose")) {
                        glucose.setText(arr[2]);
                        Toast.makeText(getApplicationContext(), arr[1], Toast.LENGTH_SHORT).show();
                        MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this, R.raw.emergency_small);
                        mPlayer.start();
                        //updateUI(arr[1]);
                       // new MedicalData().execute("glucose");
                    } else if (type.equalsIgnoreCase("pressure")) {
                        pressure.setText(arr[2]);
                        Toast.makeText(getApplicationContext(), arr[1], Toast.LENGTH_SHORT).show();
                        MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this, R.raw.emergency_small);
                        mPlayer.start();
                       // updateUI(arr[1]);
                       // new MedicalData().execute("pressure");
                    }
                } else {
                    if (type.equalsIgnoreCase("heart")) {
                        heart.setText(s);
                       // new MedicalData().execute("heart");
                    } else if (type.equalsIgnoreCase("respire")) {
                        respire.setText(s);
                       // new MedicalData().execute("respire");
                    } else if (type.equalsIgnoreCase("glucose")) {
                        glucose.setText(s);
                       // new MedicalData().execute("glucose");
                    } else if (type.equalsIgnoreCase("pressure")) {
                        pressure.setText(s);
                       // new MedicalData().execute("pressure");
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Toast.makeText(getApplicationContext(), "Ambulance 1 Selected", Toast.LENGTH_LONG).show();
                ambulanceID = "a-1";
                return true;
            case R.id.item2:
                Toast.makeText(getApplicationContext(),"Ambulance 2 Selected",Toast.LENGTH_LONG).show();
                ambulanceID = "a-2";
                return true;
            case R.id.item3:
                Toast.makeText(getApplicationContext(),"Ambulance 3 Selected",Toast.LENGTH_LONG).show();
                ambulanceID = "a-3";
                return true;
            case R.id.item4:
                Toast.makeText(getApplicationContext(),"Ambulance 4 Selected",Toast.LENGTH_LONG).show();
                ambulanceID = "a-4";
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClick(View v){

        if(v.getId()==R.id.alertButton){

            thread.stop();
        }
    }

    private void blink(){
        final Handler handler = new Handler();
        thread=new Thread(new Runnable() {
            @Override
            public void run() {

                int timeToBlink = 1000;    //in milissegunds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        TextView txt = (TextView) findViewById(R.id.heartRate);
                        if(txt.getVisibility() == View.VISIBLE){
                            txt.setVisibility(View.INVISIBLE);
                        }else{
                            txt.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        });
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 1000;    //in milissegunds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView txt = (TextView) findViewById(R.id.heartRate);
                        if(txt.getVisibility() == View.VISIBLE){
                            txt.setVisibility(View.INVISIBLE);
                        }else{
                            txt.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        }).start();
        */
        thread.start();
    }

}
