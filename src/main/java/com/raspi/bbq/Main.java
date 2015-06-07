package com.raspi.bbq;

import com.raspi.display.oled.SSD1306I2C;
import com.raspi.display.oled.ScreenBuffer;
import com.raspi.display.oled.reference.ImgInterface;
import com.raspi.display.oled.reference.RasPi;
import com.raspi.sensor.MAX6675;

public class Main {

    @SuppressWarnings("oracle.jdeveloper.java.insufficient-catch-block")
    public static void main(String[] args) {
        if ("true".equals(System.getProperty("verbose", "false")))
            System.out.println("Starting...");
        try {
            SSD1306I2C oled = new SSD1306I2C(128, 64); // Default pins (look in the SSD1306I2C code)

            oled.begin();
            oled.clear();

            ScreenBuffer sb = new ScreenBuffer(128, 64);
            sb.clear(ScreenBuffer.Mode.BLACK_ON_WHITE);

            System.out.println("Loadng...");
            ImgInterface img = new RasPi();
            String bbq = "BBQ!";
            int blen = sb.strlen(bbq);
            sb.image(img, 2, 2, ScreenBuffer.Mode.WHITE_ON_BLACK);
            sb.text(bbq, 64 - blen / 2, 30, ScreenBuffer.Mode.BLACK_ON_WHITE);
            oled.setBuffer(sb.getScreenBuffer());
            oled.display();

            MAX6675 temp = new MAX6675();

            while (true) {
                temp.read();

                String txt = "Pit " + temp.getTempF() + "F";
                int len = sb.strlen(txt);
                sb.text(txt, 128 - len, 10);
                oled.setBuffer(sb.getScreenBuffer());
                oled.display();
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                }
            }
//
            //System.out.println("Done.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}