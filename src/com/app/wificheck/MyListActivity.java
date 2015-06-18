package com.app.wificheck;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class MyListActivity  extends ListActivity { 	
	private ArrayAdapter<String> wifi_adapter;
	//public static String[] items;
	public static ArrayList<String> items;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_check);
				
		wifi_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
		setListAdapter(wifi_adapter);
	}
}
