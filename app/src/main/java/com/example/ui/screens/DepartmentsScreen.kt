package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.Department
import com.example.data.model.Semester
import com.example.data.model.Subject
import com.example.ui.TeacherViewModel
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.CardBorder
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryTeal

@Composable
fun DepartmentsScreen(
    viewModel: TeacherViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Departments, 1: Semesters, 2: Subjects

    val departments by viewModel.departments.collectAsStateWithLifecycle()
    val semesters by viewModel.semesters.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()

    val selectedDept by viewModel.selectedDepartment.collectAsStateWithLifecycle()
    val selectedSem by viewModel.selectedSemester.collectAsStateWithLifecycle()

    // Dialog States
    var showAddDeptDialog by remember { mutableStateOf(false) }
    var deptToEdit by remember { mutableStateOf<Department?>(null) }
    var deptToDelete by remember { mutableStateOf<Department?>(null) }

    var showAddSemDialog by remember { mutableStateOf(false) }
    var semToEdit by remember { mutableStateOf<Semester?>(null) }
    var semToDelete by remember { mutableStateOf<Semester?>(null) }

    var showAddSubDialog by remember { mutableStateOf(false) }
    var subToEdit by remember { mutableStateOf<Subject?>(null) }
    var subToDelete by remember { mutableStateOf<Subject?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Departments", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.Domain, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Semesters", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.Timeline, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Subjects", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.Book, contentDescription = null) }
                )
            }

            // Body
            when (selectedTab) {
                0 -> {
                    // DEPARTMENTS TAB
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Departments (${departments.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Each department manages its own semesters, subjects, and students.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (departments.isEmpty()) {
                            item {
                                EmptyStateCard(
                                    message = "No departments created yet.\nClick '+' to add your first department (e.g. Computer Technology).",
                                    icon = Icons.Default.Domain
                                )
                            }
                        } else {
                            items(departments, key = { it.id }) { dept ->
                                val isSelected = selectedDept?.id == dept.id
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("dept_card_${dept.id}")
                                        .clickable {
                                            viewModel.selectedDepartment.value = dept
                                            viewModel.selectedSemester.value = null
                                            viewModel.selectedSubject.value = null
                                        },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) PrimaryNavy else CardBorder
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .background(PrimaryNavy.copy(alpha = 0.1f), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.Domain,
                                                    contentDescription = null,
                                                    tint = PrimaryNavy,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = dept.departmentName,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                if (isSelected) {
                                                    Text(
                                                        text = "Active Selection",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = PrimaryNavy,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                            }
                                        }

                                        Row {
                                            IconButton(onClick = { deptToEdit = dept }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit Department")
                                            }
                                            IconButton(onClick = { deptToDelete = dept }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete Department", tint = AbsentRed)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(72.dp)) }
                    }
                }

                1 -> {
                    // SEMESTERS TAB
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        DepartmentSelectorDropdown(
                            departments = departments,
                            selectedDepartment = selectedDept,
                            onSelect = {
                                viewModel.selectedDepartment.value = it
                                viewModel.selectedSemester.value = null
                                viewModel.selectedSubject.value = null
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (selectedDept == null) {
                            EmptyStateCard(
                                message = "Please select a Department first to view and manage its semesters.",
                                icon = Icons.Default.Timeline
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                item {
                                    val matchedCurriculum = selectedDept?.let { com.example.data.model.DiplomaCurricula.findCurriculumForDepartment(it.departmentName) }
                                    val hasPredefinedCurriculum = matchedCurriculum != null
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Semesters in ${selectedDept?.departmentName} (${semesters.size})",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )

                                            if (hasPredefinedCurriculum) {
                                                OutlinedButton(
                                                    onClick = {
                                                        selectedDept?.let { dept ->
                                                            viewModel.populateAllCurriculumForDepartment(dept.id, dept.departmentName)
                                                        }
                                                    },
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = SecondaryTeal)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Import 1st-7th Sem", style = MaterialTheme.typography.labelMedium)
                                                }
                                            }
                                        }

                                        if (hasPredefinedCurriculum && semesters.isEmpty()) {
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = CardDefaults.cardColors(containerColor = SecondaryTeal.copy(alpha = 0.1f)),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryTeal.copy(alpha = 0.3f))
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = "${matchedCurriculum.departmentName} Curriculum Available",
                                                            style = MaterialTheme.typography.titleSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = PrimaryNavy
                                                        )
                                                        Text(
                                                            text = "Official BTEB 1st to 7th semester syllabus ready with all book codes. Click to auto-generate all semesters & subjects.",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Button(
                                                        onClick = {
                                                            selectedDept?.let { dept ->
                                                                viewModel.populateAllCurriculumForDepartment(dept.id, dept.departmentName)
                                                            }
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryTeal),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Text("Auto Setup", fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (semesters.isEmpty()) {
                                    item {
                                        EmptyStateCard(
                                            message = "No semesters yet in ${selectedDept?.departmentName}.\nClick '+' to add (e.g. 1st Semester, 6th Semester).",
                                            icon = Icons.Default.Timeline
                                        )
                                    }
                                } else {
                                    items(semesters, key = { it.id }) { sem ->
                                        val isSelected = selectedSem?.id == sem.id
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("sem_card_${sem.id}")
                                                .clickable {
                                                    viewModel.selectedSemester.value = sem
                                                    viewModel.selectedSubject.value = null
                                                },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                            ),
                                            border = androidx.compose.foundation.BorderStroke(
                                                if (isSelected) 2.dp else 1.dp,
                                                if (isSelected) PrimaryNavy else CardBorder
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        Icons.Default.Timeline,
                                                        contentDescription = null,
                                                        tint = SecondaryTeal
                                                    )
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Column {
                                                        Text(
                                                            text = sem.semesterName,
                                                            style = MaterialTheme.typography.titleSmall,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        if (isSelected) {
                                                            Text(
                                                                text = "Active Selection",
                                                                style = MaterialTheme.typography.bodySmall,
                                                                color = PrimaryNavy
                                                            )
                                                        }
                                                    }
                                                }

                                                Row {
                                                    IconButton(onClick = { semToEdit = sem }) {
                                                        Icon(Icons.Default.Edit, contentDescription = "Edit Semester")
                                                    }
                                                    IconButton(onClick = { semToDelete = sem }) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Delete Semester", tint = AbsentRed)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                item { Spacer(modifier = Modifier.height(72.dp)) }
                            }
                        }
                    }
                }

                2 -> {
                    // SUBJECTS TAB
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
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

                        if (selectedDept == null || selectedSem == null) {
                            EmptyStateCard(
                                message = "Please select both a Department and a Semester to manage subjects.",
                                icon = Icons.Default.Book
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                item {
                                    val semName = selectedSem?.semesterName ?: ""
                                    val deptName = selectedDept?.departmentName ?: ""
                                    val curriculumSubjects = com.example.data.model.DiplomaCurricula.getSubjectsForDeptAndSemester(deptName, semName)
                                        ?: com.example.data.model.DiplomaCstCurriculum.getSubjectsForSemester(semName)
                                    val matchedCurriculum = selectedDept?.let { com.example.data.model.DiplomaCurricula.findCurriculumForDepartment(it.departmentName) }
                                    val hasCurriculum = !curriculumSubjects.isNullOrEmpty()

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Subjects in $semName (${subjects.size})",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )

                                            if (hasCurriculum) {
                                                OutlinedButton(
                                                    onClick = {
                                                        if (selectedDept != null && selectedSem != null) {
                                                            viewModel.assignCurriculumSubjectsForSemester(
                                                                deptId = selectedDept!!.id,
                                                                semId = selectedSem!!.id,
                                                                semesterName = selectedSem!!.semesterName,
                                                                deptName = selectedDept!!.departmentName
                                                            )
                                                        }
                                                    },
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.testTag("assign_curriculum_btn")
                                                ) {
                                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = SecondaryTeal)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Assign Syllabus", style = MaterialTheme.typography.labelMedium)
                                                }
                                            }
                                        }

                                        if (hasCurriculum && subjects.isEmpty()) {
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = CardDefaults.cardColors(containerColor = SecondaryTeal.copy(alpha = 0.1f)),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryTeal.copy(alpha = 0.3f))
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = "${matchedCurriculum?.departmentName ?: "BTEB"} Curriculum Available",
                                                            style = MaterialTheme.typography.titleSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = PrimaryNavy
                                                        )
                                                        Text(
                                                            text = "Found ${curriculumSubjects?.size ?: 0} official book subjects for $semName. Click to populate all automatically.",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Button(
                                                        onClick = {
                                                            if (selectedDept != null && selectedSem != null) {
                                                                viewModel.assignCurriculumSubjectsForSemester(
                                                                    deptId = selectedDept!!.id,
                                                                    semId = selectedSem!!.id,
                                                                    semesterName = selectedSem!!.semesterName,
                                                                    deptName = selectedDept!!.departmentName
                                                                )
                                                            }
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryTeal),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Text("Auto Add", fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (subjects.isEmpty()) {
                                    item {
                                        EmptyStateCard(
                                            message = "No subjects found.\nClick '+' to add subject (e.g. Computer Networking, 66631).",
                                            icon = Icons.Default.Book
                                        )
                                    }
                                } else {
                                    items(subjects, key = { it.id }) { sub ->
                                        val isSelected = viewModel.selectedSubject.value?.id == sub.id
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("sub_card_${sub.id}")
                                                .clickable {
                                                    viewModel.selectedSubject.value = sub
                                                },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                            ),
                                            border = androidx.compose.foundation.BorderStroke(
                                                if (isSelected) 2.dp else 1.dp,
                                                if (isSelected) PrimaryNavy else CardBorder
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Book,
                                                        contentDescription = null,
                                                        tint = PrimaryNavy
                                                    )
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Column {
                                                        Text(
                                                            text = sub.subjectName,
                                                            style = MaterialTheme.typography.titleSmall,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = "Subject Code: ${sub.subjectCode}",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }

                                                Row {
                                                    IconButton(onClick = { subToEdit = sub }) {
                                                        Icon(Icons.Default.Edit, contentDescription = "Edit Subject")
                                                    }
                                                    IconButton(onClick = { subToDelete = sub }) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Delete Subject", tint = AbsentRed)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                item { Spacer(modifier = Modifier.height(72.dp)) }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = {
                when (selectedTab) {
                    0 -> showAddDeptDialog = true
                    1 -> {
                        if (selectedDept == null) {
                            viewModel.showToast("Please select a department first.")
                        } else {
                            showAddSemDialog = true
                        }
                    }
                    2 -> {
                        if (selectedDept == null || selectedSem == null) {
                            viewModel.showToast("Please select department and semester first.")
                        } else {
                            showAddSubDialog = true
                        }
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("curriculum_fab"),
            containerColor = PrimaryNavy,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Item")
        }
    }

    // ---------------- ADD / EDIT DEPARTMENT DIALOGS ----------------
    if (showAddDeptDialog) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDeptDialog = false },
            title = { Text("Add Department") },
            text = {
                Column {
                    Text("Enter department name (e.g. Computer Technology):", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Department Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_dept_name_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Quick Select Predefined Department:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryTeal
                    )
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(com.example.data.model.DiplomaCurricula.allCurricula) { curr ->
                            androidx.compose.material3.FilterChip(
                                selected = name.equals(curr.departmentName, ignoreCase = true),
                                onClick = { name = curr.departmentName },
                                label = { Text("${curr.shortName} - ${curr.departmentName}", style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addDepartment(name) {
                            showAddDeptDialog = false
                        }
                    },
                    modifier = Modifier.testTag("dialog_dept_save_button")
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDeptDialog = false }) { Text("Cancel") }
            }
        )
    }

    deptToEdit?.let { dept ->
        var name by remember { mutableStateOf(dept.departmentName) }
        AlertDialog(
            onDismissRequest = { deptToEdit = null },
            title = { Text("Edit Department") },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Department Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateDepartment(dept.copy(departmentName = name)) {
                            deptToEdit = null
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { deptToEdit = null }) { Text("Cancel") }
            }
        )
    }

    deptToDelete?.let { dept ->
        AlertDialog(
            onDismissRequest = { deptToDelete = null },
            title = { Text("Delete Department") },
            text = {
                Text("Are you sure you want to delete this?\n\nDeleting '${dept.departmentName}' will also remove its associated semesters, subjects, students, and attendance records.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDepartment(dept)
                        deptToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AbsentRed),
                    modifier = Modifier.testTag("dialog_confirm_delete_button")
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { deptToDelete = null }) { Text("Cancel") }
            }
        )
    }

    // ---------------- ADD / EDIT SEMESTER DIALOGS ----------------
    if (showAddSemDialog && selectedDept != null) {
        var name by remember { mutableStateOf("") }
        var autoAssignSubjects by remember { mutableStateOf(true) }
        AlertDialog(
            onDismissRequest = { showAddSemDialog = false },
            title = { Text("Add Semester") },
            text = {
                Column {
                    Text("Add semester to ${selectedDept?.departmentName}:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Semester Name (e.g. 1st Semester)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_sem_name_input")
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { autoAssignSubjects = !autoAssignSubjects },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Checkbox(
                            checked = autoAssignSubjects,
                            onCheckedChange = { autoAssignSubjects = it }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Auto-assign default curriculum subjects if available",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addSemester(selectedDept!!.id, name, autoAssignCurriculum = autoAssignSubjects) {
                            showAddSemDialog = false
                        }
                    },
                    modifier = Modifier.testTag("dialog_sem_save_button")
                ) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddSemDialog = false }) { Text("Cancel") }
            }
        )
    }

    semToEdit?.let { sem ->
        var name by remember { mutableStateOf(sem.semesterName) }
        AlertDialog(
            onDismissRequest = { semToEdit = null },
            title = { Text("Edit Semester") },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Semester Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateSemester(sem.copy(semesterName = name)) {
                            semToEdit = null
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { semToEdit = null }) { Text("Cancel") }
            }
        )
    }

    semToDelete?.let { sem ->
        AlertDialog(
            onDismissRequest = { semToDelete = null },
            title = { Text("Delete Semester") },
            text = {
                Text("Are you sure you want to delete this?\n\nDeleting '${sem.semesterName}' will delete its associated subjects, students, and records.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSemester(sem)
                        semToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AbsentRed)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { semToDelete = null }) { Text("Cancel") }
            }
        )
    }

    // ---------------- ADD / EDIT SUBJECT DIALOGS ----------------
    if (showAddSubDialog && selectedDept != null && selectedSem != null) {
        var subName by remember { mutableStateOf("") }
        var subCode by remember { mutableStateOf("") }
        val curriculumSubjects = com.example.data.model.DiplomaCurricula.getSubjectsForDeptAndSemester(
            selectedDept!!.departmentName,
            selectedSem!!.semesterName
        ) ?: com.example.data.model.DiplomaCstCurriculum.getSubjectsForSemester(selectedSem!!.semesterName)

        AlertDialog(
            onDismissRequest = { showAddSubDialog = false },
            title = { Text("Add Subject") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Add subject under ${selectedDept?.departmentName} - ${selectedSem?.semesterName}:",
                        style = MaterialTheme.typography.bodySmall
                    )

                    if (!curriculumSubjects.isNullOrEmpty()) {
                        Text(
                            "Quick Pick from Official Syllabus:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryTeal
                        )
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(curriculumSubjects) { currSub ->
                                androidx.compose.material3.FilterChip(
                                    selected = subCode == currSub.subjectCode,
                                    onClick = {
                                        subName = currSub.subjectName
                                        subCode = currSub.subjectCode
                                    },
                                    label = {
                                        Text("${currSub.subjectName} (${currSub.subjectCode})", style = MaterialTheme.typography.labelSmall)
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = subName,
                        onValueChange = { subName = it },
                        label = { Text("Subject Name") },
                        placeholder = { Text("Computer Fundamentals") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_sub_name_input")
                    )
                    OutlinedTextField(
                        value = subCode,
                        onValueChange = { subCode = it },
                        label = { Text("Subject Code") },
                        placeholder = { Text("66611") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_sub_code_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addSubject(selectedDept!!.id, selectedSem!!.id, subName, subCode) {
                            showAddSubDialog = false
                        }
                    },
                    modifier = Modifier.testTag("dialog_sub_save_button")
                ) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubDialog = false }) { Text("Cancel") }
            }
        )
    }

    subToEdit?.let { sub ->
        var subName by remember { mutableStateOf(sub.subjectName) }
        var subCode by remember { mutableStateOf(sub.subjectCode) }
        AlertDialog(
            onDismissRequest = { subToEdit = null },
            title = { Text("Edit Subject") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = subName,
                        onValueChange = { subName = it },
                        label = { Text("Subject Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = subCode,
                        onValueChange = { subCode = it },
                        label = { Text("Subject Code") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateSubject(sub.copy(subjectName = subName, subjectCode = subCode)) {
                            subToEdit = null
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { subToEdit = null }) { Text("Cancel") }
            }
        )
    }

    subToDelete?.let { sub ->
        AlertDialog(
            onDismissRequest = { subToDelete = null },
            title = { Text("Delete Subject") },
            text = {
                Text("Are you sure you want to delete this?\n\nDeleting '${sub.subjectName}' will delete its attendance and marks data.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSubject(sub)
                        subToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AbsentRed)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { subToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun DepartmentSelectorDropdown(
    departments: List<Department>,
    selectedDepartment: Department?,
    onSelect: (Department) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .testTag("dropdown_department_select"),
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
                Text("Select Department", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = selectedDepartment?.departmentName ?: "Choose a department",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selectedDepartment != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.ExpandMore, contentDescription = "Expand")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            departments.forEach { dept ->
                DropdownMenuItem(
                    text = { Text(dept.departmentName) },
                    onClick = {
                        onSelect(dept)
                        expanded = false
                    },
                    leadingIcon = {
                        if (selectedDepartment?.id == dept.id) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryNavy)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SemesterSelectorDropdown(
    semesters: List<Semester>,
    selectedSemester: Semester?,
    onSelect: (Semester) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .testTag("dropdown_semester_select"),
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
                Text("Select Semester", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = selectedSemester?.semesterName ?: "Choose a semester",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selectedSemester != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.ExpandMore, contentDescription = "Expand")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            semesters.forEach { sem ->
                DropdownMenuItem(
                    text = { Text(sem.semesterName) },
                    onClick = {
                        onSelect(sem)
                        expanded = false
                    },
                    leadingIcon = {
                        if (selectedSemester?.id == sem.id) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryNavy)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(PrimaryNavy.copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
