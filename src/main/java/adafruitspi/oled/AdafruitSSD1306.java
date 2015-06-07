package adafruitspi.oled;

import com.pi4j.io.gpio.GpioController;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;

/**
 * SSD1306, small OLED screen. SPI. 128x32
 */
public class AdafruitSSD1306
{
  public final static int SSD1306_I2C_ADDRESS         = 0x3C; // 011110+SA0+RW - 0x3C or 0x3D
  public final static int SSD1306_SETCONTRAST         = 0x81;
  public final static int SSD1306_DISPLAYALLON_RESUME = 0xA4;
  public final static int SSD1306_DISPLAYALLON        = 0xA5;
  public final static int SSD1306_NORMALDISPLAY       = 0xA6;
  public final static int SSD1306_INVERTDISPLAY       = 0xA7;
  public final static int SSD1306_DISPLAYOFF          = 0xAE;
  public final static int SSD1306_DISPLAYON           = 0xAF;
  public final static int SSD1306_SETDISPLAYOFFSET    = 0xD3;
  public final static int SSD1306_SETCOMPINS          = 0xDA;
  public final static int SSD1306_SETVCOMDETECT       = 0xDB;
  public final static int SSD1306_SETDISPLAYCLOCKDIV  = 0xD5;
  public final static int SSD1306_SETPRECHARGE        = 0xD9;
  public final static int SSD1306_SETMULTIPLEX        = 0xA8;
  public final static int SSD1306_SETLOWCOLUMN        = 0x00;
  public final static int SSD1306_SETHIGHCOLUMN       = 0x10;
  public final static int SSD1306_SETSTARTLINE        = 0x40;
  public final static int SSD1306_MEMORYMODE          = 0x20;
  public final static int SSD1306_COLUMNADDR          = 0x21;
  public final static int SSD1306_PAGEADDR            = 0x22;
  public final static int SSD1306_COMSCANINC          = 0xC0;
  public final static int SSD1306_COMSCANDEC          = 0xC8;
  public final static int SSD1306_SEGREMAP            = 0xA0;
  public final static int SSD1306_CHARGEPUMP          = 0x8D;
  public final static int SSD1306_EXTERNALVCC         = 0x1;
  public final static int SSD1306_SWITCHCAPVCC        = 0x2;


  // Scrolling constants
  public final static int SSD1306_ACTIVATE_SCROLL                      = 0x2F;
  public final static int SSD1306_DEACTIVATE_SCROLL                    = 0x2E;
  public final static int SSD1306_SET_VERTICAL_SCROLL_AREA             = 0xA3;
  public final static int SSD1306_RIGHT_HORIZONTAL_SCROLL              = 0x26;
  public final static int SSD1306_LEFT_HORIZONTAL_SCROLL               = 0x27;
  public final static int SSD1306_VERTICAL_AND_RIGHT_HORIZONTAL_SCROLL = 0x29;
  public final static int SSD1306_VERTICAL_AND_LEFT_HORIZONTAL_SCROLL  = 0x2A;

  private int width;
  private int height;
  private int address;
  private int vccstate;
  private int pages;

  private int[] buffer = null;

  private static GpioController gpio;

  private I2CBus bus;
  private I2CDevice disp;

  public AdafruitSSD1306(int w, int h) throws IOException {
    initSSD1306(w, h, SSD1306_I2C_ADDRESS);
  }

  private void initSSD1306(int w, int h, int address) throws IOException {
    this.width = w;
    this.height = h;
    this.address = address;
    this.pages = this.height / 8; // Number of lines
    this.buffer = new int[this.width * this.pages];
    clear();

    if ("true".equals(System.getProperty("verbose", "false")))
      System.out.println(this.pages);

    bus = I2CFactory.getInstance(I2CBus.BUS_1);
    disp = bus.getDevice(address);
  }

  public void setBuffer(int[] buffer) throws Exception {
    if (this.buffer.length != buffer.length) throw new Exception("Invalid buffer length!");
    this.buffer = buffer;
  }

  public int[] getBuffer()
  {
    return buffer;
  }

  private void command(int c) throws IOException {
    byte[] data = new byte[]{0x00, (byte) (c & 0xFF)};
    disp.write(address, data, 0, data.length);
  }

  private void command(int c, int d) throws IOException {
    byte[] data = new byte[]{0x00, (byte) (c & 0xFF), (byte) (d & 0xFF)};
    disp.write(address, data, 0, data.length);
  }

  private void command(int c, int d, int e) throws IOException {
    byte[] data = new byte[]{0x00, (byte) (c & 0xFF), (byte) (d & 0xFF), (byte) (e & 0xFF)};
    disp.write(address, data, 0, data.length);
  }

  public void data(int c) throws IOException {
    byte[] data = new byte[]{0x40, (byte) (c & 0xFF)};
    disp.write(address, data, 0, data.length);
  }

  private void reset()
  {
    //resetOutput.high();
    sleep(1);
    // Set reset low for 10 milliseconds.
    //resetOutput.low();
    sleep(10);
    // Set reset high again.
    //resetOutput.high();
  }

  /**
   * Initialize display
   */
  public void begin() throws IOException {
    begin(SSD1306_SWITCHCAPVCC);
  }

  public void begin(int vcc) throws IOException {
    // Save vcc state.
    this.vccstate = vcc;
    // Reset and initialize display.
    this.reset();
    this.initialize();
    // Turn on the display.
    this.command(SSD1306_DISPLAYON);
  }

  private void initialize() throws IOException // SPI, 128x32
  {
    // 128x32 pixel specific initialization.
    this.command(SSD1306_DISPLAYOFF);          // 0xAE

    this.command(SSD1306_SETDISPLAYCLOCKDIV, 0x80);
    this.command(SSD1306_SETMULTIPLEX, 0x3F);
    this.command(SSD1306_SETDISPLAYOFFSET, 0x0);                         // no offset
    this.command(SSD1306_SETSTARTLINE | 0x0);  // line //0
    this.command(SSD1306_CHARGEPUMP, this.vccstate == SSD1306_EXTERNALVCC ? 0x10 : 0x14);
    this.command(SSD1306_MEMORYMODE, 0x00);                        // 0x0 act like ks0108
    this.command(SSD1306_SEGREMAP | 0x1);
    this.command(SSD1306_COMSCANDEC);
    this.command(SSD1306_SETCOMPINS, 0x12);
    this.command(SSD1306_SETCONTRAST, this.vccstate == SSD1306_EXTERNALVCC ? 0x9F : 0xCF);
    this.command(SSD1306_SETPRECHARGE, this.vccstate == SSD1306_EXTERNALVCC ? 0x22 : 0xF1);
    this.command(SSD1306_SETVCOMDETECT, 0x40);
    this.command(SSD1306_DISPLAYALLON_RESUME); // 0xA4
    this.command(SSD1306_NORMALDISPLAY);       // 0xA6
    this.command(SSD1306_COLUMNADDR, 0, 127);
    this.command(SSD1306_PAGEADDR, 0, 7);
  }

  public void clear()
  {
    for (int i = 0; this.buffer != null && i < this.buffer.length; i++)
      this.buffer[i] = 0;
  }

  public void setContrast(int contrast)
          throws IllegalArgumentException, IOException {
    if (contrast < 0 || contrast > 255)
    {
      throw new IllegalArgumentException("Contrast must be a value in [0, 255]");
    }
    this.command(SSD1306_SETCONTRAST);
    this.command(contrast);
  }

  /**
   * Write display buffer to physical display.
   */
  public void display() throws IOException {
    this.command(SSD1306_SETLOWCOLUMN);
    this.command(SSD1306_SETHIGHCOLUMN);
    this.command(SSD1306_SETSTARTLINE);
    for (int i = 0; i < buffer.length; ) {
      byte[] buf = new byte[17];
      buf[0] = 0x40;
      for (int x = 0; x < 16; x++)
        buf[x + 1] = (byte) (this.buffer[i++] & 0xFF);
      disp.write(address, buf, 0, buf.length);
      System.out.println("[RX] " + bytesToHex(buf));
    }
  }

  public static String bytesToHex(byte[] bytes) {
    final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    char[] hexChars = new char[bytes.length * 2];
    int v;
    for ( int j = 0; j < bytes.length; j++ ) {
      v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }

  /**
   * Adjusts contrast to dim the display if dim is True, otherwise sets the
   * contrast to normal brightness if dim is False.
   */
  public void dim(boolean dim) // ???? WTF ?????
  {
    // Assume dim display.
    int contrast = 0;
    // Adjust contrast based on VCC if not dimming.
    if (!dim)
    {
      if (this.vccstate == SSD1306_EXTERNALVCC)
        contrast = 0x9F;
      else
        contrast = 0xCF;
    }
  }

  private static void sleep(long ms)
  {
    try
    {
      Thread.sleep(ms);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}