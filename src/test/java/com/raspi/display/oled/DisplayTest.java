package com.raspi.display.oled;

import com.raspi.Model;
import com.raspi.utils.PIDController;
import org.junit.Test;

/**
 * Created by Darrell on 6/12/2015.
 */
public class DisplayTest {

    @Test
    public void splash() throws Exception {
        Display d = new Display(new Model(new PIDController(1,1,1)));
        d.getScreenBuffer().dumpScreen();
    }

    @Test
    public void progress() throws Exception {
        Model m = new Model(new PIDController(1,1,1));
        Display d = new Display(m);
        m.setDisplayState(DisplayState.Params);
        d.getScreenBuffer().dumpScreen();
    }

    @Test
    public void graph() throws Exception {
        Model m = new Model(new PIDController(1,1,1));
        Display d = new Display(m);
        m.setDisplayState(DisplayState.PID);
        d.getScreenBuffer().dumpScreen();
    }
}