/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.app.wificheck;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;

/**
 * Average temperature demo chart.
 */
public class MakeGraph extends AbstractDemoChart {
    //各グラフ項目の名前。ここで項目数を決める
	public static String[] titles = new String[1];
	public static List<double[]> items;
	public static List<double[]> x_line;

  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "Average temperature";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "The average temperature in 4 Greek islands (line chart)";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
	  //各項目のグラフの色
	  int[] colors = new int[] {  Color.GREEN  };

	  //各項目のグラフポイントのスタイル
	  PointStyle[] styles = new PointStyle[] {PointStyle.CIRCLE};
	  Log.d("TAG", "eeee");

	  XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
	  Log.d("TAG", "ppp");
	  int length = renderer.getSeriesRendererCount();
	  for (int i = 0; i < length; i++) {
		  ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
		  Log.d("TAG", "tt "+i);

	  }

	  //グラフタイトル、X軸、Y軸のタイトルおよびX軸最小値、最大値、Y軸最小値、最大値の設定
	  setChartSettings(renderer, "電波レベルの推移", "測定回数", "電波レベル", 0, GraphScan.scan_cnt , -100, 0, Color.LTGRAY, Color.LTGRAY);
	  renderer.setXLabels(12);
	  renderer.setYLabels(10);
	  renderer.setShowGrid(true);
	  renderer.setXLabelsAlign(Align.RIGHT);
	  renderer.setYLabelsAlign(Align.RIGHT);
	  renderer.setZoomButtonsVisible(true);
	  //表示されるX軸とY軸の最小最大。
	  //ここでXYともに最小を0にするとグラフのマイナス表示がない
	  renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
	  renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });
	  //return用Intentの作成。ここをGraphicalViewにし、returnもGraphicalViewにすると、
	  //各レイアウトへの入れ込みが可能となる。
	  //その場合使用するメソッドはChartFactory.getLineChartViewとなる。
	  //例）GraphicalView line_chart = ChartFactory.getLineChartView(context, buildDataset(titles, x, values), renderer);
	  //（返値もGraphicalViewへ変更すること)
	  Intent intent = ChartFactory.getCubicLineChartIntent(context, buildDataset(titles, x_line, items),
			  renderer, 0.33f, "電波レベルの推移");
	  return intent;
  }
}
