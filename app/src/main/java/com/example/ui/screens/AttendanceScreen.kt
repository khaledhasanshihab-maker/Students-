package com.example.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Save
import com.example.util.AlertManager
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Attendance
import com.example.data.model.Student
import com.example.data.model.Subject
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AttendanceScreen(
    viewModel: TeacherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Take Attendance, 1: Attendance Table & History

    val departments by viewModel.departments.collectAsStateWithLifecycle()
    val semesters by viewModel.semesters.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()

    val selectedDept by viewModel.selectedDepartment.collectAsStateWithLifecycle()
    val selectedSem by viewModel.selectedSemester.collectAsStateWithLifecycle()
    val selectedSub by viewModel.selectedSubject.collectAsStateWithLifecycle()

    val selectedDate by viewModel.selectedAttendanceDate.collectAsStateWithLifecycle()
    val selectedClassType by viewModel.selectedAttendanceType.collectAsStateWithLifecycle()
    val statusMap by viewModel.attendanceStatusMap.collectAsStateWithLifecycle()
    val students by viewModel.students.collectAsStateWithLifecycle()
    val subjectAttendance by viewModel.currentSubjectAttendance.collectAsStateWithLifecycle()
    val distinctDates by viewModel.distinctAttendanceDates.collectAsStateWithLifecycle()

    val sortedStudents = remember(students) {
        students.sortedWith(
            compareBy<Student> { it.rollNumber.toIntOrNull() ?: Int.MAX_VALUE }
                .thenBy { it.rollNumber }
        )
    }

    // When subject, date, or class type changes, reload attendance map
    LaunchedEffect(selectedSub, selectedDate, selectedClassType, students) {
        selectedSub?.let { sub ->
            viewModel.loadAttendanceForDate(sub.id, selectedDate, selectedClassType)
        }
    }

    // Date picker dialog
    val calendar = Calendar.getInstance()
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val formatted = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
                viewModel.selectedAttendanceDate.value = formatted
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    var dateToDelete by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // View Mode Tab
            TabRow(
                selectedTabIndex = activeSubTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = activeSubTab == 0,
                    onClick = { activeSubTab = 0 },
                    text = { Text("Take Daily Attendance", fontWeight = if (activeSubTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.EventAvailable, contentDescription = null) }
                )
                Tab(
                    selected = activeSubTab == 1,
                    onClick = { activeSubTab = 1 },
                    text = { Text("Attendance Table", fontWeight = if (activeSubTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.ListAlt, contentDescription = null) }
                )
            }

            // Common Selection Header: Department, Semester, Subject
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

                // Class Type Selector: Theory vs Practical
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Class Type:",
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
                            .testTag("theory_type_btn"),
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
                            .testTag("practical_type_btn"),
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
                        message = "Please select Department, Semester, and Subject above to manage attendance.",
                        icon = Icons.Default.EventAvailable
                    )
                }
            } else {
                when (activeSubTab) {
                    0 -> {
                        // TAKE ATTENDANCE VIEW
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(4.dp))
                                // Date Selector Bar
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("attendance_date_card"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable { datePickerDialog.show() }
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .background(PrimaryNavy.copy(alpha = 0.1f), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.CalendarMonth,
                                                    contentDescription = "Pick Date",
                                                    tint = PrimaryNavy
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text("Class Date", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(
                                                    text = selectedDate,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            OutlinedButton(
                                                onClick = {
                                                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                                                    viewModel.selectedAttendanceDate.value = today
                                                },
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Today", fontSize = 12.sp)
                                            }
                                            Button(
                                                onClick = { datePickerDialog.show() },
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.testTag("pick_date_button")
                                            ) {
                                                Text("Change", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            // Quick Mark All Buttons
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.markAllPresent() },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("mark_all_present_button"),
                                        colors = ButtonDefaults.buttonColors(containerColor = PresentGreen),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("All Present [P]", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }

                                    Button(
                                        onClick = { viewModel.markAllAbsent() },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("mark_all_absent_button"),
                                        colors = ButtonDefaults.buttonColors(containerColor = AbsentRed),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("All Absent [A]", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            // Attendance Summary for Current Date
                            item {
                                val currentPresents = statusMap.values.count { it == "P" }
                                val currentAbsents = statusMap.values.count { it == "A" }
                                val total = sortedStudents.size

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Rollcall: $total Students",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = "P: $currentPresents",
                                            color = PresentGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "A: $currentAbsents",
                                            color = AbsentRed,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            if (sortedStudents.isEmpty()) {
                                item {
                                    EmptyStateCard(
                                        message = "No students found in ${selectedSem?.semesterName}.\nGo to Students tab to add students.",
                                        icon = Icons.Default.EventAvailable
                                    )
                                }
                            } else {
                                // Student Rollcall Items
                                items(sortedStudents, key = { it.id }) { student ->
                                    val status = statusMap[student.id] ?: "P"
                                    val isPresent = status == "P"

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("attendance_row_${student.rollNumber}"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isPresent) PresentGreenBg.copy(alpha = 0.4f) else AbsentRedBg.copy(alpha = 0.4f)
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isPresent) PresentGreen.copy(alpha = 0.5f) else AbsentRed.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                // Roll Badge
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .background(PrimaryNavy, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = student.rollNumber,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(
                                                        text = student.studentName,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    student.registrationNumber?.let {
                                                        Text(
                                                            text = "Reg: $it",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }

                                            // [P] and [A] Buttons
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                // Present [P] Button
                                                Button(
                                                    onClick = {
                                                        viewModel.setStudentAttendanceStatus(student.id, "P")
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (isPresent) PresentGreen else MaterialTheme.colorScheme.surfaceVariant,
                                                        contentColor = if (isPresent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                                    ),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier
                                                        .size(width = 54.dp, height = 44.dp)
                                                        .testTag("btn_present_${student.rollNumber}")
                                                ) {
                                                    Text(
                                                        text = "P",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.ExtraBold
                                                    )
                                                }

                                                // Absent [A] Button
                                                Button(
                                                    onClick = {
                                                        viewModel.setStudentAttendanceStatus(student.id, "A")
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (!isPresent) AbsentRed else MaterialTheme.colorScheme.surfaceVariant,
                                                        contentColor = if (!isPresent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                                    ),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier
                                                        .size(width = 54.dp, height = 44.dp)
                                                        .testTag("btn_absent_${student.rollNumber}")
                                                ) {
                                                    Text(
                                                        text = "A",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.ExtraBold
                                                    )
                                                }

                                                // If absent, show direct Alert Button to notify parents
                                                if (!isPresent) {
                                                    IconButton(
                                                        onClick = {
                                                            AlertManager.sendAbsenceSms(
                                                                context = context,
                                                                guardianPhone = null,
                                                                studentName = student.studentName,
                                                                rollNumber = student.rollNumber,
                                                                subjectName = selectedSub?.subjectName ?: "Class",
                                                                date = selectedDate
                                                            )
                                                        },
                                                        modifier = Modifier
                                                            .size(44.dp)
                                                            .background(AbsentRed.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                                    ) {
                                                        Icon(
                                                            Icons.Default.NotificationsActive,
                                                            contentDescription = "Alert Parent via SMS",
                                                            tint = AbsentRed,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                // Save Attendance Sticky Button
                                item {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            viewModel.saveCurrentAttendance()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .testTag("save_attendance_button"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Save $selectedClassType Attendance ($selectedDate)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(36.dp))
                                }
                            }
                        }
                    }

                    1 -> {
                        // ATTENDANCE TABLE VIEW
                        AttendanceTableView(
                            students = sortedStudents,
                            dates = distinctDates,
                            classType = selectedClassType,
                            attendanceRecords = subjectAttendance,
                            onDeleteDate = { dateToDelete = it },
                            onSelectDate = { date ->
                                viewModel.selectedAttendanceDate.value = date
                                activeSubTab = 0
                            }
                        )
                    }
                }
            }
        }
    }

    // Delete attendance confirmation dialog
    dateToDelete?.let { date ->
        AlertDialog(
            onDismissRequest = { dateToDelete = null },
            title = { Text("Delete $selectedClassType Attendance") },
            text = {
                Text("Are you sure you want to delete this?\n\nDeleting $selectedClassType attendance for date $date will permanently erase the records for that class.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAttendanceRecord(date, selectedClassType)
                        dateToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AbsentRed)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { dateToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AttendanceTableView(
    students: List<Student>,
    dates: List<String>,
    classType: String = "Theory",
    attendanceRecords: List<Attendance>,
    onDeleteDate: (String) -> Unit,
    onSelectDate: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    // Map: (studentId, date) -> "P" or "A"
    val recordMap = remember(attendanceRecords) {
        attendanceRecords.associate { (it.studentId to it.classDate) to it.status }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$classType Attendance Sheet (${dates.size} classes held)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Scroll horizontally ->",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (dates.isEmpty()) {
            EmptyStateCard(
                message = "No $classType attendance recorded yet for this subject.\nSwitch to 'Take Daily Attendance' tab to record class attendance.",
                icon = Icons.Default.ListAlt
            )
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("attendance_matrix_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Box(modifier = Modifier.horizontalScroll(scrollState)) {
                    LazyColumn(modifier = Modifier.padding(12.dp)) {
                        // Header Row
                        item {
                            Row(
                                modifier = Modifier
                                    .background(PrimaryNavy.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                    .padding(vertical = 10.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell(text = "Roll", width = 50.dp, isHeader = true)
                                TableCell(text = "Student Name", width = 120.dp, isHeader = true)

                                // Dynamic Date Columns (Class 1, Class 2...)
                                dates.forEachIndexed { index, date ->
                                    val shortDate = try {
                                        val parts = date.split("-")
                                        if (parts.size == 3) "${parts[2]}/${parts[1]}" else date
                                    } catch (e: Exception) { date }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .width(70.dp)
                                            .clickable { onSelectDate(date) }
                                    ) {
                                        Text(
                                            text = "Class ${index + 1}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = PrimaryNavy
                                        )
                                        Text(
                                            text = shortDate,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                TableCell(text = "Present", width = 60.dp, isHeader = true)
                                TableCell(text = "Absent", width = 60.dp, isHeader = true)
                                TableCell(text = "Att %", width = 70.dp, isHeader = true)
                            }
                            Divider(modifier = Modifier.padding(vertical = 4.dp), color = DividerColor)
                        }

                        // Student Rows
                        items(students, key = { it.id }) { student ->
                            var studentPresents = 0
                            var studentAbsents = 0

                            dates.forEach { date ->
                                val status = recordMap[student.id to date]
                                if (status == "P") studentPresents++
                                else if (status == "A") studentAbsents++
                            }

                            val totalHeld = dates.size
                            val pct = if (totalHeld > 0) (studentPresents.toDouble() / totalHeld.toDouble()) * 100.0 else 0.0

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell(text = student.rollNumber, width = 50.dp, isBold = true)
                                TableCell(text = student.studentName, width = 120.dp)

                                // Status for each date
                                dates.forEach { date ->
                                    val status = recordMap[student.id to date] ?: "-"
                                    val isP = status == "P"
                                    val isA = status == "A"

                                    Box(
                                        modifier = Modifier
                                            .width(70.dp)
                                            .padding(horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isP) PresentGreenBg else if (isA) AbsentRedBg else Color.Transparent
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = status,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (isP) PresentGreen else if (isA) AbsentRed else Color.Gray
                                            )
                                        }
                                    }
                                }

                                TableCell(text = "$studentPresents", width = 60.dp, color = PresentGreen, isBold = true)
                                TableCell(text = "$studentAbsents", width = 60.dp, color = AbsentRed, isBold = true)
                                TableCell(
                                    text = String.format(Locale.US, "%.1f%%", pct),
                                    width = 70.dp,
                                    isBold = true,
                                    color = if (pct >= 75.0) PresentGreen else TertiaryAmber
                                )
                            }
                            Divider(color = DividerColor.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Class Dates Management Row
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Recorded Class Dates (Tap date to edit, bin to delete):", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        dates.forEach { date ->
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = date,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.clickable { onSelectDate(date) }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { onDeleteDate(date) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Date", tint = AbsentRed, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isHeader: Boolean = false,
    isBold: Boolean = false,
    color: Color = Color.Unspecified
) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        fontSize = if (isHeader) 13.sp else 13.sp,
        fontWeight = if (isHeader || isBold) FontWeight.Bold else FontWeight.Normal,
        color = if (color != Color.Unspecified) color else if (isHeader) PrimaryNavy else Color.Unspecified,
        textAlign = TextAlign.Center
    )
}

@Composable
fun SubjectSelectorDropdown(
    subjects: List<Subject>,
    selectedSubject: Subject?,
    onSelect: (Subject) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .testTag("dropdown_subject_select"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Select Subject / Course", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = if (selectedSubject != null) "${selectedSubject.subjectName} (${selectedSubject.subjectCode})" else "Choose a subject",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selectedSubject != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(androidx.compose.material.icons.Icons.Default.ExpandMore, contentDescription = "Expand")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            subjects.forEach { sub ->
                DropdownMenuItem(
                    text = { Text("${sub.subjectName} (${sub.subjectCode})") },
                    onClick = {
                        onSelect(sub)
                        expanded = false
                    },
                    leadingIcon = {
                        if (selectedSubject?.id == sub.id) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryNavy)
                        }
                    }
                )
            }
        }
    }
}
