package com.example.indoor_mapview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.MapViewListener;
import com.onlylemi.mapview.library.layer.LocationLayer;

public class Main2Activity extends AppCompatActivity implements SensorEventListener {
    private MapView mapView;

    private LocationLayer locationLayer;

    private boolean openSensor = false;

    private SensorManager sensorManager;
    private Sensor mAccelerometer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mapView = (MapView) findViewById(R.id.mapview);

        Resources res=getResources();

        Bitmap bmp=BitmapFactory.decodeResource(res, R.drawable.pic);
       /*
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getAssets().open("pic.png"));
        } catch (IOException e) {
            e.printStackTrace();
        } */
        mapView.loadMap(bmp);
        mapView.setMapViewListener(new MapViewListener() {
            @Override
            public void onMapLoadSuccess() {
                locationLayer = new LocationLayer(mapView, new PointF(800, 300));
                locationLayer.setOpenCompass(true);
                //locationLayer.setCompassIndicatorCircleRotateDegree(60);
                //locationLayer.setCompassIndicatorArrowRotateDegree(-30);
                Log.d("msg","start");
                mapView.addLayer(locationLayer);

                mapView.refresh();
            }

            @Override
            public void onMapLoadFail() {

            }

        });

        Log.d("msg","oncreate");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener((SensorEventListener) this, sensorManager.getDefaultSensor
                (Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.d("msg","onResume");
        sensorManager.registerListener(this, sensorManager.getDefaultSensor
                (Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
        openSensor = !openSensor;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("msg","onsensor");
        if (mapView.isMapLoadFinish() && openSensor) {
            float mapDegree = 0; // the rotate between reality map to northern
            float degree = 0;
            /*if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {


            }*/degree = event.values[0];
            Log.d("msg","degree="+degree);
            locationLayer.setCompassIndicatorCircleRotateDegree(-degree);
            locationLayer.setCompassIndicatorArrowRotateDegree(mapDegree + mapView
                    .getCurrentRotateDegrees() + degree);
            mapView.refresh();
            Log.d("msg","degree="+degree);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

}
