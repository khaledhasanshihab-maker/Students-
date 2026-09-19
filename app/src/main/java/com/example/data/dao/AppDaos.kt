package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Attendance
import com.example.data.model.Department
import com.example.data.model.Marks
import com.example.data.model.Semester
import com.example.data.model.Student
import com.example.data.model.Subject
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Long): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdSync(id: Long): User?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}

@Dao
interface DepartmentDao {
    @Query("SELECT * FROM departments WHERE teacher_id = :teacherId ORDER BY department_name ASC")
    fun getDepartmentsByTeacher(teacherId: Long): Flow<List<Department>>

    @Query("SELECT * FROM departments WHERE teacher_id = :teacherId ORDER BY department_name ASC")
    suspend fun getDepartmentsByTeacherSync(teacherId: Long): List<Department>

    @Query("SELECT * FROM departments WHERE id = :id LIMIT 1")
    suspend fun getDepartmentById(id: Long): Department?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartment(department: Department): Long

    @Update
    suspend fun updateDepartment(department: Department)

    @Delete
    suspend fun deleteDepartment(department: Department)
}

@Dao
interface SemesterDao {
    @Query("SELECT * FROM semesters WHERE department_id = :departmentId ORDER BY semester_name ASC")
    fun getSemestersByDepartment(departmentId: Long): Flow<List<Semester>>

    @Query("SELECT * FROM semesters WHERE department_id = :departmentId ORDER BY semester_name ASC")
    suspend fun getSemestersByDepartmentSync(departmentId: Long): List<Semester>

    @Query("""
        SELECT s.* FROM semesters s 
        INNER JOIN departments d ON s.department_id = d.id 
        WHERE d.teacher_id = :teacherId 
        ORDER BY s.semester_name ASC
    """)
    fun getAllSemestersByTeacher(teacherId: Long): Flow<List<Semester>>

    @Query("SELECT * FROM semesters WHERE id = :id LIMIT 1")
    suspend fun getSemesterById(id: Long): Semester?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: Semester): Long

    @Update
    suspend fun updateSemester(semester: Semester)

    @Delete
    suspend fun deleteSemester(semester: Semester)
}

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects WHERE teacher_id = :teacherId ORDER BY subject_name ASC")
    fun getSubjectsByTeacher(teacherId: Long): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE department_id = :departmentId AND semester_id = :semesterId ORDER BY subject_name ASC")
    fun getSubjectsByDepartmentAndSemester(departmentId: Long, semesterId: Long): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE department_id = :departmentId AND semester_id = :semesterId ORDER BY subject_name ASC")
    suspend fun getSubjectsByDepartmentAndSemesterSync(departmentId: Long, semesterId: Long): List<Subject>

    @Query("SELECT * FROM subjects WHERE semester_id = :semesterId ORDER BY subject_name ASC")
    fun getSubjectsBySemester(semesterId: Long): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    suspend fun getSubjectById(id: Long): Subject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject): Long

    @Update
    suspend fun updateSubject(subject: Subject)

    @Delete
    suspend fun deleteSubject(subject: Subject)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students WHERE teacher_id = :teacherId ORDER BY CAST(roll_number AS INTEGER) ASC, roll_number ASC")
    fun getStudentsByTeacher(teacherId: Long): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE department_id = :departmentId AND semester_id = :semesterId ORDER BY CAST(roll_number AS INTEGER) ASC, roll_number ASC")
    fun getStudentsByDepartmentAndSemester(departmentId: Long, semesterId: Long): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE department_id = :departmentId AND semester_id = :semesterId ORDER BY CAST(roll_number AS INTEGER) ASC, roll_number ASC")
    suspend fun getStudentsByDepartmentAndSemesterSync(departmentId: Long, semesterId: Long): List<Student>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentById(id: Long): Student?

    @Query("""
        SELECT COUNT(*) FROM students 
        WHERE department_id = :departmentId 
          AND semester_id = :semesterId 
          AND roll_number = :rollNumber 
          AND id != :excludeId
    """)
    suspend fun checkDuplicateRoll(departmentId: Long, semesterId: Long, rollNumber: String, excludeId: Long = -1): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertStudent(student: Student): Long

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE subject_id = :subjectId AND class_date = :classDate")
    fun getAttendanceForSubjectAndDate(subjectId: Long, classDate: String): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE subject_id = :subjectId AND class_date = :classDate")
    suspend fun getAttendanceForSubjectAndDateSync(subjectId: Long, classDate: String): List<Attendance>

    @Query("SELECT * FROM attendance WHERE subject_id = :subjectId")
    fun getAttendanceForSubject(subjectId: Long): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE student_id = :studentId ORDER BY class_date DESC")
    fun getAttendanceForStudent(studentId: Long): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE student_id = :studentId AND subject_id = :subjectId ORDER BY class_date DESC")
    fun getAttendanceForStudentAndSubject(studentId: Long, subjectId: Long): Flow<List<Attendance>>

    @Query("SELECT DISTINCT class_date FROM attendance WHERE subject_id = :subjectId ORDER BY class_date ASC")
    fun getDistinctDatesForSubject(subjectId: Long): Flow<List<String>>

    @Query("SELECT DISTINCT class_date FROM attendance WHERE subject_id = :subjectId ORDER BY class_date ASC")
    suspend fun getDistinctDatesForSubjectSync(subjectId: Long): List<String>

    @Query("SELECT COUNT(DISTINCT class_date) FROM attendance WHERE subject_id = :subjectId")
    fun getTotalClassesForSubject(subjectId: Long): Flow<Int>

    @Query("SELECT COUNT(DISTINCT class_date) FROM attendance WHERE subject_id IN (SELECT id FROM subjects WHERE teacher_id = :teacherId)")
    fun getTotalClassesForTeacher(teacherId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAttendance(list: List<Attendance>)

    @Query("DELETE FROM attendance WHERE subject_id = :subjectId AND class_date = :classDate")
    suspend fun deleteAttendanceForDate(subjectId: Long, classDate: String)
}

@Dao
interface MarksDao {
    @Query("SELECT * FROM marks WHERE subject_id = :subjectId")
    fun getMarksForSubject(subjectId: Long): Flow<List<Marks>>

    @Query("SELECT * FROM marks WHERE student_id = :studentId")
    fun getMarksForStudent(studentId: Long): Flow<List<Marks>>

    @Query("SELECT * FROM marks WHERE student_id = :studentId AND subject_id = :subjectId LIMIT 1")
    fun getMarksForStudentAndSubjectFlow(studentId: Long, subjectId: Long): Flow<Marks?>

    @Query("SELECT * FROM marks WHERE student_id = :studentId AND subject_id = :subjectId LIMIT 1")
    suspend fun getMarksForStudentAndSubjectSync(studentId: Long, subjectId: Long): Marks?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMarks(marks: Marks): Long

    @Query("DELETE FROM marks WHERE student_id = :studentId AND subject_id = :subjectId")
    suspend fun deleteMarks(studentId: Long, subjectId: Long)
}
