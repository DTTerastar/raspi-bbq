import com.pi4j.wiringpi.Spi;

public class RaspiBBQ {

    public static byte READ_CMD  = 0x41;
    
    @SuppressWarnings("unused")
    public static void main(String args[]) throws InterruptedException {

        System.out.println("Pi BBQ!");
                
        int fd = Spi.wiringPiSPISetup(0, 4000000);
        if (fd <= -1) {
            System.out.println("wiringPiSPISetup FAILED");
            return;
        }
        
		read();
    }
    
    public static void read(){
        byte packet[] = new byte[2];
        packet[0] = 0b00000000;
        packet[2] = 0b00000000;
		
        System.out.println("-----------------------------------------------");
        System.out.println("[TX] " + bytesToHex(packet));
        Spi.wiringPiSPIDataRW(0, packet, 2);        
        System.out.println("[RX] " + bytesToHex(packet));
        System.out.println("-----------------------------------------------");
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