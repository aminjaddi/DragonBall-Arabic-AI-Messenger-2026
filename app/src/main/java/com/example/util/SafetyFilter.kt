package com.example.util

object SafetyFilter {

    // Forbidden roots / insults / offensive patterns
    private val offensivePatterns = listOf(
        "سب", "شتم", "لعن", "كفر", "قذر", "تافه", "حمار", "كلب",
        "ابن ال", "يلعن", "سافل", "منحط", "حقير", "غبي جدا"
    )

    // Patterns protecting sacred entities from any form of mockery or disrespect
    private val blasphemyPatterns = listOf(
        "سب الله", "شتم الله", "إهانة الله", "سب الدين", "شتم الدين",
        "سب النبي", "شتم الرسول", "إهانة القرآن", "استهزاء بالدين"
    )

    data class ValidationResult(
        val isClean: Boolean,
        val sanitizedMessage: String? = null,
        val errorMessage: String? = null
    )

    fun validateMessage(text: String): ValidationResult {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return ValidationResult(isClean = false, errorMessage = "الرسالة فارغة.")
        }

        val normalized = trimmed.lowercase()

        for (pattern in blasphemyPatterns) {
            if (normalized.contains(pattern)) {
                return ValidationResult(
                    isClean = false,
                    errorMessage = "عذرًا، يُمنع منعًا باتًا أي محتوى مسيء للذات الإلهية أو المقدسات الدينية. نرجو الالتزام بالأخلاق والاحترام."
                )
            }
        }

        for (pattern in offensivePatterns) {
            if (normalized.contains(pattern)) {
                return ValidationResult(
                    isClean = false,
                    errorMessage = "نرجو الحفاظ على لغة مهذبة وراقية خالية من الإساءات والشتائم."
                )
            }
        }

        return ValidationResult(isClean = true, sanitizedMessage = trimmed)
    }

    /**
     * Sanitizes AI response before displaying to ensure respectful terminology,
     * such as enforcing 'زعيم الدمار' instead of theological deities.
     */
    fun sanitizeAiOutput(text: String): String {
        return text
            .replace("إله الدمار", "زعيم الدمار")
            .replace("آلهة الدمار", "زعماء الدمار")
            .replace("الإله بيروس", "الزعيم بيروس")
    }
}
