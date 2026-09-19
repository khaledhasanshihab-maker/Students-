package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val password: String = "password123"
)

@Entity(
    tableName = "departments",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["teacher_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["teacher_id"])]
)
data class Department(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "teacher_id")
    val teacherId: Long,
    @ColumnInfo(name = "department_name")
    val departmentName: String
)

@Entity(
    tableName = "semesters",
    foreignKeys = [
        ForeignKey(
            entity = Department::class,
            parentColumns = ["id"],
            childColumns = ["department_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["department_id"])]
)
data class Semester(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "department_id")
    val departmentId: Long,
    @ColumnInfo(name = "semester_name")
    val semesterName: String
)

@Entity(
    tableName = "subjects",
    foreignKeys = [
        ForeignKey(
            entity = Department::class,
            parentColumns = ["id"],
            childColumns = ["department_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Semester::class,
            parentColumns = ["id"],
            childColumns = ["semester_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["teacher_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["department_id"]),
        Index(value = ["semester_id"]),
        Index(value = ["teacher_id"])
    ]
)
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "department_id")
    val departmentId: Long,
    @ColumnInfo(name = "semester_id")
    val semesterId: Long,
    @ColumnInfo(name = "teacher_id")
    val teacherId: Long,
    @ColumnInfo(name = "subject_name")
    val subjectName: String,
    @ColumnInfo(name = "subject_code")
    val subjectCode: String
)

@Entity(
    tableName = "students",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["teacher_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Department::class,
            parentColumns = ["id"],
            childColumns = ["department_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Semester::class,
            parentColumns = ["id"],
            childColumns = ["semester_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["department_id", "semester_id", "roll_number"], unique = true),
        Index(value = ["teacher_id"]),
        Index(value = ["department_id"]),
        Index(value = ["semester_id"])
    ]
)
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "teacher_id")
    val teacherId: Long,
    @ColumnInfo(name = "department_id")
    val departmentId: Long,
    @ColumnInfo(name = "semester_id")
    val semesterId: Long,
    @ColumnInfo(name = "roll_number")
    val rollNumber: String,
    @ColumnInfo(name = "registration_number")
    val registrationNumber: String? = null,
    @ColumnInfo(name = "student_name")
    val studentName: String
)

@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["id"],
            childColumns = ["student_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subject_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["student_id", "subject_id", "class_date"], unique = true),
        Index(value = ["student_id"]),
        Index(value = ["subject_id"]),
        Index(value = ["class_date"])
    ]
)
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "student_id")
    val studentId: Long,
    @ColumnInfo(name = "subject_id")
    val subjectId: Long,
    @ColumnInfo(name = "class_date")
    val classDate: String, // Format: YYYY-MM-DD or DD-MM-YYYY
    val status: String // "P" or "A"
)

@Entity(
    tableName = "marks",
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["id"],
            childColumns = ["student_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subject_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["student_id", "subject_id"], unique = true),
        Index(value = ["student_id"]),
        Index(value = ["subject_id"])
    ]
)
data class Marks(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "student_id")
    val studentId: Long,
    @ColumnInfo(name = "subject_id")
    val subjectId: Long,
    val ct1: Double? = null,
    val ct2: Double? = null,
    val ct3: Double? = null,
    @ColumnInfo(name = "mid_term")
    val midTerm: Double? = null
) {
    /**
     * Calculates Class Test Average.
     * Formula: sum of entered marks / count of entered marks.
     * If no test mark entered, returns null.
     * Does NOT treat empty marks as 0.
     */
    fun calculateCtAverage(): Double? {
        val entered = listOfNotNull(ct1, ct2, ct3)
        return if (entered.isEmpty()) null else entered.sum() / entered.size
    }
}
