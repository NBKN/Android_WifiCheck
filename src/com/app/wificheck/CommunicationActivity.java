package com.app.wificheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class CommunicationActivity extends Activity{
	
    private BluetoothAdapter adapter;
    private BroadcastReceiver receiver;
    private DeviceList deviceList;
    private ListView listView;
	static UUID MY_UUID = UUID.fromString("292bb79c-dfbc-41d8-8834-a31c092f426f");//UUID.randomUUID();
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		SearchDivece();
		setContentView(listView);
	}

 
	public void SearchDivece() {		
		listView = new ListView(this);
		deviceList = new DeviceList(this);
		listView.setAdapter(deviceList);
		listView.setBackgroundColor(Color.WHITE);
		adapter = BluetoothAdapter.getDefaultAdapter();

		//デバイス探索用のBroadcastReceiverの準備
		setReceiver();		
		deviceList.addDeviceInfo("【接続履歴あり】", "", null);     
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();	
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a ListView
				deviceList.addDeviceInfo(device.getName(), device.getAddress(), device);	
			}
		}
		deviceList.addDeviceInfo("【接続履歴なし】", "", null);
		
		//レシーバで受け取るメッセージの設定
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(receiver, filter);        
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(receiver, filter);
	            
		if(adapter.isDiscovering()){
			adapter.cancelDiscovery();
		}            
		adapter.startDiscovery();   
    }
		
	private void setReceiver(){
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				//デバイスを見つけたらアダプタに名前とアドレスを追加
				if(BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); 
					if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
						deviceList.addDeviceInfo(device.getName(), device.getAddress(), device);
						listView.invalidateViews();
					}
				}else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    //デバイスを見つけられなければメッセージを表示
					if(deviceList.getCount() == 0){
                        deviceList.addDeviceInfo("Not Found Device", "", null);
                    }
                }
            }
        };
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// デバイスの探索を止める
		if (adapter != null) {
			adapter.cancelDiscovery();
		}
		// ブロードキャストのレシーバを外す
		this.unregisterReceiver(receiver);
	}
}

//デバイスのリストを格納するアダプター
class DeviceList extends BaseAdapter{
    private Context context;
    public static List<DeviceInfo> infoList;
    DeviceList(Context context){
        this.context = context;
        infoList = new ArrayList<DeviceInfo>();
    }

	public static BluetoothDevice DList(int position) {
		return null;
	}
	public void addDeviceInfo(String name, String address, BluetoothDevice device){
        infoList.add(new DeviceInfo(name, address, device));
    }
    @Override
    public int getCount(){
        return infoList.size();
    }
    @Override
    public Object getItem(int position){
        return infoList.get(position);
    }
    
    public static BluetoothDevice getDevice(int position){
        return infoList.get(position).device;//(position);
    }
   
    @Override
    public long getItemId(int position){
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //デバイスの名前を取得してTextViewで表示
        DeviceInfo info = infoList.get(position);
        TextView deviceView = new TextView(context);
        deviceView.setText(info.getName()+"\n　　"+info.getAddress());
        deviceView.setTextSize(25f);
        return deviceView;
    }
   
    //デバイスの名前とアドレスを持つクラス
    private static class DeviceInfo{
        private final String name;
        private final String address;
        private final BluetoothDevice device;
        
        private DeviceInfo(String name, String address, BluetoothDevice device){
            this.name = name;
            this.address = address;
            this.device = device;
        }
        
        private String getName(){
            return name;
        }
        
        private String getAddress(){
            return address;
        }
    }    
}
