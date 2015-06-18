package com.app.wificheck;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.os.Build;

public class LongScan extends WifiScan{
	protected static String checkName = "";   //測定するSSID
	boolean hasScaned  = false;               //測定できたか
	protected boolean isChecking = true;      //測定中か
	protected int spend_time = 0;             //経過時間カウンタ
	protected List<ScanResult> tmpResult;     //一時的なスキャン結果  
	
	/* まとめて記録する用 */
	protected FileOutputStream fileOutputStream;
	protected OutputStreamWriter osw;
	protected BufferedWriter writer;
	
	/** 初期化
	 *  開始ボタンの設置
	 *  */
	@Override
	protected void init() {	
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	    // アラートダイアログのメッセージを設定します
	    alertDialogBuilder.setMessage("測定開始");
	    // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
	    alertDialogBuilder.setPositiveButton("OK",
	            new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int which) {
	                	init2();
	                }
	            });
	    // アラートダイアログのキャンセルが可能かどうかを設定します
	    alertDialogBuilder.setCancelable(false);
	    AlertDialog alertDialog = alertDialogBuilder.create();
	    // アラートダイアログを表示します
	    alertDialog.show();
	}
	
	protected void init2() {
		
	}
	
	/** プログレスバーの設定 */
	@Override
	protected void setProgressDialog() {
		mProgressDialog = new ProgressDialog(wifiScan);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "測定終了", 
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
                // TODO 自動生成されたメソッド・スタブ
				isChecking = false;
            }
        });
		mProgressDialog.show();
	}
	
	/** プログレスバー表示 */
	@Override
	protected void showProgressBar() {
		if(isChecking) {
			String mes = "測定中\n ";
			mes += Integer.toString(spend_time) + "秒経過\n" + Integer.toString(scan_cnt) + "回測定"; 	
			mProgressDialog.setMessage(mes);
		}
		else if(!isChecking) {
			if(mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			timerTaskStop();
		}
	}
	
	/** 終了時のメッセージ */
	protected void endDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // アラートダイアログのメッセージを設定します
        alertDialogBuilder.setMessage("測定完了");
        // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	//cancelService();
                    	Free();
                    	finish();
                    }
                });
        // アラートダイアログのキャンセルが可能かどうかを設定します
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        // アラートダイアログを表示します
        alertDialog.show();
	}
	
	/** 解放 */
	protected void Free() {
	    checkName = null; //"TORI-LAB";  //測定するSSID
	    manager = null;
		timer_cnt = 0;
		spend_time = 0;
		scan_cnt = 0;
	}
}

