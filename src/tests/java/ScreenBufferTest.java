import com.raspi.display.oled.ScreenBuffer;
import com.raspi.display.oled.reference.Verdana20;
import com.raspi.utils.misc;
import org.junit.After;
import org.junit.Test;

/**
 * Created by Darrell on 6/7/2015.
 */
public class ScreenBufferTest {


    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testText() throws Exception {
        Verdana20 f = new Verdana20();
        ScreenBuffer sb = new ScreenBuffer(128, 64);
        sb.text(f, "BLAH BLAH", 64 - f.strlen("BLAH BLAH") / 2, 27);
        sb.dumpScreen();
    }

    @Test
    public void testText1() throws Exception {

        String ip = misc.GetLocalAddress().toString();
        ScreenBuffer sb = new ScreenBuffer(128, 64);
        sb.text(ip, 128 - sb.strlen(ip), 64);
        sb.dumpScreen();
    }

}