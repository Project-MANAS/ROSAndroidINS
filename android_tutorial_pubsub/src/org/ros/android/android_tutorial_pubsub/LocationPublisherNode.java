package org.ros.android.android_tutorial_pubsub;

/**
 * Contributors Reuben John and Kaushik Nath on 7/10/15.
 */

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import sensor_msgs.NavSatFix;
import std_msgs.Header;

public class LocationPublisherNode extends AbstractNodeMain {

    static float maxFrequency = 100.f;
    static float minElapse = 1000 / maxFrequency;

    static float minFrequency = 20.f;
    static float maxElapse = 1000 / minFrequency;

    long previousPublishTime = System.currentTimeMillis();
    private boolean isMessagePending = false;

    private String topic_name;

    private final LocationListener locationListener;
    private Location cachedLocation;
    private String navSatFixFrameId;
    private OnFrameIdChangeListener locationFrameIdChangeListener;

    public LocationPublisherNode() {
        this.topic_name = "fix";
        isMessagePending = false;
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if (location != null) {
                    cachedLocation = location;
                    isMessagePending = true;
                }
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
        locationFrameIdChangeListener = new OnFrameIdChangeListener() {
            @Override
            public void onFrameIdChanged(String newFrameId) {
                navSatFixFrameId = newFrameId;
            }
        };
    }

    /*public Talker(String topic) {
        this.topic_name = topic;
    }*/

    public GraphName getDefaultNodeName() {
        return GraphName.of("rosjava_tutorial_pubsub/talker");
    }

    public void onStart(final ConnectedNode connectedNode) {

        final Publisher<NavSatFix> locationPublisher = connectedNode.newPublisher(this.topic_name, "sensor_msgs/NavSatFix");
        final NavSatFix navSatFix = locationPublisher.newMessage();
        connectedNode.executeCancellableLoop(new CancellableLoop() {

            public int sequenceNumber = 1;
            Header header = connectedNode.getTopicMessageFactory().newFromType(Header._TYPE);

            protected void loop() throws InterruptedException {
                if ((cachedLocation != null) &&
                        (isMessagePending
                                || (System.currentTimeMillis() - previousPublishTime) >= maxElapse // Or, is max elapse reached?
                        )) {
                    header.setStamp(connectedNode.getCurrentTime());
                    header.setFrameId(navSatFixFrameId);
                    header.setSeq(sequenceNumber);
                    navSatFix.setHeader(header);

                    navSatFix.setLatitude(cachedLocation.getLatitude());
                    navSatFix.setLongitude(cachedLocation.getLongitude());
                    locationPublisher.publish(navSatFix);

                    //Wait until minimum time has elapsed
                    long elapsed = System.currentTimeMillis() - previousPublishTime;
                    long remainingTime = (long) (minElapse - elapsed);
                    if (remainingTime > 0)
                        Thread.sleep(remainingTime);
                    previousPublishTime = System.currentTimeMillis();

                    isMessagePending = false;
                    ++this.sequenceNumber;
                } else {
                    Thread.sleep(1);
                }
            }
        });
    }

    public LocationListener getLocationListener() {
        return locationListener;
    }

    public OnFrameIdChangeListener getFrameIdListener() {
        return locationFrameIdChangeListener;
    }
}

