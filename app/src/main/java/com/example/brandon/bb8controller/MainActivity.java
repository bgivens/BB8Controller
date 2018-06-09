package com.example.brandon.bb8controller;

import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.example.brandon.bb8controller.databinding.ActivityMainBinding;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
//https://github.com/controlwear/virtual-joystick-android
import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {

    Handler handler;
    Thread datasendthread;
    private Runnable periodicSend;
    Button sendButton;
    Button connectButton;
    private BluetoothHandler bluetoothHandler;
    ActivityMainBinding binding;
    JoystickView horizontaljoystick;
    JoystickView verticaljoystick;
    int vertical_joystick_data = 0;
    int horizontal_joystick_data = 0;
    ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        sendButton = binding.sendbutton;
        connectButton = binding.connectbutton;
        horizontaljoystick = binding.horizontaljoystick;
        verticaljoystick = binding.verticaljoystick;

        bluetoothHandler = new BluetoothHandler();

        handler = new Handler();
        periodicSend = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this,50);
                lock.readLock().lock();
                try{
                    //Convert to integer, pad with leading 0's if necessary to ensure a constant number of characters is being sent each time
                    bluetoothHandler.write(String.format("%03d", horizontal_joystick_data));
                    bluetoothHandler.write(String.format("%03d", vertical_joystick_data));
                } finally {
                    lock.readLock().unlock();
                }

            }
        };
        datasendthread = new Thread(periodicSend);
        datasendthread.start();

        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothHandler.write("Test");
            }
        });

        connectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothHandler.connect();
            }
        });

        horizontaljoystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                //String data = "Horizonal Angle: "+Integer.toString(angle)+"  Strength: "+Integer.toString(strength)+"\n\r";
                //bluetoothHandler.write(data);
                lock.writeLock().lock();
                try{
                    if(angle==0)
                    {
                        horizontal_joystick_data = strength;
                    }
                    else if(angle==180)
                    {
                        horizontal_joystick_data = strength+100; //TODO: Refactor to send negative numbers
                    }                                            //TODO: Need to send constant width regardless of negative sign
                    else
                    {
                        horizontal_joystick_data = 0;
                    }
                } finally {
                    lock.writeLock().unlock();
                }

            }
        },100);

        verticaljoystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                lock.writeLock().lock();
                try{
                    if(angle==90)
                    {
                        vertical_joystick_data = strength;
                    }
                    else if(angle==270)
                    {
                        vertical_joystick_data = strength+100;  //TODO: Same as above
                    }
                    else
                    {
                        vertical_joystick_data = 0;
                    }
                } finally {
                    lock.writeLock().unlock();
                }

            }
        },100);
    }
}
