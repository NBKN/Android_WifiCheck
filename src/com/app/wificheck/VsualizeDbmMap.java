package com.app.wificheck;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;


/**
 * 
 * csvファイルを読み取り、dBmを可視化したMAPを表示
 *
 */
public class VsualizeDbmMap extends MakeMap {

	private static int[] colorRGB 
	= {Color.rgb(255, 0, 0), Color.rgb(250, 100, 0), Color.rgb(255, 255, 0),
	   Color.rgb(255, 0, 255), Color.rgb(0, 255, 255), Color.rgb(0, 255, 0),  Color.rgb(255, 255, 255)};

	@Override 
	protected void init2() {
		TextView[] colorText = new TextView[5];
		int dbm = -29;
		for(int i=0; i<colorText.length; i++) {
			colorText[i] = new TextView(this);
			if(dbm >= -29) {
				colorText[i].setText("-29以上");
				dbm -= 1;
			}
			else if(dbm >= -57) {
				if(dbm > -50) {
					colorText[i].setText(Integer.toString(dbm) + "〜" + Integer.toString(dbm-9));
				}
				else {
					colorText[i].setText(Integer.toString(dbm) + "〜-57");
				}
				dbm -= 10;
			}
			else {
				colorText[i].setText("-57未満");
			}
//			if(dbm >= -29) {
//				colorText[i].setText("-29以上");
//				dbm -= 1;
//			}
//			else if(dbm > -80) {
//				colorText[i].setText(Integer.toString(dbm) + "〜" + Integer.toString(dbm-9));
//				dbm -= 10;
//			}
//			else {
//				colorText[i].setText("-80以下");
//			}
			colorText[i].setTextColor(colorRGB[i]);
			btnView.addView(colorText[i]);
		}
		setView();
	}
	
	protected ArrayList<PointDataSet> fileRead() {
		ArrayList<PointDataSet> tmpPointData = new ArrayList();
		try {
			FileInputStream fileInputStream = openFileInput("map_and_dbm_data.csv");
		//	fileInputStream = openFileInput("all_dbm_map.csv");
	//		InputStream is = null;    //入力データ
	//		is = getAssets().open("old_dbm_map.csv");

//			byte[] readBytes = new byte[fileInputStream.available()];
	//		fileInputStream.read(readBytes);
	//		String readString = new String(readBytes);
			InputStreamReader isr = new InputStreamReader(fileInputStream,"UTF-8");
//			InputStreamReader isr = new InputStreamReader(is,"UTF-8");
			BufferedReader br= new BufferedReader(isr);
			String line;
			int l_cnt = 0;
			/* ファイルを１行ごとに読み込む */

			while ((line = br.readLine()) != null) {
				if(l_cnt > 1) {
					String[] array = line.split(",");
					double x, y;
					int dbm;
					x = Double.parseDouble(array[0]);
					y = Double.parseDouble(array[1]);
					dbm = Integer.parseInt(array[2]);
					tmpPointData.add(new PointDataSet(x, y, dbm));
				}
				else {
					l_cnt++;
				}
	
					
			}
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return tmpPointData;
	}
	
	/** Viewを設定(MapAndDbmで拡張するために分けておく) */
	protected void setView() {
		mapView = new VsualizeDbmMapView(getApplication(), fileRead());
		myView.addView(mapView);
		setContentView(myView);	
	}
	
	public static int getDbmColor(int dbm) {
		int color = (dbm+20)/10 * -1;
		Log.d("TAG", "dBm "+ dbm +  " color " + color);
		
		if(color < 0) {
			color = 0;
		}
//		if(dbm <= -80) {
//			return colorRGB[6];
//		}
//		else if(-80 < dbm && dbm <= -70) {
//			return colorRGB[5];
//		}
//		else if(-70 < dbm && dbm < -57) {
//			return colorRGB[4];
//		}
		if(dbm < -57) {
			return colorRGB[4];
		}
		else {
			return colorRGB[color];
		}
	}
	
}
