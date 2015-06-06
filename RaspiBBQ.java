import com.pi4j.wiringpi.Spi;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class RaspiBBQ {

    private static Pin spiCs   = RaspiPin.GPIO_10;
	private static GpioPinDigitalOutput chipSelectOutput;
	
    @SuppressWarnings("unused")
    public static void main(String args[]) throws InterruptedException {

        System.out.println("Pi BBQ!");
        
		GpioController gpio = GpioFactory.getInstance();
		chipSelectOutput = gpio.provisionDigitalOutputPin(spiCs,   "CS",   PinState.LOW);        
        int fd = Spi.wiringPiSPISetup(0, 4000000);
        if (fd <= -1) {
            System.out.println("wiringPiSPISetup FAILED");
            return;
        }
        while(true) {
			Thread.sleep(1000);
			read();
		}
    }
    
    public static void read(){
		chipSelectOutput.low();
		
        byte packet[] = new byte[2];
        packet[0] = 0b00000000;
        packet[1] = 0b00000000;
		
        //System.out.println("-----------------------------------------------");
        //System.out.println("[TX] " + bytesToHex(packet));
        Spi.wiringPiSPIDataRW(0, packet, 2);        
        System.out.println("[RX] " + bytesToHex(packet));
		long raw=((long)packet[0] << 8) + packet[1]; //raw thermocouple reading 1/4 deg C x4? x8?
		boolean tcopenerr=(raw & 0x0004) != 0; //check tc open err bit d2
		long qdeg=raw >> 3;
        System.out.println(qdeg/4.0);
		chipSelectOutput.high();
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
}