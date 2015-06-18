package com.app.wificheck;

import java.util.EventListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

public class MyDialogFragment extends DialogFragment{
    private DialogListener listener = null;
    
    /**
    * ファクトリーメソッド
    * @param type ダイアログタイプ  0:OKボタンのみ, 1:プログレスのみ, 2：プログレスとボタン
    */
    public static MyDialogFragment newInstance(String title, String message, int type) {
        MyDialogFragment instance = new MyDialogFragment();
        // ダイアログに渡すパラメータはBundleにまとめる
        Bundle arguments = new Bundle();
        arguments.putString("title", title);
        arguments.putString("message", message);
        arguments.putInt("type", type);
        instance.setArguments(arguments);
        return instance;
    }
    
    public static MyDialogFragment newInstance(String title, String message, int type, Fragment fragment) {
        MyDialogFragment instance = new MyDialogFragment();
        // ダイアログに渡すパラメータはBundleにまとめる
        Bundle arguments = new Bundle();
        arguments.putString("title", title);
        arguments.putString("message", message);
        arguments.putInt("type", type);
        instance.setTargetFragment(fragment, 0);
        instance.setArguments(arguments);
        return instance;
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Fragment targetFragment = this.getTargetFragment();
        try {
            listener = (targetFragment != null) ? (DialogListener) targetFragment : (DialogListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Don't implement OnCustomDialogListener.");
        }
    }
    /**
     * AlertDialog作成
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
    	String title = getArguments().getString("title");
    	String message = getArguments().getString("message");
    	int type = getArguments().getInt("type");
           
    	AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
    	final ProgressDialog p_alert = new ProgressDialog(getActivity());
    	
    	if(type == 0) {
        	alert
        	.setTitle(title)
        	.setMessage(message)
        	.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        		@Override
        		public void onClick(DialogInterface dialog, int which) {
        			// OKボタンが押された時
        			listener.doPositiveClick();
        			dismiss();
        		}
        	});
        	return alert.create();
    	}
    	else {
    		p_alert.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		p_alert.setTitle(title);
    		p_alert.setMessage(message);
    		if(type == 2) {
    			p_alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // ProgressDialog をキャンセル
                    	p_alert.cancel();
                    }
                });
    		}
        	return p_alert;
    	}
    }
       
       /**
       * リスナーを追加
       */
       public void setDialogListener(DialogListener listener){
           this.listener = listener;
       }
       
       /**
       * リスナー削除
       */
       public void removeDialogListener(){
           this.listener = null;
       }
   }


interface DialogListener extends EventListener{

    /**
     * OKボタンが押されたイベントを通知
     */
    public void doPositiveClick();

    /**
     * Cancelボタンが押されたイベントを通知
     */
    public void doNegativeClick();
}