package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.repository.StudentReportItem
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportManager {

    private fun escapeXml(str: String): String {
        return str
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    private fun appendMarkCell(sb: StringBuilder, mark: Double?) {
        if (mark != null) {
            val rounded = if (mark % 1.0 == 0.0) mark.toInt().toString() else String.format(Locale.US, "%.2f", mark)
            sb.append("    <Cell ss:StyleID=\"DataCellCenter\"><Data ss:Type=\"Number\">$rounded</Data></Cell>\n")
        } else {
            sb.append("    <Cell ss:StyleID=\"DataCellCenter\"><Data ss:Type=\"String\">-</Data></Cell>\n")
        }
    }

    fun generateExcelXml(
        items: List<StudentReportItem>,
        departmentName: String,
        semesterName: String,
        subjectName: String
    ): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        sb.append("<?mso-application progid=\"Excel.Sheet\"?>\n")
        sb.append("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n")
        sb.append(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n")
        sb.append(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n")
        sb.append(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n")
        sb.append(" xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n")

        // Styles
        sb.append(" <Styles>\n")
        sb.append("  <Style ss:ID=\"Default\" ss:Name=\"Normal\">\n")
        sb.append("   <Alignment ss:Vertical=\"Center\"/>\n")
        sb.append("   <Font ss:FontName=\"Calibri\" x:Family=\"Swiss\" ss:Size=\"11\" ss:Color=\"#000000\"/>\n")
        sb.append("  </Style>\n")
        sb.append("  <Style ss:ID=\"TitleStyle\">\n")
        sb.append("   <Font ss:FontName=\"Calibri\" ss:Size=\"16\" ss:Bold=\"1\" ss:Color=\"#0F2042\"/>\n")
        sb.append("   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>\n")
        sb.append("  </Style>\n")
        sb.append("  <Style ss:ID=\"SubHeader\">\n")
        sb.append("   <Font ss:FontName=\"Calibri\" ss:Size=\"11\" ss:Bold=\"1\" ss:Color=\"#333333\"/>\n")
        sb.append("   <Interior ss:Color=\"#F0F4F8\" ss:Pattern=\"Solid\"/>\n")
        sb.append("  </Style>\n")
        sb.append("  <Style ss:ID=\"TableHeader\">\n")
        sb.append("   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>\n")
        sb.append("   <Font ss:FontName=\"Calibri\" ss:Size=\"11\" ss:Bold=\"1\" ss:Color=\"#FFFFFF\"/>\n")
        sb.append("   <Interior ss:Color=\"#0F2042\" ss:Pattern=\"Solid\"/>\n")
        sb.append("  </Style>\n")
        sb.append("  <Style ss:ID=\"DataCellCenter\">\n")
        sb.append("   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>\n")
        sb.append("  </Style>\n")
        sb.append("  <Style ss:ID=\"DataCellLeft\">\n")
        sb.append("   <Alignment ss:Horizontal=\"Left\" ss:Vertical=\"Center\"/>\n")
        sb.append("  </Style>\n")
        sb.append("  <Style ss:ID=\"GreenPercent\">\n")
        sb.append("   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>\n")
        sb.append("   <Font ss:FontName=\"Calibri\" ss:Size=\"11\" ss:Bold=\"1\" ss:Color=\"#1B873F\"/>\n")
        sb.append("  </Style>\n")
        sb.append("  <Style ss:ID=\"RedPercent\">\n")
        sb.append("   <Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Center\"/>\n")
        sb.append("   <Font ss:FontName=\"Calibri\" ss:Size=\"11\" ss:Bold=\"1\" ss:Color=\"#C53030\"/>\n")
        sb.append("  </Style>\n")
        sb.append(" </Styles>\n")

        // Worksheet
        sb.append(" <Worksheet ss:Name=\"Report\">\n")
        sb.append("  <Table ss:DefaultRowHeight=\"20\">\n")
        sb.append("   <Column ss:Width=\"50\"/>\n")
        sb.append("   <Column ss:Width=\"150\"/>\n")
        sb.append("   <Column ss:Width=\"100\"/>\n")
        sb.append("   <Column ss:Width=\"90\"/>\n")
        sb.append("   <Column ss:Width=\"70\"/>\n")
        sb.append("   <Column ss:Width=\"70\"/>\n")
        sb.append("   <Column ss:Width=\"90\"/>\n")
        sb.append("   <Column ss:Width=\"60\"/>\n")
        sb.append("   <Column ss:Width=\"60\"/>\n")
        sb.append("   <Column ss:Width=\"60\"/>\n")
        sb.append("   <Column ss:Width=\"70\"/>\n")
        sb.append("   <Column ss:Width=\"70\"/>\n")

        // Title row
        sb.append("   <Row ss:Height=\"30\">\n")
        sb.append("    <Cell ss:MergeAcross=\"11\" ss:StyleID=\"TitleStyle\"><Data ss:Type=\"String\">Teacher Student Manager - Academic Report</Data></Cell>\n")
        sb.append("   </Row>\n")

        // Metadata rows
        val genDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
        sb.append("   <Row>\n")
        sb.append("    <Cell ss:StyleID=\"SubHeader\"><Data ss:Type=\"String\">Department:</Data></Cell>\n")
        sb.append("    <Cell ss:MergeAcross=\"2\"><Data ss:Type=\"String\">${escapeXml(departmentName)}</Data></Cell>\n")
        sb.append("    <Cell ss:StyleID=\"SubHeader\"><Data ss:Type=\"String\">Semester:</Data></Cell>\n")
        sb.append("    <Cell ss:MergeAcross=\"2\"><Data ss:Type=\"String\">${escapeXml(semesterName)}</Data></Cell>\n")
        sb.append("    <Cell ss:StyleID=\"SubHeader\"><Data ss:Type=\"String\">Date:</Data></Cell>\n")
        sb.append("    <Cell ss:MergeAcross=\"2\"><Data ss:Type=\"String\">$genDate</Data></Cell>\n")
        sb.append("   </Row>\n")

        sb.append("   <Row>\n")
        sb.append("    <Cell ss:StyleID=\"SubHeader\"><Data ss:Type=\"String\">Subject:</Data></Cell>\n")
        sb.append("    <Cell ss:MergeAcross=\"10\"><Data ss:Type=\"String\">${escapeXml(subjectName)}</Data></Cell>\n")
        sb.append("   </Row>\n")

        sb.append("   <Row ss:Height=\"10\"></Row>\n")

        // Table Header
        sb.append("   <Row ss:Height=\"24\">\n")
        val headers = listOf("Roll", "Student Name", "Reg No", "Total Classes", "Present", "Absent", "Attendance %", "CT 1", "CT 2", "CT 3", "CT Avg", "Mid-Term")
        for (h in headers) {
            sb.append("    <Cell ss:StyleID=\"TableHeader\"><Data ss:Type=\"String\">$h</Data></Cell>\n")
        }
        sb.append("   </Row>\n")

        // Rows
        for (item in items) {
            val pct = item.attendancePercentage
            val pctStyle = if (pct >= 75.0) "GreenPercent" else "RedPercent"
            val pctStr = String.format(Locale.US, "%.1f%%", pct)
            val reg = item.student.registrationNumber ?: "-"

            sb.append("   <Row>\n")
            sb.append("    <Cell ss:StyleID=\"DataCellCenter\"><Data ss:Type=\"String\">${escapeXml(item.student.rollNumber)}</Data></Cell>\n")
            sb.append("    <Cell ss:StyleID=\"DataCellLeft\"><Data ss:Type=\"String\">${escapeXml(item.student.studentName)}</Data></Cell>\n")
            sb.append("    <Cell ss:StyleID=\"DataCellCenter\"><Data ss:Type=\"String\">${escapeXml(reg)}</Data></Cell>\n")
            sb.append("    <Cell ss:StyleID=\"DataCellCenter\"><Data ss:Type=\"Number\">${item.totalClasses}</Data></Cell>\n")
            sb.append("    <Cell ss:StyleID=\"DataCellCenter\"><Data ss:Type=\"Number\">${item.presentCount}</Data></Cell>\n")
            sb.append("    <Cell ss:StyleID=\"DataCellCenter\"><Data ss:Type=\"Number\">${item.absentCount}</Data></Cell>\n")
            sb.append("    <Cell ss:StyleID=\"$pctStyle\"><Data ss:Type=\"String\">$pctStr</Data></Cell>\n")

            appendMarkCell(sb, item.ct1)
            appendMarkCell(sb, item.ct2)
            appendMarkCell(sb, item.ct3)
            appendMarkCell(sb, item.ctAverage)
            appendMarkCell(sb, item.midTerm)

            sb.append("   </Row>\n")
        }

        // Footer
        sb.append("  </Table>\n")
        sb.append(" </Worksheet>\n")
        sb.append("</Workbook>\n")
        return sb.toString()
    }

    /**
     * Exports and shares an Excel (.xls) file with system apps.
     */
    fun exportAndShareExcel(
        context: Context,
        items: List<StudentReportItem>,
        departmentName: String,
        semesterName: String,
        subjectName: String
    ) {
        val excelContent = generateExcelXml(items, departmentName, semesterName, subjectName)
        val reportsDir = File(context.cacheDir, "reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()

        val safeSubject = subjectName.replace(Regex("[^a-zA-Z0-9_]"), "_")
        val fileName = "Report_${safeSubject}_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())}.xls"
        val file = File(reportsDir, fileName)

        FileOutputStream(file).use { out ->
            out.write(excelContent.toByteArray(Charsets.UTF_8))
        }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.ms-excel"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Report - $departmentName - $semesterName - $subjectName")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(sendIntent, "Export Report as Excel (.xls)")
        context.startActivity(chooser)
    }

    /**
     * Generates a multi-page PDF document using native Android PdfDocument API.
     */
    fun exportAndSharePdf(
        context: Context,
        items: List<StudentReportItem>,
        departmentName: String,
        semesterName: String,
        subjectName: String
    ) {
        val document = PdfDocument()

        val pageWidth = 842
        val pageHeight = 595
        var pageNumber = 1

        val paint = Paint().apply { isAntiAlias = true }
        val titlePaint = Paint().apply {
            isAntiAlias = true
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.rgb(15, 32, 66)
        }
        val subTitlePaint = Paint().apply {
            isAntiAlias = true
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            color = Color.rgb(80, 80, 80)
        }
        val headerPaint = Paint().apply {
            isAntiAlias = true
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.WHITE
        }
        val rowTextPaint = Paint().apply {
            isAntiAlias = true
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            color = Color.rgb(30, 30, 30)
        }
        val boldRowTextPaint = Paint().apply {
            isAntiAlias = true
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.rgb(15, 32, 66)
        }
        val greenTextPaint = Paint().apply {
            isAntiAlias = true
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.rgb(27, 135, 63)
        }
        val redTextPaint = Paint().apply {
            isAntiAlias = true
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = Color.rgb(197, 48, 48)
        }

        val rowsPerPage = 14
        val totalPages = if (items.isEmpty()) 1 else ((items.size + rowsPerPage - 1) / rowsPerPage)

        for (p in 0 until totalPages) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            val page = document.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            // Background
            canvas.drawColor(Color.WHITE)

            // Header banner line
            paint.color = Color.rgb(15, 32, 66)
            canvas.drawRect(30f, 25f, (pageWidth - 30).toFloat(), 28f, paint)

            // Title
            canvas.drawText("TEACHER STUDENT MANAGER - ACADEMIC REPORT", 30f, 48f, titlePaint)

            // Subtitle info
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
            canvas.drawText("Department: $departmentName   |   Semester: $semesterName   |   Subject: $subjectName", 30f, 66f, subTitlePaint)
            canvas.drawText("Generated on: $dateStr   |   Page $pageNumber of $totalPages", (pageWidth - 250).toFloat(), 66f, subTitlePaint)

            // Table layout constants
            val startY = 82f
            val tableLeft = 30f
            val tableRight = (pageWidth - 30).toFloat()
            val headerHeight = 26f
            val rowHeight = 24f

            val colX = floatArrayOf(
                30f,   // 0: Roll (width: 45)
                75f,   // 1: Name (width: 155)
                230f,  // 2: Reg (width: 95)
                325f,  // 3: Classes (width: 55)
                380f,  // 4: Present (width: 55)
                435f,  // 5: Absent (width: 55)
                490f,  // 6: Att % (width: 65)
                555f,  // 7: CT1 (width: 50)
                605f,  // 8: CT2 (width: 50)
                655f,  // 9: CT3 (width: 50)
                705f,  // 10: CT Avg (width: 55)
                760f,  // 11: Mid-Term (width: 52)
                tableRight // 12: End
            )

            // Header Background
            paint.color = Color.rgb(15, 32, 66)
            canvas.drawRect(tableLeft, startY, tableRight, startY + headerHeight, paint)

            // Header Labels
            val headers = listOf("Roll", "Student Name", "Reg No", "Total", "Pres", "Abs", "Att %", "CT 1", "CT 2", "CT 3", "CT Avg", "Mid-Term")
            for (i in headers.indices) {
                val text = headers[i]
                val colWidth = colX[i + 1] - colX[i]
                val textWidth = headerPaint.measureText(text)
                val xPos = if (i == 1) colX[i] + 8f else colX[i] + (colWidth - textWidth) / 2f
                canvas.drawText(text, xPos, startY + 17f, headerPaint)
            }

            // Draw student rows
            var currentY = startY + headerHeight
            val startIdx = p * rowsPerPage
            val endIdx = minOf(startIdx + rowsPerPage, items.size)

            for (idx in startIdx until endIdx) {
                val item = items[idx]
                val isAlt = (idx % 2 == 1)

                // Row background
                paint.color = if (isAlt) Color.rgb(245, 247, 250) else Color.WHITE
                canvas.drawRect(tableLeft, currentY, tableRight, currentY + rowHeight, paint)

                // Divider line
                paint.color = Color.rgb(225, 230, 235)
                paint.strokeWidth = 0.8f
                canvas.drawLine(tableLeft, currentY + rowHeight, tableRight, currentY + rowHeight, paint)

                // Values
                val pct = item.attendancePercentage
                val pctStr = String.format(Locale.US, "%.1f%%", pct)
                val pctPaint = if (pct >= 75.0) greenTextPaint else redTextPaint

                // Roll
                val rollW = boldRowTextPaint.measureText(item.student.rollNumber)
                canvas.drawText(item.student.rollNumber, colX[0] + ((colX[1] - colX[0]) - rollW) / 2f, currentY + 16f, boldRowTextPaint)

                // Name
                val maxNameW = (colX[2] - colX[1]) - 16f
                var nameText = item.student.studentName
                if (rowTextPaint.measureText(nameText) > maxNameW) {
                    nameText = nameText.take(18) + ".."
                }
                canvas.drawText(nameText, colX[1] + 8f, currentY + 16f, boldRowTextPaint)

                // Reg
                val regText = item.student.registrationNumber ?: "-"
                val regW = rowTextPaint.measureText(regText)
                canvas.drawText(regText, colX[2] + ((colX[3] - colX[2]) - regW) / 2f, currentY + 16f, rowTextPaint)

                // Classes
                val totalW = rowTextPaint.measureText("${item.totalClasses}")
                canvas.drawText("${item.totalClasses}", colX[3] + ((colX[4] - colX[3]) - totalW) / 2f, currentY + 16f, rowTextPaint)

                // Present
                val presW = greenTextPaint.measureText("${item.presentCount}")
                canvas.drawText("${item.presentCount}", colX[4] + ((colX[5] - colX[4]) - presW) / 2f, currentY + 16f, greenTextPaint)

                // Absent
                val absW = redTextPaint.measureText("${item.absentCount}")
                canvas.drawText("${item.absentCount}", colX[5] + ((colX[6] - colX[5]) - absW) / 2f, currentY + 16f, redTextPaint)

                // Att %
                val pctW = pctPaint.measureText(pctStr)
                canvas.drawText(pctStr, colX[6] + ((colX[7] - colX[6]) - pctW) / 2f, currentY + 16f, pctPaint)

                // Marks
                fun drawMark(colIdx: Int, mark: Double?) {
                    val text = if (mark != null) {
                        if (mark % 1.0 == 0.0) mark.toInt().toString() else String.format(Locale.US, "%.1f", mark)
                    } else "-"
                    val textW = rowTextPaint.measureText(text)
                    val x = colX[colIdx] + ((colX[colIdx + 1] - colX[colIdx]) - textW) / 2f
                    canvas.drawText(text, x, currentY + 16f, rowTextPaint)
                }

                drawMark(7, item.ct1)
                drawMark(8, item.ct2)
                drawMark(9, item.ct3)
                drawMark(10, item.ctAverage)
                drawMark(11, item.midTerm)

                currentY += rowHeight
            }

            // Outer border
            paint.style = Paint.Style.STROKE
            paint.color = Color.rgb(200, 205, 215)
            paint.strokeWidth = 1f
            canvas.drawRect(tableLeft, startY, tableRight, currentY, paint)
            paint.style = Paint.Style.FILL

            // Footer credit
            canvas.drawText("Teacher Student Manager • Developed by Khaled Hasan Shihab", 30f, pageHeight - 20f, subTitlePaint)
            val teacherSign = "Teacher's Signature: _______________________"
            val signW = subTitlePaint.measureText(teacherSign)
            canvas.drawText(teacherSign, tableRight - signW, pageHeight - 20f, subTitlePaint)

            document.finishPage(page)
            pageNumber++
        }

        val reportsDir = File(context.cacheDir, "reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()

        val safeSubject = subjectName.replace(Regex("[^a-zA-Z0-9_]"), "_")
        val fileName = "Report_${safeSubject}_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())}.pdf"
        val file = File(reportsDir, fileName)

        FileOutputStream(file).use { out ->
            document.writeTo(out)
        }
        document.close()

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Report PDF - $departmentName - $semesterName - $subjectName")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(sendIntent, "Export Report as PDF")
        context.startActivity(chooser)
    }
}
