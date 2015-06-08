package com.raspi.bbq;

import com.raspi.display.oled.SSD1306I2C;
import com.raspi.display.oled.ScreenBuffer;
import com.raspi.display.oled.reference.BitmapFont;
import com.raspi.display.oled.reference.ImgInterface;
import com.raspi.display.oled.reference.RasPi;
import com.raspi.display.oled.reference.Verdana20;
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

            BitmapFont bf = new Verdana20();
            System.out.println("Loading...");
            ImgInterface img = new RasPi();
            String bbq = "BBQ!";
            int blen = bf.strlen(bbq);
            sb.image(img, 2, 2, ScreenBuffer.Mode.WHITE_ON_BLACK);
            sb.text(bf, bbq, 64 - blen / 2, 60, ScreenBuffer.Mode.BLACK_ON_WHITE);
            oled.setBuffer(sb.getScreenBuffer());
            oled.display();
            sleep(10000);

            sb.clear(ScreenBuffer.Mode.WHITE_ON_BLACK);
            MAX6675 temp = new MAX6675();
            while (true) {
                temp.read();

                sb.text(bf, "Pit          ", 1, 27);
                String txt = Math.round(temp.getTempF()) + "F";
                int len = bf.strlen(txt);
                sb.text(bf, txt, 128 - len, 27);
                oled.setBuffer(sb.getScreenBuffer());
                oled.display();
                sleep(1000);
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