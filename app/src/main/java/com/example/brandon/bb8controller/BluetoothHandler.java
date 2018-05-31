package com.example.brandon.bb8controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.logging.Handler;

public class BluetoothHandler{

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice device;
    private ConnectedThread mConnectedThread;

    public BluetoothHandler()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        device = mBluetoothAdapter.getRemoteDevice("98:D3:31:FD:1A:BB"); //HC-06 address: 98:D3:31:FD:1A:BB
        mConnectedThread = new ConnectedThread(device);
    }

    public void write(String data)
    {
        try {
            mConnectedThread.write(data.getBytes("UTF-8"));
        } catch (IOException e){
        }
    }

    public void connect()
    {
        mConnectedThread.connect();
    }

    //TODO: Need to close connections upon exit
    private class ConnectedThread extends Thread
    {
        private BluetoothSocket mmSocket;
        //private InputStream mmInStream; //Likely not needed, unless robot asks for input rather than sending at set intervals
        private OutputStream mmOutStream;

        public ConnectedThread(BluetoothDevice device)
        {
            connect();
        }

        public void connect()
        {
            try {
                mmSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                mmSocket.connect();
                //mmInStream = mmSocket.getInputStream();
                mmOutStream = mmSocket.getOutputStream();
            } catch (IOException e){
            }
        }

        public void write(byte[] buffer)
        {
            try{
                mmOutStream.write(buffer);
            } catch(IOException e){
            }

        }

    }


}
