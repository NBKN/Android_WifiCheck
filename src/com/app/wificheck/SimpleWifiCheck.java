package com.app.wificheck;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SimpleWifiCheck extends WifiScan {
	protected ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_check);
	}
	
	/** 初期化  */
	@Override
	protected void init() {
		Con = getApplicationContext();
		wifiScan = this;
		max_cnt = 100;
		timer_cnt = 100;
		setting();
	}
	
	/** Wifiをスキャン */
	@Override
	protected void scanWifi(Context con) {
		//初期化
		if(scan_cnt == 0) {
			results = manager.getScanResults();
			for (int i=0; i<results.size(); i++) {
				results.get(i).capabilities = "1";
			}
		}
		// cnt < max_cnt　の間、wifi levelのdBm値を加算
		else if(scan_cnt < max_cnt){
			List<ScanResult> tmp = manager.getScanResults();
			for(int i=0; i<tmp.size(); i++) {
				if(i < results.size()) {
					if(tmp.get(i).BSSID.equals(results.get(i).BSSID)) {
						//dBm値を加算
						results.get(i).level += tmp.get(i).level;
						//何回加算したか確認用
						results.get(i).capabilities = 
								String.valueOf(Integer.parseInt(results.get(i).capabilities)+1);
					}
				}
			}
		}
		else if (scan_cnt == max_cnt) {
			//dBmの平均を取る
			for (int i=0; i<results.size(); i++) {
				results.get(i).timestamp = Long.parseLong(results.get(i).capabilities);
				results.get(i).capabilities = 
						Double.toString((double)(results.get(i).level/Double.parseDouble(results.get(i).capabilities)));
			}
			//dBm小さい順にソート
			Collections.sort(results, new LevelComparator());
			setListView();
		}
		//タイマーストップ
		else {
        	timerTaskStop();
		}
	}	
	
	/** ListViewの常時 */
	protected void setListView() {
		//リストに入れる
		final String[] items = new String[results.size()];
		for (int i=0; i<results.size(); i++) {
			String ssid = "SSID : ";
			if(results.get(i).SSID.isEmpty() || results.get(i).SSID.equals(null) || results.get(i).SSID == null)
				ssid += "unknown";
			else
				ssid += results.get(i).SSID;
			items[i] = ssid + 
					"\nMAC : " +results.get(i).BSSID +
					"\n周波数 : "+ results.get(i).frequency +
					//"\n測定した回数 : " + results.get(i).timestamp +
					"\n平均受信信号強度 : " + results.get(i).capabilities + "\n";
		}			
		wifi_adapter = new ArrayAdapter<String>(Con, android.R.layout.simple_list_item_1, items);
		listView = (ListView) findViewById(R.id.list);
		listView.setAdapter(wifi_adapter);
	}
}


/**
 * ソートする用
 * 
 * */
class LevelComparator implements Comparator {
    private int sort = -1;    //デフォルトは昇順  
	@Override
	   public int compare(Object arg0, Object arg1) {  
		
		ScanResult item0 = (ScanResult)arg0;
		ScanResult item1 = (ScanResult)arg1;
		double d0 = Double.parseDouble(item0.capabilities);
		double d1 = Double.parseDouble(item1.capabilities);

		if (d0 == d1) {  
            return 0;   // arg0 = arg1  
        } else if (d0 > d1) {  
        	return 1 * sort;   // arg1 > arg2  
        } else {  
            return -1 * sort;  // arg1 < arg2  
        }
	}
}