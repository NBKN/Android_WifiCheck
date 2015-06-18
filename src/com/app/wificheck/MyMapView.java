package com.app.wificheck;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * MakeMapで使うView
 *
 */
public class MyMapView extends View {
	//座標データ
	protected ArrayList<PointDataSet>[] pointData;    
	//履歴保持用のスタック
    private final HistoryStack<ArrayList<PointF>> history = new HistoryStack<ArrayList<PointF>>();
    //今の吸った苦
    private ArrayList<PointF> currentStroke;
    private Context context;
    
	//履歴の座標
    private final Paint historyPaint = new Paint(); {
    	historyPaint.setColor(Color.CYAN);
    	historyPaint.setStrokeWidth(10.f);
    }
    
    public static int width, height;
    
    /** コンストラクタ */
	public MyMapView(Context _context) {
		super(_context);
		this.pointData = getData(_context);
		this.context = _context;
		// TODO Auto-generated constructor stub
	}
	
	/** 描写 */
	@Override
	protected void onDraw(Canvas canvas) {
		//canvas.rotate(180,width/2,height/2);
		canvas.drawColor(Color.BLACK);
		makeMap(canvas);
		Matrix mtrx = new Matrix();
		//targetImageView.setScaleType(ScaleType.MATRIX);
	//	canvas.preTranslate(-centerX, -centerY);//拡大縮小の中央点を変えたいため、一旦動かす
	//	mtrx.postRotate(0);
		//mtrx.postScale(scaleX, scaleY);
	//	mtrx.postTranslate(0,0);//最終的に動かしたい地点へ移動
	//	canvas.setMatrix(mtrx);
	//	canvas.invalidate(); 
		
        // 履歴に入っていドラックした線を描画する
        for(final ArrayList<PointF> stroke:history.iterateUndo()){
            drawStroke(canvas, historyPaint,stroke);
        }
        // 現在描画中のドラックした線を描画する
        if( currentStroke != null){
            drawStroke(canvas, historyPaint,currentStroke );
        }
	}

	protected void makeMap(Canvas canvas) {
		Paint mapPaint = new Paint();
		mapPaint.setStrokeWidth(2);
		mapPaint.setColor(Color.GREEN);
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
				canvas.drawLine((float)(-x+(float)width), (float)y, (float)(-x2+(float)width), (float)y2, mapPaint);
			}
		}
	}
	
	protected void drawDbmLine(Canvas canvas) {
		
	}
	
	/** 座標データをファイルから読み取る */
	private ArrayList<PointDataSet>[] getData(Context con) {
		ArrayList<PointDataSet>[] tmpPointData = new ArrayList[256]; //データ保持用 POLYGONが256個くらいだから
		InputStream is = null;    //入力データ
		BufferedReader br = null; //入力データ変換用
			
		/* 初期化 */
		for(int i=0; i<tmpPointData.length; i++) {
			tmpPointData[i] = new ArrayList<PointDataSet>();
		}
		try {
			is = con.getAssets().open("cad_data.txt");
	        br = new BufferedReader(new InputStreamReader(is));
			String line;     //テキストの文字読み取り用
			int p_cnt = -1;  //ポリゴンのカウンター
			/* ファイルを１行ごとに読み込む */
			while ((line = br.readLine()) != null) {
				/* POLYGON と書かれているところは読み取らない */
				if(!line.matches(".*" +  "POLYGON" + ".*")) {
					String[] array = line.split(","); //カンマで分割
					double x, y;
					x = Double.parseDouble(array[0]);
					y = Double.parseDouble(array[1]);
					tmpPointData[p_cnt].add(new PointDataSet(x, y));  //データ保存
				}
				/* 次のPOLYGON を読み込む */
				else {
					p_cnt++;
				}
			}
			/* ファイル閉じておく */
			if( is != null) {
				is.close();
			}
			if(br != null) {
				br.close();
			}	
		}
		catch(Exception e) {
			System.out.println(e);  //エラーが起きたらエラー内容を表示
		}
		return tmpPointData;
	}
	
    /**
     * PointFの配列を元に一連の線を描画する
     * @param canvas
     * @param paint
     * @param stroke
     */
	private void drawStroke(Canvas canvas,Paint paint,ArrayList<PointF> stroke){	
		PointF startPoint = null;
        for(PointF pf:stroke){
        	if( startPoint != null){
                canvas.drawLine(startPoint.x, startPoint.y, pf.x, pf.y, paint);
            }
            startPoint = pf;
        }
    }
	/** アンドゥ*/
	public void undo(){		
		history.undo();
		invalidate();
	}
 
    /** リドゥ */
    public void redo(){
        history.redo();
        invalidate();
    }
    
	 /** 座標を取得して保存(MyMapAndDbmViewで使用) */
    protected void getPoint(boolean isTouching, double _x, double _y) { }
	
    /** タッチ操作  */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
    	switch (e.getAction()) {	
    	case MotionEvent.ACTION_DOWN:
    		// 新しい描画
    		currentStroke = new ArrayList<PointF>();
    		getPoint(true, e.getX(), e.getY());
    		break;
    	case MotionEvent.ACTION_MOVE:
    		currentStroke.add(new PointF(e.getX(), e.getY()));
    		getPoint(true, e.getX(), e.getY());
    		invalidate();
    		break;
    	case MotionEvent.ACTION_UP:
    		history.add(currentStroke);
    		currentStroke = null;
    		getPoint(false, e.getX(), e.getY());
    		invalidate();
    		break;
    	default:
    		break;
    	}
    	return true;
    }
}

/**
 * MapAndDbmで使うView
 * タッチ時に座標を取得する昨日をオーバーライドしただけ
 * */
class MyMapAndDbmView extends MyMapView {
	public MyMapAndDbmView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void getPoint(boolean isTouching, double _x, double _y) {
		if(isTouching) {
			MapAndDbm.canWriting = true;
			MapAndDbm.isTouching = true;
			MapAndDbm.pointX = _x;
			MapAndDbm.pointY = _y;
		}
		else {
			MapAndDbm.canWriting = false;
			MapAndDbm.isTouching = false;
		}
	 }
}

/**
 * */
class VsualizeDbmMapView extends MyMapView {
	private ArrayList<PointDataSet> pointData;
	public VsualizeDbmMapView(Context context, ArrayList<PointDataSet> data) {
		super(context);
		pointData = data;
		// TODO Auto-generated constructor stub
	}
	
	/** 描写 */
	@Override
	protected void onDraw(Canvas canvas) {
		//canvas.rotate(180,width/2,height/2);
		canvas.drawColor(Color.BLACK);
		makeMap(canvas);
		drawDbmLine(canvas);
	}
	@Override
	protected void drawDbmLine(Canvas canvas) {
		Paint paint = new Paint();
		paint.setStrokeWidth(12);
		paint.setStyle(Paint.Style.STROKE);
		Path[] path = new Path[pointData.size()];
		float x, y;
		float pre_x = 0, pre_y = 0;
		
		float dic = 0;
		for(int i=0; i<pointData.size(); i++) {
			if(pointData.get(i).getDbm() > -350){// && pointData.get(i).getDbm() >= -49) {
			path[i] = new Path();
			paint.setColor(VsualizeDbmMap.getDbmColor(pointData.get(i).getDbm()));
			canvas.drawPoint((float)pointData.get(i).getX(), (float)pointData.get(i).getY(), paint);
		/*	x = (float)pointData.get(i).getX();
			y = (float)pointData.get(i).getY();
			if(pre_x < x) {
				dic = y-50f;
			}
			else {
				dic = y+50f;
			}
			path[i].moveTo(x, y);
			path[i].lineTo(x-10f, dic);
			path[i].lineTo(x+10f, dic);
			path[i].close();
		//	canvas.drawPath(path[i],paint);
			pre_x = x;*/
			}
		}			
	}
}

/** 
 * 
 * 図面用のデータセット 
 * 
 * */
class PointDataSet {
	private double x, y;
	private int dbm;
	
	PointDataSet(double _x, double _y) {
		this.x = _x;
		this.y = _y;
	}
	PointDataSet(double _x, double _y, int _dbm) {
		this.x = _x;
		this.y = _y;
		this.dbm = _dbm;
	}

	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public int getDbm() {
		return dbm;
	}
}