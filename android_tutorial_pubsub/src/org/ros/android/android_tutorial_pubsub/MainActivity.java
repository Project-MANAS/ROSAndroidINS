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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.ros.android.MessageCallable;
import org.ros.android.RosActivity;
import org.ros.android.view.RosTextView;
import org.ros.message.MessageFactory;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.util.Vector;

import geometry_msgs.Quaternion;
import geometry_msgs.Vector3;
import sensor_msgs.Imu;


/**
 * @author damonkohler@google.com (Damon Kohler)
 */
public class MainActivity extends RosActivity {

    private SensorManager mSensorManager, mSensorManager1;
    private Sensor mAccelerometer, mGyroscope;
    private final float NOISE = (float) 0.5;
    private Quaternion q1;
    private Imu imu;
    private RosTextView<std_msgs.String> rosTextView;
    private Talker talker;
    private Talker1 talker1;
    private Talker2 talker2;
    private Location cachedLocation = null;
    float deltaX, deltaY, deltaZ;
    //String[] xyz = new String[4];
    //private SensorEvent event1;
    String xyz;
    String XYZ;

    public MainActivity() {
        // The RosActivity constructor configures the notification title and ticker
        // messages.
        super("Pubsub Tutorial", "Pubsub Tutorial");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        LocationManager locationManager;
        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(svcName);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        String provider = LocationManager.GPS_PROVIDER;
        int t = 500;
        int distance = 1;


        final TextView tvX = (TextView) findViewById(R.id.x_axis);
        final TextView tvY = (TextView) findViewById(R.id.y_axis);
        final TextView tvZ = (TextView) findViewById(R.id.z_axis);

        final TextView tvx = (TextView) findViewById(R.id.X_axis);
        final TextView tvy = (TextView) findViewById(R.id.Y_axis);
        final TextView tvz = (TextView) findViewById(R.id.Z_axis);

        //mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                double a = sensorEvent.values[0];
                a = Math.round(a*100.0)/100.0;
                double b = sensorEvent.values[1];
                b = Math.round(b*100.0)/100.0;
                double c = sensorEvent.values[2];
                c = Math.round(c*100.0)/100.0;
                XYZ = Double.toString(a)+" "+Double.toString(b)+" "+Double.toString(c);
                tvX.setText(Double.toString(a));
                tvY.setText(Double.toString(b));
                tvZ.setText(Double.toString(c));
                /*vector3.setX(a);
                vector3.setY(b);
                vector3.setZ(c);*/
                //imu.setLinearAcceleration(vector3);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        }, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager1 = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGyroscope = mSensorManager1.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager1.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                double x = sensorEvent.values[0];
                x = Math.round(x*100.0)/100.0;
                double y = sensorEvent.values[1];
                y = Math.round(y*100.0)/100.0;
                double z = sensorEvent.values[2];
                z = Math.round(z*100.0)/100.0;
                xyz = Double.toString(x) + " " + Double.toString(y) + " " + Double.toString(z);

                tvx.setText(Double.toString(x));
                tvy.setText(Double.toString(y));
                tvz.setText(Double.toString(z));

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        }, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);


        LocationListener myLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                updateWithNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("LocationStatus", "Provider: " + provider + ", Status: " + status + ", Extras: " + extras);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("LocationProvider", "Provider enabled: " + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("LocationProvider", "Provider disabled: " + provider);
            }
        };
        Location l = locationManager.getLastKnownLocation(provider);
        updateWithNewLocation(l);
        locationManager.requestLocationUpdates(provider, t, distance, myLocationListener);

        rosTextView = (RosTextView<std_msgs.String>) findViewById(R.id.text);
        rosTextView.setTopicName("chatter");
        rosTextView.setMessageType(std_msgs.String._TYPE);
        rosTextView.setMessageToStringCallable(new MessageCallable<String, std_msgs.String>() {
            @Override
            public String call(std_msgs.String message) {
                return message.getData();
            }
        });
    }

    private void updateWithNewLocation(Location location) {
        cachedLocation = location;
    }


    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        talker = new Talker();
        talker1 = new Talker1();
        talker2 = new Talker2();
        talker.setDataListener(new DataListener<Location>() {
            @Override
            public Location getData() {
                return cachedLocation;
            }
            /*@Override
            public Object getX()
            {
                return deltaX;
            }*/
        });
        talker1.setDataListener(new DataListener<String>() {
            @Override
            public String getData() {
                return xyz;
            }
            /*@Override
            public Object getX()
            {
                return deltaX;
            }*/
        });
        talker2.setDataListener(new DataListener<String>() {
            @Override
            public String getData() {
                return (XYZ+" "+xyz);
            }
            /*@Override
            public Object getX()
            {
                return deltaX;
            }*/
        });


        // At this point, the user has already been prompted to either enter the URI
        // of a master to use or to start a master locally.

        // The user can easily use the selected ROS Hostname in the master chooser
        // activity.
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(talker, nodeConfiguration);
        nodeMainExecutor.execute(talker1, nodeConfiguration);
        nodeMainExecutor.execute(talker2, nodeConfiguration);
        // The RosTextView is also a NodeMain that must be executed in order to
        // start displaying incoming messages.
        nodeMainExecutor.execute(rosTextView, nodeConfiguration);
    }


}
