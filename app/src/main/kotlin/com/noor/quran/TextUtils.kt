package com.noor.quran

object TextUtils {
    /**
     * Cleans extra spaces and invisible characters while preserving Quranic diacritics.
     */
    fun cleanQuranicText(text: String): String {
        return text
            .replace("\\s+".toRegex(), " ")
            .trim()
    }

    /**
     * Converts integer to Arabic digits (٠١٢٣٤٥٦٧٨٩)
     */
    fun toArabicDigits(number: Int): String {
        val stringNumber = number.toString()
        val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        val builder = StringBuilder()
        for (element in stringNumber) {
            if (Character.isDigit(element)) {
                builder.append(arabicDigits[element - '0'])
            } else {
                builder.append(element)
            }
        }
        return builder.toString()
    }
}
