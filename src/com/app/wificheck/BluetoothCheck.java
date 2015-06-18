package com.app.wificheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.ArrayAdapter;

public class BluetoothCheck {
	private abstract class ReceiverThread extends Thread {
		protected BluetoothSocket mSocket;

		
		protected void sendMessage(String message) throws IOException {
			OutputStream os = mSocket.getOutputStream();
			os.write(message.getBytes());
			os.write("\n".getBytes());
		}

		protected void loop() throws IOException {
		//	mActivity.invalidate();
			BufferedReader br = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			String message;
			while ((message = br.readLine()) != null) {
				mBoard.receiveMessage(message);
			//	mActivity.invalidate();
			}
		}
	}

	private class ServerThread extends ReceiverThread  {
		private BluetoothServerSocket mServerSocket;

		private ServerThread() {
			try {
				mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(mActivity.getPackageName(), mUuid);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				Log.d(TAG, "accepting...");
				mSocket = mServerSocket.accept();
				Log.d(TAG, "accepted");
				mBoard = new Board(Board.COLOR_WHITE);
				loop();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				cancel();
			}
		}

		private void cancel() {
			try {
				mServerSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class ClientThread extends ReceiverThread  {
		private final BluetoothDevice mServer;

		private ClientThread(String address) {
			mServer = mBluetoothAdapter.getRemoteDevice(address);
			try {
				mSocket = mServer.createRfcommSocketToServiceRecord(mUuid);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			// connect() の前にデバイス検出をやめる必要がある
			mBluetoothAdapter.cancelDiscovery();
			try {
				// サーバに接続する
				mSocket.connect();
				mBoard = new Board(Board.COLOR_BLACK);
				loop();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				cancel();
			}

		}

		private void cancel() {
			mBoard = null;
			try {
				mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static final int REQUEST_ENABLE_BT = 1234;
	private static final int REQUEST_DISCOVERABLE_BT = 5678;
	private static final int DURATION = 300;
	private final String TAG = getClass().getSimpleName();
	private Board mBoard;
	private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private final MainActivity mActivity;
	private final ArrayAdapter<String> mCandidateServers;
	private final UUID mUuid = UUID.fromString("e74c254e-db32-4bb9-8f68-3b1f7d732f21"); // このアプリ固有の値。他のアプリで使用してはならない
	private ServerThread mServerThread;
	private ClientThread mClientThread;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				Log.d(TAG, "ACTION_FOUND");
				// デバイスが見つかった場合、Intent から BluetoothDevice を取り出す
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// 名前とアドレスを所定のフォーマットで ArrayAdapter に格納
				mCandidateServers.add(device.getName() + "\n" + device.getAddress());
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				Log.d(TAG, "ACTION_DISCOVERY_FINISHED");
				// デバイス検出が終了した場合は、BroadcastReceiver を解除
				context.unregisterReceiver(mReceiver);
			}
		}
	};

	public BluetoothCheck(MainActivity context, ArrayAdapter<String> candidateServers, ArrayAdapter<String> servers) {
		mActivity = context;
		mCandidateServers = candidateServers;
		Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
		for (BluetoothDevice device : devices) {
			servers.add(device.getName() + "\n" + device.getAddress());
		}
	}

	public void turnOn() {
		if (!mBluetoothAdapter.isEnabled()) {
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			mActivity.startActivityForResult(intent, REQUEST_ENABLE_BT);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + data);
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode != 0) {
				// 「はい」が選択された
			}
		} else if (requestCode == REQUEST_DISCOVERABLE_BT) {
			if (resultCode == DURATION) {
				// 「はい」が選択された
			}
		}
	}

	public void searchServer() {
		mCandidateServers.clear();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		mActivity.registerReceiver(mReceiver, filter);
		mBluetoothAdapter.startDiscovery();
	}

	public void startDiscoverable() {
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DURATION);
		mActivity.startActivityForResult(intent, REQUEST_DISCOVERABLE_BT);
	}

	public void cancelDiscovery() {
		mBluetoothAdapter.cancelDiscovery();
	}

	public void startServer() {
		if (mServerThread != null) {
			mServerThread.cancel();
		}
		mServerThread = new ServerThread();
		mServerThread.start();
	}

	public void connect(String address) {
		int index;
		if ((index = address.indexOf("\n")) != -1) {
			address = address.substring(index + 1);
		}
		// クライアント用のスレッドを生成
		mClientThread = new ClientThread(address);
		mClientThread.start();
	}

	public boolean isConnected() {
		return mBoard != null;
	}

	public Board getBoard() {
		return mBoard;
	}

	public void sendMessage(String message) {
		try {
			if (mServerThread != null) {
				mServerThread.sendMessage(message);
			}
			if (mClientThread != null) {
				mClientThread.sendMessage(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void cancel() {
		if (mServerThread != null) {
			mServerThread.cancel();
			mServerThread = null;
		}
		if (mClientThread != null) {
			mClientThread.cancel();
			mClientThread = null;
		}
	}
}

