/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros.android.android_tutorial_pubsub;

/**
 * Contributors Reuben John and Kaushik Nath on 7/10/15.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;


/**
 * @author damonkohler@google.com (Damon Kohler)
 */
public class MainActivity extends RosActivity implements View.OnClickListener {

    private EditText locationFrameIdView, imuFrameIdView;
    Button applyB;
    private OnFrameIdChangeListener locationFrameIdListener, imuFrameIdListener;

    public MainActivity() {
        // The RosActivity constructor configures the notification title and ticker
        // messages.
        super("Pubsub Tutorial", "Pubsub Tutorial");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        locationFrameIdListener = new OnFrameIdChangeListener() {
            @Override
            public void onFrameIdChanged(String newFrameId) {
                Log.w("MainActivity", "Default location OnFrameIdChangedListener called");
            }
        };
        imuFrameIdListener = new OnFrameIdChangeListener() {
            @Override
            public void onFrameIdChanged(String newFrameId) {
                Log.w("MainActivity", "Default IMU OnFrameIdChangedListener called");
            }
        };
        locationFrameIdView = (EditText) findViewById(R.id.et_location_frame_id);
        imuFrameIdView = (EditText) findViewById(R.id.et_imu_frame_id);

        SharedPreferences sp = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        locationFrameIdView.setText(sp.getString("locationFrameId", getString(R.string.default_location_frame_id)));
        imuFrameIdView.setText(sp.getString("imuFrameId", getString(R.string.default_imu_frame_id)));

        applyB = (Button) findViewById(R.id.b_apply);
        applyB.setOnClickListener(this);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        Log.d("MainActivity", "init()");

        final LocationPublisherNode locationPublisherNode = new LocationPublisherNode();
        ImuPublisherNode imuPublisherNode = new ImuPublisherNode();

        MainActivity.this.locationFrameIdListener = locationPublisherNode.getFrameIdListener();
        MainActivity.this.imuFrameIdListener = imuPublisherNode.getFrameIdListener();

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        final String provider = LocationManager.GPS_PROVIDER;
        String svcName = Context.LOCATION_SERVICE;
        final LocationManager locationManager = (LocationManager) getSystemService(svcName);
        final int t = 500;
        final float distance = 0.1f;
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                locationManager.requestLocationUpdates(provider, t, distance, locationPublisherNode.getLocationListener());
            }
        });

        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(imuPublisherNode.getAccelerometerListener(), mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        SensorManager mSensorManager1 = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mGyroscope = mSensorManager1.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager1.registerListener(imuPublisherNode.getGyroscopeListener(), mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);

        SensorManager mSensorManager2 = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mOrientation = mSensorManager2.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager2.registerListener(imuPublisherNode.getOrientationListener(), mOrientation, SensorManager.SENSOR_DELAY_FASTEST);


        // At this point, the user has already been prompted to either enter the URI
        // of a master to use or to start a master locally.

        // The user can easily use the selected ROS Hostname in the master chooser
        // activity.
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(locationPublisherNode, nodeConfiguration);
        //nodeMainExecutor.execute(talker1, nodeConfiguration);
        nodeMainExecutor.execute(imuPublisherNode, nodeConfiguration);
        // The RosTextView is also a NodeMain that must be executed in order to
        // start displaying incoming messages.
        //nodeMainExecutor.execute(rosTextView, nodeConfiguration);

        onClick(null);
    }


    @Override
    public void onClick(View view) {
        Log.i("MainActivity", "Default IMU OnFrameIdChangedListener called");

        SharedPreferences sp = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        String newLocationFrameId = locationFrameIdView.getText().toString();
        if (!newLocationFrameId.isEmpty()) {
            locationFrameIdListener.onFrameIdChanged(newLocationFrameId);
            spe.putString("locationFrameId", newLocationFrameId);
        }
        String newImuFrameId = imuFrameIdView.getText().toString();
        if (!newLocationFrameId.isEmpty()) {
            imuFrameIdListener.onFrameIdChanged(newImuFrameId);
            spe.putString("imuFrameId", newImuFrameId);
        }
        spe.apply();
    }
}
