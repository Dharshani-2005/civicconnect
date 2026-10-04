package com.example.service

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.data.model.RTIRequest
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGeneratorService {

    fun generateRTIPdf(context: Context, request: RTIRequest): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 page size
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply {
            isAntiAlias = true
            textSize = 12f
            color = Color.BLACK
        }

        val titlePaint = Paint().apply {
            isAntiAlias = true
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.rgb(30, 41, 59)
        }

        val headerPaint = Paint().apply {
            isAntiAlias = true
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.rgb(29, 78, 216)
        }

        var y = 50f
        val margin = 50f

        // Top Header
        canvas.drawText("FORM 'A' - RIGHT TO INFORMATION ACT 2005", margin, y, titlePaint)
        y += 24f
        canvas.drawText("APPLICATION FOR SEEKING INFORMATION UNDER SECTION 6(1)", margin, y, headerPaint)
        y += 20f

        // Divider Line
        paint.strokeWidth = 2f
        canvas.drawLine(margin, y, 595f - margin, y, paint)
        y += 30f

        // Applicant Details Section
        canvas.drawText("To:", margin, y, headerPaint)
        y += 18f
        canvas.drawText("The Public Information Officer (PIO)", margin + 20f, y, paint)
        y += 18f
        canvas.drawText("Department of ${request.department}", margin + 20f, y, paint)
        y += 18f
        canvas.drawText("Municipal Corporation / Local Civic Authority", margin + 20f, y, paint)
        y += 35f

        // Applicant Profile
        canvas.drawText("1. Name of Applicant: ${request.citizenName}", margin, y, paint)
        y += 20f
        canvas.drawText("2. Application Reference ID: RTI-${request.id}-${request.citizenId}", margin, y, paint)
        y += 20f
        canvas.drawText("3. Date of Submission: ${request.submittedAt}", margin, y, paint)
        y += 20f
        canvas.drawText("4. Statutory Response Deadline: ${request.deadline} (30 Days)", margin, y, paint)
        y += 30f

        // Particulars of Information Required
        canvas.drawText("PARTICULARS OF INFORMATION REQUIRED:", margin, y, headerPaint)
        y += 22f

        canvas.drawText("Subject: ${request.subject}", margin, y, Paint().apply {
            isAntiAlias = true
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        })
        y += 24f

        canvas.drawText("Details of Request:", margin, y, paint)
        y += 20f

        // Word-wrap description text
        val textWidth = 595f - (margin * 2)
        val lines = wrapText(request.description, paint, textWidth)
        for (line in lines) {
            canvas.drawText(line, margin + 10f, y, paint)
            y += 18f
            if (y > 720f) break // Avoid page overflow
        }

        y += 30f
        // Declaration
        canvas.drawText("5. Declaration:", margin, y, headerPaint)
        y += 20f
        canvas.drawText("I hereby declare that I am a citizen of India and the information sought falls", margin + 10f, y, paint)
        y += 18f
        canvas.drawText("under the purview of the Right to Information Act 2005.", margin + 10f, y, paint)

        // Bottom Signature Box
        y += 50f
        canvas.drawText("Applicant Signature / Authorization", 350f, y, headerPaint)
        y += 40f
        canvas.drawText("[Digitally Signed via CivicConnect Platform]", 320f, y, paint)

        pdfDocument.finishPage(page)

        // Save PDF File
        val pdfDir = File(context.filesDir, "rti-pdfs")
        if (!pdfDir.exists()) pdfDir.mkdirs()

        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val fileName = "RTI_Application_${request.id}_${sdf.format(Date())}.pdf"
        val pdfFile = File(pdfDir, fileName)

        val outputStream = FileOutputStream(pdfFile)
        pdfDocument.writeTo(outputStream)
        outputStream.close()
        pdfDocument.close()

        // Also copy to public Downloads folder if accessible
        try {
            val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
            if (downloadsDir != null) {
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val publicFile = File(downloadsDir, "RTI_Application_${request.id}.pdf")
                pdfFile.copyTo(publicFile, overwrite = true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return pdfFile
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val width = paint.measureText(testLine)
            if (width <= maxWidth) {
                currentLine = testLine
            } else {
                lines.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }
        return lines
    }
}
