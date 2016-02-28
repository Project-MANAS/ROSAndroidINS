package org.ros.android.android_tutorial_pubsub;

/**
 * Created by kaushik on 7/10/15.
 */

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class Talker1 extends AbstractNodeMain {

    float hz = 100.f;
    long previousLoop = System.currentTimeMillis();

    private String topic_name;
    private DataListener dataListener = new DataListener() {
        @Override
        public String getData() {
            return null;
        }
    };

    public Talker1() {
        this.topic_name = "chatter1";
    }

    /*public Talker1(String topic) {
        this.topic_name = topic;
    }*/

    public GraphName getDefaultNodeName() {
        return GraphName.of("rosjava_tutorial_pubsub/talker1");
    }

    public void onStart(ConnectedNode connectedNode) {
        final Publisher publisher = connectedNode.newPublisher(this.topic_name, "std_msgs/String");
        connectedNode.executeCancellableLoop(new CancellableLoop() {
            private int sequenceNumber;

            protected void setup() {
                this.sequenceNumber = 0;
            }

            protected void loop() throws InterruptedException {
                std_msgs.String str = (std_msgs.String) publisher.newMessage();
                //String[] xyz;
                String xyz = (String) dataListener.getData();
                str.setData("\n Gyroscope " + xyz + "\n");
                publisher.publish(str);
                waitUntilNextLoop();
                //Thread.sleep(10L);
                ++this.sequenceNumber;
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

