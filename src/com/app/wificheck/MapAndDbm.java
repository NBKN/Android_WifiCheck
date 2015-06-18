package com.app.wificheck;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MapAndDbm extends MakeMap {
	public static boolean canWriting = false;
	public static boolean isTouching = false;
	private boolean hasScaned = false;
	private boolean isConnecting = false;

	private WifiInfo w_info;
	private String connectSSIDName = null;
	private String writeString = null;
	private static int tmpdBm = 0;
	private int tmpSpeed = 0;
	public static double pointX = 0, pointY = 0;

	/* レイアウト拡張用 */
	private Button savaBtn; // セーブボタン
	private TextView pointText; // 座標用テキスト
	private TextView dbmText; // 電波レベルようテキスト

	@Override
	protected void init2() {
		pointX = 0;
		pointY = 0;
		Con = getApplicationContext();
		wifiScan = this;
		timer_cnt = 500;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 画面を消さない
		manager = (WifiManager) getSystemService(WIFI_SERVICE);
		w_info = manager.getConnectionInfo();
		connectSSIDName = w_info.getSSID();
		Log.d("TAG", connectSSIDName);
		Log.d("TAG", '"' + checkName + '"');

		/* 速度も速度も測定できたら記録 */
		if (connectSSIDName.equals('"' + checkName + '"')) {
			isConnecting = true;
		}
		setView();
		fileSet();
		timerTaskDo();
	}

	/** Viewを追加 */
	@Override
	protected void setView() {
		/* SAVEボタンを新たに追加 (csvファイルを保存) */
		savaBtn = new Button(this);
		savaBtn.setText("SAVE");
		btnView.addView(savaBtn);
		savaBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				Toast.makeText(MapAndDbm.this, "セーブしました！", Toast.LENGTH_LONG)
						.show();
				timerTaskStop();
				finish();
			}
		});

		/* 座標値とかをTextViewで表示 */
		pointText = new TextView(this);
		dbmText = new TextView(this);
		pointText.setText("x: " + (int) pointX + "\n" + "y: " + (int) pointY);
		dbmText.setText("電波: " + (int) tmpdBm);
		if (isConnecting) {
			dbmText.setText("電波: " + (int) tmpdBm + "\n" + "速度: "
					+ (int) tmpSpeed);
		} else {
			dbmText.setText("電波: " + (int) tmpdBm);
		}
		btnView.addView(pointText);
		btnView.addView(dbmText);

		mapView = new MyMapAndDbmView(getApplication());
		myView.addView(mapView);
		setContentView(myView);
	}

	@Override
	protected void fileSet() {
		try {
			fileOutputStream = openFileOutput("map_and_dbm_data.csv",
					MODE_WORLD_READABLE);
			osw = new OutputStreamWriter(fileOutputStream);
			writer = new BufferedWriter(osw);
			writer.write(Build.MODEL + "," + checkName + "\n" + "x,y,dBm,Mbps"
					+ "\n");
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	
	private  void fileSet2() {
		try {
			// Assets内のファイルストリームを開く
			InputStream input = getAssets().open("all_dbm_map.csv");

			// 書き込み先のストリームを開く
			fileOutputStream = openFileOutput("map_and_dbm_data.csv", MODE_WORLD_READABLE);

			// データをコピー
			byte[] buffer = new byte[1024];
			int n = 0;
			while (-1 != (n = input.read(buffer))) {
				fileOutputStream.write(buffer, 0, n);
			}

			// ストリームを閉じる
			input.close();

			osw = new OutputStreamWriter(fileOutputStream);
			writer = new BufferedWriter(osw);

		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	

	@Override
	protected void wifiCheck(Context con) {
		if (isTouching) {
			// WiFi繋がっているか
			hasScaned = false;
			manager = (WifiManager) getSystemService(WIFI_SERVICE);
			if (manager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
				// スキャンスタート
				manager.startScan();
				// スキャンした情報の保存
				scanWifi(con);
			}
			/* 測定できなかった場合 */
			if (!hasScaned) {
				writeString = pointX + "," + pointY + ",-200" + "\n";
				try {
					writer.write(writeString);
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
		}
	}

	/** Wifiをスキャン */
	@Override
	protected void scanWifi(Context con) {
		if (canWriting) {
			List<ScanResult> tmp = manager.getScanResults();
			if (tmp != null) {
				for (int i = 0; i < tmp.size(); i++) {
					if (tmp.get(i).SSID.equals(checkName)) {
						tmpdBm = tmp.get(i).level;
						tmpSpeed = w_info.getLinkSpeed();
						try {
							writeString = pointX + "," + pointY + ","
									+ Integer.toString(tmpdBm);
							if (isConnecting) {
								w_info = manager.getConnectionInfo();
								writeString += "," + Integer.toString(tmpSpeed);
								dbmText.setText("電波: " + (int) tmpdBm + "\n"
										+ "速度: " + (int) tmpSpeed);
							} else {
								dbmText.setText("電波: " + (int) tmpdBm);
							}
							writeString += "\n";
							writer.write(writeString);
							pointText.setText("x: " + (int) pointX + "\n"
									+ "y: " + (int) pointY);
							hasScaned = true;
						} catch (FileNotFoundException e) {
						} catch (IOException e) {
						}
						scan_cnt++; // 測定回数
						break;
					}
				}
			}
		}
	}
}
