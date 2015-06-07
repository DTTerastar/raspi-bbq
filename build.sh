#!/bin/bash
#javac -classpath '.:classes:*:classes:/opt/pi4j/lib/*' -d . com.raspi.bbq.RaspiBBQ.java && pi4j -r com.raspi.bbq.RaspiBBQ
mvn package && java -Dverbose=true -jar target/raspi-bbq-1-jar-with-dependencies.jar
