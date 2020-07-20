package it.tredi.dw4.utils;

//Program: Convert from binary (decimal) to roman numerals.
//This is the model without any user interface.
public class RomanConversion {

	// Parallel arrays used in the conversion process.
	private static final String[] RCODE = { "M", "CM", "D", "CD", "C", "XC",
			"L", "XL", "X", "IX", "V", "IV", "I" };
	private static final int[] BVAL = { 1000, 900, 500, 400, 100, 90, 50, 40,
			10, 9, 5, 4, 1 };

	// =========================================================== binaryToRoman
	public static String binaryToRoman(int binary) {
		if (binary == 0) return "0";
		if (binary <= 0 || binary >= 4000) {
			throw new NumberFormatException("Value outside roman numeral range.");
		}
		String roman = ""; // Roman notation will be accumualated here.

		// Loop from biggest value to smallest, successively subtracting,
		// from the binary value while adding to the roman representation.
		for (int i = 0; i < RCODE.length; i++) {
			while (binary >= BVAL[i]) {
				binary -= BVAL[i];
				roman += RCODE[i];
			}
		}
		return roman;
	}
	public static int valueOf(String roman) {
		roman = roman.toUpperCase();
		if(roman.length() == 0) return 0;
		for(int i = 0; i < RCODE.length; i++) {
			int pos = roman.indexOf(RCODE[i]) ;
			if(pos >= 0) return BVAL[i] - valueOf(roman.substring(0,pos)) + valueOf(roman.substring(pos+1));
		}
		throw new IllegalArgumentException("Invalid Roman Symbol.");
	}
}