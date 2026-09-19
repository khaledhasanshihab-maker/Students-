package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AttendanceDao
import com.example.data.dao.DepartmentDao
import com.example.data.dao.MarksDao
import com.example.data.dao.SemesterDao
import com.example.data.dao.StudentDao
import com.example.data.dao.SubjectDao
import com.example.data.dao.UserDao
import com.example.data.model.Attendance
import com.example.data.model.Department
import com.example.data.model.Marks
import com.example.data.model.Semester
import com.example.data.model.Student
import com.example.data.model.Subject
import com.example.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Department::class,
        Semester::class,
        Subject::class,
        Student::class,
        Attendance::class,
        Marks::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun departmentDao(): DepartmentDao
    abstract fun semesterDao(): SemesterDao
    abstract fun subjectDao(): SubjectDao
    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun marksDao(): MarksDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "teacher_student_manager_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        seedSampleData(database)
                    }
                }
            }
        }

        suspend fun seedSampleData(database: AppDatabase) {
            val userDao = database.userDao()
            if (userDao.getUserCount() > 0) return

            // Default Teacher
            val teacherId = userDao.insertUser(
                User(
                    name = "Prof. Ahmed Rahman",
                    email = "teacher@school.edu",
                    password = "password123"
                )
            )

            // Department: Computer Technology
            val deptDao = database.departmentDao()
            val compDeptId = deptDao.insertDepartment(
                Department(
                    teacherId = teacherId,
                    departmentName = "Computer Technology"
                )
            )
            val elecDeptId = deptDao.insertDepartment(
                Department(
                    teacherId = teacherId,
                    departmentName = "Electronics Technology"
                )
            )
            val foodDeptId = deptDao.insertDepartment(
                Department(
                    teacherId = teacherId,
                    departmentName = "Food Technology"
                )
            )
            val racDeptId = deptDao.insertDepartment(
                Department(
                    teacherId = teacherId,
                    departmentName = "RAC Technology"
                )
            )

            val semDao = database.semesterDao()
            val subDao = database.subjectDao()

            var networkingSubId: Long = 0L
            var sem6Id: Long = 0L

            // Seed full semesters & subjects for all predefined curricula
            val curriculaToSeed = listOf(
                Pair(compDeptId, com.example.data.model.DiplomaCurricula.computerCurriculum),
                Pair(elecDeptId, com.example.data.model.DiplomaCurricula.electronicsCurriculum),
                Pair(foodDeptId, com.example.data.model.DiplomaCurricula.foodCurriculum),
                Pair(racDeptId, com.example.data.model.DiplomaCurricula.racCurriculum)
            )

            for ((dId, curr) in curriculaToSeed) {
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

                    val sId = semDao.insertSemester(
                        Semester(
                            departmentId = dId,
                            semesterName = semName
                        )
                    )

                    if (dId == compDeptId && semNum == 6) {
                        sem6Id = sId
                    }

                    val subjects = curr.semestersWithSubjects[semName] ?: emptyList()
                    for (defSub in subjects) {
                        val subId = subDao.insertSubject(
                            Subject(
                                departmentId = dId,
                                semesterId = sId,
                                teacherId = teacherId,
                                subjectName = defSub.subjectName,
                                subjectCode = defSub.subjectCode
                            )
                        )
                        if (dId == compDeptId && semNum == 6 && defSub.subjectName.contains("Computer Networking", ignoreCase = true)) {
                            networkingSubId = subId
                        }
                    }
                }
            }

            // Students:
            // Roll 101 - Rahim
            // Roll 102 - Karim
            // Roll 103 - Hasan
            val studentDao = database.studentDao()
            val rahimId = studentDao.insertStudent(
                Student(
                    teacherId = teacherId,
                    departmentId = compDeptId,
                    semesterId = sem6Id,
                    rollNumber = "101",
                    registrationNumber = "REG-2024-101",
                    studentName = "Rahim"
                )
            )
            val karimId = studentDao.insertStudent(
                Student(
                    teacherId = teacherId,
                    departmentId = compDeptId,
                    semesterId = sem6Id,
                    rollNumber = "102",
                    registrationNumber = "REG-2024-102",
                    studentName = "Karim"
                )
            )
            val hasanId = studentDao.insertStudent(
                Student(
                    teacherId = teacherId,
                    departmentId = compDeptId,
                    semesterId = sem6Id,
                    rollNumber = "103",
                    registrationNumber = "REG-2024-103",
                    studentName = "Hasan"
                )
            )

            // Sample Attendance
            // 01-09-2026 (Theory): 101=P, 102=A, 103=P
            // 03-09-2026 (Theory): 101=P, 102=P, 103=A
            // 04-09-2026 (Practical): 101=P, 102=P, 103=P
            val attDao = database.attendanceDao()
            attDao.insertAllAttendance(
                listOf(
                    Attendance(studentId = rahimId, subjectId = networkingSubId, classDate = "2026-09-01", classType = "Theory", status = "P"),
                    Attendance(studentId = karimId, subjectId = networkingSubId, classDate = "2026-09-01", classType = "Theory", status = "A"),
                    Attendance(studentId = hasanId, subjectId = networkingSubId, classDate = "2026-09-01", classType = "Theory", status = "P"),
                    Attendance(studentId = rahimId, subjectId = networkingSubId, classDate = "2026-09-03", classType = "Theory", status = "P"),
                    Attendance(studentId = karimId, subjectId = networkingSubId, classDate = "2026-09-03", classType = "Theory", status = "P"),
                    Attendance(studentId = hasanId, subjectId = networkingSubId, classDate = "2026-09-03", classType = "Theory", status = "A"),
                    Attendance(studentId = rahimId, subjectId = networkingSubId, classDate = "2026-09-04", classType = "Practical", status = "P"),
                    Attendance(studentId = karimId, subjectId = networkingSubId, classDate = "2026-09-04", classType = "Practical", status = "P"),
                    Attendance(studentId = hasanId, subjectId = networkingSubId, classDate = "2026-09-04", classType = "Practical", status = "P")
                )
            )

            // Sample Marks for Rahim: CT1 = 15, CT2 = 18, CT3 = 17, Mid-Term = 32
            // For Karim: CT1 = 14, CT2 = 16, CT3 = null, Mid-Term = 28
            val marksDao = database.marksDao()
            marksDao.insertOrUpdateMarks(
                Marks(
                    studentId = rahimId,
                    subjectId = networkingSubId,
                    ct1 = 15.0,
                    ct2 = 18.0,
                    ct3 = 17.0,
                    midTerm = 32.0
                )
            )
            marksDao.insertOrUpdateMarks(
                Marks(
                    studentId = karimId,
                    subjectId = networkingSubId,
                    ct1 = 14.0,
                    ct2 = 16.0,
                    ct3 = null,
                    midTerm = 28.0
                )
            )
        }
    }
}
