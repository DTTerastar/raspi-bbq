#!/bin/bash
#javac -classpath '.:classes:*:classes:/opt/pi4j/lib/*' -d . com.raspi.sensor.MAX6675.java && pi4j -r com.raspi.sensor.MAX6675
mvn package && java -Dverbose=true -jar target/raspi-bbq-1-jar-with-dependencies.jar
