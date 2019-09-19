package com.example.hw03;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public ExecutorService threadPool;
    public TextView tv_numberOfTimes;
    public SeekBar seekBar;
    public Integer selectedNumberOfTimes;
    public Button btn_generate;
    public Handler handler;
    public TextView tv_min_result;
    public TextView tv_max_result;
    public TextView tv_average_result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("HW03");
        threadPool = Executors.newFixedThreadPool(2);
        tv_numberOfTimes = findViewById(R.id.tv_numberOfTimes);
        seekBar = findViewById(R.id.seekBar);
        btn_generate = findViewById(R.id.btn_generate);
        tv_min_result = findViewById(R.id.tv_min_result);
        tv_max_result = findViewById(R.id.tv_max_result);
        tv_average_result = findViewById(R.id.tv_average_result);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {


                switch (message.what){
                    case DoWork.STATUS_START:
                        Log.d("TEST", "Message Received... " + message.obj);

                        break;
                    case DoWork.STATUS_PROGRESS:
                        tv_average_result.setText( Double.toString(message.getData().getDouble(DoWork.AVERAGE_KEY)));
                        tv_max_result.setText( Double.toString(message.getData().getDouble(DoWork.MAX_KEY)));
                        tv_min_result.setText( Double.toString(message.getData().getDouble(DoWork.MIN_KEY)));
                        Log.d("TEST", "Message In Progress..." + message.getData().getDoubleArray(DoWork.DOUBLES_KEY));
                            break;
                    case DoWork.STATUS_STOP:
                        Log.d("TEST", "Message Stopped..." + message.obj);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + message.what);
                }
                return false;
            }
        });
        btn_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                threadPool.execute(new DoWork());
            }
        });

        seekBar.setMax(10);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv_numberOfTimes.setText(i + " times");
                selectedNumberOfTimes = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
    }

    class DoWork implements Runnable {

        static final int STATUS_START = 0x00;
        static final int STATUS_PROGRESS = 0x01;
        static final int STATUS_STOP = 0x02;
        static final String MAX_KEY = "max";
        static final String MIN_KEY = "min";
        static final String AVERAGE_KEY = "average";
        static final String DOUBLES_KEY = "doubles";
        @Override
        public void run() {
         ArrayList<Double> doubles =   HeavyWork.getArrayNumbers(selectedNumberOfTimes);
         double[] doubleArray = doubles.stream().mapToDouble(Double::doubleValue).toArray();

           Double average = 0.0;
           Double max = 0.0;
           Double min = 0.0;

            for (Double number : doubles) {
                average += number;
            }

            average = average / doubles.size();

            max = Collections.max(doubles);
            min = Collections.min(doubles);

         Message startMessage = new Message();
         startMessage.what = STATUS_START;
         handler.sendMessage(startMessage);

         Message progressMessage = new Message();
         progressMessage.what = STATUS_PROGRESS;
         Bundle bundle = new Bundle();

         bundle.putDoubleArray(DOUBLES_KEY, doubleArray);
         bundle.putDouble(MAX_KEY, max);
         bundle.putDouble(MIN_KEY, min);
         bundle.putDouble(AVERAGE_KEY, average);
         progressMessage.setData(bundle);
         handler.sendMessage(progressMessage);

         Message stopMessage = new Message();
         stopMessage.what = STATUS_STOP;
         handler.sendMessage(stopMessage);


        }
    }
}
