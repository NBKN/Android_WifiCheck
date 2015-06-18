package com.app.wificheck;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.app.wificheck.R.id;

public class MainActivity extends Activity {	
	public static Handler mHandler;
	public static ArrayList<Connecting> BTthreadList = new ArrayList<Connecting>();

	public static MainActivity main  = null;
	public static int root = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    main = this;
	    Button wifi = (Button)findViewById(id.wifi_button);
	    Button bt = (Button)findViewById(id.bt_button);
	    Button gyro = (Button)findViewById(id.gyro_button);
		Button l_scan = (Button)findViewById(id.Long_button);
		Button e_scan = (Button)findViewById(id.Endless_button);
		Button p_scan = (Button)findViewById(id.PluralScan_button);
		Button s_scan = (Button)findViewById(id.Speed_button);
		Button map = (Button)findViewById(id.Map_button);
		Button map_dbm = (Button)findViewById(id.MandD_button);
		Button v_map = (Button)findViewById(id.visual_button);
		Button all_map = (Button)findViewById(id.all_dbm_map_button);
		Button download = (Button)findViewById(id.Down_button);
		Button calc = (Button)findViewById(id.calc_button);
		
	    wifi.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Intent intent = new Intent(MainActivity.this, SimpleWifiCheck.class);
	    		startActivity(intent);
	    	}
	    });
	    
	    bt.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, CommunicationActivity.class);
				startActivity(intent);
	    	}
	    });
	    
	    gyro.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Intent intent = new Intent(MainActivity.this, GyroSensor.class);
	    		startActivity(intent);
	    	}
	    });
	    
	    l_scan.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		root = 0;
	    		Intent intent = new Intent(MainActivity.this, SelectSSID.class);
	    		startActivity(intent);
	    	}
	    });
	    
	    e_scan.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		root = 1;
	    		Intent intent = new Intent(MainActivity.this, SelectSSID.class);
	    		startActivity(intent);
	    	}
	    });
	    
	    p_scan.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		MyService.intRoot = 1;
	    		Intent intent = new Intent(MainActivity.this, PluralScan.class);
	    		startActivity(intent);
	    	}
	    });
	    
	    s_scan.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		MyService.intRoot = 2;
	    		Intent intent = new Intent(MainActivity.this, SpeedAndDbmScan.class);
	    		startActivity(intent);
	    	}
	    });
	    
	    map.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Intent intent = new Intent(MainActivity.this, MakeMap.class);
	    		startActivity(intent);
	    	}
	    });
	    
	    map_dbm.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		root = 2;
	    		Intent intent = new Intent(MainActivity.this, SelectSSID.class);
	    		startActivity(intent);
	    	}
	    });
	    
	    v_map.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Intent intent = new Intent(MainActivity.this, VsualizeDbmMap.class);
	    		startActivity(intent);
	    	}
	    });	   
	    
	    all_map.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Intent intent = new Intent(MainActivity.this, AllDbmMap.class);
	    		startActivity(intent);
	    	}
	    });	
	    
	    download.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Intent intent = new Intent(MainActivity.this, DownLoadActivity.class);
	    		startActivity(intent);
	    	}
	    });	
	    
	    calc.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Intent intent = new Intent(MainActivity.this, CalcActivity.class);
	    		startActivity(intent);
	    	}
	    });	
	}
}