package com.raspi.utils;

/**
 * Created by Darrell on 6/12/2015.
 */
public class SimpleKalman {

    double varInput = 1e-9f;
    double varProcess = 1e-9f;
    double Pc = 0.0f;
    double G = 0.0f;
    double P = 1.0f;
    double Xp = 0.0f;
    double Xe = 0.0f;

    public SimpleKalman(float varInput) {
        this.varInput = varInput;
    }

    public double filter(double input) {
        Pc = P + varProcess;
        G = Pc / (Pc + varInput);
        P = (1 - G) * Pc;
        Xp = Xe;
        Xe = G * (input - Xp) + Xp;
        return Xe;
    }
}