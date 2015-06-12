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

    private Model m;

    public Display(Model m) {
        this.m = m;
        DrawSplash(splash);
    }

    private void DrawProgress(ScreenBuffer sb) {
        sb.clear(ScreenBuffer.Mode.WHITE_ON_BLACK);
        sb.text_left(bf, "Pit", 1, 25);
        sb.text_right(bf, m.getPitTemp() + "F", 127, 25, ScreenBuffer.Mode.WHITE_ON_BLACK);
        sb.text_left(bf, "Set", 1, 50);
        sb.text_right(bf, m.getPitDesired() + "F", 127, 50, ScreenBuffer.Mode.WHITE_ON_BLACK);
        sb.text_left("Fan " + Math.round(m.getFan() * 100) + "%", 1, 64, ScreenBuffer.Mode.WHITE_ON_BLACK);
        drawIP(sb, ScreenBuffer.Mode.WHITE_ON_BLACK);
    }

    private void drawIP(ScreenBuffer sb, ScreenBuffer.Mode mode) {
        String ip;
        try {
            ip = misc.GetLocalAddress().toString().substring(1);
        } catch (SocketException e) {
            ip = "Unknown";
        }
        sb.text_right(ip, 128, 64, mode);
    }

    private void DrawSplash(ScreenBuffer sb) {
        sb.clear(ScreenBuffer.Mode.BLACK_ON_WHITE);
        ImgInterface img = new RasPi();
        sb.image(img, 2, 2, ScreenBuffer.Mode.WHITE_ON_BLACK);
        sb.text_center(bf, "BBQ!", 80, 40, ScreenBuffer.Mode.BLACK_ON_WHITE);

        sb.text_left("Fan " + Math.round(m.getFan() * 100) + "%", 1, 64, ScreenBuffer.Mode.WHITE_ON_BLACK);
        drawIP(sb, ScreenBuffer.Mode.BLACK_ON_WHITE);
    }

    public ScreenBuffer getScreenBuffer() {
        switch (m.getDisplayState()) {
            case Params:
                return progress;
            case Graph:
                DrawGraph(graph);
                return graph;
            default:
                DrawSplash(splash);
                return splash;
        }
    }

    private void DrawGraph(ScreenBuffer sb) {
        sb.text_left("Fan " + Math.round(m.getFan() * 100) + "%", 1, 64, ScreenBuffer.Mode.WHITE_ON_BLACK);
        drawIP(sb, ScreenBuffer.Mode.WHITE_ON_BLACK);
    }
}
