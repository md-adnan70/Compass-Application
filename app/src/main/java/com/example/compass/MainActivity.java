package com.example.compass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
private TextView degreeText;
private ImageView imageCompass;
private SensorManager sensorManager;
private Sensor accelerometerSensor, magnetometerSensor;

    private float lastAccelerometer[] = new float[3];
    private float lastMagentometer[] = new float[3];
    private float rotationMatrix[] = new float[9];
    private float orientation[] = new float[3];

    boolean isLastAccelometerArrayCopies = false;
    boolean isLastMagnetometerArrayCopies = false;

    long lastTime =0;
    float currentDegree =0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        degreeText = findViewById(R.id.degreeText);
        imageCompass = findViewById(R.id.imageCompass);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
       accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
       magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor == accelerometerSensor){
            System.arraycopy(sensorEvent.values,0,lastAccelerometer,0,sensorEvent.values.length);
            isLastAccelometerArrayCopies = true;
        }else if(sensorEvent.sensor == magnetometerSensor){
            System.arraycopy(sensorEvent.values,0,lastMagentometer,0,sensorEvent.values.length
            );
            isLastMagnetometerArrayCopies = true;
        }

        if(isLastAccelometerArrayCopies && isLastMagnetometerArrayCopies && System.currentTimeMillis() - lastTime > 250){
            SensorManager.getRotationMatrix(rotationMatrix,null,lastAccelerometer,lastMagentometer);
            SensorManager.getOrientation(rotationMatrix,orientation);

            float azimuthInRadians = orientation[0];
            float azimuthInDegree = (float) Math.toDegrees(azimuthInRadians);

            RotateAnimation rotateAnimation = new RotateAnimation(currentDegree,-azimuthInDegree, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            rotateAnimation.setDuration(250);
            rotateAnimation.setFillAfter(true);
            imageCompass.startAnimation(rotateAnimation);

            currentDegree = -azimuthInDegree;
            lastTime = System.currentTimeMillis();

            int x = (int) azimuthInDegree;
            degreeText.setText(x + "°");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,magnetometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this,accelerometerSensor);
        sensorManager.unregisterListener(this,magnetometerSensor);
    }
}