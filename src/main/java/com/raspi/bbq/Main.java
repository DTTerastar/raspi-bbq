package com.raspi.bbq;

import com.raspi.Model;
import com.raspi.display.oled.Display;
import com.raspi.display.oled.DisplayState;
import com.raspi.display.oled.SSD1306I2C;
import com.raspi.sensor.MAX6675;
import com.raspi.utils.PIDController;
import com.raspi.utils.SimpleKalman;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.Calendar;

public class Main {

    @SuppressWarnings("oracle.jdeveloper.java.insufficient-catch-block")
    public static void main(String[] args) {
        if ("true".equals(System.getProperty("verbose", "false")))
            System.out.println("Starting...");
        try {
            SSD1306I2C oled = new SSD1306I2C(128, 64); // Default pins (look in the SSD1306I2C code)

            oled.begin();
            oled.clear();

            Calendar c = Calendar.getInstance();
            DateTime d = new DateTime();
            Model m = new Model();

            Display display = new Display(m);
            MAX6675 temp = new MAX6675();
            PIDController pid = new PIDController(1,1,1);
            pid.setSetpoint(m.getPitSetpoint());
            pid.setOutputRange(0,1);
            pid.setInputRange(0,1000);
            pid.enable();
            SimpleKalman filter = new SimpleKalman(1, 1e-3);
            while (true) {
                int running = Seconds.secondsBetween(d, DateTime.now()).getSeconds() / 5;
                if (running > 0)
                    if (running % 2 == 1)
                        m.setDisplayState(DisplayState.Params);
                    else
                        m.setDisplayState(DisplayState.Graph);
                temp.read();
                double tempF = filter.filter(temp.getTempF());
                pid.getInput(tempF);
                m.setFan(pid.performPID());
                System.out.println(temp.getTempF() + "F filt: " + tempF + "F");
                m.setPitTemp((int) Math.round(tempF));
                oled.setBuffer(display.getScreenBuffer().getBitmap());
                oled.display();
            }
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