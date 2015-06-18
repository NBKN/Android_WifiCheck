package com.app.wificheck;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

public class AverageMapView extends MyMapView {
	private Context context;
	public static double INTERVAL = 11.5;
	private ArrayList<PointDataSet> pointData;
	private ArrayList<MeshData> meshData = new ArrayList<MeshData>();

	public AverageMapView(Context _context, ArrayList<PointDataSet> data) {
		super(_context);
		this.context = _context;
		this.pointData = data;
		// TODO 自動生成されたコンストラクター・スタブ
		makeMeshData();
		calcAverage();
	}

	/** 描写 */
	@Override
	protected void onDraw(Canvas canvas) {
		// canvas.rotate(180,width/2,height/2);
		canvas.drawColor(Color.BLACK);
		drawMesh(canvas);
		makeMap(canvas);
		
		
		
		//addMeshAll(canvas);
	}

	/** 電波マップの平均化 */
	private void makeMeshData() {
		for (double i = 0; i < width - INTERVAL; i += INTERVAL) {
			for (double j = 0; j < height - INTERVAL; j += INTERVAL) {
				meshData.add(new MeshData(i, i + INTERVAL, j, j + INTERVAL));
			}
		}
	}

	private void calcAverage() {
		double tmpDbm = 0;
		for (int i = 0; i < meshData.size(); i++) {
			tmpDbm = 0;
			int sum_cnt = 0;
			for (int j = 0; j < pointData.size(); j++) {
				if (checkCover(meshData.get(i), pointData.get(j).getX(),
						pointData.get(j).getY())) {
					tmpDbm += pointData.get(j).getDbm();
					sum_cnt++;
				}
			}
			if (sum_cnt != 0) {
				meshData.get(i).setAvdBm(tmpDbm / sum_cnt);
			} else {
				meshData.get(i).setAvdBm(0);
			}
		}
	}

	/** 指定したメッシュに指定した座標が含まれているか */
	private boolean checkCover(MeshData mesh, double x, double y) {
		if (mesh.getX1() <= x && x < mesh.getX2() && mesh.getY1() <= y
				&& y < mesh.getY2()) {
			return true;
		} else {
			return false;
		}
	}

	private void drawMesh(Canvas canvas) {
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		
		for (int i = 0; i < meshData.size(); i++) {
			if (meshData.get(i).getAvdBm() < 0) {
				paint.setColor(VsualizeDbmMap.getDbmColor((int) meshData.get(i)
						.getAvdBm()));
				canvas.drawRect((float) meshData.get(i).getX1(),
						(float) (meshData.get(i).getY2()-INTERVAL),
						(float) (meshData.get(i).getX1() + INTERVAL),
						(float) (meshData.get(i).getY2() ), paint);

				// canvas.drawPoint((float)meshData.get(i).getX1(),
				// (float)meshData.get(i).getY2(), paint);//, (float)INTERVAL,
				// (float)INTERVAL, paint);
			}
		}
	}

	private void addMeshAll(Canvas canvas) {
		Paint meshPaint = new Paint();
		meshPaint.setColor(Color.RED);
		for (double i = 0; i < width; i += INTERVAL) {
			for (double j = 0; j < height; j += INTERVAL) {
				canvas.drawRect((float) (i - 2), (float) (j - 2),
						(float) (i + 2), (float) (j + 2), meshPaint);
			}
		}
	}
}

class MeshData {
	private double x1, x2, y1, y2;
	private double av_dBm;

	public MeshData(double _x1, double _x2, double _y1, double _y2) {
		this.x1 = _x1;
		this.x2 = _x2;
		this.y1 = _y1;
		this.y2 = _y2;
	}

	public double getX1() {
		return x1;
	}

	public double getX2() {
		return x2;
	}

	public double getY1() {
		return y1;
	}

	public double getY2() {
		return y2;
	}

	public void setAvdBm(double _dbm) {
		this.av_dBm = _dbm;
	}

	public double getAvdBm() {
		return av_dBm;
	}

}
