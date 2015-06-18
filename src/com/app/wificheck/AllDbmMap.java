package com.app.wificheck;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;

import android.content.res.AssetManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AllDbmMap extends VsualizeDbmMap{
	
	/** Viewを追加 */
	@Override
	protected void setView() {
		Button[] avBtn = new Button[3];
		for(int i=0; i<3; i++) {
			avBtn[i] = new Button(this);
			if(i==0)
				avBtn[i].setText("平均化\n0.5m");
			else 
				avBtn[i].setText("平均化\n"+i+"m");
			btnView.addView(avBtn[i]);
			final int index = i;
			avBtn[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(index == 0) {
						AverageMapView.INTERVAL = 11.5;
					}
					else if(index == 1){
						AverageMapView.INTERVAL = 11.5*2;
					}
					else if(index == 2){
						AverageMapView.INTERVAL = 11.5*4;
					}
					mapView = null;
					myView.removeAllViews();
					myView.addView(btnView);
					mapView = new AverageMapView(getApplication(), fileRead());
					myView.addView(mapView);
					setContentView(myView);
				}
			});
		}
		
	///	mapView = new AverageMapView(getApplication(), fileRead());
		mapView = new VsualizeDbmMapView(getApplication(), fileRead());
		myView.addView(mapView);
		setContentView(myView);
	}
	
	@Override
	protected ArrayList<PointDataSet> fileRead() {
		ArrayList<PointDataSet> tmpPointData = new ArrayList();
		try {
			
			InputStream is = null;
		//	is= getAssets().open("double_wall.csv");	
			is= getAssets().open("all_dbm_map.csv");
		//	is= getAssets().open("streat_point.csv");		
		//	is = getAssets().open("all_wall.csv");
			BufferedReader br= new BufferedReader(new InputStreamReader(is));
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
			/* ファイル閉じておく */
			if( is != null) {
				is.close();
			}
			if(br != null) {
				br.close();
			}
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return calcPoint(tmpPointData);
	}
	
	/** 同じ座標は平均を取る */
	private ArrayList<PointDataSet>  calcPoint(ArrayList<PointDataSet> tmpData) {
		ArrayList<PointDataSet> resultData = new ArrayList();

		int tmp_x = (int)tmpData.get(0).getX();
		int tmp_y = (int)tmpData.get(0).getY();
		int tmp_dbm = (int)tmpData.get(0).getDbm();
		int i=1;
		
		while(i < tmpData.size()) {
			if(tmpData.get(i).getX() == tmp_x && tmpData.get(i).getY() == tmp_y) {
				double tmp_double = (double)(((double)tmp_dbm + (double)tmpData.get(i).getDbm())/2);
				BigDecimal bi = new BigDecimal(String.valueOf(tmp_double));
				//小数第一位で四捨五入
				tmp_dbm = (int)bi.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			else {
				resultData.add(new PointDataSet(tmp_x, tmp_y, tmp_dbm));
				tmp_x = (int)tmpData.get(i).getX();
				tmp_y = (int)tmpData.get(i).getY();
				tmp_dbm = (int)tmpData.get(i).getDbm();
			}
			i++;
		}
		resultData.add(new PointDataSet(tmp_x, tmp_y, tmp_dbm));
		return resultData;
	}
}

