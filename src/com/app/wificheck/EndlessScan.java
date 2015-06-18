package com.app.wificheck;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


/**
 * BACKUP_COUNTHOUR * MAX_CNT 秒もしくはそれ以下の任意の時間、
 * ひとつのSSIDを測定する。
 * csvファイルに時間とdBmを記録
 * データをまとめて記録するファイルと、
 * ひとつMAX_CNT個のデータを記録したCOUNTHOUR個のバックアップファイルを作成
 * 
 * */
public class EndlessScan extends LongScan { 
	
	private final int MAX_CNT = 3600;            //ひとつのバックアップファイルに何個記録するか
//	protected List<ScanResult> tmpResult;     //一時的なスキャン結果  
	
	/* バックアップファイル用 */
	private int BACKUP_COUNTHOUR = 12;         //バックアップファイルの数
	private FileOutputStream[] backupFileOutputStream;
	private OutputStreamWriter[] backupOsw;
	private BufferedWriter[] backupWriter;
	private int file_cnt = 0; //ファイルカウンタ

	/** 初期化
	 *  開始ボタンの設置
	 *  */

	/** いろいろ初期化 */
	protected void init2() {
		Con = getApplicationContext();
		wifiScan = this;
		max_cnt = MAX_CNT; //最大測定回数
		timer_cnt = 1000;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //画面を消さない
		startService(new Intent(EndlessScan.this, MyService.class));           //サービス開始
		fileSet();  
		setting();
	}
	
	/** ファイルの設定 */
	protected void fileSet() {	
	    backupFileOutputStream = new FileOutputStream[BACKUP_COUNTHOUR];
		backupOsw = new OutputStreamWriter[BACKUP_COUNTHOUR];
		backupWriter = new BufferedWriter[BACKUP_COUNTHOUR];
		try {
			fileOutputStream = openFileOutput("all_scan_data.csv", MODE_WORLD_READABLE);
			osw = new OutputStreamWriter(fileOutputStream);
			writer = new BufferedWriter(osw);
			writer.write(Build.MODEL + ","+ checkName + "\n");
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		for(int i=0; i<BACKUP_COUNTHOUR; i++) {
			try {
				backupFileOutputStream[i] = openFileOutput("scan_data_backup" + Integer.toString(i) + ".csv", MODE_WORLD_READABLE);
				backupOsw[i] = new OutputStreamWriter(backupFileOutputStream[i]);
				backupWriter[i] = new BufferedWriter(backupOsw[i]);
				backupWriter[i].write(Build.MODEL + ","+ checkName + "\n");
			} catch (FileNotFoundException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
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
				if(file_cnt < BACKUP_COUNTHOUR) {
					if(backupWriter[file_cnt] != null) {
						backupWriter[file_cnt].write(writeString);
					}
					if(writer != null) {
						writer.write(writeString);
					}
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
		if(isChecking && file_cnt < BACKUP_COUNTHOUR) {
			tmpResult = null;
			tmpResult = manager.getScanResults();
			if(tmpResult != null)  {
				checkSSIDName();
			}	
			/* バックアップファイルの切り替え */
			if(spend_time%max_cnt == 0) {
				try { 
					if(file_cnt < BACKUP_COUNTHOUR) {
						if(backupWriter[file_cnt] != null) {
							backupWriter[file_cnt].close();
						}
					}
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				file_cnt++;
			}
		}
		/* 測定終了時の動作 */ 
		else {
			isChecking = false;
			try {
				if(file_cnt != 0) {
					backupWriter[file_cnt-1].close();
				}
				if(writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			Toast.makeText(EndlessScan.this, "成功！", Toast.LENGTH_LONG).show();
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			endDialog();
		}
	}	
	
	/** SSID名判定 */
	protected void checkSSIDName() {
		for(int i=0; i<tmpResult.size(); i++) {
			if(tmpResult.get(i).SSID.equals(checkName)) {
				try {
					String writeString = getTime() + "," + Double.toString(tmpResult.get(i).level) + "\n";
					backupWriter[file_cnt].write(writeString);
					writer.write(writeString);
					hasScaned = true;
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
				scan_cnt++; //測定回数
				break;
			}
		}
	}

	/** 解放 */
	protected void Free() {
    	stopService(new Intent(EndlessScan.this, MyService.class));
	    checkName = null; //"TORI-LAB";  //測定するSSID
	    manager = null;
		timer_cnt = 0;
		spend_time = 0;
		scan_cnt = 0;
		file_cnt = 0;
		for(int i=0; i<BACKUP_COUNTHOUR; i++) {
			backupFileOutputStream[i] = null;
			backupOsw[i] = null;
			backupWriter[i] = null;
		}
		 fileOutputStream = null;
		 osw = null;
		 writer = null;
	}

	/** 時間取得 */
	protected String getTime() {
		long currentTimeMillis = System.currentTimeMillis();
		String strNowTime = null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(currentTimeMillis);
		strNowTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" +
			    calendar.get(Calendar.MINUTE) + ":" +
			    calendar.get(Calendar.SECOND);
		return strNowTime;
	}	
}
