package org.ros.android.android_tutorial_pubsub;

/**
 * Created by kaushik on 7/10/15.
 */

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import android.util.Log;

import geometry_msgs.Quaternion;
import geometry_msgs.Vector3;
import sensor_msgs.Imu;

public class Talker2 extends AbstractNodeMain {

    float hz = 100.f;
    long previousLoop = System.currentTimeMillis();

    private String topic_name;
    private DataListener dataListener = new DataListener() {
        @Override
        public String getData() {
            return null;
        }
    };

    public Talker2() {
        this.topic_name = "imu_data";
    }

    /*public talker2(String topic) {
        this.topic_name = topic;
    }*/

    public GraphName getDefaultNodeName() {
        return GraphName.of("rosjava_tutorial_pubsub/talker2");
    }

    public void onStart(ConnectedNode connectedNode) {
        final Publisher<Imu> publisher = connectedNode.newPublisher(this.topic_name, "sensor_msgs/Imu");
        //final Publisher<Vector3> publisher1 = connectedNode.newPublisher("any", "geometry_msgs/Vector3");
        //final Publisher<Vector3> publisher2 = connectedNode.newPublisher("any1", "geometry_msgs/Vector3");
        //final Publisher<Quaternion> publisher3 = connectedNode.newPublisher("any2", "geometry_msgs/Quaternion");
        connectedNode.executeCancellableLoop(new CancellableLoop() {


            protected void loop() throws InterruptedException {
                //std_msgs.String str = (std_msgs.String) publisher.newMessage();
                //String[] xyz;
                String xyz = (String) dataListener.getData();
                //Log.d("The String Value", xyz);
                char[] XYZ = xyz.toCharArray();
                char[] a = new char[xyz.length()];
                char[] b = new char[xyz.length()];
                char[] c = new char[xyz.length()];

                char[] d = new char[xyz.length()];
                char[] e = new char[xyz.length()];
                char[] f = new char[xyz.length()];

                char[] t = new char[xyz.length()];
                char[] u = new char[xyz.length()];
                char[] v = new char[xyz.length()];

                int i;
                for (i = 0; i < xyz.length(); i++) {
                    if (XYZ[i] == ' ') break;
                    a[i] = XYZ[i];
                }
                i++;
                for (int j = 0; i < xyz.length(); i++, j++) {
                    if (XYZ[i] == ' ') break;
                    b[j] = XYZ[i];
                }
                i++;
                for (int k = 0; i < xyz.length(); i++, k++) {
                    if (XYZ[i] == ' ') break;
                    c[k] = XYZ[i];
                }
                i++;
                for (int l = 0; i < xyz.length(); i++, l++) {
                    if (XYZ[i] == ' ') break;
                    d[l] = XYZ[i];
                }
                i++;
                for (int m = 0; i < xyz.length(); i++, m++) {
                    if (XYZ[i] == ' ') break;
                    e[m] = XYZ[i];
                }
                i++;
                for (int n = 0; i < xyz.length(); i++, n++) {
                    if (XYZ[i] == ' ') break;
                    f[n] = XYZ[i];
                }
                i++;
                for (int o = 0; i < xyz.length(); i++, o++) {
                    if (XYZ[i] == ' ') break;
                    t[o] = XYZ[i];
                }
                i++;
                for (int p = 0; i < xyz.length(); i++, p++) {
                    if (XYZ[i] == ' ') break;
                    u[p] = XYZ[i];
                }
                i++;
                for (int q = 0; i < xyz.length(); i++, q++) {
                    if (XYZ[i] == ' ') break;
                    v[q] = XYZ[i];
                }


                sensor_msgs.Imu imu = publisher.newMessage();


                //sensor_msgs.Imu imu = (Imu) publisher.newMessage();
                //geometry_msgs.Vector3 vector3 = publisher1.newMessage();
                //geometry_msgs.Vector3 vector31 = publisher2.newMessage();
                //geometry_msgs.Quaternion quaternion = publisher3.newMessage();


                imu.getLinearAcceleration().setX(Double.parseDouble(String.valueOf(a)));
                /*String hi = String.valueOf(c);
                Log.d("The vale of c",hi);*/
                imu.getLinearAcceleration().setY(Double.parseDouble(String.valueOf(b)));
                imu.getLinearAcceleration().setZ(Double.parseDouble(String.valueOf(c)));
                //imu.setLinearAcceleration(vector3);
                String hi = String.valueOf(d);
                //Log.d("The vale of c", hi);
                double g=0,h=0,r=0;
                try {
                    g+= 0.01*Double.parseDouble(String.valueOf(d));
                    h+= 0.01*Double.parseDouble(String.valueOf(e));
                    r+= 0.01*Double.parseDouble(String.valueOf(f));

                    imu.getOrientation().setX(Double.parseDouble(String.valueOf(t)));
                    imu.getOrientation().setY(Double.parseDouble(String.valueOf(u)));
                    imu.getOrientation().setZ(Double.parseDouble(String.valueOf(v)));



                    imu.getAngularVelocity().setX(g);
                    imu.getAngularVelocity().setY(h);
                    imu.getAngularVelocity().setZ(r);

                    //imu.setAngularVelocity(vector31);
                    //imu.setOrientation(quaternion);
                }
                catch (Exception e1){}
                //str.setData("\n Accelerometer " + xyz + "\n");
                waitUntilNextLoop();
                publisher.publish(imu);


                //++this.sequenceNumber;
            }
        });
    }

    private void waitUntilNextLoop() throws InterruptedException {
        long elapsed = System.currentTimeMillis() - previousLoop;
        long remainingTime = (long) (1000 / hz - elapsed);
        if (remainingTime > 0)
            Thread.sleep(remainingTime);
        previousLoop = System.currentTimeMillis();
    }

    public void setDataListener(DataListener dataListener) {
        this.dataListener = dataListener;
    }
}

