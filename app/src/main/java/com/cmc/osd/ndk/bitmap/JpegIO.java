package com.cmc.osd.ndk.bitmap;

/**
 * JPEG input and output methods.
 *
 * @author Nguyen Van An
 */
public class JpegIO {
    static {
        System.loadLibrary("lept");
    }

    /** Default quality is 85%, which is reasonably good. */
    public static final int DEFAULT_QUALITY = 85;

    /** Progressive encoding is disabled by default to increase compatibility. */
    public static final boolean DEFAULT_PROGRESSIVE = false;

    /**
     * Returns a compressed JPEG byte representation of this Pix using default
     * parameters.
     *
     * @param pixs
     * @return a compressed JPEG byte array representation of the Pix
     */
    public static byte[] compressToJpeg(Pix pixs) {
        return compressToJpeg(pixs, DEFAULT_QUALITY, DEFAULT_PROGRESSIVE);
    }

    /**
     * Returns a compressed JPEG byte representation of this Pix.
     *
     * @param pixs A source pix image.
     * @param quality The quality of the compressed image. Valid range is 0-100.
     * @param progressive Whether to use progressive compression.
     * @return a compressed JPEG byte array representation of the Pix
     */
    public static byte[] compressToJpeg(Pix pixs, int quality, boolean progressive) {
        if (pixs == null)
            throw new IllegalArgumentException("Source pix must be non-null");
        if (quality < 0 || quality > 100)
            throw new IllegalArgumentException("Quality must be between 0 and 100 (inclusive)");

        return nativeCompressToJpeg(pixs.mNativePix, quality, progressive);
    }

    // ***************
    // * NATIVE CODE *
    // ***************

    private static native byte[] nativeCompressToJpeg(
            int nativePix, int quality, boolean progressive);
}
