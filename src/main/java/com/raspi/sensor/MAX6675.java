package com.raspi.sensor;

import com.pi4j.wiringpi.Spi;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import java.io.IOException;

public class MAX6675 implements java.lang.AutoCloseable {

    private Pin spiCs = RaspiPin.GPIO_10;
    private GpioPinDigitalOutput chipSelectOutput;
    private boolean _isThermocoupleConnected;
    private double _tempF, _tempC;

    @SuppressWarnings("unused")
    public MAX6675() throws InterruptedException, IOException {
        System.out.println("Pi BBQ!");

        GpioController gpio = GpioFactory.getInstance();
        chipSelectOutput = gpio.provisionDigitalOutputPin(spiCs, "CS", PinState.LOW);
        int fd = Spi.wiringPiSPISetup(0, 4000000);
        if (fd <= -1) {
            throw new IOException("wiringPiSPISetup FAILED");
        }
    }

    public double getTempC() {
        return _tempC;
    }

    public double getTempF() {
        return _tempF;
    }

    public boolean isThermocoupleConnected() {
        return _isThermocoupleConnected;
    }

    public void read() {
        chipSelectOutput.low();
        byte[] packet = new byte[]{0x0b, 0x0b};
        Spi.wiringPiSPIDataRW(0, packet, 2);
        long raw = ((long) packet[0] << 8) | (packet[1] & 0xFF); //raw thermocouple reading 1/4 deg C x4? x8?
        _isThermocoupleConnected = (raw & 0x0004) == 0; //check tc open err bit d2
        _tempC = (raw >> 3) / 4.0;
        _tempF = ((_tempC) * 9 / 5.0) + 32;
        System.out.println(_tempC + "C " + _tempF + "F");
        chipSelectOutput.high();
    }

    @Override
    public void close() throws Exception {
    }
}