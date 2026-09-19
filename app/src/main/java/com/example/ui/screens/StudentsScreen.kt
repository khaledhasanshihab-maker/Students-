package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Student
import com.example.ui.TeacherViewModel
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.CardBorder
import com.example.ui.theme.PrimaryNavy

@Composable
fun StudentsScreen(
    viewModel: TeacherViewModel,
    onStudentClick: (Student) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val departments by viewModel.departments.collectAsStateWithLifecycle()
    val semesters by viewModel.semesters.collectAsStateWithLifecycle()
    val selectedDept by viewModel.selectedDepartment.collectAsStateWithLifecycle()
    val selectedSem by viewModel.selectedSemester.collectAsStateWithLifecycle()
    val students by viewModel.students.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }

    // Dialog states
    var showAddDialog by remember { mutableStateOf(false) }
    var studentToEdit by remember { mutableStateOf<Student?>(null) }
    var studentToDelete by remember { mutableStateOf<Student?>(null) }

    // Filter and sort by roll
    val filteredStudents = students
        .filter {
            if (searchQuery.isBlank()) true
            else it.studentName.contains(searchQuery, ignoreCase = true) ||
                 it.rollNumber.contains(searchQuery, ignoreCase = true) ||
                 (it.registrationNumber?.contains(searchQuery, ignoreCase = true) == true)
        }
        .sortedWith(
            compareBy<Student> { it.rollNumber.toIntOrNull() ?: Int.MAX_VALUE }
                .thenBy { it.rollNumber }
        )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Department & Semester Filter Row
            DepartmentSelectorDropdown(
                departments = departments,
                selectedDepartment = selectedDept,
                onSelect = {
                    viewModel.selectedDepartment.value = it
                    viewModel.selectedSemester.value = null
                    viewModel.selectedSubject.value = null
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SemesterSelectorDropdown(
                semesters = semesters,
                selectedSemester = selectedSem,
                onSelect = {
                    viewModel.selectedSemester.value = it
                    viewModel.selectedSubject.value = null
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by name, roll, or registration") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_search_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Students (${filteredStudents.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Sorted by Roll No",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedDept == null || selectedSem == null) {
                EmptyStateCard(
                    message = "Please select Department and Semester above to view students.",
                    icon = Icons.Default.Person
                )
            } else if (filteredStudents.isEmpty()) {
                EmptyStateCard(
                    message = if (searchQuery.isNotEmpty()) "No students matched '$searchQuery'."
                    else "No students added to ${selectedSem?.semesterName} yet.\nClick '+' button to enroll a student.",
                    icon = Icons.Default.Person
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredStudents, key = { it.id }) { student ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("student_card_${student.rollNumber}")
                                .clickable { onStudentClick(student) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
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
                                            .size(46.dp)
                                            .background(PrimaryNavy, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = student.rollNumber,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = student.studentName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (!student.registrationNumber.isNullOrEmpty()) {
                                            Text(
                                                text = "Reg: ${student.registrationNumber}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = "${selectedDept?.departmentName} • ${selectedSem?.semesterName}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row {
                                    IconButton(onClick = { studentToEdit = student }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Student")
                                    }
                                    IconButton(onClick = { studentToDelete = student }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Student", tint = AbsentRed)
                                    }
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }

        // Add Student FAB
        FloatingActionButton(
            onClick = {
                if (selectedDept == null || selectedSem == null) {
                    viewModel.showToast("Please select Department and Semester first.")
                } else {
                    showAddDialog = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_student_fab"),
            containerColor = PrimaryNavy,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Student")
        }
    }

    // ---------------- ADD STUDENT DIALOG ----------------
    if (showAddDialog && selectedDept != null && selectedSem != null) {
        var name by remember { mutableStateOf("") }
        var roll by remember { mutableStateOf("") }
        var reg by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Student") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Enrolling to ${selectedDept?.departmentName} - ${selectedSem?.semesterName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = roll,
                        onValueChange = { roll = it },
                        label = { Text("Roll Number *") },
                        placeholder = { Text("e.g. 101") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_student_roll_input")
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Student Name *") },
                        placeholder = { Text("e.g. Rahim") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_student_name_input")
                    )
                    OutlinedTextField(
                        value = reg,
                        onValueChange = { reg = it },
                        label = { Text("Registration Number (Optional)") },
                        placeholder = { Text("e.g. REG-2024-101") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_student_reg_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addStudent(
                            deptId = selectedDept!!.id,
                            semId = selectedSem!!.id,
                            roll = roll,
                            reg = reg,
                            name = name
                        ) {
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("dialog_student_save_button")
                ) { Text("Add Student") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }

    // ---------------- EDIT STUDENT DIALOG ----------------
    studentToEdit?.let { s ->
        var name by remember { mutableStateOf(s.studentName) }
        var roll by remember { mutableStateOf(s.rollNumber) }
        var reg by remember { mutableStateOf(s.registrationNumber ?: "") }

        AlertDialog(
            onDismissRequest = { studentToEdit = null },
            title = { Text("Edit Student") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = roll,
                        onValueChange = { roll = it },
                        label = { Text("Roll Number *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Student Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = reg,
                        onValueChange = { reg = it },
                        label = { Text("Registration Number (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateStudent(
                            s.copy(studentName = name, rollNumber = roll, registrationNumber = reg)
                        ) {
                            studentToEdit = null
                        }
                    }
                ) { Text("Save Changes") }
            },
            dismissButton = {
                TextButton(onClick = { studentToEdit = null }) { Text("Cancel") }
            }
        )
    }

    // ---------------- DELETE STUDENT CONFIRMATION ----------------
    studentToDelete?.let { s ->
        AlertDialog(
            onDismissRequest = { studentToDelete = null },
            title = { Text("Delete Student") },
            text = {
                Text("Are you sure you want to delete this?\n\nDeleting student '${s.studentName}' (Roll: ${s.rollNumber}) will also remove their attendance and marks records permanently.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteStudent(s)
                        studentToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AbsentRed),
                    modifier = Modifier.testTag("dialog_confirm_delete_student")
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { studentToDelete = null }) { Text("Cancel") }
            }
        )
    }
}
