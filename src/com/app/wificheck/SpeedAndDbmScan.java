package com.app.wificheck;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


/** dBmに対するMbpsを取得 */
public class SpeedAndDbmScan extends EndlessScan { 
    private WifiInfo w_info;
    private boolean canScan = false;
    private String connectSSIDName = null;
	private String strResult = "";
    
	/** いろいろ初期化 */
	protected void init2() {
		Con = getApplicationContext();
		wifiScan = this;
		timer_cnt = 1000;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //画面を消さない
		startService(new Intent(SpeedAndDbmScan.this, MyService.class));           //サービス開始
		fileSet();  
		setting();
    	manager = (WifiManager) getSystemService(WIFI_SERVICE);
		w_info = manager.getConnectionInfo();
		connectSSIDName = w_info.getSSID();
	}	

	/** ファイルの設定 */
	protected void fileSet() {	
		try {
			fileOutputStream = openFileOutput("speed_scan_data.csv", MODE_WORLD_READABLE);
			osw = new OutputStreamWriter(fileOutputStream);
			writer = new BufferedWriter(osw);
			writer.write(connectSSIDName + "\n" + "dBm,Mbps" + "\n");
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	/** Wifiをスキャン */
	public void wifiCheck(Context con) {
    	/* プログレスバー表示 */ 	
    	showProgressBar();
    	manager = null;
    	manager = (WifiManager) getSystemService(WIFI_SERVICE);
    	if (manager.getWifiState()==WifiManager.WIFI_STATE_ENABLED) {
    		canScan = true;
    		//スキャンスタート
    		manager.startScan();
    		//スキャンした情報の保存
    		scanWifi(con);
    	}
    	else {
    		canScan = false;	
    	}
	}
	
	/** Wifiをスキャン */
	protected void scanWifi(Context con) {	
		w_info = null;
		w_info = manager.getConnectionInfo();
		
		if(isChecking) {
			if(w_info != null && w_info.getSSID().equals(connectSSIDName))  {
				try {
					Log.d("TAG", "speed "+ w_info.getLinkSpeed() + " desc "+w_info.describeContents() +" hide " + w_info.getHiddenSSID());
					String writeString = Double.toString(w_info.getRssi()) + "," + Double.toString(w_info.getLinkSpeed()) + "\n";
					writer.write(writeString);
					scan_cnt++; //測定回数
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
			}
		}
		else {
			isChecking = false;
			try {
				if(writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			Toast.makeText(SpeedAndDbmScan.this, "成功！", Toast.LENGTH_LONG).show();
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			endDialog();
		}
	}	
	
	
	/** プログレスバー表示 */
	protected void showProgressBar() {
		if(isChecking) {
			String mes = "測定不可";
			if(canScan) {
				mes  = "測定中\n ";
				if(scan_cnt%150 == 0) {
					strResult = "";
				}
				else {
					strResult +=  "[" 
								+ Integer.toString(w_info.getRssi()) 
								+ ",  " + Integer.toString(w_info.getLinkSpeed()) 
								+ "]  ";
					if(scan_cnt%5 == 0) {
						strResult += "\n";
					}
				}	
			mes += Integer.toString(scan_cnt) +"回測定\n" + strResult;
			}
			mProgressDialog.setMessage(mes);
		}
		else {
			if(mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			timerTaskStop();
		}
	}
	
	protected void Free() {
	    manager = null;
		scan_cnt = 0;
		fileOutputStream = null;
		osw = null;
		writer = null;
	}
}