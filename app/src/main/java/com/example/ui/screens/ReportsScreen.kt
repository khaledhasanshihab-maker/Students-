package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TableChart
import com.example.ui.theme.SecondaryTeal
import com.example.util.AlertManager
import com.example.util.ExportManager
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Attendance
import com.example.data.model.Student
import com.example.data.repository.StudentReportItem
import com.example.ui.TeacherViewModel
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.AbsentRedBg
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DividerColor
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.PresentGreenBg
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.TertiaryAmber

@Composable
fun ReportsScreen(
    viewModel: TeacherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val departments by viewModel.departments.collectAsStateWithLifecycle()
    val semesters by viewModel.semesters.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()

    val selectedDept by viewModel.selectedDepartment.collectAsStateWithLifecycle()
    val selectedSem by viewModel.selectedSemester.collectAsStateWithLifecycle()
    val selectedSub by viewModel.selectedSubject.collectAsStateWithLifecycle()
    val selectedClassType by viewModel.selectedAttendanceType.collectAsStateWithLifecycle()

    val students by viewModel.students.collectAsStateWithLifecycle()
    val attendanceRecords by viewModel.currentSubjectAttendance.collectAsStateWithLifecycle()
    val distinctDates by viewModel.distinctAttendanceDates.collectAsStateWithLifecycle()
    val marksList by viewModel.currentSubjectMarks.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStudentForReport by remember { mutableStateOf<StudentReportItem?>(null) }

    val marksMap = remember(marksList) {
        marksList.associateBy { it.studentId }
    }

    val totalClasses = distinctDates.size

    // Build StudentReportItem list
    val reportItems = remember(students, attendanceRecords, marksList, totalClasses, searchQuery) {
        students
            .filter {
                if (searchQuery.isBlank()) true
                else it.studentName.contains(searchQuery, ignoreCase = true) ||
                     it.rollNumber.contains(searchQuery, ignoreCase = true) ||
                     (it.registrationNumber?.contains(searchQuery, ignoreCase = true) == true)
            }
            .map { student ->
                val studentRecords = attendanceRecords.filter { it.studentId == student.id }
                val present = studentRecords.count { it.status == "P" }
                val absent = studentRecords.count { it.status == "A" }
                val pct = if (totalClasses > 0) (present.toDouble() / totalClasses.toDouble()) * 100.0 else 0.0

                val marks = marksMap[student.id]
                StudentReportItem(
                    student = student,
                    totalClasses = totalClasses,
                    presentCount = present,
                    absentCount = absent,
                    attendancePercentage = pct,
                    ct1 = marks?.ct1,
                    ct2 = marks?.ct2,
                    ct3 = marks?.ct3,
                    ctAverage = marks?.calculateCtAverage(),
                    midTerm = marks?.midTerm
                )
            }
            .sortedWith(
                compareBy<StudentReportItem> { it.student.rollNumber.toIntOrNull() ?: Int.MAX_VALUE }
                    .thenBy { it.student.rollNumber }
            )
    }

    val scrollState = rememberScrollState()

    fun shareCsv() {
        val deptName = selectedDept?.departmentName ?: "Department"
        val semName = selectedSem?.semesterName ?: "Semester"
        val subName = selectedSub?.let { "${it.subjectName} (${it.subjectCode})" } ?: "Subject"

        val csvContent = viewModel.repository.exportToCsv(reportItems, deptName, semName, subName)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, csvContent)
            putExtra(Intent.EXTRA_SUBJECT, "Report - $deptName - $semName - $subName")
            type = "text/csv"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Export Report as CSV")
        context.startActivity(shareIntent)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header selection
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        DepartmentSelectorDropdown(
                            departments = departments,
                            selectedDepartment = selectedDept,
                            onSelect = {
                                viewModel.selectedDepartment.value = it
                                viewModel.selectedSemester.value = null
                                viewModel.selectedSubject.value = null
                            }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SemesterSelectorDropdown(
                            semesters = semesters,
                            selectedSemester = selectedSem,
                            onSelect = {
                                viewModel.selectedSemester.value = it
                                viewModel.selectedSubject.value = null
                            }
                        )
                    }
                }

                SubjectSelectorDropdown(
                    subjects = subjects,
                    selectedSubject = selectedSub,
                    onSelect = {
                        viewModel.selectedSubject.value = it
                    }
                )

                // Report Class Type: Theory vs Practical
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Attendance Filter:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val isTheory = selectedClassType == "Theory"
                    val isPractical = selectedClassType == "Practical"

                    Button(
                        onClick = { viewModel.selectedAttendanceType.value = "Theory" },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("report_theory_type_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTheory) PrimaryNavy else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isTheory) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "📘 Theory",
                            fontWeight = if (isTheory) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = { viewModel.selectedAttendanceType.value = "Practical" },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("report_practical_type_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPractical) PrimaryNavy else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isPractical) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "🔬 Practical",
                            fontWeight = if (isPractical) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Divider(color = DividerColor)

            if (selectedDept == null || selectedSem == null || selectedSub == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateCard(
                        message = "Please select Department, Semester, and Subject above to generate reports.",
                        icon = Icons.Default.Assessment
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Search and Action Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search report") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("report_search_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // EXPORT ACTIONS ROW (PDF, Excel, CSV)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // PDF Export Button
                        Button(
                            onClick = {
                                val deptName = selectedDept?.departmentName ?: "Department"
                                val semName = selectedSem?.semesterName ?: "Semester"
                                val subName = selectedSub?.let { "${it.subjectName} (${it.subjectCode})" } ?: "Subject"
                                ExportManager.exportAndSharePdf(context, reportItems, deptName, semName, subName)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("export_pdf_button")
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PDF Export", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // Excel Export Button
                        Button(
                            onClick = {
                                val deptName = selectedDept?.departmentName ?: "Department"
                                val semName = selectedSem?.semesterName ?: "Semester"
                                val subName = selectedSub?.let { "${it.subjectName} (${it.subjectCode})" } ?: "Subject"
                                ExportManager.exportAndShareExcel(context, reportItems, deptName, semName, subName)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryTeal),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("export_excel_button")
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Excel (.xls)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // CSV Export Button
                        OutlinedButton(
                            onClick = { shareCsv() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(0.9f)
                                .height(46.dp)
                                .testTag("export_csv_button")
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("CSV", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Master Report ($selectedClassType Attendance)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap student for full report",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // MASTER REPORT TABLE
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("report_table_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Box(modifier = Modifier.horizontalScroll(scrollState)) {
                            LazyColumn(modifier = Modifier.padding(12.dp)) {
                                // Header
                                item {
                                    Row(
                                        modifier = Modifier
                                            .background(PrimaryNavy.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                            .padding(vertical = 10.dp, horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TableCell(text = "Roll", width = 50.dp, isHeader = true)
                                        TableCell(text = "Student Name", width = 120.dp, isHeader = true)
                                        TableCell(text = "Classes", width = 60.dp, isHeader = true)
                                        TableCell(text = "Present", width = 60.dp, isHeader = true, color = PresentGreen)
                                        TableCell(text = "Absent", width = 60.dp, isHeader = true, color = AbsentRed)
                                        TableCell(text = "Att %", width = 75.dp, isHeader = true)
                                        TableCell(text = "CT1", width = 55.dp, isHeader = true)
                                        TableCell(text = "CT2", width = 55.dp, isHeader = true)
                                        TableCell(text = "CT3", width = 55.dp, isHeader = true)
                                        TableCell(text = "CT Avg", width = 75.dp, isHeader = true, color = SecondaryTeal)
                                        TableCell(text = "Mid-Term", width = 75.dp, isHeader = true, color = TertiaryAmber)
                                    }
                                    Divider(modifier = Modifier.padding(vertical = 4.dp), color = DividerColor)
                                }

                                if (reportItems.isEmpty()) {
                                    item {
                                        Text(
                                            text = "No records found.",
                                            modifier = Modifier.padding(16.dp),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                } else {
                                    items(reportItems, key = { it.student.id }) { item ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { selectedStudentForReport = item }
                                                .padding(vertical = 8.dp, horizontal = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TableCell(text = item.student.rollNumber, width = 50.dp, isBold = true)
                                            TableCell(text = item.student.studentName, width = 120.dp)
                                            TableCell(text = "${item.totalClasses}", width = 60.dp)
                                            TableCell(text = "${item.presentCount}", width = 60.dp, color = PresentGreen, isBold = true)
                                            TableCell(text = "${item.absentCount}", width = 60.dp, color = AbsentRed, isBold = true)
                                            TableCell(
                                                text = viewModel.repository.formatPercentage(item.attendancePercentage),
                                                width = 75.dp,
                                                isBold = true,
                                                color = if (item.attendancePercentage >= 75.0) PresentGreen else TertiaryAmber
                                            )
                                            TableCell(text = viewModel.repository.formatMark(item.ct1), width = 55.dp)
                                            TableCell(text = viewModel.repository.formatMark(item.ct2), width = 55.dp)
                                            TableCell(text = viewModel.repository.formatMark(item.ct3), width = 55.dp)
                                            TableCell(
                                                text = viewModel.repository.formatMark(item.ctAverage),
                                                width = 75.dp,
                                                isBold = true,
                                                color = SecondaryTeal
                                            )
                                            TableCell(
                                                text = viewModel.repository.formatMark(item.midTerm),
                                                width = 75.dp,
                                                isBold = true,
                                                color = TertiaryAmber
                                            )
                                        }
                                        Divider(color = DividerColor.copy(alpha = 0.5f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ---------------- COMPLETE STUDENT REPORT DIALOG ----------------
    selectedStudentForReport?.let { item ->
        val studentRecords = attendanceRecords
            .filter { it.studentId == item.student.id }
            .sortedByDescending { it.classDate }

        AlertDialog(
            onDismissRequest = { selectedStudentForReport = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(PrimaryNavy, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.student.rollNumber,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(item.student.studentName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = "${selectedDept?.departmentName} • ${selectedSem?.semesterName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!item.student.registrationNumber.isNullOrEmpty()) {
                        item {
                            Text("Registration No: ${item.student.registrationNumber}", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    // ATTENDANCE STATS CARD
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("ATTENDANCE SUMMARY", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Classes Held", style = MaterialTheme.typography.bodySmall)
                                        Text("${item.totalClasses}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                    Column {
                                        Text("Present", style = MaterialTheme.typography.bodySmall)
                                        Text("${item.presentCount}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PresentGreen)
                                    }
                                    Column {
                                        Text("Absent", style = MaterialTheme.typography.bodySmall)
                                        Text("${item.absentCount}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AbsentRed)
                                    }
                                    Column {
                                        Text("Attendance %", style = MaterialTheme.typography.bodySmall)
                                        Text(
                                            viewModel.repository.formatPercentage(item.attendancePercentage),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = if (item.attendancePercentage >= 75.0) PresentGreen else TertiaryAmber
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // MARKS SUMMARY CARD
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("MARKS SUMMARY", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("CT1", style = MaterialTheme.typography.bodySmall)
                                        Text(viewModel.repository.formatMark(item.ct1), fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("CT2", style = MaterialTheme.typography.bodySmall)
                                        Text(viewModel.repository.formatMark(item.ct2), fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("CT3", style = MaterialTheme.typography.bodySmall)
                                        Text(viewModel.repository.formatMark(item.ct3), fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("CT Avg", style = MaterialTheme.typography.bodySmall, color = SecondaryTeal, fontWeight = FontWeight.SemiBold)
                                        Text(viewModel.repository.formatMark(item.ctAverage), fontWeight = FontWeight.Bold, color = SecondaryTeal)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Mid-Term", style = MaterialTheme.typography.bodySmall, color = TertiaryAmber, fontWeight = FontWeight.SemiBold)
                                        Text(viewModel.repository.formatMark(item.midTerm), fontWeight = FontWeight.Bold, color = TertiaryAmber)
                                    }
                                }
                            }
                        }
                    }

                    // ATTENDANCE HISTORY BY DATE
                    item {
                        Text("Attendance History by Date:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }

                    if (studentRecords.isEmpty()) {
                        item {
                            Text("No attendance recorded yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        items(studentRecords, key = { it.id }) { record ->
                            val isP = record.status == "P"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isP) PresentGreenBg.copy(alpha = 0.4f) else AbsentRedBg.copy(alpha = 0.4f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(record.classDate, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                Text(
                                    text = if (isP) "PRESENT (P)" else "ABSENT (A)",
                                    color = if (isP) PresentGreen else AbsentRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            val deptName = selectedDept?.departmentName ?: "Department"
                            val semName = selectedSem?.semesterName ?: "Semester"
                            val subName = selectedSub?.let { "${it.subjectName} (${it.subjectCode})" } ?: "Subject"
                            AlertManager.shareStudentReportWithParent(
                                context = context,
                                studentName = item.student.studentName,
                                rollNumber = item.student.rollNumber,
                                department = deptName,
                                semester = semName,
                                subject = subName,
                                totalClasses = item.totalClasses,
                                present = item.presentCount,
                                absent = item.absentCount,
                                percentage = item.attendancePercentage,
                                ctAvg = item.ctAverage,
                                midTerm = item.midTerm
                            )
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Send to Guardian", fontSize = 12.sp)
                    }
                    Button(onClick = { selectedStudentForReport = null }) {
                        Text("Close")
                    }
                }
            }
        )
    }
}
