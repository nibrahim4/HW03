package com.example.hw03;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public ExecutorService threadPool;
    public TextView tv_numberOfTimes;
    public SeekBar seekBar;
    public Integer selectedNumberOfTimes;
    public Button btn_generate;
    public Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("HW03");
        threadPool = Executors.newFixedThreadPool(2);
        tv_numberOfTimes = findViewById(R.id.tv_numberOfTimes);
        seekBar = findViewById(R.id.seekBar);
        btn_generate = findViewById(R.id.btn_generate);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
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
        @Override
        public void run() {
         ArrayList<Double> doubles =   HeavyWork.getArrayNumbers(selectedNumberOfTimes);

         Message startMessage = new Message();
         startMessage.what = STATUS_START;
         handler.sendMessage(startMessage);

         Message progressMessage = new Message();
         progressMessage.what = STATUS_PROGRESS;
         handler.sendMessage(progressMessage);

         Message stopMessage = new Message();
         stopMessage.what = STATUS_STOP;
         handler.sendMessage(stopMessage);


        }
    }
}
