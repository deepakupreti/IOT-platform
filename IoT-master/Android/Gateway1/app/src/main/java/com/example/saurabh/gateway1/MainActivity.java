package com.example.saurabh.gateway1;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {

    BluetoothAdapter mBluetoothAdapter;
    private TextView txtv;
    private int REQUEST_ENABLE_BT = 2;

    //String SERVER_IP = "10.1.130.231";
    String SERVER_IP = "192.168.125.1";
    String REGISTRY_SERVER_PORT = "5000";
    String LOGIC_SERVER_PORT = "9658";
    String FILTER_SERVER_PORT = "19620";
    String GatewayName = "gateway100";
    String ambulanceID = "a-1";

    Button bip, bg, ba;
    EditText eip, eg, ea;

    private final String TAG = "com.example.sensor";
    private final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final String NAME = "BluetoothComm";
    int count = 5, i = 0;

    ArrayList<String> sensorList;
    String idData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtv = (TextView) findViewById(R.id.sensorval);

        Button start = (Button) findViewById(R.id.startGateway);
        bip = (Button) findViewById(R.id.setIP);
        eip = (EditText) findViewById(R.id.newip);

        bg = (Button) findViewById(R.id.bgate);
        eg = (EditText) findViewById(R.id.egate);

        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GatewayName = eg.getText().toString();
                Toast.makeText(getApplicationContext(), "Gateway Name: "+GatewayName, Toast.LENGTH_LONG).show();
            }
        });

        bip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = eip.getText().toString();
                SERVER_IP = ip;
                Toast.makeText(getApplicationContext(), "New IP: "+SERVER_IP, Toast.LENGTH_SHORT).show();
            }
        });

        sensorList = new ArrayList<String>();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMySensors();
//                for(int i=0;i<5;i++)
//                {
//
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        });

    }

    public void getMySensors()
    {
        Thread background = new Thread(new Runnable() {

            HttpClient Client = new DefaultHttpClient();
            private String URL = "http://"+SERVER_IP+":"+REGISTRY_SERVER_PORT+"/getsensors/?gateway="+GatewayName;
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

                            for(int i=0;i<jra.length();i++)
                            {
                                JSONObject o = jra.getJSONObject(i);
                                String id = o.getString("id");
                                sensorList.add(id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // ALERT MESSAGE
                        Toast.makeText(
                                getBaseContext(),
                                "Server Response: "+aResponse,
                                Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(2000);
                            //executor();
                            new GatewayExecutor().execute("sensor"+sensorList.get(0));
                            i++;
                            //new RunHelper().execute();
                        } catch (InterruptedException e) {
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

    private class RunHelper extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            while(i<sensorList.size())
            {
                if(i==sensorList.size()-1)
                {
                    publishProgress();
                    i = 0;
                }
                else
                {
                    publishProgress();
                    i++;
                }
                try {
                    Thread.sleep(5000);
                    //if (idData.charAt(idData.length() - 1) != ':')
                        //txtv.setText(idData.substring(idData.indexOf(':')+1));
                    //sendToFilterServer(idData);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            new GatewayExecutor().execute("sensor" + sensorList.get(i));
        }
    }

    public void executor()
    {
        while(i<sensorList.size())
        {
            if(i==sensorList.size()-1)
            {
                new GatewayExecutor().execute("sensor"+sensorList.get(i));
                i = 0;
            }
            else
            {
                new GatewayExecutor().execute("sensor"+sensorList.get(i));
                i++;
            }
            try {
                Thread.sleep(5000);
                if (idData.charAt(idData.length() - 1) != ':')
                    txtv.setText(idData.substring(idData.indexOf(':')+1));
                sendToFilterServer(idData);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendToFilterServer(String response)
    {
        try {
            Log.v("TAG", "sent to server:" + response);
            if (response.charAt(response.length() - 1) == ':')
                return;
            final String arr[] = response.split(":");

            Thread background = new Thread(new Runnable() {

                HttpClient Client = new DefaultHttpClient();
                private String URL = "http://" + SERVER_IP + ":" + FILTER_SERVER_PORT + "/new/?id=" + arr[0] + "&data=" + arr[1];

                //private String URL = "http://"+SERVER_IP+":"+FILTER_SERVER_PORT+"/new/?id=1"+"&data=10";
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

                            // ALERT MESSAGE
                            Toast.makeText(
                                    getBaseContext(),
                                    "Server Response: " + aResponse,
                                    Toast.LENGTH_SHORT).show();
                        } else {

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
        catch (Exception e)
        {
            Log.e("Error","Something Messed Up!");
            e.printStackTrace();
        }
    }

    private class GatewayExecutor extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothSocket mmSocket = null;
            String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
            ByteArrayOutputStream out;
            byte[] buffer = new byte[4096];
            String result = "";
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            Log.v("TAG","REACHED");
            List<String> s = new ArrayList<String>();
            BluetoothDevice temp = null;
            String sensorName = "";

            for (BluetoothDevice bt : pairedDevices) {
                if (bt.getName().trim().equals(params[0])) {
                    temp = bt;
                    sensorName = params[0];
                    Log.v("TAG", sensorName);
                }
            }

            if (temp != null) {

                try {
                    mmSocket = temp.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
                } catch (Exception e) {
                    Log.e("", "Error creating socket");
                }

                try {
                    mmSocket.connect();
                    Log.e("", "Connected");
                } catch (IOException e) {
                    Log.e("", e.getMessage());
                    try {
                        Log.e("", "trying fallback...");

                        mmSocket = (BluetoothSocket) temp.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(temp, 1);
                        mmSocket.connect();

                        Log.e("", "Connected");
                    } catch (Exception e2) {
                        Log.e("", "Couldn't establish Bluetooth connection!");
                    }
                }

                InputStream mmInStream = null;
                OutputStream mmOutStream = null;
                try {
                    mmInStream = mmSocket.getInputStream();
                    mmOutStream = mmSocket.getOutputStream();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    out = new ByteArrayOutputStream();
                    mmOutStream.write(sensorName.getBytes());
                    Log.v("Tag", sensorName.toString());
                    int len = mmInStream.read(buffer);
                    out.write(buffer, 0, len);
                    Log.v("TAG", "data");
                    result = out.toString("UTF-8");
                    Log.v("TAG", result + "from "+ params[0]);
                    mmOutStream.write("finish".getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                    //break;
                }
            }
            idData = params[0].replace("sensor","")+":  "+result;

            if(idData.charAt(idData.length()-1)!=':') {
                try {
                    String arr[] = idData.split(":");
                    String URL = "http://" + SERVER_IP + ":" + FILTER_SERVER_PORT + "/new/?id=" + arr[0].trim() + "&data=" + arr[1].trim();
                    //String URL = "http://" + "192.168.125.1" + ":" + FILTER_SERVER_PORT + "/new/?id=" + arr[0].trim() + "&data=" + arr[1].trim();
                    int semiCount = 0;
                    for(int i=0;i<idData.length();i++)
                    {
                        if(idData.charAt(i)==';')
                            semiCount++;
                    }
                    Log.v("TAG","Semicount:"+semiCount);
                    if(semiCount==3)
                    {
                        Log.v("TAG","Semicount: 3");
                        StringBuilder med = new StringBuilder();
                        String arr1[] = idData.substring(idData.indexOf(':')+1).trim().split(";");
                        med.append("Heart: "+arr1[0]+"\n"+"Respiratory: "+arr1[1]+"\n"+"Blood Pressure: "+arr1[2]+"\n"+"Circulation: "+arr1[3]);
                        Log.v("TAG",med.toString());
                        publishProgress(med.toString());
                        URL = "http://" + SERVER_IP + ":" + FILTER_SERVER_PORT + "/newmedicaldata/?id=" + arr[0].trim() + "&data=" + arr1[0]+";"+arr1[1]+";"+arr1[2]+";"+arr1[3] +
                        "&type=medical&aid="+ambulanceID;
                        Log.v("TAG","URL: "+URL);
                    }
                    else if(semiCount==13)
                    {
                        // Values for Heat Map Data
                        Log.v("TAG","Semicount: 13");
                        StringBuilder med = new StringBuilder();
                        String arr1[] = idData.substring(idData.indexOf(':')+1).trim().split(";");
                        med.append("No. of Cars\nAB: "+arr1[0]+"\t\t\t\t"+"AC: "+arr1[1]+"\t\t\t\t"+"AD: "+arr1[2]+"\t\t\t\t"+"BG: "+arr1[3]+"\t\t\t\t"+"BF: "+arr1[4]+"\t\t\t\t"+"CF: "+arr1[5]+"\t\t\t\t"+"CE: "+arr1[6]
                                +"\t\t\t\t"+"DE: "+arr1[7]+"\t\t\t\t"+"GH: "+arr1[8]+"\t\t\t\t"+"FH: "+arr1[9]+"\t\t\t\t"+"FI: "+arr1[10]+"\t\t\t\t"+"EI: "+arr1[11]+"\t\t\t\t"+"HJ: "+arr1[12]+"\t\t\t\t"+"IJ: "+arr1[13]);
                        Log.v("TAG",med.toString());
                        publishProgress(med.toString());
                        URL = "http://" + SERVER_IP + ":" + FILTER_SERVER_PORT + "/path/?id=" + arr[0].trim() + "&data=" + arr1[0]+";"+arr1[1]+";"+arr1[2]+";"+arr1[3]+
                        ";"+arr1[4]+";"+arr1[5]+";"+arr1[6]+";"+arr1[7]+";"+arr1[8]+";"+arr1[9]+";"+arr1[10]+";"+arr1[11]+";"+arr1[12]+";"+arr1[13]+
                        "&type=heat&aid="+ambulanceID;
                        Log.v("TAG","URL: "+URL);
                    }
                    else {
                        Log.v("TAG","Pipecount: none");
                        URL = "http://" + SERVER_IP + ":" + FILTER_SERVER_PORT + "/new/?id=" + arr[0].trim() + "&data=" + arr[1].trim();
                        publishProgress(idData);
                    }

                    HttpClient Client = new DefaultHttpClient();


                    HttpGet httpget = new HttpGet(URL);
                    Log.v("TAG", "httpget: " + URL);
                    HttpResponse response = Client.execute(httpget);

                    int status = response.getStatusLine().getStatusCode();

                    if (status == 200) {
                        HttpEntity entity = response.getEntity();
                        String data = EntityUtils.toString(entity);
                        Log.v("TAG", "SERVER RESPONSE OK !!!");
                    }
                } catch (Exception e) {
                    Log.v("Error", "Sending to Server Failed");
                    //e.printStackTrace();
                }
                Log.v("TAG", "Client.exe");
            }

            //sendToFilterServer(params[0].replace("sensor","")+":"+result);
            return params[0].replace("sensor","")+":"+result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String val = values[0].trim();
            if(val.charAt(val.length()-1)!=':')
                txtv.setText(val);

        }

        @Override
        protected void onPostExecute(String rec) {
            //Toast.makeText(getApplicationContext(), rec, Toast.LENGTH_LONG).show();
            //super.onPostExecute(rec);
            //txtv.setText(rec);
            //sendToFilterServer(rec);

            try {
                Thread.sleep(5000);
                //executor();
                if(i<sensorList.size()-1)
                {
                    new GatewayExecutor().execute("sensor"+sensorList.get(i));
                    i++;
                }
                else
                {
                    new GatewayExecutor().execute("sensor"+sensorList.get(i));
                    i = 0;
                }
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
            catch (Exception e)
            {
                i = 0;
                //e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (adapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!adapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
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
}
