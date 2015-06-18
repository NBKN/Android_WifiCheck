package com.app.wificheck;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

class Connecting extends Thread  {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    public static final int MESSAGE_READ = 2;
    public int TGTNUM = 0;

    public Connecting(boolean hostFlag, BluetoothSocket socket) {
    	System.out.println("device found");
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
 
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }
 
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        this.TGTNUM = MainActivity.BTthreadList.size()+1;
    }
 
    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
 
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                // Send the obtained bytes to the UI Activity
//                MainActivity.mHandler.obtainMessage(MESSAGE_READ*100+1, bytes, -1, buffer).sendToTarget();
       
                	MainActivity.mHandler.obtainMessage(MESSAGE_READ*100, bytes, -1, buffer).sendToTarget();
                
                System.out.println("getMessage:"+new String(buffer, 0, bytes));
            } catch (IOException e) {
                break;
            }
        }
    }
 
    /* Call this from the main Activity to send data to the remote device */
    public void write(byte[] bytes) {
        System.out.println("sendMessage:"+new String(bytes, 0, bytes.length));
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }
 
    /* Call this from the main Activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
