package adafruitspi.oled;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

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

  //private final static int SPI_PORT   =  0;

  private int width  = 128;
  private int height =  32;
  private int address;
  private int clockHertz = 8000000; // 8 MHz
  private int vccstate = 0;
  private int pages = 0;
  private int[] buffer = null;

  private static GpioController gpio;

  private I2CBus bus;
  private I2CDevice disp;

  /**
   * @param w Buffer width (pixles).  Default is 128
   * @param h Buffer height (pixels). Default is  32
   */
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

    bus = I2CFactory.getInstance(I2CBus.BUS_1);
    disp = bus.getDevice(address);
    gpio = GpioFactory.getInstance();
  }

  public void shutdown()
  {
    gpio.shutdown();
  }

  public void setBuffer(int[] buffer)
  {
    this.buffer = buffer;
  }

  public int[] getBuffer()
  {
    return buffer;
  }

  private final int MASK = 0x80; // MSBFIRST, 0x80 = 0&10000000
//private final int MASK = 0x01; // LSBFIRST

  private void write(byte[] data) throws IOException {
    disp.write(address, data, 0, data.length);
  }

  private void command(int c) throws IOException {
    this.write(new byte[] { 0b0,(byte)c });
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

  public void data(int c) throws IOException {
    this.write(new byte[] { 0x50, (byte)c });
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
    this.command(SSD1306_SETDISPLAYCLOCKDIV);  // 0xD5
    this.command(0x80);                        // the suggested ratio 0x80
    this.command(SSD1306_SETMULTIPLEX);        // 0xA8
    this.command(0x3F);
    this.command(SSD1306_SETDISPLAYOFFSET);    // 0xD3
    this.command(0x0);                         // no offset
    this.command(SSD1306_SETSTARTLINE | 0x0);  // line //0
    this.command(SSD1306_CHARGEPUMP);          // 0x8D
    if (this.vccstate == SSD1306_EXTERNALVCC)
      this.command(0x10);
    else
      this.command(0x14);
    this.command(SSD1306_MEMORYMODE);          // 0x20
    this.command(0x00);                        // 0x0 act like ks0108
    this.command(SSD1306_SEGREMAP | 0x1);
    this.command(SSD1306_COMSCANDEC);
    this.command(SSD1306_SETCOMPINS);          // 0xDA
    this.command(0x12);
    this.command(SSD1306_SETCONTRAST);         // 0x81
    if (this.vccstate == SSD1306_EXTERNALVCC)
      this.command(0x9F);
    else
      this.command(0xCF);
    this.command(SSD1306_SETPRECHARGE);        // 0xd9
    if (this.vccstate == SSD1306_EXTERNALVCC)
      this.command(0x22);
    else
      this.command(0xF1);
    this.command(SSD1306_SETVCOMDETECT);       // 0xDB
    this.command(0x40);
    this.command(SSD1306_DISPLAYALLON_RESUME); // 0xA4
    this.command(SSD1306_NORMALDISPLAY);       // 0xA6
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
    this.command(SSD1306_COLUMNADDR);
    this.command(0); // Column start address. (0 = reset)
    this.command(this.width - 1); // Column end address.
    this.command(SSD1306_PAGEADDR);
    this.command(0); // Page start address. (0 = reset)
    this.command(this.pages - 1); // Page end address.
    // Write buffer data.

    for (int i = 0; i < (width * height / 8); i++) {
      byte[] buf = new byte[17];
      buf[0] = 0x40;
      for (int x = 0; x < 16; x++) {
        buf[x + 1] = (byte) (this.buffer[i] & 0xFF);
        i++;
      }
      i--;
      write(buf);
    }
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