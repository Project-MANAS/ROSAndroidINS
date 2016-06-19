About
---
This application supportss publishing of IMU and GPS messages along with associated headers using the Accelerometer, Gyroscope and GPS respectively.

Features
---
 - Asynchronous publishing of readings from the sensors. (e.g. it uses interpolation of cached values from accelerometer when receiving the gyroscope reading when packaging it together for publishing an IMU message).
 - GUI interface to select frame_ids and covariance to be published along with sensor data at runtime.
 - Uses ROS conventions of direction and rotation.

Project MANAS
---
This application was developed at [Project MANAS](http://projectmanas.in/) to allow the use of Android sensors as cheaper alternatives to dedicated hardware for initial prototyping. Project MANAS is the official AI robotics team of the Manipal University. Project MANAS is currectly dedicated to the development of autonomous systems specifically adapted to Indian traffic and road conditions. This project is built on top of the rosjava platform for Android.

===

The project uses rosjava: The first pure Java implementation of ROS.

From [ROS.org](http://www.ros.org/wiki/): ROS is an open-source, meta-operating system for your robot. It provides the services you would expect from an operating system, including hardware abstraction, low-level device control, implementation of commonly-used functionality, message-passing between processes, and package management.

rosjava was developed at Google in cooperation with Willow Garage, rosjava enables integration of Android and ROS compatible robots. This project is under active development and currently alpha quality software. Please report bugs and feature requests on the [issues list](https://github.com/rosjava/rosjava/issues?state=open).

To get started, visit the [rosjava_core](http://rosjava.github.com/rosjava_core/) and [android_core](http://rosjava.github.com/android_core/) documentation.

Still have questions? Check out the ros-users [discussion list](https://code.ros.org/mailman/listinfo/ros-users), post questions to [ROS Answers](http://answers.ros.org/questions/) with the tag "rosjava," or join #ROS on irc.oftc.net.

rosjava was announced publicly during the [Cloud Robotics tech talk at Google I/O 2011](http://www.youtube.com/watch?feature=player_embedded&v=FxXBUp-4800).

Looking for a robot platform to experiment with ROS, Android, and cloud robotics? The [Willow Garage](http://www.willowgarage.com/) [TurtleBot](http://www.willowgarage.com/turtlebot) is a great mobile perception platform for [getting started with robotics development](http://www.youtube.com/watch?feature=player_embedded&v=MOEjL8JDvd0).

Visit the rosjava_core wiki for instructions.

http://ros.org/wiki/android_core
