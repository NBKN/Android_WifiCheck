package com.app.wificheck;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;

public class WifiScan extends Activity {
	protected static WifiManager manager;
	protected ArrayAdapter<String> wifi_adapter;
	protected List<ScanResult> results;
	protected ProgressDialog mProgressDialog = null;
	
	protected int max_cnt = 100;
	protected int scan_cnt = 0;
	protected int timer_cnt = 0;

	protected Timer scanTimer;
	protected TimerTask scanTimerTask;
	
	protected WifiScan wifiScan = null;
	
	protected Context Con = null;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	
	protected void init() {

	}
	
	protected void setting() {
		setProgressDialog();
		timerTaskDo();
	}
	
	
	protected void timerTaskDo() {
		scanTimer = new Timer();
		scanTimerTask = new MyTimerTask(Con, wifiScan);
	    scanTimer.scheduleAtFixedRate(scanTimerTask, timer_cnt, timer_cnt);
	}
	
	protected void timerTaskStop() {
		if(scanTimer != null) {
			scanTimer.cancel();
			scanTimer = null;
		}
		if(scanTimerTask != null) {
			scanTimerTask.cancel();
			scanTimerTask = null;
		}
	}
	
	protected void setProgressDialog() {
		mProgressDialog = new ProgressDialog(wifiScan);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
	}

	protected void setDialog() {
		mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "測定終了", 
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
                // TODO 自動生成されたメソッド・スタブ
            }
        });
	}
	
	protected void wifiCheck(Context _con) {
		wifi_adapter = null;
		// WiFi繋がっているか
	    manager = (WifiManager) getSystemService(WIFI_SERVICE);
	    if (manager.getWifiState()==WifiManager.WIFI_STATE_ENABLED) {
	    	//スキャンスタート
	    	manager.startScan();
	    	scanWifi(_con);
	    	showProgressBar();
	    	scan_cnt++;
        }
	}
	
	/** Wifiをスキャン */
	protected void scanWifi(Context con) { }
	
	/** プログレスバー表示 */
	protected void showProgressBar() {
		 if(scan_cnt == 0) {
	        	mProgressDialog.setMessage("測定中");
	        	mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        	mProgressDialog.setCancelable(false);
	        	mProgressDialog.show();
	        }
	        else if(0<scan_cnt && scan_cnt <max_cnt) {
	        	String mes = "測定中\nあと ";
	        	mes += Integer.toString((max_cnt-scan_cnt)/10) + "秒";
	        	mProgressDialog.setMessage(mes);
	        }
	        else if(scan_cnt > max_cnt) {
	        	if(mProgressDialog != null) {
	        		mProgressDialog.dismiss();
	        		mProgressDialog = null;
	        	}
	        }
	}	
	
}


/** タイマータスククラス */
class MyTimerTask extends TimerTask {
	private Handler handler;
	private WifiScan wifiScen = null;
	private Context Con;
	
	public MyTimerTask(Context _context, WifiScan _wifiScen) {
		this.Con = _context;
		this.wifiScen = _wifiScen;
		handler = new Handler();
	}	    
	@Override
	public void run() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				wifiScen.wifiCheck(Con);
			}
		});
	} 
}