
package com.oymotion.newgforceprofiledemo;

        import android.graphics.Color;
        import android.widget.CheckBox;

        import com.jjoe64.graphview.GraphView;
        import com.jjoe64.graphview.series.DataPoint;
        import com.jjoe64.graphview.series.LineGraphSeries;

        import java.util.concurrent.locks.Lock;
        import java.util.concurrent.locks.ReentrantLock;

public class EMGPlotting{

    private static final String TAG = "EMGPlottingClass";




    private LineGraphSeries<DataPoint> left_ch0 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> left_ch1 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> left_ch2 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> left_ch3 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> left_ch4 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> left_ch5 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> left_ch6 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> left_ch7 = new LineGraphSeries<DataPoint>();



    private LineGraphSeries<DataPoint> right_ch0 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> right_ch1 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> right_ch2 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> right_ch3 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> right_ch4 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> right_ch5 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> right_ch6 = new LineGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> right_ch7 = new LineGraphSeries<DataPoint>();



    private GraphView myGraphView_left;
    private GraphView myGraphView_right;

    private CheckBox mych0_chk;
    private CheckBox mych1_chk;
    private CheckBox mych2_chk;
    private CheckBox mych3_chk;
    private CheckBox mych4_chk;
    private CheckBox mych5_chk;
    private CheckBox mych6_chk;
    private CheckBox mych7_chk;

    private int leftframecount = 0;

    private int rightframecount = 0;

    private int plotsampling_rate = 1;

    private boolean one_hand_loaded = false;

    private Lock l = new ReentrantLock();



    public EMGPlotting(GraphView graphView_left, GraphView graphView_right,
                       CheckBox ch0_chk, CheckBox ch1_chk, CheckBox ch2_chk, CheckBox ch3_chk,
                       CheckBox ch4_chk, CheckBox ch5_chk, CheckBox ch6_chk, CheckBox ch7_chk){


        myGraphView_left = graphView_left;
        myGraphView_right = graphView_right;

        mych0_chk = ch0_chk;
        mych1_chk = ch1_chk;
        mych2_chk = ch2_chk;
        mych3_chk = ch3_chk;
        mych4_chk = ch4_chk;
        mych5_chk = ch5_chk;
        mych6_chk = ch6_chk;
        mych7_chk = ch7_chk;

        set_graph(myGraphView_left, left_ch0, left_ch1, left_ch2, left_ch3, left_ch4, left_ch5,
                left_ch6, left_ch7);

        set_graph(myGraphView_right, right_ch0, right_ch1, right_ch2, right_ch3, right_ch4, right_ch5,
                right_ch6, right_ch7);


    }



    public void plot(int hand, double ch0, double ch1, double ch2, double ch3, double ch4,
                     double ch5, double ch6, double ch7){



        if (hand == 0){


            if(leftframecount%plotsampling_rate == 0) {


                add_latest(ch0, left_ch0, leftframecount);
                add_latest(ch1, left_ch1, leftframecount);
                add_latest(ch2, left_ch2, leftframecount);
                add_latest(ch3, left_ch3, leftframecount);
                add_latest(ch4, left_ch4, leftframecount);
                add_latest(ch5, left_ch5, leftframecount);
                add_latest(ch6, left_ch6, leftframecount);
                add_latest(ch7, left_ch7, leftframecount);



            }

            leftframecount+=1;


        } else if (hand == 1){

            if(rightframecount%plotsampling_rate == 0) {



                add_latest(ch0, right_ch0, rightframecount);
                add_latest(ch1, right_ch1, rightframecount);
                add_latest(ch2, right_ch2, rightframecount);
                add_latest(ch3, right_ch3, rightframecount);
                add_latest(ch4, right_ch4, rightframecount);
                add_latest(ch5, right_ch5, rightframecount);
                add_latest(ch6, right_ch6, rightframecount);
                add_latest(ch7, right_ch7, rightframecount);



            }

            rightframecount+=1;


        }


    }


    private void add_latest(double emg_val, LineGraphSeries<DataPoint> series, int frame){


        series.appendData(new DataPoint(frame, emg_val),
                false, frame + 1);

    }


    private void add_series(GraphView graph, LineGraphSeries<DataPoint> series1, LineGraphSeries<DataPoint> series2,
                            LineGraphSeries<DataPoint> series3, LineGraphSeries<DataPoint> series4,
                            LineGraphSeries<DataPoint> series5, LineGraphSeries<DataPoint> series6,
                            LineGraphSeries<DataPoint> series7, LineGraphSeries<DataPoint> series8){

        graph.removeAllSeries();

        if (mych0_chk.isChecked()) {
            graph.addSeries(series1);
        }
        if (mych1_chk.isChecked()) {
            graph.addSeries(series2);
        }
        if (mych2_chk.isChecked()) {
            graph.addSeries(series3);
        }
        if (mych3_chk.isChecked()) {
            graph.addSeries(series4);
        }
        if (mych4_chk.isChecked()) {
            graph.addSeries(series5);
        }
        if (mych5_chk.isChecked()) {
            graph.addSeries(series6);
        }
        if (mych6_chk.isChecked()) {
            graph.addSeries(series7);
        }
        if (mych7_chk.isChecked()) {
            graph.addSeries(series8);
        }


    }


    public void set_graph(GraphView graph, LineGraphSeries<DataPoint> series1,
                          LineGraphSeries<DataPoint> series2, LineGraphSeries<DataPoint> series3,
                          LineGraphSeries<DataPoint> series4, LineGraphSeries<DataPoint> series5,
                          LineGraphSeries<DataPoint> series6, LineGraphSeries<DataPoint> series7,
                          LineGraphSeries<DataPoint> series8){


        set_series(series1, series2, series3, series4, series5, series6, series7, series8);

        add_series(graph, series1, series2, series3, series4, series5, series6, series7, series8);




        graph.getLegendRenderer().setVisible(true);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-300);
        graph.getViewport().setMaxY(300);



    }

    private void set_series(LineGraphSeries<DataPoint> series1, LineGraphSeries<DataPoint> series2,
                            LineGraphSeries<DataPoint> series3, LineGraphSeries<DataPoint> series4,
                            LineGraphSeries<DataPoint> series5, LineGraphSeries<DataPoint> series6,
                            LineGraphSeries<DataPoint> series7, LineGraphSeries<DataPoint> series8){

        int thickness = 5;


        series1.setColor(Color.CYAN);
        series1.setThickness(thickness);
        series1.setTitle("ch1");

        series2.setColor(Color.BLACK);
        series2.setThickness(thickness);
        series2.setTitle("ch2");

        series3.setColor(Color.BLUE);
        series3.setThickness(thickness);
        series3.setTitle("ch3");

        series4.setColor(Color.GREEN);
        series4.setThickness(thickness);
        series4.setTitle("ch4");

        series5.setColor(Color.MAGENTA);
        series5.setThickness(thickness);
        series5.setTitle("ch5");

        series6.setColor(Color.RED);
        series6.setThickness(thickness);
        series6.setTitle("ch6");

        series7.setColor(Color.YELLOW);
        series7.setThickness(thickness);
        series7.setTitle("ch7");

        series8.setColor(Color.GRAY);
        series8.setThickness(thickness);
        series8.setTitle("ch8");



    }



}