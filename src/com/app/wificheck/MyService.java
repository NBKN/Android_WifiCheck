package com.app.wificheck;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
	public static Timer timer;
	private TimerTask timerTask;
    static final String TAG="TAG";
    public static int intRoot = 0;
 
    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        Toast.makeText(this, "MyService#onCreate", Toast.LENGTH_SHORT).show();
    }
 
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand Received start id " + startId + ": " + intent);
        Toast.makeText(this, "MyService#onStartCommand", Toast.LENGTH_SHORT).show();
        //明示的にサービスの起動、停止が決められる場合の返り値
  //      timer = new Timer();
        int time = 1000;
        if(intRoot == 1) {
        }
        else if(intRoot == 2) {
        	time = 500;
        }
//	    timer.schedule(timerTask, time, time);
        return START_STICKY;
    }
 
    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        Toast.makeText(this, "MyService#onDestroy", Toast.LENGTH_SHORT).show();
		if(timer != null) {
			timer = null;
			timerTask = null;
		}
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
}