package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Student
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DepartmentsScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MarksScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StudentsScreen
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryTeal

enum class AppDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    DASHBOARD("dashboard", "Dashboard", Icons.Default.Dashboard),
    COURSES("departments", "Courses", Icons.Default.Domain),
    STUDENTS("students", "Students", Icons.Default.Groups),
    ATTENDANCE("attendance", "Attendance", Icons.Default.CheckCircle),
    MARKS("marks", "Marks", Icons.Default.Grade),
    REPORTS("reports", "Reports", Icons.Default.Assessment)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    viewModel: TeacherViewModel,
    modifier: Modifier = Modifier
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val teacherName by viewModel.teacherName.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var currentScreen by remember { mutableStateOf(AppDestination.DASHBOARD.route) }
    var selectedStudentForDetail by remember { mutableStateOf<Student?>(null) }
    var showSplash by remember { mutableStateOf(true) }

    // Listen to ViewModel Toast events and display via Snackbar
    LaunchedEffect(viewModel) {
        viewModel.toastMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    if (showSplash) {
        SplashScreen(
            onSplashFinished = { showSplash = false },
            modifier = modifier
        )
    } else if (!isLoggedIn) {
        LoginScreen(
            viewModel = viewModel,
            modifier = modifier
        )
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Teacher Student Manager",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = teacherName.ifEmpty { "Instructor" },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { currentScreen = AppDestination.DASHBOARD.route }) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Dashboard",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { currentScreen = "settings" },
                            modifier = Modifier.testTag("topbar_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = PrimaryNavy
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    AppDestination.entries.forEach { destination ->
                        val selected = currentScreen == destination.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = { currentScreen = destination.route },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.title,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = destination.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryNavy,
                                selectedTextColor = PrimaryNavy,
                                indicatorColor = PrimaryNavy.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.testTag("nav_item_${destination.route}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "ScreenTransition"
                ) { target ->
                    when (target) {
                        AppDestination.DASHBOARD.route -> {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigate = { route -> currentScreen = route }
                            )
                        }
                        AppDestination.COURSES.route -> {
                            DepartmentsScreen(viewModel = viewModel)
                        }
                        AppDestination.STUDENTS.route -> {
                            StudentsScreen(
                                viewModel = viewModel,
                                onStudentClick = { student ->
                                    selectedStudentForDetail = student
                                }
                            )
                        }
                        AppDestination.ATTENDANCE.route -> {
                            AttendanceScreen(viewModel = viewModel)
                        }
                        AppDestination.MARKS.route -> {
                            MarksScreen(viewModel = viewModel)
                        }
                        AppDestination.REPORTS.route -> {
                            ReportsScreen(viewModel = viewModel)
                        }
                        "settings" -> {
                            SettingsScreen(viewModel = viewModel)
                        }
                        else -> {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigate = { route -> currentScreen = route }
                            )
                        }
                    }
                }
            }
        }

        // Quick Student Detail Dialog from Students list
        selectedStudentForDetail?.let { student ->
            AlertDialog(
                onDismissRequest = { selectedStudentForDetail = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                            Text(student.studentName, fontWeight = FontWeight.Bold)
                            student.registrationNumber?.let {
                                Text("Reg: $it", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Quick Actions for ${student.studentName}:", style = MaterialTheme.typography.bodySmall)
                        Button(
                            onClick = {
                                selectedStudentForDetail = null
                                currentScreen = AppDestination.ATTENDANCE.route
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Take Attendance")
                        }
                        Button(
                            onClick = {
                                selectedStudentForDetail = null
                                currentScreen = AppDestination.MARKS.route
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Enter / View Marks")
                        }
                        Button(
                            onClick = {
                                selectedStudentForDetail = null
                                currentScreen = AppDestination.REPORTS.route
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Open Complete Report")
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { selectedStudentForDetail = null }) {
                        Text("Done")
                    }
                }
            )
        }
    }
}
