/**
 * 
 */
package com.plustech.print.common;

import java.util.ArrayList;


/**
 * @author NVan - Tqtuan
 *
 */
public class PaperSizes {

	/**
	 * 
	 */
	public PaperSizes() {
		// TODO Auto-generated constructor stub
		PaperSize_C paperSize;
		paperSize = new PaperSize_C("Letter",		(float)216,		(float)279,		(float) 8.5,(float)11	,	MEDIA_SIZE_ID.EPS_MSID_LETTER, 	"na-letter-white");
		sizesList.add(paperSize);
		paperSize = new PaperSize_C("A4",			(float)210,		(float)297,		(float)8.3,	(float)11.7	,	MEDIA_SIZE_ID.EPS_MSID_A4,		"iso-a4-white");
		sizesList.add(paperSize);
		paperSize = new PaperSize_C("Legal",		(float)216,		(float)356,		(float)8.5,	(float)15	,	MEDIA_SIZE_ID.EPS_MSID_LEGAL,	"na-legal-white");
		sizesList.add(paperSize);
		paperSize = new PaperSize_C("A3",			(float)297,		(float)420,		(float)11.7,(float)16.5	,	MEDIA_SIZE_ID.EPS_MSID_A3,		"iso-a3-white");
		sizesList.add(paperSize);
		paperSize = new PaperSize_C("Large Photo",	(float)203.2,	(float)254,		(float)8.0,	(float)10.0	,	MEDIA_SIZE_ID.EPS_MSID_8X10,	null);
		sizesList.add(paperSize);
		paperSize = new PaperSize_C("Medium Photo",	(float)127,		(float)177.8,	(float)5.0,	(float)7.0	,	MEDIA_SIZE_ID.EPS_MSID_2L,		null);
		sizesList.add(paperSize);
		paperSize = new PaperSize_C("Small Photo",	(float)101.6,	(float)152.4,	(float)4.0,	(float)6.0	,	MEDIA_SIZE_ID.EPS_MSID_4X6,		null);
		sizesList.add(paperSize);
	}
	public enum MEDIA_SIZE_ID {
		        /* Dec  Hex  [      mm       ]  */
		EPS_MSID_A4             (0), /*  0  0x00  [210.000,297.000]  */
		EPS_MSID_LETTER         (1), /*  1  0x01  [215.900,279.400]  */
		EPS_MSID_LEGAL          (2), /*  2  0x02  [215.900,355.600]  */
		EPS_MSID_A5             (3), /*  3  0x03  [148.000,210.000]  */
		EPS_MSID_A6             (4), /*  4  0x04  [105.000,148.000]  */
		EPS_MSID_B5             (5), /*  5  0x05  [176.000,250.000]  */
		EPS_MSID_EXECUTIVE      (6), /*  6  0x06  [184.150,266.700]  */
		EPS_MSID_HALFLETTER     (7), /*  7  0x07  [127.000,215.900]  */
		EPS_MSID_PANORAMIC      (8), /*  8  0x08  [210.000,594.000]  */
		EPS_MSID_TRIM_4X6       (9), /*  9  0x09  [113.600,164.400]  */
		EPS_MSID_4X6           (10), /* 10  0x0A  [101.600,152.400]  */
		EPS_MSID_5X8           (11), /* 11  0x0B  [127.000,203.200]  */
		EPS_MSID_8X10          (12), /* 12  0x0C  [203.200,203.200]  */
		EPS_MSID_10X15         (13), /* 13  0x0D  [254.000,381.000]  */
		EPS_MSID_200X300       (14), /* 14  0x0E  [200.000,300.000]  */
		EPS_MSID_L             (15), /* 15  0x0F  [ 88.900,127.000]  */
		EPS_MSID_POSTCARD      (16), /* 16  0x10  [100.000,148.000]  */
		EPS_MSID_DBLPOSTCARD   (17), /* 17  0x11  [200.000,148.000]  */
		EPS_MSID_ENV_10_L      (18), /* 18  0x12  [241.300,104.775]  */
		EPS_MSID_ENV_C6_L      (19), /* 19  0x13  [162.000,114.000]  */
		EPS_MSID_ENV_DL_L      (20), /* 20  0x14  [220.000,110.000]  */
		EPS_MSID_NEWEVN_L      (21), /* 21  0x15  [220.000,132.000]  */
		EPS_MSID_CHOKEI_3      (22), /* 22  0x16  [120.000,235.000]  */
		EPS_MSID_CHOKEI_4      (23), /* 23  0x17  [ 90.000,205.000]  */
		EPS_MSID_YOKEI_1       (24), /* 24  0x18  [120.000,176.000]  */
		EPS_MSID_YOKEI_2       (25), /* 25  0x19  [114.000,162.000]  */
		EPS_MSID_YOKEI_3       (26), /* 26  0x1A  [ 98.000,148.000]  */
		EPS_MSID_YOKEI_4       (27), /* 27  0x1B  [105.000,235.000]  */
		EPS_MSID_2L            (28), /* 28  0x1C  [127.000,177.800]  */
		EPS_MSID_ENV_10_P      (29), /* 29  0x1D  [104.775,241.300]  */
		EPS_MSID_ENV_C6_P      (30), /* 30  0x1E  [114.000,162.000]  */
		EPS_MSID_ENV_DL_P      (31), /* 31  0x1F  [110.000,220.000]  */
		EPS_MSID_NEWENV_P      (32), /* 32  0x20  [132.000,220.000]  */
		EPS_MSID_MEISHI        (33), /* 33  0x21  [ 89.000, 55.000]  */
		EPS_MSID_BUZCARD_89X50 (34), /* 34  0x22  [ 89.000, 50.000]  */
		EPS_MSID_CARD_54X86    (35), /* 35  0x23  [ 54.000, 86.000]  */
		EPS_MSID_BUZCARD_55X91 (36), /* 36  0x24  [ 55.000, 91.000]  */
		EPS_MSID_ALBUM_L       (37), /* 37  0x25  [127.000,198.000]  */
		EPS_MSID_ALBUM_A5      (38), /* 38  0x26  [210.000,321.000]  */
		EPS_MSID_PALBUM_L_L    (39), /* 39  0x27  [127.000  89.000]  */
		EPS_MSID_PALBUM_2L     (40), /* 40  0x28  [127.000,177.900]  */
		EPS_MSID_PALBUM_A5_L   (41), /* 41  0x29  [210.000,148.300]  */
		EPS_MSID_PALBUM_A4     (42), /* 42  0x2A  [210.000,296.300]  */
		EPS_MSID_HIVISION      (43), /* 43  0x2B  [101.600,180.600]  */
		EPS_MSID_A3NOBI        (61), /* 61  0x3D  [329.000,483.000]  */
		EPS_MSID_A3            (62), /* 62  0x3E  [297.000,420.000]  */
		EPS_MSID_B4            (63), /* 63  0x3F  [257.000,364.000]  */
		EPS_MSID_USB           (64), /* 64  0x40  [279.400,431.800]  */
		EPS_MSID_11X14         (65), /* 65  0x41  [279.400,355.600]  */
		EPS_MSID_B3            (66), /* 66  0x42  [364.000,515.000]  */
		EPS_MSID_A2            (67), /* 67  0x43  [420.000,594.000]  */
		EPS_MSID_USC           (68), /* 68  0x44  [431.800,558.800]  */
		EPS_MSID_10X12         (69), /* 69  0x45  [254.000,304.800]  */
		EPS_MSID_12X12         (70), /* 70  0x46  [304.800,304.800]  */
		EPS_MSID_USER          (99), /* 99  0x63  [  0.000,  0.000]  */
		EPS_MSID_UNKNOWN       (0xff);/* unknown                      */
		
		public int index;
		MEDIA_SIZE_ID(int index) {
			this.index = index;
		}
	};
	
	public final class PAPER_SIZE {
		public static final int A3 = 14;
		public static final int A4 = 1;
		public static final int A5 = 15;
		public static final int A6 = 4;
		public static final int A6_WITH_TEAR_OFF_TAB = 10;
		public static final int B4 = 6;
		public static final int B5 = 7;
		public static final int CARD_4x6 = 5;
		public static final int CDDVD_120 = 13;
		public static final int CDDVD_80 = 12;
		public static final int CUSTOM_SIZE = 20;
		public static final int ENVELOPE_A2 = 22;
		public static final int ENVELOPE_C6 = 23;
		public static final int ENVELOPE_DL = 24;
		public static final int ENVELOPE_JPN3 = 25;
		public static final int ENVELOPE_JPN4 = 26;
		public static final int ENVELOPE_NO_10 = 21;
		public static final int EXECUTIVE = 18;
		public static final int FLSA = 19;
		public static final int HAGAKI = 9;
		public static final int L = 29;
		public static final int LEDGER = 16;
		public static final int LEGAL = 2;
		public static final int LETTER = 0;
		public static final int MAX_PAPER_SIZE = 30;
		public static final int OFUKU = 8;
		public static final int OUFUKU = 8;
		public static final int PHOTO = 3;
		public static final int PHOTO_4x12 = 28;
		public static final int PHOTO_4x8 = 27;
		public static final int PHOTO_5x7 = 11;
		public static final int SUPERB_SIZE = 17;
		public static final int UNSUPPORTED = -1;
	}
	public class PaperSize_C {
		public String	name;
		public float	mmWidth;
		public float	mmHeight;
		public float	inchWidth;
		public float	inchHeight;
		public MEDIA_SIZE_ID	epsonSize;	//Size enum that our Epson SDK knows about.
		public String ippSize;
		public int hpSize;
		
		PaperSize_C(String	name, float	mmWidth, float	mmHeight, float	inchWidth,
					float	inchHeight, MEDIA_SIZE_ID	epsonSize, String ippSize) {
			this.name = name.trim();
			this.mmWidth = mmWidth;
			this.mmHeight = mmHeight;
			this.inchWidth = inchWidth;
			this.inchHeight = inchHeight;
			this.epsonSize = epsonSize;
			this.ippSize = ippSize;
		}
	}
	public ArrayList<PaperSize_C> sizesList = new ArrayList<PaperSize_C>();
}
