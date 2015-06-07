package com.raspi.bbq;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;

/**
 * Created by Darrell on 6/7/2015.
 */
public class DisplayOff {
    public static void main(String args[]) throws InterruptedException, IOException {

        System.out.println("Display Off");
        I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
        I2CDevice dev = bus.getDevice(0x3c);
        dev.write(0x00, (byte) 0xafb);
    }
}
