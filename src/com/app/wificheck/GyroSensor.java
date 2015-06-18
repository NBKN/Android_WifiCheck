package com.app.wificheck;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class GyroSensor extends Activity implements SensorEventListener {
	private SensorManager manager;
	private TextView values, values2;
	private float[] max_val = new float [3];
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gyro);
		values = (TextView)findViewById(R.id.text1);
		values2 = (TextView)findViewById(R.id.text2);
		manager = (SensorManager)getSystemService(SENSOR_SERVICE);
		for(int i=0; i<max_val.length; i++) {
			max_val[i] = 0;
		}
	}

	 
    /** 行列数 */
    private static final int MATRIX_SIZE = 16;
    /** 三次元(XYZ) */
    private static final int DIMENSION = 3;
 
    /** センサー管理クラス */
    private SensorManager mManager;
 
    /** 地磁気行列 */
    private float[] mMagneticValues;
    /** 加速度行列 */
    private float[] mAccelerometerValues;
 
    /** X軸の回転角度 */
    private int mPitchX;
    /** Y軸の回転角度 */
    private int mRollY;
    /** Z軸の回転角度(方位角) */
    private int mAzimuthZ;
 
    /**
     * センサーイベント取得開始
     *
     * @param context
     *            コンテキスト
     */
    public synchronized void resume(Context context) {
        if (context == null) {
            // 引数不正
            return;
        }
 
        // 登録済なら一旦止める
        pause();
 
        if (mManager == null) {
            // 初回実行時
            mManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
 
        // 地磁気センサー登録
        mManager.registerListener(this, mManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_UI);
        // 加速度センサー登録
        mManager.registerListener(this, mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }
 
    /**
     * センサーイベント取得終了
     */
    public synchronized void pause() {
        if (mManager != null) {
            mManager.unregisterListener(this);
        }
    }
    
    /**
     * X軸の回転角度を取得する
     *
     * @return X軸の回転角度
     */
    public synchronized int getPitch() {
        return mPitchX;
    }
 
    /**
     * Y軸の回転角度を取得する
     *
     * @return Y軸の回転角度
     */
    public synchronized int getRoll() {
        return mRollY;
    }
 
    /**
     * Z軸の回転角度(方位角)を取得する
     *
     * @return Z軸の回転角度
     */
    public synchronized int getAzimuth() {
        return mAzimuthZ;
    }
 
    /**
     * ラジアンを角度に変換する
     *
     * @param angrad
     *            ラジアン
     * @return 角度
     */
    private int radianToDegrees(float angrad) {
        return (int) Math
                .floor(angrad >= 0 ? Math.toDegrees(angrad) : 360 + Math.toDegrees(angrad));
    }
 
   
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// Listenerの登録解除	
		manager.unregisterListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// Listenerの登録
		List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if(sensors.size() > 0) {
			Sensor s = sensors.get(0);
			manager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
		}
		 
        if (mManager == null) {
            // 初回実行時
            mManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        }
 
        // 地磁気センサー登録
        mManager.registerListener(this, mManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_UI);
        // 加速度センサー登録
        mManager.registerListener(this, mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
	}
	@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
	      // センサーイベント
        switch (event.sensor.getType()) {
        case Sensor.TYPE_MAGNETIC_FIELD:
            // 地磁気センサー
            mMagneticValues = event.values.clone();
            break;
        case Sensor.TYPE_ACCELEROMETER:
            // 加速度センサー
            mAccelerometerValues = event.values.clone();
            break;
        default:
            // それ以外は無視
            return;
        }
		// TODO Auto-generated method stub
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			String str = "センサー値:"
					+ "\nX軸中心:" + event.values[0]
							+ "\nY軸中心:" + event.values[1]
									+ "\nZ軸中心:" + event.values[2];
			values.setText(str);
			for(int i=0; i<max_val.length; i++) {
				if(max_val[i] < event.values[i]) {
					max_val[i] = event.values[i];
				}
			}
			String str2 = "MAX値:"
					+ "\nX軸中心:" + max_val[0]
							+ "\nY軸中心:" + max_val[1]
									+ "\nZ軸中心:" + max_val[2];
		//	values2.setText(str2);
		}
        if (mMagneticValues != null && mAccelerometerValues != null) {
            float[] rotationMatrix = new float[MATRIX_SIZE];
            float[] inclinationMatrix = new float[MATRIX_SIZE];
            float[] remapedMatrix = new float[MATRIX_SIZE];
 
            float[] orientationValues = new float[DIMENSION];
 
            // 加速度センサーと地磁気センサーから回転行列を取得
            SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix,
                    mAccelerometerValues, mMagneticValues);
            SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X,
                    SensorManager.AXIS_Z, remapedMatrix);
            SensorManager.getOrientation(remapedMatrix, orientationValues);
 
            // ラジアン値を変換し、それぞれの回転角度を取得する
            mAzimuthZ = radianToDegrees(orientationValues[0]);
            mPitchX = radianToDegrees(orientationValues[1]);
            mRollY = radianToDegrees(orientationValues[2]);
 
			values2.setText("X=" + mPitchX + "Y=" + mRollY + "Z=" + mAzimuthZ);
        }
	}
}