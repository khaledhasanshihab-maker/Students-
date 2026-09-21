package com.example.data.repository

import com.example.data.AppDatabase
import com.example.data.model.Attendance
import com.example.data.model.Department
import com.example.data.model.Marks
import com.example.data.model.Semester
import com.example.data.model.Student
import com.example.data.model.Subject
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow
import java.util.Locale

data class StudentReportItem(
    val student: Student,
    val totalClasses: Int,
    val presentCount: Int,
    val absentCount: Int,
    val attendancePercentage: Double,
    val ct1: Double?,
    val ct2: Double?,
    val ct3: Double?,
    val ctAverage: Double?,
    val midTerm: Double?
)

class TeacherRepository(val db: AppDatabase) {
    private val userDao = db.userDao()
    private val departmentDao = db.departmentDao()
    private val semesterDao = db.semesterDao()
    private val subjectDao = db.subjectDao()
    private val studentDao = db.studentDao()
    private val attendanceDao = db.attendanceDao()
    private val marksDao = db.marksDao()

    // ---------------- AUTH ----------------
    suspend fun login(email: String, pass: String): Result<User> {
        val trimmedEmail = email.trim()
        val trimmedPass = pass.trim()
        if (trimmedEmail.isEmpty() || trimmedPass.isEmpty()) {
            return Result.failure(IllegalArgumentException("Email and password cannot be empty."))
        }
        val user = userDao.getUserByEmail(trimmedEmail)
            ?: return Result.failure(IllegalArgumentException("No account found with this email."))
        if (user.password != trimmedPass) {
            return Result.failure(IllegalArgumentException("Incorrect password."))
        }
        return Result.success(user)
    }

    suspend fun register(name: String, email: String, pass: String): Result<User> {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val trimmedPass = pass.trim()
        if (trimmedName.isEmpty()) return Result.failure(IllegalArgumentException("Name cannot be empty."))
        if (trimmedEmail.isEmpty() || !trimmedEmail.contains("@")) return Result.failure(IllegalArgumentException("Please enter a valid email."))
        if (trimmedPass.length < 4) return Result.failure(IllegalArgumentException("Password must be at least 4 characters."))

        val existing = userDao.getUserByEmail(trimmedEmail)
        if (existing != null) {
            return Result.failure(IllegalArgumentException("An account with this email already exists."))
        }
        val id = userDao.insertUser(User(name = trimmedName, email = trimmedEmail, password = trimmedPass))
        return Result.success(User(id = id, name = trimmedName, email = trimmedEmail, password = trimmedPass))
    }

    suspend fun resetPassword(email: String, newPass: String): Result<Unit> {
        val trimmedEmail = email.trim()
        val trimmedPass = newPass.trim()
        if (trimmedEmail.isEmpty()) return Result.failure(IllegalArgumentException("Email cannot be empty."))
        if (trimmedPass.length < 4) return Result.failure(IllegalArgumentException("Password must be at least 4 characters."))

        val user = userDao.getUserByEmail(trimmedEmail)
            ?: return Result.failure(IllegalArgumentException("No account found with this email."))
        userDao.updateUser(user.copy(password = trimmedPass))
        return Result.success(Unit)
    }

    // ---------------- DEPARTMENTS ----------------
    fun getDepartments(teacherId: Long): Flow<List<Department>> =
        departmentDao.getDepartmentsByTeacher(teacherId)

    suspend fun addDepartment(teacherId: Long, name: String): Result<Long> {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return Result.failure(IllegalArgumentException("Department name cannot be empty."))
        val id = departmentDao.insertDepartment(Department(teacherId = teacherId, departmentName = trimmed))
        return Result.success(id)
    }

    suspend fun updateDepartment(department: Department): Result<Unit> {
        val trimmed = department.departmentName.trim()
        if (trimmed.isEmpty()) return Result.failure(IllegalArgumentException("Department name cannot be empty."))
        departmentDao.updateDepartment(department.copy(departmentName = trimmed))
        return Result.success(Unit)
    }

    suspend fun deleteDepartment(department: Department) =
        departmentDao.deleteDepartment(department)

    // ---------------- SEMESTERS ----------------
    fun getSemesters(departmentId: Long): Flow<List<Semester>> =
        semesterDao.getSemestersByDepartment(departmentId)

    fun getAllSemesters(teacherId: Long): Flow<List<Semester>> =
        semesterDao.getAllSemestersByTeacher(teacherId)

    suspend fun addSemester(departmentId: Long, name: String): Result<Long> {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return Result.failure(IllegalArgumentException("Semester name cannot be empty."))
        val id = semesterDao.insertSemester(Semester(departmentId = departmentId, semesterName = trimmed))
        return Result.success(id)
    }

    suspend fun updateSemester(semester: Semester): Result<Unit> {
        val trimmed = semester.semesterName.trim()
        if (trimmed.isEmpty()) return Result.failure(IllegalArgumentException("Semester name cannot be empty."))
        semesterDao.updateSemester(semester.copy(semesterName = trimmed))
        return Result.success(Unit)
    }

    suspend fun deleteSemester(semester: Semester) =
        semesterDao.deleteSemester(semester)

    // ---------------- SUBJECTS ----------------
    fun getSubjects(teacherId: Long): Flow<List<Subject>> =
        subjectDao.getSubjectsByTeacher(teacherId)

    fun getSubjectsByDeptAndSem(deptId: Long, semId: Long): Flow<List<Subject>> =
        subjectDao.getSubjectsByDepartmentAndSemester(deptId, semId)

    suspend fun addSubject(
        deptId: Long,
        semId: Long,
        teacherId: Long,
        name: String,
        code: String
    ): Result<Long> {
        val trimmedName = name.trim()
        val trimmedCode = code.trim()
        if (trimmedName.isEmpty()) return Result.failure(IllegalArgumentException("Subject name cannot be empty."))
        if (trimmedCode.isEmpty()) return Result.failure(IllegalArgumentException("Subject code cannot be empty."))
        val id = subjectDao.insertSubject(
            Subject(
                departmentId = deptId,
                semesterId = semId,
                teacherId = teacherId,
                subjectName = trimmedName,
                subjectCode = trimmedCode
            )
        )
        return Result.success(id)
    }

    suspend fun updateSubject(subject: Subject): Result<Unit> {
        val trimmedName = subject.subjectName.trim()
        val trimmedCode = subject.subjectCode.trim()
        if (trimmedName.isEmpty()) return Result.failure(IllegalArgumentException("Subject name cannot be empty."))
        if (trimmedCode.isEmpty()) return Result.failure(IllegalArgumentException("Subject code cannot be empty."))
        subjectDao.updateSubject(subject.copy(subjectName = trimmedName, subjectCode = trimmedCode))
        return Result.success(Unit)
    }

    suspend fun deleteSubject(subject: Subject) =
        subjectDao.deleteSubject(subject)

    // ---------------- STUDENTS ----------------
    fun getStudents(deptId: Long, semId: Long): Flow<List<Student>> =
        studentDao.getStudentsByDepartmentAndSemester(deptId, semId)

    fun getStudentsByTeacher(teacherId: Long): Flow<List<Student>> =
        studentDao.getStudentsByTeacher(teacherId)

    suspend fun addStudent(
        teacherId: Long,
        deptId: Long,
        semId: Long,
        roll: String,
        reg: String?,
        name: String
    ): Result<Long> {
        val trimmedRoll = roll.trim()
        val trimmedName = name.trim()
        val trimmedReg = reg?.trim()?.ifEmpty { null }

        if (trimmedName.isEmpty()) return Result.failure(IllegalArgumentException("Student name cannot be empty."))
        if (trimmedRoll.isEmpty()) return Result.failure(IllegalArgumentException("Roll number cannot be empty."))

        val duplicate = studentDao.checkDuplicateRoll(deptId, semId, trimmedRoll)
        if (duplicate > 0) {
            return Result.failure(IllegalArgumentException("Roll number '$trimmedRoll' already exists in this semester."))
        }

        val id = studentDao.insertStudent(
            Student(
                teacherId = teacherId,
                departmentId = deptId,
                semesterId = semId,
                rollNumber = trimmedRoll,
                registrationNumber = trimmedReg,
                studentName = trimmedName
            )
        )
        return Result.success(id)
    }

    suspend fun updateStudent(student: Student): Result<Unit> {
        val trimmedRoll = student.rollNumber.trim()
        val trimmedName = student.studentName.trim()
        val trimmedReg = student.registrationNumber?.trim()?.ifEmpty { null }

        if (trimmedName.isEmpty()) return Result.failure(IllegalArgumentException("Student name cannot be empty."))
        if (trimmedRoll.isEmpty()) return Result.failure(IllegalArgumentException("Roll number cannot be empty."))

        val duplicate = studentDao.checkDuplicateRoll(
            student.departmentId,
            student.semesterId,
            trimmedRoll,
            excludeId = student.id
        )
        if (duplicate > 0) {
            return Result.failure(IllegalArgumentException("Roll number '$trimmedRoll' already exists in this semester."))
        }

        studentDao.updateStudent(
            student.copy(
                rollNumber = trimmedRoll,
                studentName = trimmedName,
                registrationNumber = trimmedReg
            )
        )
        return Result.success(Unit)
    }

    suspend fun deleteStudent(student: Student) =
        studentDao.deleteStudent(student)

    // ---------------- ATTENDANCE ----------------
    fun getAttendanceForDate(subjectId: Long, date: String): Flow<List<Attendance>> =
        attendanceDao.getAttendanceForSubjectAndDate(subjectId, date)

    fun getAttendanceForDateAndType(subjectId: Long, date: String, classType: String): Flow<List<Attendance>> =
        attendanceDao.getAttendanceForSubjectDateAndType(subjectId, date, classType)

    fun getAttendanceForSubject(subjectId: Long): Flow<List<Attendance>> =
        attendanceDao.getAttendanceForSubject(subjectId)

    fun getAttendanceForSubjectAndType(subjectId: Long, classType: String): Flow<List<Attendance>> =
        attendanceDao.getAttendanceForSubjectAndType(subjectId, classType)

    fun getDistinctDates(subjectId: Long): Flow<List<String>> =
        attendanceDao.getDistinctDatesForSubject(subjectId)

    fun getDistinctDatesForType(subjectId: Long, classType: String): Flow<List<String>> =
        attendanceDao.getDistinctDatesForSubjectAndType(subjectId, classType)

    fun getAttendanceForStudent(studentId: Long): Flow<List<Attendance>> =
        attendanceDao.getAttendanceForStudent(studentId)

    fun getAttendanceForStudentAndSubject(studentId: Long, subjectId: Long): Flow<List<Attendance>> =
        attendanceDao.getAttendanceForStudentAndSubject(studentId, subjectId)

    fun getTotalClassesForTeacher(teacherId: Long): Flow<Int> =
        attendanceDao.getTotalClassesForTeacher(teacherId)

    suspend fun saveAttendance(
        subjectId: Long,
        date: String,
        statusMap: Map<Long, String>,
        classType: String = "Theory"
    ) {
        val records = statusMap.map { (studentId, status) ->
            Attendance(
                studentId = studentId,
                subjectId = subjectId,
                classDate = date,
                classType = classType,
                status = if (status == "A") "A" else "P"
            )
        }
        attendanceDao.insertAllAttendance(records)
    }

    suspend fun deleteAttendanceForDate(subjectId: Long, date: String, classType: String? = null) {
        if (classType != null) {
            attendanceDao.deleteAttendanceForDateAndType(subjectId, date, classType)
        } else {
            attendanceDao.deleteAttendanceForDate(subjectId, date)
        }
    }

    // ---------------- MARKS ----------------
    fun getMarksForSubject(subjectId: Long): Flow<List<Marks>> =
        marksDao.getMarksForSubject(subjectId)

    fun getMarksForStudentAndSubjectFlow(studentId: Long, subjectId: Long): Flow<Marks?> =
        marksDao.getMarksForStudentAndSubjectFlow(studentId, subjectId)

    suspend fun saveMarks(
        studentId: Long,
        subjectId: Long,
        ct1: Double?,
        ct2: Double?,
        ct3: Double?,
        midTerm: Double?,
        maxMark: Double = 100.0
    ): Result<Unit> {
        val marksList = listOfNotNull(ct1, ct2, ct3, midTerm)
        for (m in marksList) {
            if (m < 0.0) {
                return Result.failure(IllegalArgumentException("Marks cannot be negative."))
            }
            if (m > maxMark) {
                return Result.failure(IllegalArgumentException("Marks cannot exceed maximum mark ($maxMark)."))
            }
        }

        val existing = marksDao.getMarksForStudentAndSubjectSync(studentId, subjectId)
        val toSave = existing?.copy(
            ct1 = ct1,
            ct2 = ct2,
            ct3 = ct3,
            midTerm = midTerm
        ) ?: Marks(
            studentId = studentId,
            subjectId = subjectId,
            ct1 = ct1,
            ct2 = ct2,
            ct3 = ct3,
            midTerm = midTerm
        )
        marksDao.insertOrUpdateMarks(toSave)
        return Result.success(Unit)
    }

    // ---------------- REPORT GENERATOR HELPERS ----------------
    fun calculateAttendancePercentage(present: Int, total: Int): Double {
        if (total <= 0) return 0.0
        return (present.toDouble() / total.toDouble()) * 100.0
    }

    fun formatPercentage(pct: Double): String {
        return String.format(Locale.US, "%.2f%%", pct)
    }

    fun formatMark(mark: Double?): String {
        return if (mark != null) {
            if (mark % 1.0 == 0.0) {
                mark.toInt().toString()
            } else {
                String.format(Locale.US, "%.2f", mark)
            }
        } else {
            "-"
        }
    }

    fun exportToCsv(
        items: List<StudentReportItem>,
        departmentName: String,
        semesterName: String,
        subjectName: String
    ): String {
        val sb = StringBuilder()
        sb.append("TEACHER STUDENT MANAGER - REPORT\n")
        sb.append("Department:,\"").append(departmentName).append("\"\n")
        sb.append("Semester:,\"").append(semesterName).append("\"\n")
        sb.append("Subject:,\"").append(subjectName).append("\"\n\n")

        // Table Header
        sb.append("Roll,Student Name,Registration No,Total Classes,Present,Absent,Attendance %,CT1,CT2,CT3,CT Average,Mid-Term\n")
        for (item in items) {
            sb.append("\"").append(item.student.rollNumber).append("\",")
            sb.append("\"").append(item.student.studentName).append("\",")
            sb.append("\"").append(item.student.registrationNumber ?: "").append("\",")
            sb.append(item.totalClasses).append(",")
            sb.append(item.presentCount).append(",")
            sb.append(item.absentCount).append(",")
            sb.append("\"").append(formatPercentage(item.attendancePercentage)).append("\",")
            sb.append(formatMark(item.ct1)).append(",")
            sb.append(formatMark(item.ct2)).append(",")
            sb.append(formatMark(item.ct3)).append(",")
            sb.append(formatMark(item.ctAverage)).append(",")
            sb.append(formatMark(item.midTerm)).append("\n")
        }
        return sb.toString()
    }
}
