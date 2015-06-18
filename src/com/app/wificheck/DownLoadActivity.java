package com.app.wificheck;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DownLoadActivity extends WifiScan {
	private final String DOWNLOAD_FILE_NAME = "download_test.zip";
	private final String DOWNLOAD_FILE_URL = "http://192.168.10.2/"
			+ DOWNLOAD_FILE_NAME;

	private ProgressDialog progressDialog;
	private ProgressHandler progressHandler;
	private AsyncFileDownload asyncfiledownload;

	private static ImageView img;
	private static TextView text;
	private static Button btn;

	private static long startTime;
	private static long stopTime;
	
	private static WifiInfo w_info;
	private static int sum_dBm = 0;
	private  int sum_cnt = 0;
	
	private Timer timer;
	private TimerTask t_task;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download);

		progressHandler = new ProgressHandler();
	

		img = (ImageView) findViewById(R.id.imageView1);
		text = (TextView) findViewById(R.id.textView1);
		btn = (Button) findViewById(R.id.button1);
		
		Con = getApplicationContext();
		wifiScan = this;
		ProgressHandler.dl = this;
		

		timer = new Timer();
		t_task = new MyTimerTask(this, wifiScan);
		
		btn.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		sum_cnt = 0;
	    		sum_dBm = 0;
	    		timer = new Timer();
	    		t_task = new MyTimerTask(Con, wifiScan);
	    	    timer.scheduleAtFixedRate(t_task, 0, 100);	
	    		startTime = System.currentTimeMillis();
	    		download_start();
	    	}
	    });
		
		manager = (WifiManager) getSystemService(WIFI_SERVICE);

	}

	public void download_start() {
		initFileLoader();
		showDialog(0);
		progressHandler.progressDialog = progressDialog;
		progressHandler.asyncfiledownload = asyncfiledownload;

		if (progressDialog != null && asyncfiledownload != null) {
			progressDialog.setProgress(0);
			progressHandler.sendEmptyMessage(0);
		} else {
			Toast ts = Toast.makeText(this, "NULLエラー", Toast.LENGTH_LONG);
			ts.show();
		}
	}

	private void initFileLoader() {
		/*
		 * File sdCard = Environment.getExternalStorageDirectory(); File
		 * directory = new File(sdCard.getAbsolutePath() + "/SampleFolder");
		 * if(directory.exists() == false){ if (directory.mkdir() == true){
		 * }else{ Toast ts = Toast.makeText(this, "ディレクトリ作成に失敗",
		 * Toast.LENGTH_LONG); ts.show(); } } File outputFile = new
		 * File(directory, "test.jpg"); asyncfiledownload = new
		 * AsyncFileDownload(this,DOWNLOAD_FILE_URL, outputFile);
		 * asyncfiledownload.execute();
		 */

		// 内部メモリの領域を用いる場合
		File dataDir = this.getFilesDir();
		File directory = new File(dataDir.getAbsolutePath() + "/SampleFolder");
		if (directory.exists() == false) {
			if (directory.mkdir() == true) {
			} else {
				Toast ts = Toast.makeText(this, "ディレクトリ作成に失敗",
						Toast.LENGTH_LONG);
				ts.show();
			}
		}
		File outputFile = new File(directory, "test.jpg");
		asyncfiledownload = new AsyncFileDownload(this, DOWNLOAD_FILE_URL,
				outputFile);
		asyncfiledownload.execute();
	}

	public void viewText() {
		stopTime = System.currentTimeMillis();
		long time = stopTime - startTime;
		int second = (int) (time/1000);
		int comma = (int) (time % 1000);
		
		text.setText((second + "秒" + comma).toString() + "\ndBm : "+(sum_dBm/sum_cnt));
		taskStop();
		/*	
		Bitmap bitmap = BitmapFactory
				.decodeFile("/data/data/com.app.wificheck/files/SampleFolder/test.jpg");
		if (bitmap != null) {
			img.setImageBitmap(bitmap);
		}*/
	}
	
	private void taskStop() {
		if(timer != null) {
			timer.cancel();
			timer = null;
		}
		if(t_task != null) {
			t_task.cancel();
			t_task = null;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		cancelLoad();
	}

	@Override
	protected void onStop() {
		super.onStop();
		cancelLoad();
	}

	private void cancelLoad() {
		if (asyncfiledownload != null) {
			asyncfiledownload.cancel(true);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			progressDialog = new ProgressDialog(this);
			progressDialog.setIcon(R.drawable.ic_launcher);
			progressDialog.setTitle("Downloading files..");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Hide",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});

			progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							cancelLoad();
						}
					});
		}
		return progressDialog;
	}
	
	/** 初期化  */
	@Override
	protected void init() {

	}
	
	/** Wifiをスキャン */
	@Override
	protected void wifiCheck(Context con) {
		w_info = null;
		w_info = manager.getConnectionInfo();
		sum_dBm += w_info.getRssi();
		sum_cnt++;
		Log.d("TAG"," w "+w_info.getRssi()+" rss "+sum_dBm);
	}	
}

class ProgressHandler extends Handler {
	public ProgressDialog progressDialog;
	public AsyncFileDownload asyncfiledownload;

	public static DownLoadActivity dl = null;

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		if (asyncfiledownload.isCancelled()) {
			progressDialog.dismiss();
		} else if (asyncfiledownload.getStatus() == AsyncTask.Status.FINISHED) {
			Log.d("TAG", "finis");
			progressDialog.dismiss();
			dl.viewText();
		} else {
			progressDialog
					.setProgress(asyncfiledownload.getLoadedBytePercent());
			this.sendEmptyMessageDelayed(0, 100);
		}
	}
}
