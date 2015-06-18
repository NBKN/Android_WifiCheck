package com.app.wificheck;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

public class GraphScan extends LongScan { 
	final private int MAX_CNT = 3600;
	public static int scan_cnt = 0;
	private double[] tmpdata = new double[MAX_CNT];   //グラフ作成のために一時的に取得したデータを保存しておく
	private double[] cnt_data = new double[MAX_CNT];  //グラフ作成のために測定した回数を保存（x軸のメモリになる）
	
	/** いろいろ初期化 */
	protected void init2() {
		Con = getApplicationContext();
		wifiScan = this;
		MakeGraph.items = new ArrayList<double[]>();
		MakeGraph.titles[0] = checkName;   //測定する名前代入
		MakeGraph.x_line = new ArrayList<double[]>(); //x軸用変数の初期化
		max_cnt = MAX_CNT; //最大測定回数
		timer_cnt = 1000;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //画面を消さない
	//	startService(new Intent(EndlessScan.this, MyService.class));           //サービス開始
		setting();
	}
	
	/** Wifiをスキャン */
	protected void scanWifi(Context con) {	
		// cnt < max_cnt　の間、wifi levelのdBm値を保存
		cnt_data[spend_time] = (double)spend_time;
		tmpResult = null;
		if(isChecking && scan_cnt<max_cnt) {
			tmpResult = manager.getScanResults();
			for(int i=0; i<tmpResult.size(); i++) {
				if(tmpResult.get(i).SSID.equals(checkName)) {
					tmpdata[scan_cnt] = (double)tmpResult.get(i).level;
					scan_cnt++; //測定回数
					break;
				}		
			}
		}
		else {
			isChecking = false;
			//グラフ作成用
			MakeGraph.items.add(tmpdata);
			MakeGraph.x_line.add(cnt_data);
			//グラフ画面に遷移
			IDemoChart[] mCharts = new IDemoChart[] { new MakeGraph() };
			  Log.d("TAG", "bbbbb");
			Intent intent = mCharts[0].execute(con);
			finish();
    		startActivity(intent);
		}
		spend_time++; //測定時間用
	}		
}