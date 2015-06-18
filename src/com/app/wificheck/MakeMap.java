package com.app.wificheck;

import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class MakeMap extends EndlessScan {	
	protected LinearLayout myView;   //全体のレイアウト
	protected LinearLayout btnView;  //ボタン用のレイアウト
	protected Button undoBtn;        //ボタン
	protected Button redoBtn;        //ボタン
	protected MyMapView mapView;     //図面表示領域
	
	@Override
	protected void init() {
		myView  = new LinearLayout(this);
		btnView = new LinearLayout(this);
		undoBtn = new Button(this);
		redoBtn = new Button(this);

		undoBtn.setText("UNDO");
		redoBtn.setText("REDO");
		btnView.setOrientation(LinearLayout.VERTICAL);
		btnView.addView(undoBtn);
		btnView.addView(redoBtn);
		myView.addView(btnView);
		
		undoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	mapView.undo();
            }
        });
		redoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	mapView.redo();
            }
        });
		getWindowSize();
		init2();
	}
	
	protected void init2() {
		setView();
	}
	
	/** Viewを設定(MapAndDbmで拡張するために分けておく) */
	protected void setView() {
		mapView = new MyMapView(getApplication());
		myView.addView(mapView);
		setContentView(myView);	
	}
	
	protected void getWindowSize() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		MyMapView.width = displaymetrics.widthPixels;
		MyMapView.height = displaymetrics.heightPixels;
	}
}

