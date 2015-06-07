package adafruitspi.oled;

import adafruitspi.oled.img.ImgInterface;
import adafruitspi.oled.utils.CharacterMatrixes;

import java.awt.Point;
import java.awt.Polygon;

public class ScreenBuffer
{
    private final static int WIDTH = 128, HEIGHT = 32; // Default values

    public enum Mode
    {
        WHITE_ON_BLACK,
        BLACK_ON_WHITE
    };

    private int w = 128,
            h =  32;
    // This is the buffer that will be pushed on the device
    private int[] screenBuffer    = null;
    // This represents the led array (128x32). 'X' means on, ' ' means off.
    // The dumpScreen method displays this one.
    private char[][] screenMatrix = null;

    public ScreenBuffer()
    {
        this(WIDTH, HEIGHT);
    }

    public ScreenBuffer(int w, int h)
    {
        super();
        this.w = w;
        this.h = h;
        this.screenBuffer = new int[w * (h / 8)];
        this.screenMatrix = new char[h][w]; // h lines, w columns
    }

    public void clear()
    {
        clear(Mode.WHITE_ON_BLACK);
    }
    public void clear(Mode mode)
    {
        for (int i=0; i<this.h; i++)
        {
            for (int j=0; j<this.w; j++)
                screenMatrix[i][j] = (mode == Mode.WHITE_ON_BLACK ? ' ' : 'X');
        }
        for (int i=0; i<this.screenBuffer.length; i++)
            this.screenBuffer[i] = (mode == Mode.WHITE_ON_BLACK ? 0 : 1);
    }

    /**
     * Generate and return the screenbuffer from the screenmatrix
     * @return the buffer to display on the OLED
     */
    public int[] getScreenBuffer()
    {
        for (int line=0; line<(this.h / 8); line++)
        {
            for (int col=0; col<this.w; col++)
            {
                int bmVal = 0;
                for (int b=0; b<8; b++)
                {
                    if (screenMatrix[(line * 8) + b][col] == 'X')
                        bmVal |= (1 << b);
                }
                //    System.out.println(lpad(Integer.toHexString(bmVal), "0", 2) + ", " + lpad(Integer.toBinaryString(bmVal), "0", 8));
                this.screenBuffer[(this.w * (line)) + col] = bmVal;
            }
        }
        return this.screenBuffer;
    }

    /**
     * Draw a text on the screenMatrix
     *
     * @param txt Character String to display
     * @param xPx Bottom left X origin in Pixels (top left is 0,0)
     * @param yPx Bottom left Y origin in Pixels (top left is 0,0)
     */
    public void text(String txt, int xPx, int yPx)
    {
        text(txt, xPx, yPx, Mode.WHITE_ON_BLACK);
    }
    public void text(String txt, int xPx, int yPx, Mode mode)
    {
        int xProgress = xPx;
        for (int i=0; i<txt.length(); i++)           // For each character of the string to display
        {
            String c = new String(new char[] { txt.charAt(i) });
            if (CharacterMatrixes.characters.containsKey(c))
            {
                String[] matrix = CharacterMatrixes.characters.get(c);
                for (int x=0; x<matrix[0].length(); x++) // Each COLUMN of the matrix
                {
                    char[] verticalBitmap = new char[CharacterMatrixes.FONT_SIZE];
                    for (int y=0; y<matrix.length; y++)    // Each LINE of the matrix
                        verticalBitmap[y] = matrix[y].charAt(x);
                    // Write in the scren matrix
                    // screenMatrix[line][col]
                    for (int y=0; y<CharacterMatrixes.FONT_SIZE; y++)
                    {
                        int l = (y + yPx - (CharacterMatrixes.FONT_SIZE - 1));
                        if (l >= 0 && l < this.h && xProgress >= 0 && xProgress < this.w)
                            screenMatrix[l][xProgress] = (mode == Mode.WHITE_ON_BLACK ? verticalBitmap[y] : invert(verticalBitmap[y]));
                    }
                    xProgress++;
                }
            }
            else
            {
                System.out.println("Character not found for the OLED [" + c + "]");
            }
        }
    }

    private char invert(char c)
    {
        return (c == ' ' ? 'X' : ' ');
    }
    /**
     * For debug...
     */
    public void dumpScreen()
    {
        for (int l=0; l<this.h; l++)
        {
            System.out.println(new String(screenMatrix[l]));
        }
    }

    public void plot(int x, int y)
    {
        plot(x, y, Mode.WHITE_ON_BLACK);
    }
    public void plot(int x, int y, Mode mode)
    {
        if (x >= 0 && x < this.w && y >= 0 && y < this.h)
            screenMatrix[y][x] = (mode == Mode.WHITE_ON_BLACK ? 'X' : ' ');
    }

    public void unplot(int x, int y)
    {
        unplot(x, y, Mode.WHITE_ON_BLACK);
    }
    public void unplot(int x, int y, Mode mode)
    {
        if (x >= 0 && x < this.w && y >= 0 && y < this.h)
            screenMatrix[y][x] = (mode == Mode.WHITE_ON_BLACK ? ' ' : 'X');
    }

    public void line(int fromx, int fromy, int tox, int toy)
    {
        line(fromx, fromy, tox, toy, Mode.WHITE_ON_BLACK);
    }
    public void line(int fromx, int fromy, int tox, int toy, Mode mode)
    {
        int deltaX = (tox - fromx);
        int deltaY = (toy - fromy);
        if (deltaX == 0 && deltaY == 0)
        {
            screenMatrix[fromy][fromx] = (mode == Mode.WHITE_ON_BLACK ? 'X' : ' ');
            return;
        }
        if (deltaX == 0)
        {
            for (int y=Math.min(fromy, toy); y<=Math.max(toy, fromy); y++)
            {
                if (fromx >= 0 && fromx < this.w && y >= 0 && y < this.h)
                    screenMatrix[y][fromx] = (mode == Mode.WHITE_ON_BLACK ? 'X' : ' ');
            }
        }
        else if (deltaY == 0)
        {
            for (int x=Math.min(fromx, tox); x<=Math.max(tox, fromx); x++)
            {
                if (x >= 0 && x < this.w && fromy >= 0 && fromy < this.h)
                    screenMatrix[fromy][x] = (mode == Mode.WHITE_ON_BLACK ? 'X' : ' ');
            }
        }
        else if (Math.abs(deltaX) > Math.abs(deltaY)) // [-45, +45]
        {
            if (deltaX < 0)
            {
                int X = fromx;
                int Y = fromy;
                fromx = tox;
                tox = X;
                fromy = toy;
                toy = Y;
                deltaX = (tox - fromx);
                deltaY = (toy - fromy);
            }
            double coeffDir = (double)deltaY / (double)deltaX;
//    if (fromx < tox)
            {
                for (int x=0; x<=deltaX; x++)
                {
                    int y = fromy + (int)(Math.round(x * coeffDir));
                    int _x = x + fromx;
                    if (_x >= 0 && _x < this.w && y >= 0 && y < this.h)
                        screenMatrix[y][_x] = (mode == Mode.WHITE_ON_BLACK ? 'X' : ' ');
                }
            }
        }
        else if (Math.abs(deltaX) < Math.abs(deltaY)) // > 45, < -45
        {
            if (deltaY < 0)
            {
                int X = fromx;
                int Y = fromy;
                fromx = tox;
                tox = X;
                fromy = toy;
                toy = Y;
                deltaX = (tox - fromx);
                deltaY = (toy - fromy);
            }
            double coeffDir = (double)deltaX / (double)deltaY;
            //    if (fromx < tox)
            {
                for (int y=0; y<=deltaY; y++)
                {
                    int x = fromx + (int)(Math.round(y * coeffDir));
                    int _y = y + fromy;
                    if (_y >= 0 && _y < this.h && x >= 0 && x < this.w)
                        screenMatrix[_y][x] = (mode == Mode.WHITE_ON_BLACK ? 'X' : ' ');
                }
            }
        }
    }

    public void shape(Polygon polygon, boolean closed)
    {
        shape(polygon, closed, Mode.WHITE_ON_BLACK);
    }
    public void shape(Polygon polygon, boolean closed, Mode mode)
    {
        int[] x = polygon.xpoints;
        int[] y = polygon.ypoints;
        for (int i=1; i<polygon.npoints; i++)
            line(x[i-1], y[i-1], x[i], y[i], mode);
        if (closed)
            line(x[0], y[0], x[polygon.npoints - 1], y[polygon.npoints - 1], mode);
    }

    public void rectangle(int tlX, int tlY, int brX, int brY)
    {
        rectangle(tlX, tlY, brX, brY, Mode.WHITE_ON_BLACK);
    }
    public void rectangle(int tlX, int tlY, int brX, int brY, Mode mode)
    {
        line(tlX, tlY, tlX, brY, mode);
        line(tlX, brY, brX, brY, mode);
        line(brX, brY, brX, tlY, mode);
        line(brX, tlY, tlX, tlY, mode);
    }

    public void circle(int centerX, int centerY, int radius)
    {
        circle(centerX, centerY, radius, Mode.WHITE_ON_BLACK);
    }
    public void circle(int centerX, int centerY, int radius, Mode mode)
    {
        arc(centerX, centerY, radius, 0, 360, mode);
    }

    public void arc(int centerX, int centerY, int radius, int fromDeg, int toDeg)
    {
        arc(centerX, centerY, radius, fromDeg, toDeg, Mode.WHITE_ON_BLACK);
    }
    public void arc(int centerX, int centerY, int radius, int fromDeg, int toDeg, Mode mode)
    {
        Point prevPt = null;
        for (int i=fromDeg; i<=toDeg; i++)
        {
            int x = centerX + (int)Math.round(radius * Math.sin(Math.toRadians(i)));
            int y = centerY + (int)Math.round(radius * Math.cos(Math.toRadians(i)));
            Point pt = new Point(x, y);
            if (x >= 0 && x < this.w && y >= 0 && y < this.h)
            {
                screenMatrix[y][x] = (mode == Mode.WHITE_ON_BLACK ? 'X' : ' ');
                prevPt = pt;
            }
            else
                prevPt = null;

        }
    }

    public void image(ImgInterface img, int topLeftX, int topLeftY)
    {
        image(img, topLeftX, topLeftY, Mode.WHITE_ON_BLACK);
    }
    public void image(ImgInterface img, int topLeftX, int topLeftY, Mode mode)
    {
        int w = img.getW();
        int h = img.getH(); // Assume h % 8 = 0
        int[] imgBuf = img.getImgBuffer();
        for (int col=0; col<w; col++)
        {
            for (int row=0; row<(h / 8); row++)
            {
                String bitMapCol = lpad(Integer.toBinaryString(imgBuf[col + (w * row)]), "0", 8).replace('0', (mode == Mode.WHITE_ON_BLACK ? ' ' : 'X')).replace('1', (mode == Mode.WHITE_ON_BLACK ? 'X' : ' '));
                // Write in the scren matrix
                // screenMatrix[line][col]
                for (int y=0; y<8; y++)
                {
                    int l = (topLeftY + (7 - y) + (row * 8));
                    if (l >= 0 && l < this.h && (col + topLeftX) >= 0 && (col +topLeftX) < this.w)
                        screenMatrix[l][(col + topLeftX)] = bitMapCol.charAt(y);
                }
            }
        }
    }

    public int strlen(String s)
    {
        int len = 0;
        for (int i=0; i<s.length(); i++) // For each character of the string to display
        {
            String c = new String(new char[] { s.charAt(i) });
            if (CharacterMatrixes.characters.containsKey(c))
            {
                String[] matrix = CharacterMatrixes.characters.get(c);
                len += matrix[0].length();
            }
        }
        return len;
    }

    private static String lpad(String str, String with, int len)
    {
        String s = str;
        while (s.length() < len)
            s = with + s;
        return s;
    }
}