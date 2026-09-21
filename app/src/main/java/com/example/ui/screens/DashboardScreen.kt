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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.TeacherViewModel
import com.example.ui.theme.CardBorder
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.PresentGreenBg
import com.example.ui.theme.PrimaryLight
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.TertiaryAmber
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: TeacherViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val teacherName by viewModel.teacherName.collectAsStateWithLifecycle()
    val departments by viewModel.departments.collectAsStateWithLifecycle()
    val semesters by viewModel.allSemesters.collectAsStateWithLifecycle()
    val subjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    val students by viewModel.allStudents.collectAsStateWithLifecycle()
    val totalClassesCount by viewModel.totalClassesHeld.collectAsStateWithLifecycle()
    val attendanceRecords by viewModel.currentSubjectAttendance.collectAsStateWithLifecycle()
    val distinctDates by viewModel.distinctAttendanceDates.collectAsStateWithLifecycle()

    // Aggregate statistics (Total counts across all departments and semesters)
    val totalDepartments = departments.size
    val totalSemesters = semesters.size
    val totalSubjects = subjects.size
    val totalStudents = students.size
    val totalClassesHeld = totalClassesCount

    val presentCount = attendanceRecords.count { it.status == "P" }
    val totalAttendanceRecords = attendanceRecords.size
    val overallAttendancePercentage = if (totalAttendanceRecords > 0) {
        (presentCount.toDouble() / totalAttendanceRecords.toDouble()) * 100.0
    } else 0.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Teacher Welcome Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_welcome_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryNavy
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Teacher Icon",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Welcome Back,",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = teacherName.ifEmpty { "Teacher" },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Semester Academic Session 2026",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Overview Key Stats (Cards Grid)
        item {
            Text(
                text = "Overview Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Departments",
                    value = "$totalDepartments",
                    icon = Icons.Default.Domain,
                    iconTint = PrimaryLight,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("stat_departments")
                        .clickable { onNavigate("departments") }
                )
                StatCard(
                    title = "Semesters",
                    value = "$totalSemesters",
                    icon = Icons.Default.Timeline,
                    iconTint = SecondaryTeal,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("stat_semesters")
                        .clickable { onNavigate("departments") }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Subjects",
                    value = "$totalSubjects",
                    icon = Icons.Default.Book,
                    iconTint = TertiaryAmber,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("stat_subjects")
                        .clickable { onNavigate("departments") }
                )
                StatCard(
                    title = "Students",
                    value = "$totalStudents",
                    icon = Icons.Default.Groups,
                    iconTint = Color(0xFF8B5CF6),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("stat_students")
                        .clickable { onNavigate("students") }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Classes Held",
                    value = "$totalClassesHeld",
                    icon = Icons.Default.EventNote,
                    iconTint = Color(0xFF0284C7),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("stat_classes_held")
                        .clickable { onNavigate("attendance") }
                )
                StatCard(
                    title = "Attendance %",
                    value = String.format(Locale.US, "%.1f%%", overallAttendancePercentage),
                    icon = Icons.Default.CheckCircle,
                    iconTint = PresentGreen,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("stat_attendance_pct")
                        .clickable { onNavigate("attendance") }
                )
            }
        }

        // Attendance Progress Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("overall_attendance_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Overall Attendance Rate",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PresentGreenBg)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = String.format(Locale.US, "%.2f%%", overallAttendancePercentage),
                                color = PresentGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { (overallAttendancePercentage / 100.0).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = PresentGreen,
                        trackColor = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$presentCount present marks out of $totalAttendanceRecords total recorded sessions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Quick Actions Section
        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionButton(
                        title = "+ Add Student",
                        subtitle = "Enroll to semester",
                        icon = Icons.Default.PersonAdd,
                        containerColor = PrimaryNavy,
                        contentColor = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_add_student"),
                        onClick = { onNavigate("students") }
                    )
                    QuickActionButton(
                        title = "+ Take Attendance",
                        subtitle = "Daily P/A rollcall",
                        icon = Icons.Default.CheckCircle,
                        containerColor = SecondaryTeal,
                        contentColor = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_take_attendance"),
                        onClick = { onNavigate("attendance") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionButton(
                        title = "+ Add Marks",
                        subtitle = "CT1, CT2, CT3 & Mid",
                        icon = Icons.Default.Grade,
                        containerColor = TertiaryAmber,
                        contentColor = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_add_marks"),
                        onClick = { onNavigate("marks") }
                    )
                    QuickActionButton(
                        title = "View Reports",
                        subtitle = "Semester & export",
                        icon = Icons.Default.Assessment,
                        containerColor = Color(0xFF475569),
                        contentColor = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_view_reports"),
                        onClick = { onNavigate("reports") }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconTint.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}
