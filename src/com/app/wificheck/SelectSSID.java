package com.app.wificheck;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SelectSSID extends SimpleWifiCheck {
	
	protected void init() {
		Con = getApplicationContext();
		wifiScan = this;
		max_cnt = 10;
		timer_cnt = 100;
		setting();
	}

	/** ListView　表示*/
	protected void setListView() {
		//リストに入れる
				final String[] items = new String[results.size()];
				for (int i=0; i<results.size(); i++) {
					String ssid = "";
					if(results.get(i).SSID.isEmpty() || results.get(i).SSID.equals(null) || results.get(i).SSID == null)
						ssid = "unknown";
					else
						ssid = results.get(i).SSID;
					items[i] = ssid;
				}			
				wifi_adapter = new ArrayAdapter<String>(Con, android.R.layout.simple_list_item_1, items);
				
				listView = (ListView) findViewById(R.id.list);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView listView = (ListView) parent;
				String item = (String) listView.getItemAtPosition(position);
				LongScan.checkName = item;
				Toast.makeText(SelectSSID.this, item, Toast.LENGTH_LONG).show();
				Intent intent = null;
				if(MainActivity.root == 0) {
					intent = new Intent(SelectSSID.this, GraphScan.class);
				}
				else if(MainActivity.root == 1) {
					intent = new Intent(SelectSSID.this, EndlessScan.class);
				}
				else if(MainActivity.root == 2) {
					intent = new Intent(SelectSSID.this, MapAndDbm.class);
				}
				if(intent != null) {
					startActivity(intent);
					finish();
				}
			}
		});
		listView.setAdapter(wifi_adapter);
	}
	
	
	
	/** Wifiをスキャン *//*
	protected void scanWifi(Context con) {
		results = manager.getScanResults();
		//dBm小さい順にソート
		//リストに入れる
		final String[] items = new String[results.size()];	
		for (int i=0; i<results.size(); i++) {
			String ssid = null;
			if(results.get(i).SSID.isEmpty() || results.get(i).SSID.equals(null) || results.get(i).SSID == null)
				ssid = "unknown";
			else
				ssid = results.get(i).SSID;
			items[i] = ssid;
		}
		wifi_adapter = new ArrayAdapter<String>(con, android.R.layout.simple_list_item_1, items);
		ListView listView = (ListView) findViewById(R.id.list);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListView listView = (ListView) parent;
				// クリックされたアイテムを取得します
				String item = (String) listView.getItemAtPosition(position);
				Toast.makeText(SelectSSID.this, item, Toast.LENGTH_LONG).show();
				Intent intent = null;
				if(MainActivity.root == 0) {
					LongScan.checkName = item;
					intent = new Intent(SelectSSID.this, LongScan.class);
				}
				else if(MainActivity.root == 1) {
					MyService.intRoot = 0;
					checkName = item;
					intent = new Intent(SelectSSID.this, EndlessScan.class);
				}
				else if(MainActivity.root == 2) {
					checkName = item;
					intent = new Intent(SelectSSID.this, MapAndDbm.class);
				}
				startActivity(intent);
				finish();
			}
		});
		listView.setAdapter(wifi_adapter);
	}*/
}
