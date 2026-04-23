package com.screenreader

import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

object TranslationHelper {

    private val languageIdentifier = LanguageIdentification.getClient()
    private val translators = mutableMapOf<String, com.google.mlkit.nl.translate.Translator>()

    suspend fun translate(text: String, targetLanguage: String): String {
        // Identify source language
        val sourceLanguage = identifyLanguage(text)

        // If source and target are the same, return original text
        if (sourceLanguage == targetLanguage) {
            return text
        }

        // Get or create translator
        val translatorKey = "$sourceLanguage-$targetLanguage"
        val translator = translators.getOrPut(translatorKey) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build()
            Translation.getClient(options)
        }

        // Download model if needed
        val conditions = com.google.mlkit.common.model.DownloadConditions.Builder()
            .requireWifi()
            .build()

        try {
            translator.downloadModelIfNeeded(conditions).await()
        } catch (e: Exception) {
            // If WiFi is not available, try without WiFi requirement
            val fallbackConditions = com.google.mlkit.common.model.DownloadConditions.Builder()
                .build()
            translator.downloadModelIfNeeded(fallbackConditions).await()
        }

        // Translate
        return translator.translate(text).await()
    }

    private suspend fun identifyLanguage(text: String): String {
        return try {
            val languageCode = languageIdentifier.identifyLanguage(text).await()
            
            // Map language codes to ML Kit language codes
            when (languageCode) {
                "ar" -> TranslateLanguage.ARABIC
                "en" -> TranslateLanguage.ENGLISH
                "tr" -> TranslateLanguage.TURKISH
                "fa" -> TranslateLanguage.PERSIAN
                "und" -> TranslateLanguage.ENGLISH // Undetermined, default to English
                else -> {
                    // Try to map other common languages
                    mapLanguageCode(languageCode)
                }
            }
        } catch (e: Exception) {
            // Default to English if identification fails
            TranslateLanguage.ENGLISH
        }
    }

    private fun mapLanguageCode(code: String): String {
        return when (code) {
            "es" -> TranslateLanguage.SPANISH
            "fr" -> TranslateLanguage.FRENCH
            "de" -> TranslateLanguage.GERMAN
            "it" -> TranslateLanguage.ITALIAN
            "pt" -> TranslateLanguage.PORTUGUESE
            "ru" -> TranslateLanguage.RUSSIAN
            "zh" -> TranslateLanguage.CHINESE
            "ja" -> TranslateLanguage.JAPANESE
            "ko" -> TranslateLanguage.KOREAN
            "hi" -> TranslateLanguage.HINDI
            else -> TranslateLanguage.ENGLISH
        }
    }

    fun cleanup() {
        translators.values.forEach { it.close() }
        translators.clear()
        languageIdentifier.close()
    }
}
