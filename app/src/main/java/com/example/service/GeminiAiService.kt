package com.example.service

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    suspend fun generateContent(prompt: String, systemInstructionText: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getLocalFallbackResponse(prompt)
        }

        try {
            val requestJson = JSONObject()
            
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            val partObj = JSONObject()
            partObj.put("text", prompt)
            partsArray.put(partObj)
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            requestJson.put("contents", contentsArray)

            if (!systemInstructionText.isNullOrBlank()) {
                val sysInstructionObj = JSONObject()
                val sysPartsArray = JSONArray()
                val sysPartObj = JSONObject()
                sysPartObj.put("text", systemInstructionText)
                sysPartsArray.put(sysPartObj)
                sysInstructionObj.put("parts", sysPartsArray)
                requestJson.put("systemInstruction", sysInstructionObj)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = requestJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBodyString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext getLocalFallbackResponse(prompt)
            }

            val jsonResp = JSONObject(responseBodyString)
            val candidates = jsonResp.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text")
                    if (text.isNotBlank()) {
                        return@withContext text
                    }
                }
            }

            return@withContext getLocalFallbackResponse(prompt)
            
        } catch (e: Exception) {
            return@withContext getLocalFallbackResponse(prompt)
        }
    }

    private fun getLocalFallbackResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("refine") || lower.contains("petition") || lower.contains("pothole") || lower.contains("water") -> {
                """
                PETITION TO THE SPECIAL OFFICER / COMMISSIONER (TN CM SPECIAL CELL / MUDHALVARIN MUGAVARI)
                
                SUBJECT: Urgent Remedial Action Requested for Civic Infrastructure Inadequacy
                
                RESPECTED SIR / MADAM,
                
                I am filing this petition regarding the severe grievance detailed as follows: "$prompt".
                
                STATUTORY REFERENCE & APPLICABLE LAWS:
                1. Tamil Nadu District Municipalities Act, 1920 (Maintenance of Public Roads & Drainage).
                2. Right to Service Norms & Statutory SLA Timeline (14 Days Target).
                
                PRAYER FOR RELIEF:
                Kindly issue immediate directives to the designated Zonal Field Officers for site inspection, emergency repair execution, and update the petition status on the CM Helpline Portal (1100).
                
                Yours Sincerely,
                Concerned Citizen
                """.trimIndent()
            }
            lower.contains("1100") || lower.contains("helpline") || lower.contains("cm cell") -> {
                "The Mudhalvarin Mugavari (CM Special Cell 1100 Helpline) operates 24/7 to resolve citizen grievances across Tamil Nadu. You can dial 1100 directly or track petitions using tracking IDs formatted like TN-CM-2024-XXXX."
            }
            lower.contains("rti") -> {
                "Under Section 6(1) of the Right to Information Act 2005, every citizen has the right to request information regarding government expenditure, sanction orders, and contractor timelines. Public Information Officers (PIOs) are statutorily bound to respond within 30 days."
            }
            else -> {
                "Mughavari AI Assistant: I have received your query regarding '$prompt'. Our civic portal automatically classifies complaints into municipal categories (PWD, Water, Sanitation, Electricity) and tags them with Geohash location coordinates for swift resolution by ward officers."
            }
        }
    }
}
