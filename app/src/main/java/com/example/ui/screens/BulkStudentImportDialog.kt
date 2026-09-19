package com.example.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Department
import com.example.data.model.Semester
import com.example.ui.TeacherViewModel
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.CardBorder
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.PrimaryNavy
import com.example.util.ParsedStudent
import com.example.util.StudentImportParser

@Composable
fun BulkStudentImportDialog(
    department: Department,
    semester: Semester,
    viewModel: TeacherViewModel,
    onDismiss: () -> Unit,
    onSuccess: (count: Int) -> Unit
) {
    val context = LocalContext.current
    var activeTab by remember { mutableIntStateOf(0) } // 0: File Pick, 1: Paste Text / CSV

    var manualText by remember {
        mutableStateOf(
            "Roll,Name,Registration\n" +
            "101,Rahim Ahmed,150201\n" +
            "102,Karim Hasan,150202\n" +
            "103,Hasan Mahmud,150203\n" +
            "104,Fatima Akter,150204\n" +
            "105,Tanvir Islam,150205"
        )
    }

    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var parsedStudents by remember { mutableStateOf<List<ParsedStudent>>(emptyList()) }
    var parseErrors by remember { mutableStateOf<List<String>>(emptyList()) }
    var isImporting by remember { mutableStateOf(false) }

    // File picker launcher for Excel/CSV/TSV/Text
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val result = StudentImportParser.parseStream(inputStream)
                    parsedStudents = result.parsedStudents
                    parseErrors = result.errors
                    selectedFileName = uri.lastPathSegment ?: "Selected File"
                }
            } catch (e: Exception) {
                parseErrors = listOf("Failed to read file: ${e.localizedMessage}")
            }
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isImporting) onDismiss() },
        title = {
            Column {
                Text("Bulk Student Import (এক্সেলে বাল্ক এনরোল)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "${department.departmentName} • ${semester.semesterName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = PrimaryNavy
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Choose File (XLS/CSV)") },
                        icon = { Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Paste / Edit Data") },
                        icon = { Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }

                if (activeTab == 0) {
                    // FILE PICKER TAB
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(PrimaryNavy.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.FileUpload, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(28.dp))
                            }

                            Text(
                                text = "Select Excel (.xls / .xlsx XML) or .csv file",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = "Columns expected: Roll, Student Name, Registration (optional)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = {
                                    filePickerLauncher.launch("*/*")
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                                modifier = Modifier.testTag("pick_excel_file_button")
                            ) {
                                Icon(Icons.Default.FileUpload, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (selectedFileName == null) "Select Excel / CSV File" else "Choose Another File")
                            }

                            selectedFileName?.let {
                                Text(
                                    text = "Selected: $it",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryNavy
                                )
                            }
                        }
                    }
                } else {
                    // PASTE / EDIT TEXT TAB
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Paste comma or tab separated student data (Roll, Name, Registration):",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = manualText,
                            onValueChange = {
                                manualText = it
                                // re-parse automatically
                                val stream = it.byteInputStream(Charsets.UTF_8)
                                val res = StudentImportParser.parseStream(stream)
                                parsedStudents = res.parsedStudents
                                parseErrors = res.errors
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .testTag("paste_student_data_field"),
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            placeholder = { Text("Roll, Name, Registration") },
                            shape = RoundedCornerShape(8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Format: Roll, Name, Reg",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(
                                onClick = {
                                    manualText = StudentImportParser.getSampleCsvContent()
                                    val stream = manualText.byteInputStream(Charsets.UTF_8)
                                    val res = StudentImportParser.parseStream(stream)
                                    parsedStudents = res.parsedStudents
                                    parseErrors = res.errors
                                }
                            ) {
                                Text("Load Sample Template", fontSize = 11.sp)
                            }
                        }
                    }
                }

                // PARSE SUMMARY PREVIEW
                if (parsedStudents.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = PresentGreen.copy(alpha = 0.08f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PresentGreen.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PresentGreen, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Ready to enroll ${parsedStudents.size} students",
                                    fontWeight = FontWeight.Bold,
                                    color = PresentGreen,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 110.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(parsedStudents.take(15)) { s ->
                                    Text(
                                        text = "• Roll ${s.rollNumber}: ${s.studentName}${if (s.registrationNumber != null) " (Reg: ${s.registrationNumber})" else ""}",
                                        fontSize = 12.sp
                                    )
                                }
                                if (parsedStudents.size > 15) {
                                    item {
                                        Text("... and ${parsedStudents.size - 15} more students", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                if (parseErrors.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = AbsentRed.copy(alpha = 0.08f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AbsentRed.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = AbsentRed, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Notes / Warnings (${parseErrors.size})",
                                    fontWeight = FontWeight.Bold,
                                    color = AbsentRed,
                                    fontSize = 12.sp
                                )
                            }
                            parseErrors.take(3).forEach { err ->
                                Text("• $err", fontSize = 11.sp, color = AbsentRed)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (activeTab == 1 && parsedStudents.isEmpty()) {
                        // parse current manual text
                        val stream = manualText.byteInputStream(Charsets.UTF_8)
                        val res = StudentImportParser.parseStream(stream)
                        parsedStudents = res.parsedStudents
                        parseErrors = res.errors
                    }

                    if (parsedStudents.isEmpty()) {
                        viewModel.showToast("Please choose a valid file or paste student list first.")
                        return@Button
                    }

                    isImporting = true
                    viewModel.bulkAddStudents(
                        deptId = department.id,
                        semId = semester.id,
                        studentList = parsedStudents
                    ) { successCount, skipped, errors ->
                        isImporting = false
                        if (successCount > 0) {
                            onSuccess(successCount)
                            onDismiss()
                        }
                    }
                },
                enabled = !isImporting && (parsedStudents.isNotEmpty() || manualText.isNotBlank()),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                modifier = Modifier.testTag("execute_bulk_import_btn")
            ) {
                Text(if (isImporting) "Importing..." else "Enroll All (${if (parsedStudents.isNotEmpty()) parsedStudents.size else "Students"})")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isImporting
            ) {
                Text("Cancel")
            }
        }
    )
}
