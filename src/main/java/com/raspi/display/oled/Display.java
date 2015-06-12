package com.raspi.display.oled;

import com.raspi.Model;
import com.raspi.display.oled.reference.BitmapFont;
import com.raspi.display.oled.reference.ImgInterface;
import com.raspi.display.oled.reference.RasPi;
import com.raspi.display.oled.reference.Verdana20;
import com.raspi.utils.misc;

import java.net.SocketException;

/**
 * Created by Darrell on 6/12/2015.
 */
public class Display {
    ScreenBuffer splash = new ScreenBuffer(128, 64);
    ScreenBuffer progress = new ScreenBuffer(128, 64);
    ScreenBuffer graph = new ScreenBuffer(128, 64);
    BitmapFont bf = new Verdana20();

    DisplayState ds = DisplayState.Splash;
    private Model m;

    public Display(Model m) {
        this.m = m;
        DrawSplash(splash);
        DrawProgress(progress);
    }

    public DisplayState getDisplayState() {
        return ds;
    }

    public void setDisplayState(DisplayState ds) {
        this.ds = ds;
    }

    private void DrawProgress(ScreenBuffer sb) {
        sb.clear(ScreenBuffer.Mode.WHITE_ON_BLACK);
        String txt = " " + Math.round(m.getTempF()) + "F";
        sb.text(bf, "Pit          ", 1, 27);
        int len = bf.strlen(txt);
        sb.text(bf, txt, 127 - len, 27);
        String ip = null;
        drawIP(sb);

    }

    private void drawIP(ScreenBuffer sb) {
        String ip;
        try {
            ip = misc.GetLocalAddress().toString();
        } catch (SocketException e) {
            ip = "Unknown";
        }
        sb.text(ip, 128 - sb.strlen(ip), 64);
    }

    private void DrawSplash(ScreenBuffer sb) {
        sb.clear(ScreenBuffer.Mode.BLACK_ON_WHITE);
        ImgInterface img = new RasPi();
        sb.image(img, 2, 2, ScreenBuffer.Mode.WHITE_ON_BLACK);
        String bbq = "BBQ!";
        sb.text_center(bf, bbq, 80, 40, ScreenBuffer.Mode.BLACK_ON_WHITE);
        drawIP(sb);
    }

    public ScreenBuffer getScreenBuffer() {
        switch (ds) {
            case Progress:
                return progress;
            case Graph:
                return graph;
            default:
                return splash;
        }
    }
}
