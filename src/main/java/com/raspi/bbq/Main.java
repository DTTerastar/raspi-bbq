package com.raspi.bbq;

import com.raspi.Model;
import com.raspi.display.oled.Display;
import com.raspi.display.oled.DisplayState;
import com.raspi.display.oled.SSD1306I2C;
import com.raspi.sensor.MAX6675;
import com.raspi.utils.SimpleKalman;

public class Main {

    @SuppressWarnings("oracle.jdeveloper.java.insufficient-catch-block")
    public static void main(String[] args) {
        if ("true".equals(System.getProperty("verbose", "false")))
            System.out.println("Starting...");
        try {
            SSD1306I2C oled = new SSD1306I2C(128, 64); // Default pins (look in the SSD1306I2C code)

            oled.begin();
            oled.clear();

            Model m = new Model();
            m.setDisplayState(DisplayState.Params);
            Display display = new Display(m);
            MAX6675 temp = new MAX6675();
            SimpleKalman filter = new SimpleKalman(1.2, 1e-4);
            while (true) {
                temp.read();
                double tempF = filter.filter(temp.getTempF());
                m.setPitTemp((int)Math.round(tempF));
                oled.setBuffer(display.getScreenBuffer().getBitmap());
                oled.display();
            }
//
            //System.out.println("Done.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ex) {
        }
    }
}