import com.raspi.display.oled.ScreenBuffer;
import com.raspi.display.oled.reference.BitmapFont;
import com.raspi.display.oled.reference.Verdana20;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Darrell on 6/7/2015.
 */
public class ScreenBufferTest {

    ScreenBuffer sb = new ScreenBuffer(128,64
    );
    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testText() throws Exception {
        Verdana20 f = new Verdana20();
        sb.text(f, "BLAH BLAH", 64 - f.strlen("BLAH BLAH")/2, 27);
        sb.dumpScreen();
    }

    @Test
    public void testText1() throws Exception {

    }
}