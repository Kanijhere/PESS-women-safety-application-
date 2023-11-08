package com.example.securepal2;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7f;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    private OnShakeListener listener;
    private long lastShakeTime;
    private int shakeCount;

    public ShakeDetector(Context context, OnShakeListener listener) {
        this.listener = listener;
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

            if (acceleration > SHAKE_THRESHOLD_GRAVITY) {
                long currentTime = System.currentTimeMillis();

                if (lastShakeTime == 0) {
                    lastShakeTime = currentTime;
                } else {
                    long timeDiff = currentTime - lastShakeTime;
                    if (timeDiff > SHAKE_SLOP_TIME_MS) {
                        shakeCount = 0;
                    }
                    if (timeDiff > SHAKE_COUNT_RESET_TIME_MS) {
                        shakeCount = 0;
                    }

                    lastShakeTime = currentTime;
                    shakeCount++;

                    if (shakeCount >= 2) {
                        listener.onShake();
                        shakeCount = 0;
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing here
    }

    public interface OnShakeListener {
        void onShake();
    }
}
