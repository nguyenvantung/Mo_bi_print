package com.cmc.osd.ndk.bitmap;

/**
 * Image sharpening methods.
 *
 * @author Nguyen Van An
 */
public class Enhance {
    static {
        System.loadLibrary("lept");
    }

    /**
     * Performs unsharp masking (edge enhancement).
     * <p>
     * Notes:
     * <ul>
     * <li>We use symmetric smoothing filters of odd dimension, typically use
     * sizes of 3, 5, 7, etc. The <code>halfwidth</code> parameter for these is
     * (size - 1)/2; i.e., 1, 2, 3, etc.</li>
     * <li>The <code>fract</code> parameter is typically taken in the range: 0.2
     * &lt; <code>fract</code> &lt; 0.7</li>
     * </ul>
     *
     * @param halfwidth The half-width of the smoothing filter.
     * @param fraction The fraction of edge to be added back into the source
     *            image.
     * @return an edge-enhanced Pix image or copy if no enhancement requested
     */
    public static Pix unsharpMasking(Pix pixs, int halfwidth, float fraction) {
        if (pixs == null)
            throw new IllegalArgumentException("Source pix must be non-null");

        int nativePix = nativeUnsharpMasking(pixs.mNativePix, halfwidth, fraction);

        if (nativePix == 0) {
            throw new OutOfMemoryError();
        }

        return new Pix(nativePix);
    }

    // ***************
    // * NATIVE CODE *
    // ***************

    private static native int nativeUnsharpMasking(int nativePix, int halfwidth, float fract);
}
