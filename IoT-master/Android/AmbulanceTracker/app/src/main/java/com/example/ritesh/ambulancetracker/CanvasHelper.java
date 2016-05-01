package com.example.ritesh.ambulancetracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.MailTo;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.CheckedOutputStream;

/**
 * Created by Ritesh on 17-04-2015.
 */
public class CanvasHelper extends View {

    public static ArrayList<String> coords;
    public static ArrayList<Integer> x, y, xc, yc;
    int cx, cy;
    public static Paint cPaints[], lPaints[];
    public static Bitmap images[];
    MainActivity ma;

    public static HashMap<String, String> cMap;

    public CanvasHelper(Context context) {
        super(context);

        ma = new MainActivity();
        cx = MainActivity.width/2;
        cy = MainActivity.height/2;

        x = new ArrayList<Integer>();
        y = new ArrayList<Integer>();

        xc = new ArrayList<Integer>();
        yc = new ArrayList<Integer>();

        cMap = new HashMap<String, String>();

        cPaints = new Paint[10];
        lPaints = new Paint[14];

        images = new Bitmap[10];

        for(int i=0;i<10;i++)
        {
            images[i] = BitmapFactory.decodeResource(getResources(), R.drawable.redss);
        }

        Bitmap i1 = BitmapFactory.decodeResource(getResources(), R.drawable.ambulance2ss);
        images[0] = i1;

        for(int i=0;i<10;i++)
        {
            Paint p = new Paint();
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.parseColor("#CD5C5C"));
            cPaints[i] = p;
        }
        Paint p1 = new Paint();
        p1.setStyle(Paint.Style.FILL);
        p1.setColor(Color.parseColor("#008000"));
        cPaints[0] = p1;
        for(int i=0;i<14;i++)
        {
            Paint l = new Paint();
            l.setStrokeWidth(3);
            l.setColor(Color.BLACK);
            lPaints[i] = l;
        }

        coords = new ArrayList<String>();
        coords.add(""+cx+","+(cy+200)); //a
        coords.add(""+(cx-250)+","+(cy)); //b
        coords.add(""+cx+","+(cy)); //c
        coords.add(""+(cx+250)+","+(cy)); //d
        coords.add(""+(cx-200)+","+(cy-150)); //g
        coords.add(""+(cx-100)+","+(cy-150)); //f
        coords.add(""+(cx+150)+","+(cy-150)); //e
        coords.add(""+(cx-120)+","+(cy-250)); //h
        coords.add(""+(cx+120)+","+(cy-250)); //i
        coords.add(""+cx+","+(cy-350)); //j

        cMap.put("ab", ""+cx+","+(cy+200)+","+ ""+(cx-250)+","+(cy) );
        cMap.put("ac", ""+cx+","+(cy+200)+","+ ""+cx+","+(cy) );
        cMap.put("ad", ""+cx+","+(cy+200)+","+ ""+(cx+250)+","+(cy) );

        cMap.put("bg", ""+(cx-250)+","+(cy)+","+ ""+(cx-200)+","+(cy-150) );
        cMap.put("bf", ""+(cx-250)+","+(cy)+","+ ""+(cx-100)+","+(cy-150) );

        cMap.put("cf", ""+cx+","+(cy)+","+ ""+(cx-100)+","+(cy-150) );
        cMap.put("ce", ""+cx+","+(cy)+","+ ""+(cx+150)+","+(cy-150) );

        cMap.put("de", ""+(cx+250)+","+(cy)+","+""+(cx+150)+","+(cy-150) );

        cMap.put("gh", ""+(cx-200)+","+(cy-150)+","+ ""+(cx-120)+","+(cy-250) );

        cMap.put("fh", ""+(cx-100)+","+(cy-150)+","+ ""+(cx-120)+","+(cy-250) );
        cMap.put("fi", ""+(cx-100)+","+(cy-150)+","+ ""+(cx+120)+","+(cy-250) );

        cMap.put("ei", ""+(cx+150)+","+(cy-150)+","+ ""+(cx+120)+","+(cy-250) );

        cMap.put("hj", ""+(cx-120)+","+(cy-250)+","+ ""+cx+","+(cy-350) );

        cMap.put("ij", ""+(cx+120)+","+(cy-250)+","+ ""+cx+","+(cy-350) );

        for(int i=0;i<coords.size();i++)
        {
            String c = coords.get(i);
            String c1[] = c.split(",");
            //canvas.drawCircle(Integer.parseInt(c1[0]), Integer.parseInt(c1[1]), 20, cPaints[i]);
            x.add(Integer.parseInt(c1[0]));
            y.add(Integer.parseInt(c1[1]));

            xc.add(Integer.parseInt(c1[0]));
            yc.add(Integer.parseInt(c1[1]));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map),0,0,null);

        // Drawing lines
        canvas.drawLine(x.get(0), y.get(0), x.get(1), y.get(1), lPaints[0]); //ab
        canvas.drawLine(x.get(0), y.get(0), x.get(2), y.get(2), lPaints[1]); //ac
        canvas.drawLine(x.get(0), y.get(0), x.get(3), y.get(3), lPaints[2]); //ad

        canvas.drawLine(x.get(1), y.get(1), x.get(4), y.get(4), lPaints[3]); //bg
        canvas.drawLine(x.get(1), y.get(1), x.get(5), y.get(5), lPaints[4]); //bf

        canvas.drawLine(x.get(2), y.get(2), x.get(5), y.get(5), lPaints[5]); //cf
        canvas.drawLine(x.get(2), y.get(2), x.get(6), y.get(6), lPaints[6]); //ce

        canvas.drawLine(x.get(3), y.get(3), x.get(6), y.get(6), lPaints[7]); //de
        canvas.drawLine(x.get(4), y.get(4), x.get(7), y.get(7), lPaints[8]); //gh

        canvas.drawLine(x.get(5), y.get(5), x.get(7), y.get(7), lPaints[9]); //fh
        canvas.drawLine(x.get(5), y.get(5), x.get(8), y.get(8), lPaints[10]); //fi

        canvas.drawLine(x.get(6), y.get(6), x.get(8), y.get(8), lPaints[11]); //ei

        canvas.drawLine(x.get(7), y.get(7), x.get(9), y.get(9), lPaints[12]); //hj
        canvas.drawLine(x.get(8), y.get(8), x.get(9), y.get(9), lPaints[13]); //ij

        for(int i=0;i<coords.size();i++)
        {
            String c = coords.get(i);
            String c1[] = c.split(",");
            //canvas.drawCircle(Integer.parseInt(c1[0]), Integer.parseInt(c1[1]), 20, cPaints[i]);
            //canvas.drawBitmap(images[i], Integer.parseInt(c1[0]) - 18, Integer.parseInt(c1[1]) - 19, null);
            canvas.drawBitmap(images[i], xc.get(i) - 18, yc.get(i) - 19, null);
        }

        invalidate();
    }

    @Override

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for(int i=0;i<10;i++)
                {
                    Paint p = new Paint();
                    p.setStyle(Paint.Style.FILL);
                    p.setColor(Color.parseColor("#CD5C5C"));
                    cPaints[i] = p;
                }

                for(int i=0;i<14;i++)
                {
                    Paint l = new Paint();
                    l.setStrokeWidth(3);
                    l.setColor(Color.BLACK);
                    lPaints[i] = l;
                }

                for(int i=0;i<10;i++)
                {
                    images[i] = BitmapFactory.decodeResource(getResources(), R.drawable.redss);
                }

                Bitmap i1 = BitmapFactory.decodeResource(getResources(), R.drawable.ambulance2ss);
                images[0] = i1;

                x = new ArrayList<Integer>();
                y = new ArrayList<Integer>();
                xc = new ArrayList<Integer>();
                yc = new ArrayList<Integer>();

                for(int i=0;i<coords.size();i++)
                {
                    String c = coords.get(i);
                    String c1[] = c.split(",");
                    //canvas.drawCircle(Integer.parseInt(c1[0]), Integer.parseInt(c1[1]), 20, cPaints[i]);
                    x.add(Integer.parseInt(c1[0]));
                    y.add(Integer.parseInt(c1[1]));
                    xc.add(Integer.parseInt(c1[0]));
                    yc.add(Integer.parseInt(c1[1]));
                }

                Paint p1 = new Paint();
                p1.setStyle(Paint.Style.FILL);
                p1.setColor(Color.parseColor("#008000"));
                cPaints[0] = p1;
                ma.startTracking();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
           case MotionEvent.ACTION_UP:
                break;
            }

        return true;

    }

}
