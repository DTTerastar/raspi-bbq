#!/bin/bash
javac -classpath '.:classes:*:classes:/opt/pi4j/lib/*' -d . RaspiBBQ.java && pi4j -r RaspiBBQ