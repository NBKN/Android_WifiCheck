package com.app.wificheck;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class CalcMapView extends MyMapView {

	private Context context;
	private double max_x=900, max_y=0, min_x=90, min_y=100000;
	private ArrayList<PointDataSet> allPointData = new ArrayList<PointDataSet>();

	private int[][] roomPoint = {{5,7,8,9,10},{11},{12},{80,84,86,90,92,96,98,102,104,118}};

	final double INTERVAL = 11.5;
	

	
	final double[] ROOM_WIDTH = {675,1354,1509,309};
	final double[] ROOM_HEIGHT = {1200,326,326,675};
	final double[] DIV_X = {6,13,15,3};
	final double[] DIV_Y = {12,3,3,6};


	//final String fileName = "point_data.csv";
//	final String fileName = "candidate_point_allPoint.csv";
	

	/* まとめて記録する用 */
	private FileOutputStream fileOutputStream;
	private OutputStreamWriter osw;
	private BufferedWriter writer;
	
	
	
	public CalcMapView(Context _context) {
		super(_context);
		this.context = _context;
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	protected void makeMap(Canvas canvas) {
		Paint mapPaint = new Paint();
		mapPaint.setColor(Color.BLACK);
		// 図面の線を描く
		double x=0, y=0, x2 = 0, y2 = 0;
		double scale = (double) (width / 160);
		
		for(int i=0; i<pointData.length; i++) {//画面再描画
			for(int j=0; j<pointData[i].size(); j++) {
				x = pointData[i].get(j).getX() *scale;// + (scale)*10;
				y = pointData[i].get(j).getY() *scale - (scale+4)*80;
				if(j < pointData[i].size()-1) {
					x2 = pointData[i].get(j+1).getX() *scale;// +  (scale)*10;
					y2 = pointData[i].get(j+1).getY() *scale -  (scale+4)*80;
				}
				else {
					x2 = pointData[i].get(0).getX() *scale;// +  (scale)*10;
					y2 = pointData[i].get(0).getY() *scale -  (scale+4)*80;
				}
	//			fileWrite(pointData[i].get(j).getX(), pointData[i].get(j).getY(), -x+width, y);
				canvas.drawLine((float)(-x+(float)width), (float)y, (float)(-x2+(float)width), (float)y2, mapPaint);
//				canvas.drawRect((float)(-x+(float)width)-1, (float)y-1, (float)(-x+(float)width)+1, (float)y+1, mapPaint);

		/*		if((-x+(float)width) > max_x)
					max_x = (-x+(float)width);
				if((-x+(float)width) < min_x)
					min_x = (-x+(float)width);				*/
				if(y > max_y)
					max_y = y;
				if(y < min_y)
					min_y = y;		
				
				allPointData.add(new PointDataSet(-x+width, y));
			}
		}
	//	fileFin();
		addMeshAll(canvas);
	//	addMeshRoom(canvas);
	}

	private void fileInit(String fileName) {
		try {
			fileOutputStream = context.openFileOutput(fileName, context.MODE_WORLD_READABLE);
			osw = new OutputStreamWriter(fileOutputStream);
			writer = new BufferedWriter(osw);
		//	writer.write("cnt,ori_x,ori_y,,x,y\n");
			writer.write("x,y,interval:" + Double.toString(INTERVAL) + "\n");
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	private void fileWrite(String str) {//double ori_x, double ori_y, double x, double y) {
		try {
			//String str = Double.toString(ori_x) + "," + Double.toString(ori_y) + ",," + Double.toString(x) + "," + Double.toString(y) + "\n";
		//	String str =  Double.toString(ori_x) + "," + Double.toString(ori_y) + "\n";
			writer.write(str);
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	private void fileFin() {
		try {
			fileOutputStream.flush();
			osw.flush();
			writer.flush();
			fileOutputStream.close();
			osw.close();
			writer.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
		
	private void addMeshAll(Canvas canvas) {
		fileInit("candidate_point_allPoint.csv");
		Paint meshPaint = new Paint();
		meshPaint.setColor(Color.RED);
		int cnt=0;
		for(double i=0; i<width; i+=INTERVAL) {
			for(double j=0; j<height; j+=INTERVAL) {
				for(int k=0; k<roomPoint.length; k++) {
					//if() {
					if(i>=min_x && i<=max_x && j>=min_y && j<=max_y) {

						canvas.drawRect((float)(i-2), (float)(j-2), (float)(i+2), (float)(j+2), meshPaint);
						String str =  Double.toString(i) + "," + Double.toString(j) + "\n";
						fileWrite(str);
						cnt++;
					}
				}
			}
		}	
		fileWrite("\n total, "+cnt);
		fileFin();
	}
	
	private void addMeshRoom(Canvas canvas) {
		ArrayList<RoomPointData> roomPointData = new ArrayList<RoomPointData>();
		try {
			InputStream isr = context.getAssets().open("room_point.csv");
			BufferedReader br = new BufferedReader(new InputStreamReader(isr));
			String line;
			br.readLine();
			/* ファイルを１行ごとに読み込む */
			while ((line = br.readLine()) != null) {
				String[] array = line.split(",");
				int p_num = Integer.parseInt(array[0]);
				double[] point = new double[4];
				for(int i=0; i<4; i++) {
					point[i] = Double.parseDouble(array[i+1]);
				}
				roomPointData.add(new RoomPointData(p_num, point[0], point[1], point[2], point[3]));
			}
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		//fileInputStream = openFileInput("all_dbm_map.csv");
		catch (UnsupportedEncodingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		fileInit("candidate_point_RoomPoint.csv");
		Paint meshPaint = new Paint();
		meshPaint.setColor(Color.RED);
		int cnt=0;

		for(int k=0; k<roomPointData.size(); k++) {
			int type = 0;
			double r_max_x = roomPointData.get(k).getMaxX();
			double r_max_y = roomPointData.get(k).getMaxY();
			double r_min_x = roomPointData.get(k).getMinX();
			double r_min_y = roomPointData.get(k).getMinY();
			for(int l=0; l<roomPoint.length; l++) {
				for(int m=0; m<roomPoint[l].length; m++) {
					if(roomPointData.get(k).getPolygonNum() == roomPoint[l][m]) {
						type = l;
					}
				}
			}
			double interval_x = (r_max_x - r_min_x) / (DIV_X[type]*2.0);
			double interval_y = (r_max_y - r_min_y) / (DIV_Y[type]*2.0);

			for(double i=r_min_x+INTERVAL; i<r_max_x-INTERVAL; i+=interval_x) {
				for(double j=r_min_y+INTERVAL; j<r_max_y-INTERVAL; j+=interval_y) {
							canvas.drawRect((float)(i-2), (float)(j-2), (float)(i+2), (float)(j+2), meshPaint);
							String str =  Double.toString(i) + "," + Double.toString(j) + "\n";
							fileWrite(str);
							cnt++;
				}
			}
		}
		Log.d("TAG", "cnt " +cnt);
		fileWrite("\ntortal,"+cnt);
		fileFin();
	}
}

class RoomPointData {
	private int polygon_num;
	private double min_x;
	private double max_x;
	private double min_y;
	private double max_y;
	
	public RoomPointData(int _polygon_num, double _min_x, double _max_x, double _min_y, double _max_y) {
		this.polygon_num = _polygon_num;
		this.min_x = _min_x;
		this.max_x = _max_x;
		this.min_y = _min_y;
		this.max_y = _max_y;
	}
	
	public int getPolygonNum() {
		return polygon_num;
	}
	
	public double getMinX() {
		return min_x;
	}
	
	public double getMaxX() {
		return max_x;
	}
	
	public double getMinY() {
		return min_y;
	}
	
	public double getMaxY() {
		return max_y;
	}
	
}
