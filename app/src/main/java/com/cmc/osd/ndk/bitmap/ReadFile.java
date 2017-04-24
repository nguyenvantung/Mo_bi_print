
package com.cmc.osd.ndk.bitmap;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;

/**
 * Image input and output methods.
 *
 * @author Nguyen Van An
 */
public class ReadFile {
    static {
        System.loadLibrary("lept");
    }

    /**
     * Creates a 32bpp Pix object from encoded data. Supported formats are BMP
     * and JPEG.
     *
     * @param encodedData JPEG or BMP encoded byte data.
     * @return a 32bpp Pix object
     */
    public static Pix readMem(byte[] encodedData) {
        if (encodedData == null)
            throw new IllegalArgumentException("Image data byte array must be non-null");

        int nativePix = nativeReadMem(encodedData, encodedData.length);

        if (nativePix == 0)
            throw new RuntimeException("Failed to read pix from memory");

        return new Pix(nativePix);
    }

    /**
     * Creates an 8bpp Pix object from raw 8bpp grayscale pixels.
     *
     * @param pixelData 8bpp grayscale pixel data.
     * @param width The width of the input image.
     * @param height The height of the input image.
     * @return an 8bpp Pix object
     */
    public static Pix readBytes8(byte[] pixelData, int width, int height) {
        if (pixelData == null)
            throw new IllegalArgumentException("Byte array must be non-null");
        if (width <= 0)
            throw new IllegalArgumentException("Image width must be greater than 0");
        if (height <= 0)
            throw new IllegalArgumentException("Image height must be greater than 0");
        if (pixelData.length < width * height)
            throw new IllegalArgumentException("Array length does not match dimensions");

        int nativePix = nativeReadBytes8(pixelData, width, height);

        if (nativePix == 0)
            throw new RuntimeException("Failed to read pix from memory");

        return new Pix(nativePix);
    }

    /**
     * Replaces the bytes in an 8bpp Pix object with raw grayscale 8bpp pixels.
     * Width and height be identical to the input Pix.
     *
     * @param pixs The Pix whose bytes will be replaced.
     * @param pixelData 8bpp grayscale pixel data.
     * @param width The width of the input image.
     * @param height The height of the input image.
     * @return an 8bpp Pix object
     */
    public static boolean replaceBytes8(Pix pixs, byte[] pixelData, int width, int height) {
        if (pixs == null)
            throw new IllegalArgumentException("Source pix must be non-null");
        if (pixelData == null)
            throw new IllegalArgumentException("Byte array must be non-null");
        if (width <= 0)
            throw new IllegalArgumentException("Image width must be greater than 0");
        if (height <= 0)
            throw new IllegalArgumentException("Image height must be greater than 0");
        if (pixelData.length < width * height)
            throw new IllegalArgumentException("Array length does not match dimensions");
        if (pixs.getWidth() != width)
            throw new IllegalArgumentException("Source pix width does not match image width");
        if (pixs.getHeight() != height)
            throw new IllegalArgumentException("Source pix width does not match image width");

        return nativeReplaceBytes8(pixs.mNativePix, pixelData, width, height);
    }

    /**
     * Creates a Pixa object from encoded files in a directory. Supported
     * formats are BMP and JPEG.
     *
     * @param dir The directory containing the files.
     * @param prefix The prefix of the files to load into a Pixa.
     * @return a Pixa object containing one Pix for each file
     */
    public static Pixa readFiles(File dir, String prefix) {
        if (dir == null)
            throw new IllegalArgumentException("Directory must be non-null");
        if (!dir.exists())
            throw new IllegalArgumentException("Directory does not exist");
        if (!dir.canRead())
            throw new IllegalArgumentException("Cannot read directory");

        int nativePixa = nativeReadFiles(dir.getAbsolutePath(), prefix);

        if (nativePixa == 0)
            throw new RuntimeException("Failed to read pixs from files");

        // TODO Get bounding box from Pixa

        return new Pixa(nativePixa, -1, -1);
    }

    /**
     * Creates a Pix object from encoded file data. Supported formats are BMP
     * and JPEG.
     *
     * @param file The JPEG or BMP-encoded file to read in as a Pix.
     * @return a Pix object
     */
    public static Pix readFile(File file) {
        if (file == null)
            throw new IllegalArgumentException("File must be non-null");
        if (!file.exists())
            throw new IllegalArgumentException("File does not exist");
        if (!file.canRead())
            throw new IllegalArgumentException("Cannot read file");

        Log.i("ReadFile", "File path: "+ file.getPath());
        int nativePix = nativeReadFile(file.getPath());

        if (nativePix == 0)
            throw new RuntimeException("Failed to read pix from file");

        return new Pix(nativePix);
    }

    /**
     * Creates a Pix object from Bitmap data.
     *
     * @param bmp The Bitmap object to convert to a Pix.
     * @return a Pix object
     */
    public static Pix readBitmap(Bitmap bmp) {
        if (bmp == null)
            throw new IllegalArgumentException("Bitmap must be non-null");

        int nativePix = nativeReadBitmap(bmp);

        if (nativePix == 0)
            throw new RuntimeException("Failed to read pix from bitmap");

        return new Pix(nativePix);
    }

    // ***************
    // * NATIVE CODE *
    // ***************

    private static native int nativeReadMem(byte[] data, int size);

    private static native int nativeReadBytes8(byte[] data, int w, int h);

    private static native boolean nativeReplaceBytes8(int nativePix, byte[] data, int w, int h);

    private static native int nativeReadFiles(String dirname, String prefix);

    private static native int nativeReadFile(String filename);

    private static native int nativeReadBitmap(Bitmap bitmap);
}
