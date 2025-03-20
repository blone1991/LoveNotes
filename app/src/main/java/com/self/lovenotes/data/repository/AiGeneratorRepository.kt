package com.self.lovenotes.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.self.lovenotes.BuildConfig
import javax.inject.Inject

class AiGeneratorRepository @Inject constructor() {
    private val generativeModel = GenerativeModel(
        // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
        modelName = "gemini-1.5-flash",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun sendPrompt (promt: String) : String {
        return generativeModel.generateContent(promt).text?: "Sorry, but I have no idea."
    }

}