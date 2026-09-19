package com.example.util

import android.content.Context
import android.net.Uri
import com.example.data.model.Student
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

data class ParsedStudent(
    val rollNumber: String,
    val studentName: String,
    val registrationNumber: String? = null
)

data class ImportResult(
    val parsedStudents: List<ParsedStudent>,
    val errors: List<String>
)

object StudentImportParser {

    /**
     * Parses a CSV, TSV, or XML Spreadsheet (Excel 2003 XML) or raw text file from an input stream.
     */
    fun parseStream(inputStream: InputStream): ImportResult {
        val parsed = mutableListOf<ParsedStudent>()
        val errors = mutableListOf<String>()

        try {
            val content = inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
            if (content.trim().startsWith("<?xml") || content.contains("<Workbook") || content.contains("<ss:Workbook")) {
                // Parse Excel XML Spreadsheet
                parseExcelXml(content, parsed, errors)
            } else {
                // Parse CSV / TSV / Delimited text
                parseDelimitedText(content, parsed, errors)
            }
        } catch (e: Exception) {
            errors.add("Failed to read file: ${e.localizedMessage ?: "Unknown error"}")
        }

        return ImportResult(parsed, errors)
    }

    /**
     * Parses Excel XML Spreadsheet format (<Row><Cell><Data>...)
     */
    private fun parseExcelXml(content: String, parsed: MutableList<ParsedStudent>, errors: MutableList<String>) {
        val rowRegex = Pattern.compile("<(?:ss:)?Row[^>]*>(.*?)</(?:ss:)?Row>", Pattern.DOTALL or Pattern.CASE_INSENSITIVE)
        val cellRegex = Pattern.compile("<(?:ss:)?Data[^>]*>(.*?)</(?:ss:)?Data>", Pattern.DOTALL or Pattern.CASE_INSENSITIVE)

        val rowMatcher = rowRegex.matcher(content)
        var rowIndex = 0

        var rollCol = 0
        var nameCol = 1
        var regCol = 2

        while (rowMatcher.find()) {
            rowIndex++
            val rowContent = rowMatcher.group(1) ?: continue
            val cellMatcher = cellRegex.matcher(rowContent)
            val rowValues = mutableListOf<String>()

            while (cellMatcher.find()) {
                val value = cellMatcher.group(1)
                    ?.replace("&lt;", "<")
                    ?.replace("&gt;", ">")
                    ?.replace("&amp;", "&")
                    ?.replace("&quot;", "\"")
                    ?.replace("&apos;", "'")
                    ?.trim() ?: ""
                rowValues.add(value)
            }

            if (rowValues.isEmpty()) continue

            // Check if header row
            if (rowIndex == 1 && isHeaderRow(rowValues)) {
                // Detect column positions
                rowValues.forEachIndexed { index, col ->
                    val lower = col.lowercase()
                    if (lower.contains("roll")) rollCol = index
                    else if (lower.contains("name")) nameCol = index
                    else if (lower.contains("reg")) regCol = index
                }
                continue
            }

            // Extract student
            val roll = rowValues.getOrNull(rollCol)?.trim() ?: ""
            val name = rowValues.getOrNull(nameCol)?.trim() ?: ""
            val reg = rowValues.getOrNull(regCol)?.trim()?.ifEmpty { null }

            if (roll.isNotEmpty() && name.isNotEmpty()) {
                parsed.add(ParsedStudent(rollNumber = roll, studentName = name, registrationNumber = reg))
            } else if (roll.isNotEmpty() || name.isNotEmpty()) {
                errors.add("Row $rowIndex: Missing name or roll number ($rowValues)")
            }
        }
    }

    /**
     * Parses standard CSV or TSV lines
     */
    private fun parseDelimitedText(content: String, parsed: MutableList<ParsedStudent>, errors: MutableList<String>) {
        val lines = content.lines()
        var rollCol = 0
        var nameCol = 1
        var regCol = 2

        var firstValidRow = true

        for ((index, rawLine) in lines.withIndex()) {
            val line = rawLine.trim()
            if (line.isEmpty()) continue

            // Determine delimiter: comma, tab, or semicolon
            val delimiter = when {
                line.contains("\t") -> "\t"
                line.contains(";") -> ";"
                else -> ","
            }

            val tokens = splitCsvLine(line, delimiter)
            if (tokens.isEmpty()) continue

            if (firstValidRow && isHeaderRow(tokens)) {
                firstValidRow = false
                tokens.forEachIndexed { colIdx, colName ->
                    val lower = colName.lowercase()
                    if (lower.contains("roll")) rollCol = colIdx
                    else if (lower.contains("name")) nameCol = colIdx
                    else if (lower.contains("reg")) regCol = colIdx
                }
                continue
            }
            firstValidRow = false

            val roll = tokens.getOrNull(rollCol)?.trim() ?: ""
            val name = tokens.getOrNull(nameCol)?.trim() ?: ""
            val reg = tokens.getOrNull(regCol)?.trim()?.ifEmpty { null }

            if (roll.isNotEmpty() && name.isNotEmpty()) {
                parsed.add(ParsedStudent(rollNumber = roll, studentName = name, registrationNumber = reg))
            } else if (roll.isNotEmpty() || name.isNotEmpty()) {
                errors.add("Line ${index + 1}: Incomplete record (Roll: '$roll', Name: '$name')")
            }
        }
    }

    private fun isHeaderRow(tokens: List<String>): Boolean {
        val lower = tokens.map { it.lowercase() }
        return lower.any { it.contains("roll") } || lower.any { it.contains("name") } || lower.any { it.contains("reg") }
    }

    private fun splitCsvLine(line: String, delimiter: String): List<String> {
        val result = mutableListOf<String>()
        val sb = java.lang.StringBuilder()
        var inQuotes = false

        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (c == '\"') {
                inQuotes = !inQuotes
            } else if (!inQuotes && line.startsWith(delimiter, i)) {
                result.add(sb.toString().trim())
                sb.setLength(0)
                i += delimiter.length - 1
            } else {
                sb.append(c)
            }
            i++
        }
        result.add(sb.toString().trim())
        return result
    }

    /**
     * Generates a sample CSV template for teachers to download/share or copy
     */
    fun getSampleCsvContent(): String {
        return """Roll,Name,Registration
101,Rahim Ahmed,150201
102,Karim Hasan,150202
103,Hasan Mahmud,150203
104,Fatima Akter,150204
105,Tanvir Islam,150205
""".trimIndent()
    }
}
