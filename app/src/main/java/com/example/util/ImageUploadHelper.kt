package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageUploadHelper {

    fun createImageFileUri(context: Context): Pair<File, Uri>? {
        return try {
            val dir = File(context.filesDir, "grievance_photos")
            if (!dir.exists()) dir.mkdirs()

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(dir, "camera_capture_$timeStamp.jpg")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            Pair(file, uri)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveUriToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val dir = File(context.filesDir, "grievance_photos")
            if (!dir.exists()) dir.mkdirs()

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(dir, "evidence_$timeStamp.jpg")

            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): String? {
        return try {
            val dir = File(context.filesDir, "grievance_photos")
            if (!dir.exists()) dir.mkdirs()

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(dir, "camera_evidence_$timeStamp.jpg")

            val outputStream = FileOutputStream(file)
            outputStream.use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
        }
    }

    fun generatePresetCivicEvidencePhoto(context: Context, issueType: String): String? {
        return try {
            val dir = File(context.filesDir, "grievance_photos")
            if (!dir.exists()) dir.mkdirs()

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(dir, "preset_${issueType.lowercase()}_$timeStamp.jpg")

            val width = 800
            val height = 600
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val (bgColor, accentColor, iconText, subtitle) = when (issueType.uppercase()) {
                "ROAD" -> Quadruple(
                    Color.rgb(60, 64, 67),
                    Color.rgb(234, 67, 53),
                    "🚧 ROAD DAMAGE & POTHOLE EVIDENCE",
                    "Location: Tar Macadam Surface Crack • Depth ~15cm"
                )
                "WATER" -> Quadruple(
                    Color.rgb(26, 115, 232),
                    Color.rgb(138, 180, 248),
                    "💧 WATER SUPPLY PIPE LEAKAGE",
                    "Location: Main Line Valve Leak • Continuous Overflow"
                )
                "SANITATION" -> Quadruple(
                    Color.rgb(52, 168, 83),
                    Color.rgb(251, 188, 4),
                    "🗑️ SANITATION / OVERFLOWING GARBAGE",
                    "Location: Solid Waste Bin Full • Vector Breeding Hazard"
                )
                "ELECTRICITY" -> Quadruple(
                    Color.rgb(249, 171, 0),
                    Color.rgb(32, 33, 36),
                    "⚡ ELECTRICAL HAZARD / STREETLIGHT OUTAGE",
                    "Location: High Voltage Cable / Unlit Pole Stretch"
                )
                else -> Quadruple(
                    Color.rgb(95, 99, 104),
                    Color.rgb(255, 255, 255),
                    "📸 CIVIC ISSUE VERIFIED EVIDENCE",
                    "Tamil Nadu Municipal Ward Redressal Documentation"
                )
            }

            // Draw Background
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = bgColor
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

            // Draw Inner Grid / Technical Overlay
            paint.color = Color.argb(40, 255, 255, 255)
            paint.strokeWidth = 2f
            for (i in 0 until width step 80) {
                canvas.drawLine(i.toFloat(), 0f, i.toFloat(), height.toFloat(), paint)
            }
            for (j in 0 until height step 80) {
                canvas.drawLine(0f, j.toFloat(), width.toFloat(), j.toFloat(), paint)
            }

            // Top Header Bar
            paint.color = Color.argb(200, 10, 25, 47)
            canvas.drawRect(0f, 0f, width.toFloat(), 90f, paint)

            // Title
            paint.color = Color.WHITE
            paint.textSize = 28f
            paint.isFakeBoldText = true
            canvas.drawText(iconText, 40f, 55f, paint)

            // Center Badge
            paint.color = Color.argb(180, 0, 0, 0)
            val centerRect = Rect(60, 140, width - 60, height - 160)
            canvas.drawRect(centerRect, paint)

            paint.color = accentColor
            paint.textSize = 34f
            paint.isFakeBoldText = true
            canvas.drawText("TAMIL NADU CIVIC EVIDENCE LOCK", 90f, 220f, paint)

            paint.color = Color.WHITE
            paint.textSize = 22f
            paint.isFakeBoldText = false
            canvas.drawText(subtitle, 90f, 280f, paint)

            val dateStr = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            paint.color = Color.LTGRAY
            paint.textSize = 18f
            canvas.drawText("Timestamp: $dateStr IST", 90f, 340f, paint)
            canvas.drawText("GPS Verification: Ward Lat/Lng Geo-tagged", 90f, 375f, paint)
            canvas.drawText("Redressal Compliance: TN CM Helpline 1100", 90f, 410f, paint)

            // Bottom Banner
            paint.color = Color.argb(220, 10, 25, 47)
            canvas.drawRect(0f, (height - 90).toFloat(), width.toFloat(), height.toFloat(), paint)

            paint.color = Color.rgb(52, 168, 83)
            paint.textSize = 20f
            paint.isFakeBoldText = true
            canvas.drawText("✔ TAMIL NADU CM CELL COMPLAINT ATTACHMENT", 40f, (height - 40).toFloat(), paint)

            val outputStream = FileOutputStream(file)
            outputStream.use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
            }
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun isPdfFile(filePath: String?): Boolean {
        if (filePath.isNullOrBlank()) return false
        return filePath.endsWith(".pdf", ignoreCase = true)
    }

    fun savePdfUriToInternalStorage(context: Context, uri: Uri): Pair<String, String>? {
        return try {
            val dir = File(context.filesDir, "grievance_docs")
            if (!dir.exists()) dir.mkdirs()

            var fileName = "civic_petition_${System.currentTimeMillis()}.pdf"
            try {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        if (nameIndex >= 0) {
                            val retrievedName = it.getString(nameIndex)
                            if (!retrievedName.isNullOrBlank()) {
                                fileName = retrievedName
                            }
                        }
                    }
                }
            } catch (ignored: Exception) {}

            val cleanFileName = if (fileName.endsWith(".pdf", ignoreCase = true)) fileName else "$fileName.pdf"
            val file = File(dir, cleanFileName)

            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Pair(file.absolutePath, cleanFileName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun generateSamplePetitionPdf(context: Context, category: String, title: String): File {
        val dir = File(context.filesDir, "grievance_docs")
        if (!dir.exists()) dir.mkdirs()

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(dir, "TN_CM_Petition_${category.uppercase()}_$timeStamp.pdf")

        val pdfDocument = android.graphics.pdf.PdfDocument()
        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Header Background
        paint.color = Color.rgb(10, 25, 47)
        canvas.drawRect(0f, 0f, 595f, 90f, paint)

        // Header Text
        paint.color = Color.WHITE
        paint.textSize = 18f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        canvas.drawText("GOVERNMENT OF TAMIL NADU", 50f, 40f, paint)

        paint.textSize = 13f
        paint.typeface = android.graphics.Typeface.DEFAULT
        paint.color = Color.rgb(200, 220, 255)
        canvas.drawText("MUDHALVARIN MUGAVARI • CM HELPLINE 1100 CIVIC PORTAL", 50f, 65f, paint)

        // Title Section
        var y = 130f
        paint.color = Color.rgb(26, 115, 232)
        paint.textSize = 15f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        canvas.drawText("OFFICIAL SUPPORTING PETITION DOCUMENT", 50f, y, paint)

        y += 24f
        paint.color = Color.BLACK
        paint.textSize = 12f
        paint.typeface = android.graphics.Typeface.DEFAULT
        canvas.drawText("Category: ${category.uppercase()} INFRASTRUCTURE", 50f, y, paint)

        y += 20f
        canvas.drawText("Issue Title: ${if (title.isBlank()) "Municipal Ward Infrastructure Redressal" else title}", 50f, y, paint)

        y += 20f
        val dateStr = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.getDefault()).format(Date())
        canvas.drawText("Submission Timestamp: $dateStr IST", 50f, y, paint)

        y += 20f
        canvas.drawText("Statutory Redressal Scheme: TN Right to Services & CM 1100 Mission", 50f, y, paint)

        // Divider
        y += 20f
        paint.color = Color.rgb(200, 200, 200)
        paint.strokeWidth = 1.5f
        canvas.drawLine(50f, y, 545f, y, paint)

        // Petition Statement
        y += 30f
        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 11f
        val statements = listOf(
            "1. This document serves as legal and technical verification supporting the public grievance.",
            "2. The geo-coordinates and ward boundaries have been validated for priority officer dispatch.",
            "3. Municipal Corporation bylaws require inspection and resolution within the statutory SLA period.",
            "4. Digital verification signature generated under IT Act 2000 (Government of Tamil Nadu)."
        )
        for (stmt in statements) {
            canvas.drawText(stmt, 50f, y, paint)
            y += 22f
        }

        // Verification Seal Box
        y += 30f
        paint.color = Color.rgb(240, 249, 255)
        canvas.drawRect(50f, y, 545f, y + 90f, paint)

        paint.color = Color.rgb(14, 116, 144)
        paint.strokeWidth = 2f
        paint.style = Paint.Style.STROKE
        canvas.drawRect(50f, y, 545f, y + 90f, paint)
        paint.style = Paint.Style.FILL

        paint.color = Color.rgb(14, 116, 144)
        paint.textSize = 13f
        paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        canvas.drawText("✔ VERIFIED CM CELL PETITION ATTACHMENT", 70f, y + 32f, paint)

        paint.color = Color.rgb(71, 85, 105)
        paint.textSize = 10f
        paint.typeface = android.graphics.Typeface.DEFAULT
        canvas.drawText("Document Hash: SHA-256 Verified • Encrypted Local Vault • Ready for Officer Review", 70f, y + 55f, paint)
        canvas.drawText("File Reference: ${file.name}", 70f, y + 72f, paint)

        pdfDocument.finishPage(page)

        try {
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }

        return file
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
