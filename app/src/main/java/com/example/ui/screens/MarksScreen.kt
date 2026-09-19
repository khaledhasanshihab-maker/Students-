package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Marks
import com.example.data.model.Student
import com.example.ui.TeacherViewModel
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DividerColor
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.TertiaryAmber

@Composable
fun MarksScreen(
    viewModel: TeacherViewModel,
    modifier: Modifier = Modifier
) {
    val departments by viewModel.departments.collectAsStateWithLifecycle()
    val semesters by viewModel.semesters.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()

    val selectedDept by viewModel.selectedDepartment.collectAsStateWithLifecycle()
    val selectedSem by viewModel.selectedSemester.collectAsStateWithLifecycle()
    val selectedSub by viewModel.selectedSubject.collectAsStateWithLifecycle()

    val students by viewModel.students.collectAsStateWithLifecycle()
    val marksList by viewModel.currentSubjectMarks.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var studentForMarksEdit by remember { mutableStateOf<Student?>(null) }

    // Map: studentId -> Marks
    val marksMap = remember(marksList) {
        marksList.associateBy { it.studentId }
    }

    val sortedStudents = remember(students, searchQuery) {
        students
            .filter {
                if (searchQuery.isBlank()) true
                else it.studentName.contains(searchQuery, ignoreCase = true) ||
                     it.rollNumber.contains(searchQuery, ignoreCase = true)
            }
            .sortedWith(
                compareBy<Student> { it.rollNumber.toIntOrNull() ?: Int.MAX_VALUE }
                    .thenBy { it.rollNumber }
            )
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Filters
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
                        message = "Please select Department, Semester, and Subject above to view and enter marks.",
                        icon = Icons.Default.Grade
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search by name or roll number") },
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
                            .testTag("marks_search_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Marks Table (${sortedStudents.size} students)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap student or edit icon",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // MARKS TABLE
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("marks_table_card"),
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
                                        TableCell(text = "CT 1", width = 60.dp, isHeader = true)
                                        TableCell(text = "CT 2", width = 60.dp, isHeader = true)
                                        TableCell(text = "CT 3", width = 60.dp, isHeader = true)
                                        TableCell(text = "CT Avg", width = 75.dp, isHeader = true, color = SecondaryTeal)
                                        TableCell(text = "Mid-Term", width = 75.dp, isHeader = true, color = TertiaryAmber)
                                        TableCell(text = "Action", width = 60.dp, isHeader = true)
                                    }
                                    Divider(modifier = Modifier.padding(vertical = 4.dp), color = DividerColor)
                                }

                                if (sortedStudents.isEmpty()) {
                                    item {
                                        Text(
                                            text = "No students found.",
                                            modifier = Modifier.padding(16.dp),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                } else {
                                    items(sortedStudents, key = { it.id }) { student ->
                                        val marks = marksMap[student.id]
                                        val ctAvg = marks?.calculateCtAverage()

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { studentForMarksEdit = student }
                                                .padding(vertical = 8.dp, horizontal = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TableCell(text = student.rollNumber, width = 50.dp, isBold = true)
                                            TableCell(text = student.studentName, width = 120.dp)
                                            TableCell(text = viewModel.repository.formatMark(marks?.ct1), width = 60.dp)
                                            TableCell(text = viewModel.repository.formatMark(marks?.ct2), width = 60.dp)
                                            TableCell(text = viewModel.repository.formatMark(marks?.ct3), width = 60.dp)
                                            TableCell(
                                                text = viewModel.repository.formatMark(ctAvg),
                                                width = 75.dp,
                                                isBold = true,
                                                color = SecondaryTeal
                                            )
                                            TableCell(
                                                text = viewModel.repository.formatMark(marks?.midTerm),
                                                width = 75.dp,
                                                isBold = true,
                                                color = TertiaryAmber
                                            )
                                            Box(
                                                modifier = Modifier.width(60.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                IconButton(
                                                    onClick = { studentForMarksEdit = student },
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .testTag("edit_marks_${student.rollNumber}")
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit Marks", tint = PrimaryNavy, modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        }
                                        Divider(color = DividerColor.copy(alpha = 0.5f))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• CT Average is calculated automatically from entered test marks (empty marks are not counted as 0).\n• Tap any row to enter or edit marks.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // ---------------- EDIT MARKS DIALOG ----------------
    studentForMarksEdit?.let { student ->
        val currentMarks = marksMap[student.id]

        var ct1Text by remember { mutableStateOf(currentMarks?.ct1?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "") }
        var ct2Text by remember { mutableStateOf(currentMarks?.ct2?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "") }
        var ct3Text by remember { mutableStateOf(currentMarks?.ct3?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "") }
        var midText by remember { mutableStateOf(currentMarks?.midTerm?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "") }

        // Live preview of CT Average
        val previewCt1 = ct1Text.toDoubleOrNull()
        val previewCt2 = ct2Text.toDoubleOrNull()
        val previewCt3 = ct3Text.toDoubleOrNull()
        val previewEntered = listOfNotNull(previewCt1, previewCt2, previewCt3)
        val previewAvg = if (previewEntered.isNotEmpty()) previewEntered.sum() / previewEntered.size else null

        AlertDialog(
            onDismissRequest = { studentForMarksEdit = null },
            title = {
                Column {
                    Text("Enter Marks", fontWeight = FontWeight.Bold)
                    Text(
                        "${student.studentName} (Roll: ${student.rollNumber})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Leave field empty if test not taken yet (will NOT count as zero).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = ct1Text,
                        onValueChange = { ct1Text = it },
                        label = { Text("Class Test 1 (CT1)") },
                        placeholder = { Text("e.g. 15") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_ct1_input")
                    )

                    OutlinedTextField(
                        value = ct2Text,
                        onValueChange = { ct2Text = it },
                        label = { Text("Class Test 2 (CT2)") },
                        placeholder = { Text("e.g. 18") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_ct2_input")
                    )

                    OutlinedTextField(
                        value = ct3Text,
                        onValueChange = { ct3Text = it },
                        label = { Text("Class Test 3 (CT3)") },
                        placeholder = { Text("e.g. 17") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_ct3_input")
                    )

                    // Calculated Average Preview Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Calculated CT Average:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(
                                text = viewModel.repository.formatMark(previewAvg),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SecondaryTeal
                            )
                        }
                    }

                    OutlinedTextField(
                        value = midText,
                        onValueChange = { midText = it },
                        label = { Text("Mid-Term Mark") },
                        placeholder = { Text("e.g. 32") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_midterm_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsedCt1 = ct1Text.trim().ifEmpty { null }?.toDoubleOrNull()
                        val parsedCt2 = ct2Text.trim().ifEmpty { null }?.toDoubleOrNull()
                        val parsedCt3 = ct3Text.trim().ifEmpty { null }?.toDoubleOrNull()
                        val parsedMid = midText.trim().ifEmpty { null }?.toDoubleOrNull()

                        viewModel.saveMarks(
                            studentId = student.id,
                            ct1 = parsedCt1,
                            ct2 = parsedCt2,
                            ct3 = parsedCt3,
                            midTerm = parsedMid
                        ) {
                            studentForMarksEdit = null
                        }
                    },
                    modifier = Modifier.testTag("dialog_save_marks_button")
                ) { Text("Save Marks") }
            },
            dismissButton = {
                TextButton(onClick = { studentForMarksEdit = null }) { Text("Cancel") }
            }
        )
    }
}
