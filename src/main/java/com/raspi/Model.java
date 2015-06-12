package com.raspi;

import com.raspi.display.oled.DisplayState;

/**
 * Created by Darrell on 6/12/2015.
 */
public class Model {

    DisplayState ds = DisplayState.Splash;
    int pitTemp = 100;
    int pitDesired = 225;
    double fan;

    public int getPitSetpoint() {
        return pitDesired;
    }

    public void setPitDesired(int pitDesired) {
        this.pitDesired = pitDesired;
    }

    public double getFan() {
        return fan;
    }

    public void setFan(double fan) {
        this.fan = fan;
    }

    public int getPitTemp() {
        return pitTemp;
    }

    public void setPitTemp(int pitTemp) {
        this.pitTemp = pitTemp;
    }

    public DisplayState getDisplayState() {
        return ds;
    }

    public void setDisplayState(DisplayState ds) {
        this.ds = ds;
    }
}