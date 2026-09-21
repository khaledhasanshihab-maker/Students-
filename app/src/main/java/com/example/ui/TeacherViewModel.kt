package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.SessionManager
import com.example.data.model.Attendance
import com.example.data.model.Department
import com.example.data.model.Marks
import com.example.data.model.Semester
import com.example.data.model.Student
import com.example.data.model.Subject
import com.example.data.model.User
import com.example.data.repository.StudentReportItem
import com.example.data.repository.TeacherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TeacherViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val repository = TeacherRepository(database)
    val sessionManager = SessionManager(application)

    private val _isLoggedIn = MutableStateFlow(sessionManager.isLoggedIn)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentTeacherId = MutableStateFlow(sessionManager.teacherId)
    val currentTeacherId: StateFlow<Long> = _currentTeacherId.asStateFlow()

    private val _teacherName = MutableStateFlow(sessionManager.teacherName)
    val teacherName: StateFlow<String> = _teacherName.asStateFlow()

    private val _teacherEmail = MutableStateFlow(sessionManager.teacherEmail)
    val teacherEmail: StateFlow<String> = _teacherEmail.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    // ---------------- SELECTIONS ----------------
    val selectedDepartment = MutableStateFlow<Department?>(null)
    val selectedSemester = MutableStateFlow<Semester?>(null)
    val selectedSubject = MutableStateFlow<Subject?>(null)

    // Current date formatted YYYY-MM-DD
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val selectedAttendanceDate = MutableStateFlow(dateFormat.format(Date()))
    val selectedAttendanceType = MutableStateFlow("Theory") // "Theory" or "Practical"

    // Current attendance edit map: Student ID -> "P" / "A"
    val attendanceStatusMap = MutableStateFlow<Map<Long, String>>(emptyMap())

    init {
        // If not logged in but DB has default teacher, check or auto-login default for instant ease of use
        viewModelScope.launch(Dispatchers.IO) {
            if (!_isLoggedIn.value) {
                // Check if user exists
                val defaultUser = database.userDao().getUserByEmail("teacher@school.edu")
                if (defaultUser != null) {
                    setSession(defaultUser)
                }
            }
        }
    }

    private fun setSession(user: User) {
        sessionManager.saveSession(user.id, user.name, user.email)
        _currentTeacherId.value = user.id
        _teacherName.value = user.name
        _teacherEmail.value = user.email
        _isLoggedIn.value = true
    }

    fun showToast(msg: String) {
        viewModelScope.launch {
            _toastMessage.emit(msg)
        }
    }

    // ---------------- AUTH ----------------
    fun login(email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.login(email, pass)
            result.onSuccess { user ->
                setSession(user)
                showToast("Welcome back, ${user.name}!")
                onSuccess()
            }.onFailure { err ->
                showToast(err.message ?: "Login failed.")
            }
        }
    }

    fun register(name: String, email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.register(name, email, pass)
            result.onSuccess { user ->
                setSession(user)
                showToast("Account created successfully!")
                onSuccess()
            }.onFailure { err ->
                showToast(err.message ?: "Registration failed.")
            }
        }
    }

    fun forgotPassword(email: String, newPass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.resetPassword(email, newPass)
            result.onSuccess {
                showToast("Password updated. You can now login.")
                onSuccess()
            }.onFailure { err ->
                showToast(err.message ?: "Failed to reset password.")
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _isLoggedIn.value = false
        _currentTeacherId.value = -1L
        _teacherName.value = ""
        _teacherEmail.value = ""
        selectedDepartment.value = null
        selectedSemester.value = null
        selectedSubject.value = null
        showToast("Logged out successfully.")
    }

    // ---------------- REACTIVE FLOWS ----------------
    val departments: StateFlow<List<Department>> = _currentTeacherId.flatMapLatest { id ->
        if (id > 0) repository.getDepartments(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val semesters: StateFlow<List<Semester>> = selectedDepartment.flatMapLatest { dept ->
        if (dept != null) repository.getSemesters(dept.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSemesters: StateFlow<List<Semester>> = _currentTeacherId.flatMapLatest { id ->
        if (id > 0) repository.getAllSemesters(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubjects: StateFlow<List<Subject>> = _currentTeacherId.flatMapLatest { id ->
        if (id > 0) repository.getSubjects(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudents: StateFlow<List<Student>> = _currentTeacherId.flatMapLatest { id ->
        if (id > 0) repository.getStudentsByTeacher(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalClassesHeld: StateFlow<Int> = _currentTeacherId.flatMapLatest { id ->
        if (id > 0) repository.getTotalClassesForTeacher(id) else flowOf(0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val subjects: StateFlow<List<Subject>> = combine(
        selectedDepartment,
        selectedSemester
    ) { dept, sem ->
        dept to sem
    }.flatMapLatest { (dept, sem) ->
        if (dept != null && sem != null) {
            repository.getSubjectsByDeptAndSem(dept.id, sem.id)
        } else if (_currentTeacherId.value > 0) {
            repository.getSubjects(_currentTeacherId.value)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val students: StateFlow<List<Student>> = combine(
        selectedDepartment,
        selectedSemester
    ) { dept, sem ->
        dept to sem
    }.flatMapLatest { (dept, sem) ->
        if (dept != null && sem != null) {
            repository.getStudents(dept.id, sem.id)
        } else if (_currentTeacherId.value > 0) {
            repository.getStudentsByTeacher(_currentTeacherId.value)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ---------------- ATTENDANCE STATE ----------------
    val currentSubjectAttendance: StateFlow<List<Attendance>> = combine(
        selectedSubject,
        selectedAttendanceType
    ) { sub, type ->
        sub to type
    }.flatMapLatest { (sub, type) ->
        if (sub != null) repository.getAttendanceForSubjectAndType(sub.id, type) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val distinctAttendanceDates: StateFlow<List<String>> = combine(
        selectedSubject,
        selectedAttendanceType
    ) { sub, type ->
        sub to type
    }.flatMapLatest { (sub, type) ->
        if (sub != null) repository.getDistinctDatesForType(sub.id, type) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentSubjectMarks: StateFlow<List<Marks>> = selectedSubject.flatMapLatest { sub ->
        if (sub != null) repository.getMarksForSubject(sub.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadAttendanceForDate(subjectId: Long, date: String, classType: String = selectedAttendanceType.value) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = database.attendanceDao().getAttendanceForSubjectDateAndTypeSync(subjectId, date, classType)
            val studentsList = students.value
            val newMap = mutableMapOf<Long, String>()

            if (existing.isNotEmpty()) {
                existing.forEach { att ->
                    newMap[att.studentId] = att.status
                }
                // For any student who didn't have record for that date, default to P
                studentsList.forEach { s ->
                    if (!newMap.containsKey(s.id)) {
                        newMap[s.id] = "P"
                    }
                }
            } else {
                // New date -> default all students to "P" (Present)
                studentsList.forEach { s ->
                    newMap[s.id] = "P"
                }
            }
            attendanceStatusMap.value = newMap
        }
    }

    fun setStudentAttendanceStatus(studentId: Long, status: String) {
        val current = attendanceStatusMap.value.toMutableMap()
        current[studentId] = if (status == "A") "A" else "P"
        attendanceStatusMap.value = current
    }

    fun markAllPresent() {
        val current = attendanceStatusMap.value.toMutableMap()
        students.value.forEach { s ->
            current[s.id] = "P"
        }
        attendanceStatusMap.value = current
    }

    fun markAllAbsent() {
        val current = attendanceStatusMap.value.toMutableMap()
        students.value.forEach { s ->
            current[s.id] = "A"
        }
        attendanceStatusMap.value = current
    }

    fun saveCurrentAttendance(onSuccess: () -> Unit = {}) {
        val sub = selectedSubject.value
        if (sub == null) {
            showToast("Please select a subject first.")
            return
        }
        val date = selectedAttendanceDate.value.trim()
        if (date.isEmpty()) {
            showToast("Please enter a valid class date.")
            return
        }
        val type = selectedAttendanceType.value
        val map = attendanceStatusMap.value
        if (map.isEmpty()) {
            showToast("No students to save attendance for.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            repository.saveAttendance(sub.id, date, map, type)
            showToast("$type Attendance saved successfully for $date!")
            onSuccess()
        }
    }

    fun deleteAttendanceRecord(date: String, classType: String = selectedAttendanceType.value) {
        val sub = selectedSubject.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAttendanceForDate(sub.id, date, classType)
            showToast("$classType attendance for $date deleted.")
            loadAttendanceForDate(sub.id, selectedAttendanceDate.value, classType)
        }
    }

    // ---------------- MARKS OPERATIONS ----------------
    fun saveMarks(
        studentId: Long,
        ct1: Double?,
        ct2: Double?,
        ct3: Double?,
        midTerm: Double?,
        maxMark: Double = 100.0,
        onSuccess: () -> Unit = {}
    ) {
        val sub = selectedSubject.value
        if (sub == null) {
            showToast("Please select a subject first.")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.saveMarks(
                studentId = studentId,
                subjectId = sub.id,
                ct1 = ct1,
                ct2 = ct2,
                ct3 = ct3,
                midTerm = midTerm,
                maxMark = maxMark
            )
            res.onSuccess {
                showToast("Marks updated successfully!")
                onSuccess()
            }.onFailure { err ->
                showToast(err.message ?: "Failed to save marks.")
            }
        }
    }

    // ---------------- CRUD DEPARTMENT ----------------
    fun addDepartment(name: String, onSuccess: () -> Unit = {}) {
        val teacherId = _currentTeacherId.value
        if (teacherId <= 0) return
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.addDepartment(teacherId, name)
            res.onSuccess { id ->
                showToast("Department added successfully!")
                onSuccess()
            }.onFailure { err ->
                showToast(err.message ?: "Error adding department.")
            }
        }
    }

    fun updateDepartment(department: Department, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.updateDepartment(department)
            res.onSuccess {
                showToast("Department updated.")
                if (selectedDepartment.value?.id == department.id) {
                    selectedDepartment.value = department
                }
                onSuccess()
            }.onFailure { err ->
                showToast(err.message ?: "Error updating department.")
            }
        }
    }

    fun deleteDepartment(department: Department) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDepartment(department)
            if (selectedDepartment.value?.id == department.id) {
                selectedDepartment.value = null
                selectedSemester.value = null
                selectedSubject.value = null
            }
            showToast("Department '${department.departmentName}' deleted.")
        }
    }

    // ---------------- CRUD SEMESTER ----------------
    fun addSemester(
        deptId: Long,
        name: String,
        autoAssignCurriculum: Boolean = true,
        onSuccess: (semId: Long) -> Unit = {}
    ) {
        val teacherId = _currentTeacherId.value
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.addSemester(deptId, name)
            res.onSuccess { semId ->
                var msg = "Semester added successfully!"
                if (autoAssignCurriculum) {
                    val deptName = selectedDepartment.value?.departmentName ?: ""
                    val predefinedList = com.example.data.model.DiplomaCurricula.getSubjectsForDeptAndSemester(deptName, name)
                        ?: com.example.data.model.DiplomaCstCurriculum.getSubjectsForSemester(name)
                    if (!predefinedList.isNullOrEmpty()) {
                        var added = 0
                        for (sub in predefinedList) {
                            repository.addSubject(
                                deptId = deptId,
                                semId = semId,
                                teacherId = teacherId,
                                name = sub.subjectName,
                                code = sub.subjectCode
                            )
                            added++
                        }
                        if (added > 0) {
                            msg = "Semester added with $added default curriculum subjects!"
                        }
                    }
                }
                showToast(msg)
                withContext(Dispatchers.Main) {
                    onSuccess(semId)
                }
            }.onFailure { err ->
                showToast(err.message ?: "Error adding semester.")
            }
        }
    }

    fun updateSemester(semester: Semester, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.updateSemester(semester)
            res.onSuccess {
                showToast("Semester updated.")
                if (selectedSemester.value?.id == semester.id) {
                    selectedSemester.value = semester
                }
                onSuccess()
            }.onFailure { err ->
                showToast(err.message ?: "Error updating semester.")
            }
        }
    }

    fun deleteSemester(semester: Semester) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSemester(semester)
            if (selectedSemester.value?.id == semester.id) {
                selectedSemester.value = null
                selectedSubject.value = null
            }
            showToast("Semester '${semester.semesterName}' deleted.")
        }
    }

    fun assignCurriculumSubjectsForSemester(
        deptId: Long,
        semId: Long,
        semesterName: String,
        deptName: String? = null,
        onSuccess: (count: Int) -> Unit = {}
    ) {
        val teacherId = _currentTeacherId.value
        val actualDeptName = deptName ?: selectedDepartment.value?.departmentName ?: ""
        val predefinedList = com.example.data.model.DiplomaCurricula.getSubjectsForDeptAndSemester(actualDeptName, semesterName)
            ?: com.example.data.model.DiplomaCstCurriculum.getSubjectsForSemester(semesterName)

        if (predefinedList.isNullOrEmpty()) {
            showToast("No predefined syllabus found for '$semesterName'.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val existing = repository.db.subjectDao().getSubjectsByDepartmentAndSemesterSync(deptId, semId)
            val existingCodes = existing.map { it.subjectCode.trim().lowercase() }.toSet()
            val existingNames = existing.map { it.subjectName.trim().lowercase() }.toSet()

            var addedCount = 0
            for (sub in predefinedList) {
                if (!existingCodes.contains(sub.subjectCode.trim().lowercase()) &&
                    !existingNames.contains(sub.subjectName.trim().lowercase())
                ) {
                    repository.addSubject(
                        deptId = deptId,
                        semId = semId,
                        teacherId = teacherId,
                        name = sub.subjectName,
                        code = sub.subjectCode
                    )
                    addedCount++
                }
            }
            withContext(Dispatchers.Main) {
                if (addedCount > 0) {
                    showToast("Successfully assigned $addedCount curriculum subjects for $semesterName!")
                } else {
                    showToast("All curriculum subjects for $semesterName are already assigned.")
                }
                onSuccess(addedCount)
            }
        }
    }

    fun populateAllCurriculumForDepartment(
        deptId: Long,
        deptName: String? = null,
        onSuccess: (semCount: Int, subCount: Int) -> Unit = { _, _ -> }
    ) {
        val teacherId = _currentTeacherId.value
        val actualDeptName = deptName ?: selectedDepartment.value?.departmentName ?: "Computer"
        val matchedCurriculum = com.example.data.model.DiplomaCurricula.findCurriculumForDepartment(actualDeptName)
            ?: com.example.data.model.DiplomaCurricula.computerCurriculum

        viewModelScope.launch(Dispatchers.IO) {
            val existingSemesters = repository.db.semesterDao().getSemestersByDepartmentSync(deptId)
            var addedSems = 0
            var addedSubs = 0

            for (semNum in 1..7) {
                val semName = when (semNum) {
                    1 -> "1st Semester"
                    2 -> "2nd Semester"
                    3 -> "3rd Semester"
                    4 -> "4th Semester"
                    5 -> "5th Semester"
                    6 -> "6th Semester"
                    7 -> "7th Semester"
                    else -> "$semNum" + "th Semester"
                }

                var sem = existingSemesters.firstOrNull { it.semesterName.equals(semName, ignoreCase = true) }
                val semId = if (sem == null) {
                    val newId = repository.db.semesterDao().insertSemester(
                        Semester(
                            departmentId = deptId,
                            semesterName = semName
                        )
                    )
                    addedSems++
                    newId
                } else {
                    sem.id
                }

                val subjectsForSem = matchedCurriculum.semestersWithSubjects[semName] ?: emptyList()
                val existingSubs = repository.db.subjectDao().getSubjectsByDepartmentAndSemesterSync(deptId, semId)
                val existingCodes = existingSubs.map { it.subjectCode.trim().lowercase() }.toSet()
                val existingNames = existingSubs.map { it.subjectName.trim().lowercase() }.toSet()

                for (sub in subjectsForSem) {
                    if (!existingCodes.contains(sub.subjectCode.trim().lowercase()) &&
                        !existingNames.contains(sub.subjectName.trim().lowercase())
                    ) {
                        repository.addSubject(
                            deptId = deptId,
                            semId = semId,
                            teacherId = teacherId,
                            name = sub.subjectName,
                            code = sub.subjectCode
                        )
                        addedSubs++
                    }
                }
            }

            withContext(Dispatchers.Main) {
                showToast("Populated $addedSems semesters & $addedSubs subjects for ${matchedCurriculum.departmentName}!")
                onSuccess(addedSems, addedSubs)
            }
        }
    }

    // ---------------- CRUD SUBJECT ----------------
    fun addSubject(
        deptId: Long,
        semId: Long,
        name: String,
        code: String,
        onSuccess: () -> Unit = {}
    ) {
        val teacherId = _currentTeacherId.value
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.addSubject(deptId, semId, teacherId, name, code)
            res.onSuccess {
                showToast("Subject added successfully!")
                onSuccess()
            }.onFailure { err ->
                showToast(err.message ?: "Error adding subject.")
            }
        }
    }

    fun updateSubject(subject: Subject, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.updateSubject(subject)
            res.onSuccess {
                showToast("Subject updated.")
                if (selectedSubject.value?.id == subject.id) {
                    selectedSubject.value = subject
                }
                onSuccess()
            }.onFailure { err ->
                showToast(err.message ?: "Error updating subject.")
            }
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSubject(subject)
            if (selectedSubject.value?.id == subject.id) {
                selectedSubject.value = null
            }
            showToast("Subject '${subject.subjectName}' deleted.")
        }
    }

    // ---------------- CRUD STUDENT ----------------
    fun addStudent(
        deptId: Long,
        semId: Long,
        roll: String,
        reg: String?,
        name: String,
        onSuccess: () -> Unit = {}
    ) {
        val teacherId = _currentTeacherId.value
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.addStudent(teacherId, deptId, semId, roll, reg, name)
            res.onSuccess {
                showToast("Student '$name' (Roll: $roll) added!")
                onSuccess()
            }.onFailure { err ->
                showToast(err.message ?: "Error adding student.")
            }
        }
    }

    fun bulkAddStudents(
        deptId: Long,
        semId: Long,
        studentList: List<com.example.util.ParsedStudent>,
        onComplete: (successCount: Int, skippedCount: Int, errors: List<String>) -> Unit
    ) {
        val teacherId = _currentTeacherId.value
        viewModelScope.launch(Dispatchers.IO) {
            var successCount = 0
            var skippedCount = 0
            val errorMessages = mutableListOf<String>()

            for (item in studentList) {
                val res = repository.addStudent(
                    teacherId = teacherId,
                    deptId = deptId,
                    semId = semId,
                    roll = item.rollNumber,
                    reg = item.registrationNumber,
                    name = item.studentName
                )
                if (res.isSuccess) {
                    successCount++
                } else {
                    skippedCount++
                    val err = res.exceptionOrNull()?.message ?: "Unknown error"
                    errorMessages.add("Roll ${item.rollNumber} (${item.studentName}): $err")
                }
            }

            withContext(Dispatchers.Main) {
                if (successCount > 0 && skippedCount == 0) {
                    showToast("Successfully enrolled all $successCount students!")
                } else if (successCount > 0) {
                    showToast("Enrolled $successCount students. Skipped $skippedCount duplicates/errors.")
                } else {
                    showToast("Could not import students: All entries failed or already exist.")
                }
                onComplete(successCount, skippedCount, errorMessages)
            }
        }
    }

    fun updateStudent(student: Student, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.updateStudent(student)
            res.onSuccess {
                showToast("Student details updated.")
                onSuccess()
            }.onFailure { err ->
                showToast(err.message ?: "Error updating student.")
            }
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteStudent(student)
            showToast("Student '${student.studentName}' deleted.")
        }
    }

    // ---------------- RESET SAMPLE DATA ----------------
    fun resetToSampleData() {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.seedSampleData(database)
            val teacher = database.userDao().getUserByEmail("teacher@school.edu")
            if (teacher != null) {
                setSession(teacher)
            }
            showToast("Sample data loaded successfully!")
        }
    }

    // ---------------- RELOAD AFTER RESTORE ----------------
    fun reloadDatabaseSession() {
        viewModelScope.launch(Dispatchers.IO) {
            // Find current teacher or first available teacher in restored database
            val curEmail = _teacherEmail.value
            val user = if (curEmail.isNotEmpty()) {
                database.userDao().getUserByEmail(curEmail)
            } else null

            val restoredUser = user ?: database.userDao().getUserByEmail("teacher@school.edu")
            if (restoredUser != null) {
                setSession(restoredUser)
            } else {
                // If no matching user, query if any user exists or reset teacher id to trigger flow re-query
                val anyId = _currentTeacherId.value
                if (anyId > 0) {
                    _currentTeacherId.value = anyId
                }
            }
            selectedDepartment.value = null
            selectedSemester.value = null
            selectedSubject.value = null
            showToast("Database restored and reloaded successfully!")
        }
    }
}
