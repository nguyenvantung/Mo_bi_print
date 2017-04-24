
package com.cmc.osd.ndk.bitmap;

/**
 * Image scaling methods.
 * 
 * @author Nguyen Van An
 */
public class Scale {
    static {
        System.loadLibrary("lept");
    }

    public enum ScaleType {
        /* Scale in X and Y independently, so that src matches dst exactly. */
        FILL,

        /*
         * Compute a scale that will maintain the original src aspect ratio, but
         * will also ensure that src fits entirely inside dst. May shrink or
         * expand src to fit dst.
         */
        FIT,

        /*
         * Compute a scale that will maintain the original src aspect ratio, but
         * will also ensure that src fits entirely inside dst. May shrink src to
         * fit dst, but will not expand it.
         */
        FIT_SHRINK,
    }
    
    public enum Color {
    	COLOR_RED, COLOR_GREEN, COLOR_BLUE, L_ALPHA_CHANNEL
    }

    /**
     * Scales the Pix to a specified width and height using a specified scaling
     * type (fill, stretch, etc.). Returns a scaled image or a clone of the Pix
     * if no scaling is required.
     *
     * @param pixs
     * @param width
     * @param height
     * @param type
     * @return a scaled image or a clone of the Pix if no scaling is required
     */
    public static Pix scaleToSize(Pix pixs, int width, int height, ScaleType type) {
        if (pixs == null)
            throw new IllegalArgumentException("Source pix must be non-null");

        int pixWidth = pixs.getWidth();
        int pixHeight = pixs.getHeight();

        float scaleX = width / (float) pixWidth;
        float scaleY = height / (float) pixHeight;

        switch (type) {
            case FILL:
                // Retains default scaleX and scaleY values
                break;
            case FIT:
                scaleX = Math.min(scaleX, scaleY);
                scaleY = scaleX;
                break;
            case FIT_SHRINK:
                scaleX = Math.min(1.0f, Math.min(scaleX, scaleY));
                scaleY = scaleX;
                break;
        }

        return scale(pixs, scaleX, scaleY);
    }

    /**
     * Scales the Pix to specified scale. If no scaling is required, returns a
     * clone of the source Pix.
     *
     * @param pixs the source Pix
     * @param scale dimension scaling factor
     * @return a Pix scaled according to the supplied factors
     */
    public static Pix scale(Pix pixs, float scale) {
        return scale(pixs, scale, scale);
    }

    /**
     * Scales the Pix to specified x and y scale. If no scaling is required,
     * returns a clone of the source Pix.
     *
     * @param pixs the source Pix
     * @param scaleX x-dimension (width) scaling factor
     * @param scaleY y-dimension (height) scaling factor
     * @return a Pix scaled according to the supplied factors
     */
    public static Pix scale(Pix pixs, float scaleX, float scaleY) {
        if (pixs == null)
            throw new IllegalArgumentException("Source pix must be non-null");
        if (scaleX <= 0.0f)
            throw new IllegalArgumentException("X scaling factor must be positive");
        if (scaleY <= 0.0f)
            throw new IllegalArgumentException("Y scaling factor must be positive");

        int nativePix = nativeScale(pixs.mNativePix, scaleX, scaleY);

        if (nativePix == 0)
            throw new RuntimeException("Failed to natively scale pix");

        return new Pix(nativePix);
    }
    
    public static Pix scaleRGBToGrayFast(Pix pixs, int factor, Color color) {
    	if (pixs == null)
            throw new IllegalArgumentException("Source pix must be non-null");
    	if (pixs.getDepth() != 32)
            throw new IllegalArgumentException("Depth not 32 bpp");
        if (factor < 1)
            throw new IllegalArgumentException("Scaling factor must be greater or equal to 1");
        if ((color != Color.COLOR_RED) && (color != Color.COLOR_GREEN) && (color != Color.COLOR_BLUE)) 
            throw new IllegalArgumentException("Invalid color");
        
        int iColor = -1;
        switch (color)
        {
	        case COLOR_RED:
	        	iColor = 0;
	        	break;
	        case COLOR_GREEN:
	        	iColor = 1;
	        	break;
	        case COLOR_BLUE:
	        	iColor = 2;
	        	break;
        }
    	int nativePix = nativeScaleRGBToGrayFast(pixs.mNativePix, factor, iColor); 
        return new Pix(nativePix);
    }
    
    public static Pix scaleRGBToGray2(Pix pixs, float rwt, float gwt, float bwt) {
    	if (pixs == null)
            throw new IllegalArgumentException("Source pix must be non-null");
    	if (pixs.getDepth() != 32)
            throw new IllegalArgumentException("Depth not 32 bpp");
        if (rwt + gwt + bwt < 0.98 || rwt + gwt + bwt > 1.02)
            throw new IllegalArgumentException("sum of wts should be 1.0");
        int nativePix = nativeScaleRGBToGray2(pixs.mNativePix, rwt, gwt, bwt); 
        return new Pix(nativePix);
    }
    
    public static Pix scaleRGBToGrayFast1(Pix pixs, int factor, float rwt, float gwt, float bwt) {
    	if (pixs == null)
            throw new IllegalArgumentException("Source pix must be non-null");
    	if (pixs.getDepth() != 32)
            throw new IllegalArgumentException("Depth not 32 bpp");
    	if (factor < 1)
            throw new IllegalArgumentException("Scaling factor must be greater or equal to 1");
        if (rwt + gwt + bwt < 0.98 || rwt + gwt + bwt > 1.02)
            throw new IllegalArgumentException("sum of wts should be 1.0");
        int nativePix = nativeScaleRGBToGrayFast1(pixs.mNativePix, factor, rwt, gwt, bwt); 
        return new Pix(nativePix);
    }
    
    public static Pix scaleGray2xLIDither(Pix pixs) {
    	if (pixs == null)
            throw new IllegalArgumentException("Source pix must be non-null");
    	if (pixs.getDepth() != 8)
            throw new IllegalArgumentException("Depth not 32 bpp");

        int nativePix = nativeScaleGray2xLIDither(pixs.mNativePix); 
        return new Pix(nativePix);
    }
    
    public static Pix scaleGray4xLIDither(Pix pixs) {
    	if (pixs == null)
            throw new IllegalArgumentException("Source pix must be non-null");
    	if (pixs.getDepth() != 8)
            throw new IllegalArgumentException("Depth not 32 bpp");

        int nativePix = nativeScaleGray4xLIDither(pixs.mNativePix); 
        return new Pix(nativePix);
    }

    // ***************
    // * NATIVE CODE *
    // ***************

    private static native int nativeScale(int nativePix, float scaleX, float scaleY);
    private static native int nativeScaleRGBToGrayFast(int nativePix, int factor, int color);
    private static native int nativeScaleRGBToGray2(int nativePix, float rwt, float gwt, float bwt);
    /**
     * 
     * @param nativePix
     * @param factor: 
     * 	0.5 <-> scaled down by 2x in each direction
     *  0.33333 <-> scaled down by 3x in each direction
     *  0.25 <-> scaled down by 4x in each direction
     *  0.16666 <-> scaled down by 6x in each direction
     *  0.125 <-> scaled down by 8x in each direction
     *  0.0625 <-> scaled down by 16x in each direction
     * @param rwt
     * @param gwt
     * @param bwt
     * @return
     */
    private static native int nativeScaleRGBToGrayFast1(int nativePix, int factor, float rwt, float gwt, float bwt);
    private static native int nativeScaleGray2xLIDither(int nativePix);
    private static native int nativeScaleGray4xLIDither(int nativePix);
}
