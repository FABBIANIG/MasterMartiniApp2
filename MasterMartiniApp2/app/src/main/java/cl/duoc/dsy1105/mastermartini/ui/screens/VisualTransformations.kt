package cl.duoc.dsy1105.mastermartini.ui.screens

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class RutVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Remove todo menos números y K
        val raw = text.text.replace(Regex("[^0-9kK]"), "").uppercase()
        var out = ""
        for (i in raw.indices) {
            out += raw[i]
            val rToL = raw.length - i - 1
            if (rToL == 1) out += "-"
            else if (rToL > 1 && (rToL - 1) % 3 == 0) out += "."
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val originalText = raw
                if (offset <= 0) return 0
                val validOffset = offset.coerceAtMost(originalText.length)
                var outOffset = 0
                for (i in 0 until validOffset) {
                    outOffset++
                    val rToL = originalText.length - i - 1
                    if (rToL == 1) outOffset++
                    else if (rToL > 1 && (rToL - 1) % 3 == 0) outOffset++
                }
                return outOffset
            }

            override fun transformedToOriginal(offset: Int): Int {
                val transformedText = out
                if (offset <= 0) return 0
                val validOffset = offset.coerceAtMost(transformedText.length)
                var originalOffset = 0
                for (i in 0 until validOffset) {
                    val char = transformedText[i]
                    if (char != '.' && char != '-') {
                        originalOffset++
                    }
                }
                return originalOffset
            }
        }
        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Formato para Chile: +56 9 1234 5678
        val raw = text.text.replace(Regex("[^0-9]"), "")
        var out = ""
        for (i in raw.indices) {
            if (i == 0) out += "+"
            out += raw[i]
            if (i == 1 || i == 2 || i == 6) out += " "
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val validOffset = offset.coerceAtMost(raw.length)
                var outOffset = 0
                for (i in 0 until validOffset) {
                    if (i == 0) outOffset++
                    outOffset++
                    if (i == 1 || i == 2 || i == 6) outOffset++
                }
                return outOffset
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                val validOffset = offset.coerceAtMost(out.length)
                var originalOffset = 0
                for (i in 0 until validOffset) {
                    if (out[i] != '+' && out[i] != ' ') {
                        originalOffset++
                    }
                }
                return originalOffset
            }
        }
        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}