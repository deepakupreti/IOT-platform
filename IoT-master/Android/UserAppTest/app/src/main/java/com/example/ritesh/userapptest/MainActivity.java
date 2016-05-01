package com.example.ritesh.userapptest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements  View.OnClickListener{

    ArrayAdapter adapter;
    ListView active_Sensors;

    ArrayAdapter adapter_found;
    ListView found_Sensors;

    //ActionBarActivity aba;
//    String test[]=new String[10];
    ArrayList<String> test;
    ArrayList<String> found;

    TextView tvrange;

    String SERVER_IP = "10.1.130.231";
    String REGISTRY_SERVER_PORT = "5000";
    String LOGIC_SERVER_PORT = "9658";
    String FILTER_SERVER_PORT = "19620";
    Button b1,b2,b3,b4, setip;
    EditText e1,e2,e3,e4,e5,e6,e11,e12, newip;
    TextView sdata;
    RadioGroup rg1,rg2, rg3;
    CheckBox cb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //aba=this;

        test = new ArrayList<String>();
        found = new ArrayList<String>();

        sdata = (TextView) findViewById(R.id.textView1);
        tvrange = (TextView) findViewById(R.id.range);

        adapter_found=new ArrayAdapter<String>(this,R.layout.activity_ias,found);
        found_Sensors = (ListView)findViewById(R.id.listview2);
        found_Sensors.setAdapter(adapter_found);
        found_Sensors.setVisibility(View.GONE);

        adapter=new ArrayAdapter<String>(this,R.layout.activity_ias,test);
        active_Sensors = (ListView)findViewById(R.id.listview1);
        active_Sensors.setAdapter(adapter);
        active_Sensors.setVisibility(View.GONE);

        b1=(Button)findViewById(R.id.button1);
        b1.setOnClickListener(this);
        b2=(Button)findViewById(R.id.button2);
        b2.setOnClickListener(this);
        b3=(Button)findViewById(R.id.button3);
        b3.setOnClickListener(this);
        b4=(Button)findViewById(R.id.button4);
        b4.setOnClickListener(this);

        findViewById(R.id.rg2).setVisibility(View.GONE);
        findViewById(R.id.editText11).setVisibility(View.GONE);
        findViewById(R.id.editText12).setVisibility(View.GONE);
        findViewById(R.id.rg3).setVisibility(View.GONE);

        e1=(EditText)findViewById(R.id.editText1);
        e2=(EditText)findViewById(R.id.editText2);
        e3=(EditText)findViewById(R.id.editText3);
        e4=(EditText)findViewById(R.id.editText4);

        e5=(EditText)findViewById(R.id.editText5);
        e6=(EditText)findViewById(R.id.editText6);
        e11=(EditText)findViewById(R.id.editText11);
        e12=(EditText)findViewById(R.id.editText12);
        newip =(EditText)findViewById(R.id.newip);
        cb=(CheckBox)findViewById(R.id.checkBox1);
        cb.setOnClickListener(this);

        e1.setOnClickListener(this);
        e2.setOnClickListener(this);
        e3.setOnClickListener(this);
        e4.setOnClickListener(this);

        rg1 = (RadioGroup) findViewById(R.id.rg1);
        rg2 = (RadioGroup) findViewById(R.id.rg2);
        rg3 = (RadioGroup) findViewById(R.id.rg3);

        setip = (Button) findViewById(R.id.setIP);
        setip.setOnClickListener(this);


        ListView lv = (ListView) findViewById(R.id.listview1);
        lv.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate  menu; this adds items to the action bar if it is present.
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


    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.setIP)
        {
            SERVER_IP = newip.getText().toString();
            Toast.makeText(getApplicationContext(), "New IP Set: "+SERVER_IP, Toast.LENGTH_SHORT).show();
        }

        if(v.getId()==R.id.button1){

            //test=new ArrayList<String>();
            // Create Inner Thread Class
            Thread background = new Thread(new Runnable() {

                HttpClient Client = new DefaultHttpClient();
                private String URL = "http://"+SERVER_IP+":"+REGISTRY_SERVER_PORT+"/activesensors";
               // String u1 = "http://10.1.130.231:5000/activesensors";
                // After call for background.start this run method call
                public void run() {
                    try {

                        String SetServerString = "";
                        HttpGet httpget = new HttpGet(URL);
                        Log.v("TAG", "httpget");
                        HttpResponse responseGet = Client.execute(httpget);
                        Log.v("TAG", "Client.exe");
                        //t.setText("connected");
                        HttpEntity resEntityGet = responseGet.getEntity();
                        Log.v("TAG", "Reached");
                        if (resEntityGet != null) {
                            //do something with the response
                            Log.i("GET RESPONSE", EntityUtils.toString(resEntityGet));
                            // t.setText("connected");
                        }
                        else{

                            // t.setText("Not connected");
                        }
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
                        ArrayList<String> sid = new ArrayList<String>();
                        if ((null != aResponse)) {
                            String res = "{ SENSORS:"+aResponse+"}";
                            try {
                                JSONObject jso = new JSONObject(res);
                                JSONArray jra = jso.getJSONArray("SENSORS");

                                if(jra.length()<test.size())
                                {
                                    int j = jra.length();
                                    for(int k=j;k<test.size();k++)
                                        test.remove(k);
                                }
                                for(int i=0;i<jra.length();i++)
                                {
                                    JSONObject o = jra.getJSONObject(i);
                                    String id = o.getString("id");
                                    String type = o.getString("type");

                                    if(i<test.size())
                                        test.set(i, id+": "+type);
                                    else
                                        test.add(id+": "+type);
                                }
                                //test = sid;
                                loadlist();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // ALERT MESSAGE
                            Toast.makeText(
                                    getBaseContext(),
                                    "Server Response: "+aResponse,
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

            //loadlist();
            //adapter.notifyDataSetChanged();
            //
        }

        if(v.getId()==R.id.button2){

            found=new ArrayList<String>();
            final String lat = e1.getText().toString();
            final String lon = e2.getText().toString();
            final String range = e3.getText().toString();

            // Fetch from URL

            Thread background = new Thread(new Runnable() {

                HttpClient Client = new DefaultHttpClient();
                private String URL = "http://"+SERVER_IP+":"+FILTER_SERVER_PORT+"/inrange/?lat="+
                        lat+"&lon="+lon+"&range="+range;
                // String u1 = "http://10.1.130.231:5000/activesensors";
                // After call for background.start this run method call
                public void run() {
                    try {

                        String SetServerString = "";
                        HttpGet httpget = new HttpGet(URL);
                        Log.v("TAG", "httpget");
                        HttpResponse responseGet = Client.execute(httpget);
                        Log.v("TAG", "Client.exe");
                        //t.setText("connected");
                        HttpEntity resEntityGet = responseGet.getEntity();
                        Log.v("TAG", "Reached");
                        if (resEntityGet != null) {
                            //do something with the response
                            Log.i("GET RESPONSE", EntityUtils.toString(resEntityGet));
                            // t.setText("connected");
                        }
                        else{

                            // t.setText("Not connected");
                        }
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
                        ArrayList<String> sid = new ArrayList<String>();
                        if ((null != aResponse)) {
                            String res = aResponse;
                            StringBuilder sb = new StringBuilder();
                            try {
                                JSONObject jso = new JSONObject(res);
                                JSONArray jra = jso.getJSONArray("inrange");

                                if(jra.length()<found.size())
                                {
                                    int j = jra.length();
                                    for(int k=j;k<found.size();k++)
                                        found.remove(k);
                                }
                                for(int i=0;i<jra.length();i++)
                                {
                                    JSONObject o = jra.getJSONObject(i);
                                    String id = o.getString("id");
                                    String type = o.getString("type");
                                    Log.v("TAG",id+": "+type);
                                    sb.append(id+": "+type+", ");
                                    if(i<found.size())
                                        found.set(i, id+": "+type);
                                    else
                                        found.add(id+": "+type);
                                }

                                String m = sb.toString();
                                if(m.length()>2)
                                    m = m.substring(0, m.length()-2);
                                tvrange.setText(m);
                                Log.v("TAG","Size: "+found.size());
                                //loadlist1();
                                //adapter_found.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // ALERT MESSAGE
                            Toast.makeText(
                                    getBaseContext(),
                                    "Server Response: "+aResponse,
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

            loadlist1();
            adapter_found.notifyDataSetChanged();
            found_Sensors.setVisibility(View.VISIBLE);
        }

        if(v.getId()==R.id.button3){
            final String id = e4.getText().toString();

            // Fetch from URL

            Thread background = new Thread(new Runnable() {

                HttpClient Client = new DefaultHttpClient();
                private String URL = "http://"+SERVER_IP+":"+LOGIC_SERVER_PORT+"/command/?id="+id;
                // After call for background.start this run method call
                public void run() {
                    try {

                        String SetServerString = "";
                        HttpGet httpget = new HttpGet(URL);
                        Log.v("TAG", "httpget: "+URL);
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

                            sdata.setText(aResponse);
                            // ALERT MESSAGE
                            Toast.makeText(
                                    getBaseContext(),
                                    "Server Response: "+aResponse,
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


        if(v.getId()==R.id.checkBox1){
            if(cb.isChecked()){
                findViewById(R.id.rg2).setVisibility(View.VISIBLE);
                findViewById(R.id.editText11).setVisibility(View.VISIBLE);
                findViewById(R.id.editText12).setVisibility(View.VISIBLE);
                findViewById(R.id.rg3).setVisibility(View.VISIBLE);
            }
            else{
                findViewById(R.id.rg2).setVisibility(View.GONE);
                findViewById(R.id.editText11).setVisibility(View.GONE);
                findViewById(R.id.editText12).setVisibility(View.GONE);
                findViewById(R.id.rg3).setVisibility(View.GONE);
            }
        }


        if(v.getId()==R.id.button4){

            String id1 = e5.getText().toString();
            String id2 = "", op1 = "", op2 = "", join = "", val1 = "", val2 = "";
            val1 = e6.getText().toString();

            int rb1id = rg1.getCheckedRadioButtonId();
            View rb1 = rg1.findViewById(rb1id);
            int idx = rg1.indexOfChild(rb1);

            if(idx==0)
                op1 = "gt";
            else if(idx == 1)
                op1 = "lt";
            else if(idx == 2)
                op1 = "le";
            else if(idx==3)
                op1 = "ge";
            else
                op1 = "eq";

            // ?id1=2&val1=50&op1=lt&join=or&id2=3&val2=50&op2=lt
            //final String URL = "http://"+SERVER_IP+":"+LOGIC_SERVER_PORT+"/callback/?id1="+id1+"&val1="+val1+
            //        "&op1="+op1+"&join=none";
            final String URL;
            CheckBox cb=(CheckBox)findViewById(R.id.checkBox1);
            if(cb.isChecked()){//Join is there
                id2 = e11.getText().toString();
                val2 = e12.getText().toString();

                int rb3id = rg3.getCheckedRadioButtonId();
                View rb3 = rg3.findViewById(rb3id);
                int idx2 = rg3.indexOfChild(rb3);

                if(idx2==0)
                    join = "and";
                else
                    join = "or";

                int rb2id = rg2.getCheckedRadioButtonId();
                View rb2 = rg2.findViewById(rb2id);
                int idx1 = rg2.indexOfChild(rb2);

                if(idx1==0)
                    op2 = "gt";
                else if(idx1 == 1)
                    op2 = "lt";
                else if(idx1 == 2)
                    op2 = "le";
                else if(idx1==3)
                    op2 = "ge";
                else
                    op2 = "eq";

                //URL = "http://"+SERVER_IP+":"+LOGIC_SERVER_PORT+"/callback/?id1="+id1+"&val1="+val1+
                 //       "&op1="+op1+"&join="+join+"&id2="+id2+"&val2="+val2+"&op2="+op2;

                //Toast.makeText(getApplicationContext(), "checked: "+URL, Toast.LENGTH_LONG).show();
            }
            else{

                //Toast.makeText(getApplicationContext(), "not checked: "+URL, Toast.LENGTH_LONG).show();
            }

            if(cb.isChecked())
                URL="http://"+SERVER_IP+":"+FILTER_SERVER_PORT+"/callback/?id1="+id1+"&val1="+val1+
                      "&op1="+op1+"&join="+join+"&id2="+id2+"&val2="+val2+"&op2="+op2;
            else
                URL = "http://"+SERVER_IP+":"+FILTER_SERVER_PORT+"/callback/?id1="+id1+"&val1="+val1+
                               "&op1="+op1+"&join=none";;
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

                            // ALERT MESSAGE
                            Toast.makeText(
                                    getBaseContext(),
                                    "Server Response: "+aResponse,
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



    }

    public void displayValue()
    {
        String sensorId=((EditText)findViewById(R.id.editText4)).getText().toString();
    }

    public void loadlist1()
    {
        for(int i=0;i<4;i++)
            found.add("hello");
        adapter_found.notifyDataSetChanged();
        found_Sensors.setVisibility(View.VISIBLE);
    }

    public void loadlist()
    {
        adapter.notifyDataSetChanged();
        active_Sensors.setVisibility(View.VISIBLE);
    }

}