
package com.oymotion.gforceprofiledemo;

        import android.content.ContentValues;
        import android.os.Environment;
        import android.os.Handler;
        import android.os.HandlerThread;
        import android.os.Looper;
        import android.provider.ContactsContract;
        import android.util.Log;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;

        import com.jjoe64.graphview.GraphView;
        import com.jjoe64.graphview.series.DataPoint;
        import com.jjoe64.graphview.series.LineGraphSeries;

        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.util.Arrays;
        import java.util.Collection;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.List;
        import java.util.ListIterator;
        import java.util.Map;
        import java.util.concurrent.locks.Lock;
        import java.util.concurrent.locks.ReentrantLock;

public class EMGPlottingThread extends HandlerThread {

    private static final String TAG = "EMGPlottingClass";

    private Handler myHandler;
    private BufferedWriter myWriter;
    private String storageDirectoryName = "TCC_GFORCE_OYMOTION";

    private Map<String, Boolean> checkData = new HashMap<>();

    private Map<String, String> tempStore = new HashMap<>();

    List<String> hands = Arrays.asList("left", "right");



    private LineGraphSeries<DataPoint> left_ch1 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> left_ch2 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> left_ch3 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> left_ch4 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> left_ch5 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> left_ch6 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> left_ch7 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> left_ch8 = new LineGraphSeries<DataPoint>();



    private LineGraphSeries<DataPoint> right_ch1 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> right_ch2 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> right_ch3 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> right_ch4 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> right_ch5 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> right_ch6 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> right_ch7 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> right_ch8 = new LineGraphSeries<DataPoint>();


    private GraphView myGraphView;

    private int leftframecount = 0;

    private int rightframecount = 0;

    private int plotsampling_rate = 10000;

    private boolean one_hand_loaded = false;

    private Lock l = new ReentrantLock();



    public EMGPlottingThread(String name, GraphView graphView){

        super(name);
        myHandler = new Handler(Looper.myLooper());
        myGraphView = graphView;

    }

    public Handler getHandler(){
        return myHandler;
    }


    public void plot(int hand, int ch0, int ch1, int ch2, int ch3, int ch4, int ch5, int ch6, int ch7){

        boolean plot_now = false;

        if (hand == 0){


            if(leftframecount%plotsampling_rate == 0) {


                add_latest(ch0, left_ch1, leftframecount);
                add_latest(ch1, left_ch2, leftframecount);
                add_latest(ch2, left_ch3, leftframecount);
                add_latest(ch3, left_ch4, leftframecount);
                add_latest(ch4, left_ch5, leftframecount);
                add_latest(ch5, left_ch6, leftframecount);
                add_latest(ch6, left_ch7, leftframecount);
                add_latest(ch7, left_ch8, leftframecount);

                plot_now = true;



            }

            leftframecount+=1;


            } else if (hand == 1){

                if(rightframecount%plotsampling_rate == 0) {



                    add_latest(ch0, right_ch1, rightframecount);
                    add_latest(ch1, right_ch2, rightframecount);
                    add_latest(ch2, right_ch3, rightframecount);
                    add_latest(ch3, right_ch4, rightframecount);
                    add_latest(ch4, right_ch5, rightframecount);
                    add_latest(ch5, right_ch6, rightframecount);
                    add_latest(ch6, right_ch7, rightframecount);
                    add_latest(ch7, right_ch8, rightframecount);

                    plot_now = true;


                }

                rightframecount+=1;


            }

        if (plot_now) {


            l.lock();
            try {
                myGraphView.removeAllSeries();
            } finally {
                l.unlock();
            }

            plot_basic(left_ch1);
            plot_basic(left_ch2);
            plot_basic(left_ch3);
            plot_basic(left_ch4);
            plot_basic(left_ch5);
            plot_basic(left_ch6);
            plot_basic(left_ch7);
            plot_basic(left_ch8);

            plot_basic(right_ch1);
            plot_basic(right_ch2);
            plot_basic(right_ch3);
            plot_basic(right_ch4);
            plot_basic(right_ch5);
            plot_basic(right_ch6);
            plot_basic(right_ch7);
            plot_basic(right_ch8);
        }



    }


    private void add_latest(int emg_val, LineGraphSeries<DataPoint> series, int frame){



        series.appendData(new DataPoint(frame, emg_val),
                false, frame + 1);



    }


    private void plot_basic(LineGraphSeries<DataPoint> series){


        l.lock();
        try {
            myGraphView.addSeries(series);
        } finally {
            l.unlock();
        }




    }


}