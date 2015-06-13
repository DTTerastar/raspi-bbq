package com.raspi;

import com.raspi.display.oled.DisplayState;
import com.raspi.utils.PIDController;

/**
 * Created by Darrell on 6/12/2015.
 */
public class Model {

    DisplayState ds = DisplayState.Splash;
    int pitTemp = 100;
    int pitDesired = 100;
    double fan;
    public PIDController pid;

    public Model(PIDController pid) {

        this.pid = pid;
    }

    public int getPitSetpoint() {
        return pitDesired;
    }

    public void setPitSetpoint(int pitDesired) {
        this.pitDesired = pitDesired;
        pid.setSetpoint(pitDesired);
    }

    public double getFan() {
        return pid.performPID()/100.0;
    }

    public int getPitTemp() {
        return pitTemp;
    }

    public void setPitTemp(int pitTemp) {
        this.pitTemp = pitTemp;
        pid.getInput(pitTemp);
    }

    public DisplayState getDisplayState() {
        return ds;
    }

    public void setDisplayState(DisplayState ds) {
        this.ds = ds;
    }
}