package com.example.ritesh.sensor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {

    BluetoothAdapter mBluetoothAdapter;

    private int REQUEST_ENABLE_BT = 1;

    private final String GatewayName = "Gateway1";
    private final String GatewayAddr = "";
    private final String TAG = "com.example.sensor";
    private final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    double initial_val = 10;

    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //startBluetooth();
                new SensorService().execute("sensorss");
                // Perform action on click
            }
        });

    }

    @Override
    public void onStart() {
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

    private class SensorService extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... params) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothSocket mmSocket = null;
            BluetoothServerSocket mmServerSocket = null;
            String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
            byte[] buffer = new byte[4096];  // buffer store for the stream
            byte[] sensorName = new byte[4096];
            int bytes; // bytes returned from read()
            ByteArrayOutputStream out, outName;
            Random rn = new Random();
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                mmServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("sensor", UUID.fromString(SPP_UUID));
            } catch (IOException e) {
            }

            while (true) {

                try {
                    Log.v("TAG", "ba");
                    mmSocket = mmServerSocket.accept();
                    Log.v("TAG", "aa");
                } catch (IOException e) {
                    try {
                        mmSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    return null;
                }

                InputStream mmInStream = null;
                OutputStream mmOutStream = null;

                if (mmSocket != null) {
                    try {
                        mmInStream = mmSocket.getInputStream();
                        mmOutStream = mmSocket.getOutputStream();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    while (true) {
                        try {
                            int l1 = mmInStream.read(sensorName);
                            outName = new ByteArrayOutputStream();
                            outName.write(sensorName, 0, l1);
                            String name = outName.toString("UTF-8");

                            Log.d("SensorService", "sensorName: " + sensorName.toString());
                            if(name.equalsIgnoreCase("finish"))
                                break;

                            if (name.equals("sensor11"))
                            {
                                // sends 4 values, simulates 4 sensors i.e. Heart rate, Respiratory, etc.
                                int h = rn.nextInt(100)%100 + 1;
                                int r = rn.nextInt(100)%100 + 1;
                                int b = rn.nextInt(100)%100 + 1;
                                int x = rn.nextInt(100)%100 + 1;

                                mmOutStream.write((""+h+";"+r+";"+b+";"+x).getBytes());
//                                out = new ByteArrayOutputStream();
//                                int len = mmInStream.read(buffer);
//                                out.write(buffer, 0, len);

                                if(name.equalsIgnoreCase("finish"))
                                    break;
                            }
                            else if (name.equals("sensor22"))
                            {
                                // Results in the values of heat sensors for all the paths of the graph
                                // Filter server selects the best path from it

                                int a, b, c, d, e, f, g, h, i, j, k, l, m, n;
                                a = rn.nextInt(100)%100+1; f = rn.nextInt(100)%100+1; j = rn.nextInt(100)%100+1;
                                b = rn.nextInt(100)%100+1; g = rn.nextInt(100)%100+1; k = rn.nextInt(100)%100+1;
                                c = rn.nextInt(100)%100+1; h = rn.nextInt(100)%100+1; l = rn.nextInt(100)%100+1;
                                d = rn.nextInt(100)%100+1; i = rn.nextInt(100)%100+1; m = rn.nextInt(100)%100+1;
                                e = rn.nextInt(100)%100+1; n = rn.nextInt(100)%100+1;

                                mmOutStream.write((""+a+";"+b+";"+c+";"+d+";"+e+";"+f+";"+g+";"+h+";"+i+";"+j+";"+k+";"
                                        +l+";"+m+";"+n).getBytes());

                                if(name.equalsIgnoreCase("finish"))
                                    break;
                            }
                            else
                            {
                                double val = rn.nextDouble() + 1;
                                val = val * 2;
                                val = Math.round(val * 100.0) / 100.0;
                                initial_val += val;
                                mmOutStream.write(("" + initial_val).getBytes());
//                                out = new ByteArrayOutputStream();
//                                int len = mmInStream.read(buffer);
//                                out.write(buffer, 0, len);
//                                String result = out.toString("UTF-8");
//                                String s;


                            Log.d("SensorService", "from gateway " + name);
                            if (name.equals("finish")) {
                                break;
                            }
                        }

                        } catch (Exception e) {
                            e.printStackTrace();

                            break;
                        }
                    }

                    try {
                        mmInStream.close();
                        mmOutStream.close();
                        mmSocket.close();
                    } catch (Exception e) {

                    }
                }

            }
        }
    }

}