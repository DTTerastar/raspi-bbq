package com.raspi.display.oled.reference;

/**
 * Created by Darrell on 6/7/2015.
 */
public class BitmapFont {

    private String characters;
    private final int charHeight;
    private final int widthSpace;
    private final int[][] descriptors;
    private final int[] bitmaps;

    public int getCharHeight() {
        return charHeight;
    }

    public int getWidthSpace() {
        return widthSpace;
    }

    public int[][] getDescriptors() {
        return descriptors;
    }

    public int[] getBitmaps() {
        return bitmaps;
    }

    public BitmapFont(String characters, int charHeight, int widthSpace, int[][] descriptors, int[] bitmaps) {
        this.characters = characters;
        this.charHeight = charHeight;
        this.widthSpace = widthSpace;
        this.descriptors = descriptors;
        this.bitmaps = bitmaps;
    }

    public int strlen(String txt) {
        int len = 0;
        for (int i = 0; i < txt.length(); i++) {
            char current = txt.charAt(i);
            int index = characters.indexOf(current);
            len += (i == 0 ? 0 : widthSpace);
            if (index >= 0)
                len += getDescriptors()[index][0];
            else {
                if (current == ' ')
                    len += widthSpace * 2;
            }
        }
        return len;
    }

    public int getIndex(char c) {
        return characters.indexOf(c);
    }
}
