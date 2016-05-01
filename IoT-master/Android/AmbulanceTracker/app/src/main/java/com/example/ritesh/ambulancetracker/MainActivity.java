package com.example.ritesh.ambulancetracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.HashMap;


public class MainActivity extends ActionBarActivity {

    CanvasHelper cView;
    public static int height, width;

    String SERVER_IP = "192.168.125.1";
    String REGISTRY_SERVER_PORT = "5000";
    String LOGIC_SERVER_PORT = "9658";
    String FILTER_SERVER_PORT = "19620";

    public static HashMap<String, Integer> nodeMap;
    public static HashMap<String, Integer> pathMap;

    static String ambulanceID = "a-1";

    public static Context appContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

        nodeMap = new HashMap<String, Integer>();
        nodeMap.put("a",0);
        nodeMap.put("b",1);
        nodeMap.put("c",2);
        nodeMap.put("d",3);
        nodeMap.put("g",4);
        nodeMap.put("f",5);
        nodeMap.put("e",6);
        nodeMap.put("h",7);
        nodeMap.put("i",8);
        nodeMap.put("j",9);

        pathMap = new HashMap<String, Integer>();
        pathMap.put("ab",0);
        pathMap.put("ac",1);
        pathMap.put("ad",2);
        pathMap.put("bg",3);
        pathMap.put("bf",4);
        pathMap.put("cf",5);
        pathMap.put("ce",6);
        pathMap.put("de",7);
        pathMap.put("gh",8);
        pathMap.put("fh",9);
        pathMap.put("fi",10);
        pathMap.put("ei",11);
        pathMap.put("hj",12);
        pathMap.put("ij",13);


        cView = new CanvasHelper(this);
        setContentView(cView);

        appContext = getApplicationContext();
    }

    public void startTracking()
    {
        new FindDestination().execute("a");
    }

    private class FindDestination extends AsyncTask<String, String, String>
    {
        String previous = "a";

        @Override
        protected String doInBackground(String... params) {
            String responseGet = null;
            try {

                previous = params[0];
                if(previous.equalsIgnoreCase("j"))
                    return "finish";

                HttpClient Client = new DefaultHttpClient();
                String URL = "http://"+SERVER_IP+":"+LOGIC_SERVER_PORT+"/minpath/?interpoint="+previous+"&aid="+ambulanceID;
                HttpGet httpget = new HttpGet(URL);
                Log.v("TAG", "httpget: " + URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                responseGet = Client.execute(httpget, responseHandler);
                Log.v("TAG", "Client.exe");
                Log.v("TAG",""+responseGet.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            if(responseGet!=null)
                return responseGet.toString().substring(0, responseGet.toString().indexOf(':'));
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if(s.equalsIgnoreCase("finish"))
                    return;
                Log.v("TAG","PostExecute: "+s);
                Log.v("TAG","NodeMap: "+nodeMap.get(s));
                Paint p = new Paint();
                p.setStyle(Paint.Style.FILL);
                p.setColor(Color.parseColor("#008000"));
                CanvasHelper.cPaints[nodeMap.get(s)] = p;

                // Run a loop to change values of x and y of ambulance

                /*
                int x1 = CanvasHelper.x.get(nodeMap.get(previous));
                int y1 = CanvasHelper.y.get(nodeMap.get(previous));

                int x2 = CanvasHelper.x.get(nodeMap.get(s));
                int y2 = CanvasHelper.y.get(nodeMap.get(s));

                double m = ((double)y2-y1)/((double)x2-x1);

                int px = x1, py = y1;

                while(px!=x2 && py!=y2)
                {
                    px+=1;
                    py = (int)m*(px-x1)+y1;
                    CanvasHelper.xc.set(nodeMap.get(previous),px);
                    CanvasHelper.yc.set(nodeMap.get(previous),py);
                    Bitmap b = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.ambulance2ss);
                    CanvasHelper.images[nodeMap.get(previous)] = b;
                    //Thread.sleep(30);
                }
                */

                // Set Ambulance Image
                Bitmap b = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.ambulance2ss);
                CanvasHelper.images[nodeMap.get(s)] = b;

                Paint lp = new Paint();
                lp.setStrokeWidth(3);
                lp.setColor(Color.GREEN);
                CanvasHelper.lPaints[pathMap.get(previous+s)] = lp;
                Thread.sleep(1000);
                new FindDestination().execute(s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    private class FindPath extends AsyncTask<String, String, String>
    {
        String previous = "a";

        @Override
        protected String doInBackground(String... params) {
            try {

                Thread.sleep(3000);
                Paint p = new Paint();
                p.setStyle(Paint.Style.FILL);
                p.setColor(Color.parseColor("#008000"));
                CanvasHelper.cPaints[5] = p;
                Thread.sleep(2000);
                CanvasHelper.cPaints[3] = p;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
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
