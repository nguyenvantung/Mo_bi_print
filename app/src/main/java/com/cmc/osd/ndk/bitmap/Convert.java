package com.cmc.osd.ndk.bitmap;

/**
 * Image bit-depth conversion methods.
 * 
 * @author Nguyen Van An
 */
public class Convert {
    static {
        System.loadLibrary("lept");
    }
    
    /**
     * Converts an image of any bit depth to 8-bit grayscale.
     *
     * @param pixs Source pix of any bit-depth.
     * @return a new Pix image or <code>null</code> on error
     */
    public static Pix convertTo8(Pix pixs) {
        if (pixs == null)
            throw new IllegalArgumentException("Source pix must be non-null");

        int nativePix = nativeConvertTo8(pixs.mNativePix);

        if (nativePix == 0)
            throw new RuntimeException("Failed to natively convert pix");

        return new Pix(nativePix);
    }

    // ***************
    // * NATIVE CODE *
    // ***************

    private static native int nativeConvertTo8(int nativePix);
}
