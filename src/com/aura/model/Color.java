package com.aura.model;


public class Color {

    public static final int BLACK       = 0xFF000000;
    public static final int DKGRAY      = 0xFF444444;
    public static final int GRAY        = 0xFF888888;
    public static final int LTGRAY      = 0xFFCCCCCC;
    public static final int WHITE       = 0xFFFFFFFF;
    public static final int RED         = 0xFFFF0000;
    public static final int GREEN       = 0xFF00FF00;
    public static final int BLUE        = 0xFF0000FF;
    public static final int YELLOW      = 0xFFFFFF00;
    public static final int CYAN        = 0xFF00FFFF;
    public static final int MAGENTA     = 0xFFFF00FF;
    public static final int TRANSPARENT = 0;

    private static final float DEFAULT_FACTOR = 0.7F;

    public static final int darker(int color, float factor) {
    	int r = Math.max((int)(red(color)   * factor), 0);
    	int g = Math.max((int)(green(color) * factor), 0);
    	int b = Math.max((int)(blue(color)  * factor), 0);
    	int a = alpha(color);
    	return Color.argb(a, r, g, b);
    }

    public static final int brighter(int color, float factor) {
    	int r = red(color);
    	int g = green(color);
    	int b = blue(color);
    	int a = alpha(color);
    	int i = (int)(1.0/(1.0-factor));
    	if ( r == 0 && g == 0 && b == 0) {
    		return argb(a, r, g, b);
    	}
	    if ( r > 0 && r < i ) r = i;
	    if ( g > 0 && g < i ) g = i;
	    if ( b > 0 && b < i ) b = i;
	    r = Math.min((int)(r/factor), 255);
	    g = Math.min((int)(g/factor), 255);
	    b = Math.min((int)(b/factor), 255);
    	return Color.argb(a, r, g, b);
    }

    public static final int darker(int color) {
    	return darker(color, DEFAULT_FACTOR);
    }

    public static final int brighter(int color) {
    	return brighter(color, DEFAULT_FACTOR);
    }

    public static int setAlpha(int color, int alpha) {
    	int r = red(color);
    	int g = green(color);
    	int b = blue(color);
    	return Color.argb(alpha, r, g, b);
    }

    public static int random() {
     	int r = (int) (255 * Math.random());
     	int g = (int) (255 * Math.random());
     	int b = (int) (255 * Math.random());
     	return Color.rgb(r, g, b);
    }

    public static int alpha(int color) {
        return color >>> 24;
    }

    public static int red(int color) {
        return (color >> 16) & 0xFF;
    }

    public static int green(int color) {
        return (color >> 8) & 0xFF;
    }

    public static int blue(int color) {
        return color & 0xFF;
    }

    public static int rgb(int red, int green, int blue) {
        return (0xFF << 24) | (red << 16) | (green << 8) | blue;
    }

    public static int argb(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static int decode(String colorString) {
        if (colorString.charAt(0) == '#') {
            long color = Long.parseLong(colorString.substring(1), 16);
            if (colorString.length() == 7) {
                color |= 0x00000000ff000000;
            } else if (colorString.length() != 9) {
                throw new IllegalArgumentException("Unknown color");
            }
            return (int)color;
        }
        throw new IllegalArgumentException("Unknown color");
    }

}
