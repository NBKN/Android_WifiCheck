package com.app.wificheck;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.view.WindowManager;
import android.widget.Toast;


/**
 * MAX_CNT秒間
 * strPluralCheckName　のSSIDを同時に測定
 * 
 *
 */
public class PluralScan extends EndlessScan {    
	//private String[] strPluralCheckName = {"SWS1day", "au_Wi-Fi", "0001softbank"};
	private static String[] strPluralCheckName = {"TORI-LAB", "Suzuki-Lab-G"};
	private FileOutputStream[] pluralFileOutputStream = new FileOutputStream[strPluralCheckName.length];	
	private OutputStreamWriter[] pluralOsw  = new OutputStreamWriter[strPluralCheckName.length];
	private BufferedWriter[] pluralWriter = new BufferedWriter[strPluralCheckName.length];	
	private final int MAX_CNT = 3600;          //ひとつのバックアップファイルに何個記録するか

	@Override
	protected void init2() {
		Con = getApplicationContext();
		wifiScan = this;
		max_cnt = MAX_CNT; //最大測定回数
		timer_cnt = 1000;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //画面を消さない
		startService(new Intent(PluralScan.this, MyService.class));           //サービス開始
		fileSet();  
		setting();
	}
	
	/** 繰り返し実行する部分 */
	@Override
	protected void wifiCheck(Context con) {
    	/* プログレスバー表示 */ 	
		hasScaned  = false;
    	showProgressBar();
    	manager = (WifiManager) getSystemService(WIFI_SERVICE);
    	if (manager.getWifiState()==WifiManager.WIFI_STATE_ENABLED) {
    		//スキャンスタート
    		manager.startScan();
    		//スキャンした情報の保存
    		scanWifi(con);
    	}
    	/* 測定できなかった場合 */
		if(!hasScaned) {
			String writeString = getTime() + ",-100" + "\n";
			try {
				for(int i=0; i<pluralWriter.length; i++) {
					pluralWriter[i].write(writeString);
				}
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		spend_time++; //測定時間用
	}
	
	/** Wifiをスキャンしデータを記録 */
	@Override
	protected void scanWifi(Context con) {	
		if(isChecking && max_cnt > spend_time) {
			tmpResult = null;
			tmpResult = manager.getScanResults();
			if(tmpResult != null)  {
				checkSSIDName();
			}	
		}
		/* 測定終了時の動作 */ 
		else {
			isChecking = false;
			try {
				for(int i=0; i<pluralWriter.length; i++) {
					pluralWriter[i].close();
				}
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			Toast.makeText(PluralScan.this, "成功！", Toast.LENGTH_LONG).show();
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			endDialog();
		}
	}	
	
	/** ファイルの設定 */
	@Override
	protected void fileSet() {	
		for(int i=0; i<pluralWriter.length; i++) {
			try {
				pluralFileOutputStream[i] = openFileOutput("plural_scan_data" + Integer.toString(i) + ".csv", MODE_WORLD_READABLE);
				pluralOsw[i] = new OutputStreamWriter(pluralFileOutputStream[i]);
				pluralWriter[i] = new BufferedWriter(pluralOsw[i]);
				pluralWriter[i].write(Build.MODEL + "," + strPluralCheckName[i] + "\n");
			} catch (FileNotFoundException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}
	
	/** SSID名判定 */
	@Override
	protected void checkSSIDName() {
		String nowTime = getTime() + ",";
		int three_cnt = 0;
		for(int j=0; j<pluralWriter.length; j++) {
			for(int i=0; i<tmpResult.size(); i++) {
				if(tmpResult.get(i).SSID.equals(strPluralCheckName[j])) {
					try {
						String writeString = nowTime + Double.toString(tmpResult.get(i).level) + ",\n";
						pluralWriter[j].write(writeString);
						//Log.d("TAG",  "j  "+COUNTHOUR + " i  "+i+"   "+ writeString);

						hasScaned = true;
						if(three_cnt == 0) {
							scan_cnt++; //測定回数
						}
						three_cnt++;
					} catch (FileNotFoundException e) {
					} catch (IOException e) {
					}
					break;
				}
			}
		}
	}
	
	/** 解放 */
	protected void Free() {
	    checkName = null;
	    manager = null;
		timer_cnt = 0;
		spend_time = 0;
		scan_cnt = 0;
	}
}

