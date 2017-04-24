
package com.cmc.osd.ndk.bitmap;

/**
 * Image rotation and skew detection methods.
 *
 * @author Nguyen Van An
 */
public class Skew {
    static {
        System.loadLibrary("lept");
    }

    // Text alignment defaults
    
    /** Default range for sweep, will detect rotation of + or - 30 degrees. */
    public final static float SWEEP_RANGE = 30.0f;

    /** Default sweep delta, reasonably accurate within 0.05 degrees. */
    public final static float SWEEP_DELTA = 5.0f;

    /** Default sweep reduction, one-eighth the size of the original image. */
    public final static int SWEEP_REDUCTION = 8;

    /** Default sweep reduction, one-fourth the size of the original image. */
    public final static int SEARCH_REDUCTION = 4;

    /** Default search minimum delta, reasonably accurate within 0.05 degrees. */
    public final static float SEARCH_MIN_DELTA = 0.01f;

    /** 
     * Finds and returns the skew angle using default parameters.
     * 
     * @param pixs Input pix (1 bpp).
     * @return the detected skew angle, or 0.0 on failure
     */
    public static float findSkew(Pix pixs) {
        return findSkew(pixs, SWEEP_RANGE, SWEEP_DELTA, SWEEP_REDUCTION, SEARCH_REDUCTION,
                SEARCH_MIN_DELTA);
    }

    /**
     * Finds and returns the skew angle, doing first a sweep through a set of
     * equal angles, and then doing a binary search until convergence.
     * <p>
     * Notes:
     * <ol>
     * <li>In computing the differential line sum variance score, we sum the
     * result over scanlines, but we always skip:
     * <ul>
     * <li>at least one scanline
     * <li>not more than 10% of the image height
     * <li>not more than 5% of the image width
     * </ul>
     * </ol>
     *
     * @param pixs Input pix (1 bpp).
     * @param sweepRange Half the full search range, assumed about 0; in
     *            degrees.
     * @param sweepDelta Angle increment of sweep; in degrees.
     * @param sweepReduction Sweep reduction factor = 1, 2, 4 or 8.
     * @param searchReduction Binary search reduction factor = 1, 2, 4 or 8; and
     *            must not exceed redsweep.
     * @param searchMinDelta Minimum binary search increment angle; in degrees.
     * @return the detected skew angle, or 0.0 on failure
     */
    public static float findSkew(Pix pixs, float sweepRange, float sweepDelta, int sweepReduction,
            int searchReduction, float searchMinDelta) {
        if (pixs == null)
            throw new IllegalArgumentException("Source pix must be non-null");

        return nativeFindSkew(pixs.mNativePix, sweepRange, sweepDelta, sweepReduction,
                searchReduction, searchMinDelta);
    }

    // ***************
    // * NATIVE CODE *
    // ***************

    private static native float nativeFindSkew(int nativePix, float sweepRange, float sweepDelta,
            int sweepReduction, int searchReduction, float searchMinDelta);

}
