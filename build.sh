#!/bin/bash
javac -classpath '.:classes:*:classes:/opt/pi4j/lib/*' -d . com.raspi.bbq.RaspiBBQ.java && pi4j -r com.raspi.bbq.RaspiBBQ