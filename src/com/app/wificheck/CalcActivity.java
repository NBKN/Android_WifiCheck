package com.app.wificheck;

public class CalcActivity extends MakeMap {

	/** Viewを設定(MapAndDbmで拡張するために分けておく) */
	protected void setView() {
		mapView = new CalcMapView(getApplication());
		myView.addView(mapView);
		setContentView(myView);	
	}
}
