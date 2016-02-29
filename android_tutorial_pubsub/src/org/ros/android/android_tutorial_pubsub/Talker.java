package org.ros.android.android_tutorial_pubsub;

/**
 * Created by kaushik on 7/10/15.
 */

import android.location.Location;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import sensor_msgs.NavSatFix;

public class Talker extends AbstractNodeMain {

    static float hz = 100.f;
    long previousLoop = System.currentTimeMillis();

    private String topic_name;
    private DataListener dataListener=new DataListener() {
        @Override
        public Location getData() {
            return null;
        }
    };

    /*private DataListener1 dataListener1=new DataListener1() {
        @Override
        public SensorEvent getXYZ() {
            return null;
        }
    };*/

    public Talker() {
        this.topic_name = "chatter";
    }

    /*public Talker(String topic) {
        this.topic_name = topic;
    }*/

    public GraphName getDefaultNodeName() {
        return GraphName.of("rosjava_tutorial_pubsub/talker");
    }

    public void onStart(ConnectedNode connectedNode) {
        final Publisher publisher = connectedNode.newPublisher(this.topic_name, "std_msgs/String");

        final Publisher<NavSatFix> publisher1 = connectedNode.newPublisher("NavSatFix", "sensor_msgs/NavSatFix");
        final NavSatFix NavSatFix = publisher1.newMessage();
        connectedNode.executeCancellableLoop(new CancellableLoop() {
            private int sequenceNumber;

            protected void setup() {
                this.sequenceNumber = 0;
            }

            protected void loop() throws InterruptedException {
                std_msgs.String str = (std_msgs.String) publisher.newMessage();
                String latLongString = null;
                Location location = (Location) dataListener.getData();


                if (location != null) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    latLongString = "Lat:" + lat + "\nLong:" + lng;

                    NavSatFix.setLatitude(location.getLatitude());
                    NavSatFix.setLongitude(location.getLongitude());
                }
                str.setData("Location: " + latLongString);
                waitUntilNextLoop();
                publisher.publish(str);
                publisher1.publish(NavSatFix);
                //++this.sequenceNumber;
            }
        });
    }

    private void waitUntilNextLoop() throws InterruptedException {
        long elapsed = System.currentTimeMillis() - previousLoop;
        long remainingTime = (long) (1000 / hz - elapsed);
        if (remainingTime > 0)
            Thread.sleep(remainingTime);
        previousLoop=System.currentTimeMillis();
    }

    public void setDataListener(DataListener dataListener) {
        this.dataListener = dataListener;
    }
}

